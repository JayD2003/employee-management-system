package com.emp_mgmt_sys.service.Impl;

import com.emp_mgmt_sys.dto.requestDTO.CreateLeaveRequest;
import com.emp_mgmt_sys.dto.requestDTO.UpdateLeaveRequest;
import com.emp_mgmt_sys.dto.responseDTO.LeaveBalanceResponseDTO;
import com.emp_mgmt_sys.dto.responseDTO.LeaveRequestResponseDTO;
import com.emp_mgmt_sys.entity.LeaveBalance;
import com.emp_mgmt_sys.entity.LeaveRequest;
import com.emp_mgmt_sys.entity.User;
import com.emp_mgmt_sys.enums.Status;
import com.emp_mgmt_sys.exception.InsufficientLeaveBalanceException;
import com.emp_mgmt_sys.exception.InvalidLeaveRequestException;
import com.emp_mgmt_sys.exception.ResourceNotFoundException;
import com.emp_mgmt_sys.repository.LeaveBalanceRepository;
import com.emp_mgmt_sys.repository.LeaveRequestRepository;
import com.emp_mgmt_sys.repository.UserRepository;
import com.emp_mgmt_sys.service.LeaveService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class LeaveServiceImpl implements LeaveService {

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private static final int DEFAULT_PAID_LEAVE = 20;
    private static final int DEFAULT_SICK_LEAVE = 10;
    private static final int DEFAULT_UNPAID_LEAVE = 20;

    @Override
    public void createLeaveRequest(CreateLeaveRequest leave) {
        User user = userRepository.findById(leave.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + leave.getUserId()));

        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            throw new InvalidLeaveRequestException("End date cannot be before start date");
        }

        boolean hasOverlap = leaveRequestRepository.existsOverlappingLeaveRequest(user.getId(), leave.getStartDate(), leave.getEndDate());
        if (hasOverlap) {
            throw new InvalidLeaveRequestException("Overlapping leave request exists.");
        }


        LeaveBalance balance = leaveBalanceRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found for user"));

        long daysRequested = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;

        // Check leave balance based on leave type
        switch (leave.getLeaveType()) {
            case PAID:
                if (balance.getPaidLeaveBalance() < daysRequested) {
                    throw new InsufficientLeaveBalanceException("Insufficient paid leave balance");
                }
                break;
            case SICK:
                if (balance.getSickLeaveBalance() < daysRequested) {
                    throw new InsufficientLeaveBalanceException("Insufficient sick leave balance");
                }
                break;
            case UNPAID:
                if (balance.getUnpaidLeaveBalance() < daysRequested) {
                    throw new InsufficientLeaveBalanceException("Insufficient unpaid leave balance");
                }
                break;
            default:
                throw new InvalidLeaveRequestException("Unknown leave type");
        }

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setUser(user);
        leaveRequest.setLeaveType(leave.getLeaveType());
        leaveRequest.setStartDate(leave.getStartDate());
        leaveRequest.setEndDate(leave.getEndDate());
        leaveRequest.setReason(leave.getReason());
        leaveRequest.setStatus(Status.PENDING);
        leaveRequest.setRequestDate(LocalDateTime.now());

        leaveRequestRepository.save(leaveRequest);
    }

    @Override
    @Transactional
    public void updateLeaveStatus(UpdateLeaveRequest dto) {
        User manager = userRepository.findById(dto.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        LeaveRequest leaveRequest = leaveRequestRepository.findById(dto.getLeaveRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));

        if (leaveRequest.getStatus() != Status.PENDING) {
            throw new InvalidLeaveRequestException("Only pending requests can be updated");
        }

        leaveRequest.setStatus(dto.getStatus());

        if (dto.getStatus() == Status.APPROVED) {
            LeaveBalance balance = leaveBalanceRepository.findByUserId(leaveRequest.getUser().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));

            long days = ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate()) + 1;

            // Deduct leave balance atomically & check
            switch (leaveRequest.getLeaveType()) {
                case PAID:
                    if (balance.getPaidLeaveBalance() < days)
                        throw new InsufficientLeaveBalanceException("Insufficient paid leave balance");
                    balance.setPaidLeaveBalance(balance.getPaidLeaveBalance() - (int) days);
                    break;
                case SICK:
                    if (balance.getSickLeaveBalance() < days)
                        throw new InsufficientLeaveBalanceException("Insufficient sick leave balance");
                    balance.setSickLeaveBalance(balance.getSickLeaveBalance() - (int) days);
                    break;
                case UNPAID:
                    if (balance.getUnpaidLeaveBalance() < days)
                        throw new InsufficientLeaveBalanceException("Insufficient unpaid leave balance");
                    balance.setUnpaidLeaveBalance(balance.getUnpaidLeaveBalance() - (int) days);
                    break;
                default:
                    throw new InvalidLeaveRequestException("Unknown leave type");
            }

            leaveBalanceRepository.save(balance);
        }

        leaveRequestRepository.save(leaveRequest);
    }

    @Override
    public List<LeaveRequestResponseDTO> getLeaveRequests(Long userId) {
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByUserId(userId);
        return leaveRequests.stream()
                .map(LeaveRequest::getDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveRequestResponseDTO> getLeaveRequestsForManagerOnLeaveStatus(String managerEmail, String status) {
        Status leaveStatus;
        try {
            leaveStatus = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidLeaveRequestException("Invalid leave status: " + status);
        }

        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        List<User> users = userRepository.findByManager(manager);
        List<Long> userIds = users.stream().map(User::getId).toList();

        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByUserIdInAndStatus(userIds, leaveStatus);

        return leaveRequests.stream()
                .map(LeaveRequest::getDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveRequestResponseDTO> getLeaveRequestHistoryForManager(String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        List<User> users = userRepository.findByManager(manager);
        List<Long> userIds = users.stream().map(User::getId).toList();

        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByUserIdIn(userIds);

        return leaveRequests.stream()
                .map(LeaveRequest::getDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void createLeaveBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        LeaveBalance leaveBalance = new LeaveBalance();
        leaveBalance.setUser(user);
        leaveBalance.setPaidLeaveBalance(DEFAULT_PAID_LEAVE);
        leaveBalance.setSickLeaveBalance(DEFAULT_SICK_LEAVE);
        leaveBalance.setUnpaidLeaveBalance(DEFAULT_UNPAID_LEAVE);

        leaveBalanceRepository.save(leaveBalance);
    }

    @Override
    public LeaveBalanceResponseDTO getLeaveBalance(Long userId) {
        LeaveBalance leaveBalance = leaveBalanceRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveBalance not found with id " + userId));
        return leaveBalance.getDTO();
    }

    @Override
    public List<LeaveBalanceResponseDTO> getLeaveBalancesForManager(String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        List<User> users = userRepository.findByManager(manager);
        List<Long> userIds = users.stream().map(User::getId).toList();

        List<LeaveBalance> leaveBalances = leaveBalanceRepository.findByUserIdIn(userIds);

        return leaveBalances.stream()
                .map(LeaveBalance::getDTO)
                .collect(Collectors.toList());
    }
}
