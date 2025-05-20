package com.emp_mgmt_sys.service.Impl;

import com.emp_mgmt_sys.dto.requestDTO.CreateShiftSwapRequest;
import com.emp_mgmt_sys.dto.requestDTO.ShiftAssignmentRequest;
import com.emp_mgmt_sys.dto.responseDTO.ShiftSwapResponseDTO;
import com.emp_mgmt_sys.dto.requestDTO.UpdateShiftSwapRequest;
import com.emp_mgmt_sys.dto.responseDTO.ShiftResponseDTO;
import com.emp_mgmt_sys.entity.Shift;
import com.emp_mgmt_sys.entity.ShiftBalance;
import com.emp_mgmt_sys.entity.ShiftSwapRequest;
import com.emp_mgmt_sys.entity.User;
import com.emp_mgmt_sys.enums.Status;
import com.emp_mgmt_sys.exception.InsufficientSwapBalanceException;
import com.emp_mgmt_sys.exception.InvalidSwapRequestException;
import com.emp_mgmt_sys.repository.ShiftBalanceRepository;
import com.emp_mgmt_sys.repository.ShiftRepository;
import com.emp_mgmt_sys.repository.ShiftSwapRequestRepository;
import com.emp_mgmt_sys.repository.UserRepository;
import com.emp_mgmt_sys.service.ShiftService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShiftServiceImpl implements ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShiftBalanceRepository shiftBalanceRepository;

    @Autowired
    private ShiftSwapRequestRepository shiftSwapRequestRepository;

    /**
     * Helper method to get or create ShiftBalance record for a user for a given month.
     * Default monthly swap count is 7.
     */
    private ShiftBalance getOrCreateShiftBalance(User user, YearMonth ym) {
        return shiftBalanceRepository.findByUserIdAndYearAndMonth(user.getId(), ym.getYear(), ym.getMonthValue())
                .orElseGet(() -> {
                    ShiftBalance balance = new ShiftBalance();
                    balance.setUser(user);
                    balance.setYear(ym.getYear());
                    balance.setMonth(ym.getMonthValue());
                    balance.setSwapCount(7);  // Default swap allowance per month
                    return shiftBalanceRepository.save(balance);
                });
    }

    @Override
    public void assignShift(ShiftAssignmentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new InvalidSwapRequestException("User not found"));

        if (shiftRepository.findByUserIdAndShiftDate(user.getId(), request.getDate()).isPresent()) {
            throw new InvalidSwapRequestException("Shift already assigned on this date for user");
        }

        YearMonth ym = YearMonth.from(request.getDate());
        getOrCreateShiftBalance(user, ym);

        Shift shift = new Shift();
        shift.setUser(user);
        shift.setShiftDate(request.getDate());
        shift.setShiftType(request.getShiftType());
        shift.setCreatedDate(LocalDateTime.now());

        shiftRepository.save(shift);
    }

    @Override
    public void assignWeeklyShifts(ShiftAssignmentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new InvalidSwapRequestException("User not found"));

        LocalDate monday = request.getDate();
        LocalDate friday = monday.plusDays(4);

        List<Shift> existingShifts = shiftRepository.findByUserIdAndShiftDateBetween(user.getId(), monday, friday);
        Set<LocalDate> existingDates = existingShifts.stream()
                .map(Shift::getShiftDate)
                .collect(Collectors.toSet());

        Set<YearMonth> involvedMonths = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            involvedMonths.add(YearMonth.from(monday.plusDays(i)));
        }
        involvedMonths.forEach(ym -> getOrCreateShiftBalance(user, ym));

        for (int i = 0; i < 5; i++) {
            LocalDate date = monday.plusDays(i);
            if (!existingDates.contains(date)) {
                Shift shift = new Shift();
                shift.setUser(user);
                shift.setShiftDate(date);
                shift.setShiftType(request.getShiftType());
                shift.setCreatedDate(LocalDateTime.now());
                shiftRepository.save(shift);
            }
        }
    }

    @Override
    public List<ShiftResponseDTO> getEmployeeShifts(Long userId, LocalDate referenceDate) {
        LocalDate startOfWeek = referenceDate.with(DayOfWeek.MONDAY);
        LocalDate today = LocalDate.now();

        if (startOfWeek.isBefore(today)) {
            startOfWeek = today;
        }
        LocalDate endOfWeek = startOfWeek.plusDays(4);

        return shiftRepository.findByUserIdAndShiftDateBetween(userId, startOfWeek, endOfWeek)
                .stream()
                .map(Shift::getDto)
                .collect(Collectors.toList());
    }

    @Override
    public ShiftResponseDTO getEmployeeShiftByDate(Long userId, LocalDate date) {
        return shiftRepository.findByUserIdAndShiftDate(userId, date)
                .map(Shift::getDto)
                .orElse(null);
    }

    @Override
    public int getSwapBalance(Long userId) {
        YearMonth now = YearMonth.now();
        return shiftBalanceRepository.findByUserIdAndYearAndMonth(userId, now.getYear(), now.getMonthValue())
                .map(ShiftBalance::getSwapCount)
                .orElse(0);
    }

    @Override
    public List<ShiftResponseDTO> getShiftsByDateForManager(Long managerId, LocalDate date) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new InvalidSwapRequestException("Manager not found"));

        List<User> subordinates = userRepository.findByManager(manager);
        if (subordinates.isEmpty()) {
            return Collections.emptyList();
        }

        return shiftRepository.findByUserInAndShiftDate(subordinates, date)
                .stream()
                .map(Shift::getDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createSwapRequest(CreateShiftSwapRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new InvalidSwapRequestException("User not found"));

        Shift currentShift = shiftRepository.findById(request.getShiftId())
                .orElseThrow(() -> new InvalidSwapRequestException("Current shift not found"));

        int year = currentShift.getShiftDate().getYear();
        int month = currentShift.getShiftDate().getMonthValue();

        ShiftBalance balance = shiftBalanceRepository.findByUserIdAndYearAndMonth(user.getId(), year, month)
                .orElseThrow(() -> new InvalidSwapRequestException("Shift balance record not found"));

        if (balance.getSwapCount() <= 0) {
            throw new InsufficientSwapBalanceException("No swap balance left");
        }

        if (shiftRepository.findByUserIdAndShiftDate(user.getId(), request.getShiftDate()).isPresent()) {
            throw new InvalidSwapRequestException("User already has shift on requested date");
        }

        if (!request.getShiftDate().isAfter(LocalDate.now())) {
            throw new InvalidSwapRequestException("Swap request must be at least 1 day in advance");
        }

        ShiftSwapRequest swapRequest = new ShiftSwapRequest();
        swapRequest.setUser(user);
        swapRequest.setCreatedDate(LocalDateTime.now());
        swapRequest.setShift(currentShift);
        swapRequest.setShiftType(request.getShiftType());
        swapRequest.setShiftDate(request.getShiftDate());
        swapRequest.setStatus(Status.PENDING);

        shiftSwapRequestRepository.save(swapRequest);
    }

    @Override
    @Transactional
    public void updateSwapRequestStatus(UpdateShiftSwapRequest request) {
        ShiftSwapRequest swapRequest = shiftSwapRequestRepository.findById(request.getSwapId())
                .orElseThrow(() -> new InvalidSwapRequestException("Shift swap request not found"));

        if (request.getStatus() == Status.APPROVED) {
            Shift currentShift = swapRequest.getShift();
            YearMonth ym = YearMonth.from(currentShift.getShiftDate());

            ShiftBalance balance = shiftBalanceRepository.findByUserIdAndYearAndMonth(swapRequest.getUser().getId(), ym.getYear(), ym.getMonthValue())
                    .orElseThrow(() -> new InvalidSwapRequestException("Shift balance not found"));

            if (balance.getSwapCount() <= 0) {
                throw new InsufficientSwapBalanceException("No swap balance left");
            }

            balance.setSwapCount(balance.getSwapCount() - 1);
            shiftBalanceRepository.save(balance);

            currentShift.setShiftDate(swapRequest.getShiftDate());
            currentShift.setShiftType(swapRequest.getShiftType());
            shiftRepository.save(currentShift);

            swapRequest.setStatus(Status.APPROVED);
            shiftSwapRequestRepository.save(swapRequest);

        } else if (request.getStatus() == Status.REJECTED) {
            swapRequest.setStatus(Status.REJECTED);
            shiftSwapRequestRepository.save(swapRequest);
        } else {
            throw new InvalidSwapRequestException("Invalid status update");
        }
    }

    @Override
    public List<ShiftSwapResponseDTO> getSwapRequestsByUser(Long userId) {
        List<ShiftSwapRequest> requests = shiftSwapRequestRepository.findByUserId(userId);
        return requests.stream()
                .map(ShiftSwapRequest::getDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShiftSwapResponseDTO> getShiftSwapRequestsForManagerOnStatus(String managerEmail, String statusStr) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new InvalidSwapRequestException("Manager not found"));

        List<User> subordinates = userRepository.findByManager(manager);
        if (subordinates.isEmpty()) {
            return Collections.emptyList();
        }

        Status status;
        try {
            status = Status.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidSwapRequestException("Invalid status value");
        }

        List<ShiftSwapRequest> requests = shiftSwapRequestRepository.findByUserInAndStatus(subordinates, status);
        return requests.stream()
                .map(ShiftSwapRequest::getDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShiftSwapResponseDTO> getSwapRequestsForManager(Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new InvalidSwapRequestException("Manager not found"));

        List<User> subordinates = userRepository.findByManager(manager);
        if (subordinates.isEmpty()) {
            return Collections.emptyList();
        }

        List<ShiftSwapRequest> requests = shiftSwapRequestRepository.findByUserIdIn(subordinates);
        return requests.stream()
                .map(ShiftSwapRequest::getDto)
                .collect(Collectors.toList());
    }
}