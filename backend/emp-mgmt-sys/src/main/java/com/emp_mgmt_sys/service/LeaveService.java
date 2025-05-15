package com.emp_mgmt_sys.service;

import com.emp_mgmt_sys.dto.*;
import com.emp_mgmt_sys.entity.Attendance;
import com.emp_mgmt_sys.entity.LeaveBalance;
import com.emp_mgmt_sys.entity.LeaveRequest;
import com.emp_mgmt_sys.entity.User;
import com.emp_mgmt_sys.enums.LeaveStatus;
import com.emp_mgmt_sys.enums.UserRole;
import com.emp_mgmt_sys.exception.UserNotFoundException;
import com.emp_mgmt_sys.repository.LeaveBalanceRepository;
import com.emp_mgmt_sys.repository.LeaveRequestRepository;
import com.emp_mgmt_sys.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeaveService {

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private UserRepository userRepository;

    public void createLeaveRequest(CreateLeaveRequest leave){

        User user = userRepository.findById(leave.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + leave.getUserId()));

        LeaveBalance balance = leaveBalanceRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Leave balance not found for user"));

        long daysRequested = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;

        switch (leave.getLeaveType()) {
            case PAID:
                if (balance.getPaidLeaveBalance() <= daysRequested) {
                    throw new RuntimeException("Insufficient paid leave balance");
                }
                break;
            case SICK:
                if (balance.getSickLeaveBalance() <= daysRequested) {
                    throw new RuntimeException("Insufficient sick leave balance");
                }
                break;
            case UNPAID:
                if (balance.getUnpaidLeaveBalance() <= daysRequested) {
                    throw new RuntimeException("Insufficient sick leave balance");
                }
                break;
        }


        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setUser(user);
        leaveRequest.setLeaveType(leave.getLeaveType());
        leaveRequest.setStartDate(leave.getStartDate());
        leaveRequest.setEndDate(leave.getEndDate());
        leaveRequest.setReason(leave.getReason());
        leaveRequest.setStatus(LeaveStatus.PENDING);
        leaveRequest.setRequestDate(LocalDateTime.now());

        leaveRequestRepository.save(leaveRequest);
    }

    public void updateLeaveStatus(UpdateLeaveRequest dto) {
        User manager = userRepository.findById(dto.getManagerId())
                .orElseThrow(() -> new UserNotFoundException("Manager not found"));

        LeaveRequest leaveRequest = leaveRequestRepository.findById(dto.getLeaveRequestId())
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be updated");
        }

        leaveRequest.setStatus(dto.getStatus());

        if (dto.getStatus() == LeaveStatus.APPROVED) {
            LeaveBalance balance = leaveBalanceRepository.findByUserId(leaveRequest.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("Leave balance not found"));

            long days = ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate()) + 1;

            switch (leaveRequest.getLeaveType()) {
                case PAID:
                    if (balance.getPaidLeaveBalance() <= days)
                        throw new RuntimeException("Insufficient paid leave balance");
                    balance.setPaidLeaveBalance((int) (balance.getPaidLeaveBalance() - days));
                    break;
                case SICK:
                    if (balance.getSickLeaveBalance() <= days)
                        throw new RuntimeException("Insufficient sick leave balance");
                    balance.setSickLeaveBalance((int) (balance.getSickLeaveBalance() - days));
                    break;
                case UNPAID:
                    if (balance.getUnpaidLeaveBalance() <= days)
                        throw new RuntimeException("Insufficient sick leave balance");
                    balance.setUnpaidLeaveBalance((int) (balance.getUnpaidLeaveBalance() - days));
                    break;
            }

            leaveBalanceRepository.save(balance);
        }

        leaveRequestRepository.save(leaveRequest);
    }

    public List<LeaveRequestDTO> getLeaveRequests(Long userId){
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByUserId(userId);
        return leaveRequests.stream().map(LeaveRequest::getDTO).collect(Collectors.toList());
    }

    public List<LeaveRequestDTO> getLeaveRequestsForManagerOnLeaveStatus(String managerEmail, String status) {
        LeaveStatus leaveStatus;

        try {
            leaveStatus = LeaveStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid leave status: " + status);
        }

        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new UserNotFoundException("Manager not found"));

        List<User> users = userRepository.findByManager(manager);

        List<UserDTO> employees = users.stream().map(User::getDTO).toList();

        List<LeaveRequest> leaveRequests = new ArrayList<>();

        for (UserDTO employee : employees) {
            List<LeaveRequest> leaveRequestList = leaveRequestRepository.findByUserIdAndStatus(employee.getId(), leaveStatus);
            leaveRequests.addAll(leaveRequestList);
        }

        return leaveRequests.stream()
                .map(LeaveRequest::getDTO)
                .collect(Collectors.toList());
    }

    public List<LeaveRequestDTO> getLeaveRequestHistoryForManager(String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new UserNotFoundException("Manager not found"));

        List<User> users = userRepository.findByManager(manager);

        List<UserDTO> employees = users.stream().map(User::getDTO).toList();

        List<LeaveRequest> leaveRequests = new ArrayList<>();

        for (UserDTO employee : employees) {
            List<LeaveRequest> leaveRequestList = leaveRequestRepository.findByUserId(employee.getId());
            leaveRequests.addAll(leaveRequestList);
        }

        return leaveRequests.stream()
                .map(LeaveRequest::getDTO)
                .collect(Collectors.toList());
    }

    public void createLeaveBalance(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        LeaveBalance leaveBalance = new LeaveBalance();
        leaveBalance.setUser(user);
        leaveBalance.setPaidLeaveBalance(20);
        leaveBalance.setSickLeaveBalance(10);
        leaveBalance.setUnpaidLeaveBalance(20);

        leaveBalanceRepository.save(leaveBalance);
    }

    public LeaveBalanceDTO getLeaveBalance(Long userId){
        Optional<LeaveBalance> leaveBalance = leaveBalanceRepository.findByUserId(userId);// Assuming 'email' is used as username
        if(leaveBalance.isPresent()) {
            return leaveBalance.get().getDTO();
        }else{
            throw new UserNotFoundException("LeaveBalance not found with id" + userId);
        }
    }

    public List<LeaveBalanceDTO> getLeaveBalancesForManager(String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new UserNotFoundException("Manager not found"));

        List<User> users = userRepository.findByManager(manager);

        List<UserDTO> employees = users.stream().map(User::getDTO).toList();

        List<LeaveBalance> leaveBalances = new ArrayList<>();

        for (UserDTO employee : employees) {
            Optional<LeaveBalance> leaveBalance = leaveBalanceRepository.findByUserId(employee.getId());
            leaveBalance.ifPresent(leaveBalances::add);
        }

        return leaveBalances.stream()
                .map(LeaveBalance::getDTO)
                .collect(Collectors.toList());
    }

}
