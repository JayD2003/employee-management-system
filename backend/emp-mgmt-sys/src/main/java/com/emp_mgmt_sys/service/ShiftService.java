package com.emp_mgmt_sys.service;

import com.emp_mgmt_sys.dto.*;
import com.emp_mgmt_sys.entity.*;
import com.emp_mgmt_sys.enums.ShiftType;
import com.emp_mgmt_sys.enums.Status;
import com.emp_mgmt_sys.repository.ShiftBalanceRepository;
import com.emp_mgmt_sys.repository.ShiftRepository;
import com.emp_mgmt_sys.repository.ShiftSwapRequestRepository;
import com.emp_mgmt_sys.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShiftBalanceRepository shiftBalanceRepository;

    @Autowired
    private ShiftSwapRequestRepository shiftSwapRequestRepository;

    public void assignShift(ShiftAssignmentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Check for existing swap balance, else create one
        YearMonth ym = YearMonth.from(request.getDate());
        Optional<ShiftBalance> balanceOpt = shiftBalanceRepository
                .findByUserIdAndYearAndMonth(user.getId(), ym.getYear(), ym.getMonthValue());

        if (balanceOpt.isEmpty()) {
            ShiftBalance balance = new ShiftBalance();
            balance.setUser(user);
            balance.setYear(ym.getYear());
            balance.setMonth(ym.getMonthValue());
            balance.setSwapCount(7);
            shiftBalanceRepository.save(balance);
        }

        // 2. Save the shift
        Shift shift = new Shift();
        shift.setUser(user);
        shift.setShiftDate(request.getDate());
        shift.setShiftType(request.getShiftType());
        shift.setCreatedDate(LocalDateTime.now());

        shiftRepository.save(shift);
    }

    public void assignWeeklyShifts(ShiftAssignmentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate monday = request.getDate();

        for (int i = 0; i < 5; i++) { // Monday to Friday
            LocalDate date = monday.plusDays(i);
            YearMonth ym = YearMonth.from(date);

            // Create swap balance if not exists
            shiftBalanceRepository.findByUserIdAndYearAndMonth(user.getId(), ym.getYear(), ym.getMonthValue())
                    .orElseGet(() -> {
                        ShiftBalance balance = new ShiftBalance();
                        balance.setUser(user);
                        balance.setYear(ym.getYear());
                        balance.setMonth(ym.getMonthValue());
                        balance.setSwapCount(7);
                        return shiftBalanceRepository.save(balance);
                    });

            // Skip if already assigned
            if (shiftRepository.findByUserIdAndShiftDate(user.getId(), date).isPresent()) {
                continue;
            }

            // Save shift
            Shift shift = new Shift();
            shift.setUser(user);
            shift.setShiftDate(date);
            shift.setShiftType(request.getShiftType());
            shift.setCreatedDate(LocalDateTime.now());
            shiftRepository.save(shift);
        }
    }


    public List<ShiftDTO> getEmployeeShifts(Long userId, LocalDate referenceDate) {
        DayOfWeek day = referenceDate.getDayOfWeek();
        LocalDate startOfWeek;

        if (day == DayOfWeek.FRIDAY) {
            // Show next week's shift
            startOfWeek = referenceDate.plusDays(3); // Next Monday
        } else {
            // Show current week's future shifts only
            startOfWeek = referenceDate.with(DayOfWeek.MONDAY);
        }

        LocalDate today = LocalDate.now();
        if (startOfWeek.isBefore(today)) {
            startOfWeek = today;
        }

        LocalDate endOfWeek = startOfWeek.plusDays(4); // Monday to Friday

        return shiftRepository.findByUserIdAndShiftDateBetween(userId, startOfWeek, endOfWeek)
                .stream()
                .filter(shift -> !shift.getShiftDate().isBefore(today)) // extra safeguard
                .map(Shift::getDto)
                .collect(Collectors.toList());
    }

    public ShiftDTO getEmployeeShiftByDate(Long userId, LocalDate date) {
        return shiftRepository.findByUserIdAndShiftDate(userId, date)
                .map(Shift::getDto)
                .orElse(null);
    }

    public int getSwapBalance(Long userId) {
        YearMonth currentMonth = YearMonth.now();
        return shiftBalanceRepository
                .findByUserIdAndYearAndMonth(userId, currentMonth.getYear(), currentMonth.getMonthValue())
                .map(ShiftBalance::getSwapCount)
                .orElse(0);
    }

    public List<ShiftDTO> getShiftsByDateForManager(Long managerId, LocalDate date) {
        Optional<User> managerOpt = userRepository.findById(managerId);
        if (managerOpt.isEmpty()) {
            throw new RuntimeException("Manager not found with id: " + managerId);
        }

        User manager = managerOpt.get();
        List<User> subordinates = userRepository.findByManager(manager);

        if (subordinates.isEmpty()) {
            return Collections.emptyList();
        }

        return shiftRepository.findByUserInAndShiftDate(subordinates, date)
                .stream()
                .map(Shift::getDto)
                .collect(Collectors.toList());
    }

    public void createSwapRequest(CreateShiftSwapRequest request) {
        // Fetch users
        User requester = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Requester not found"));

        // Fetch shifts
        Shift currentShift = shiftRepository.findById(request.getShiftId())
                .orElseThrow(() -> new RuntimeException("Requester shift not found"));

        // Ensure swap balance exists or create one (limit 7)
        int currentYear = currentShift.getShiftDate().getYear();
        int currentMonth = currentShift.getShiftDate().getMonthValue();

        Optional<ShiftBalance> balance = shiftBalanceRepository.findByUserIdAndYearAndMonth(requester.getId(), currentYear, currentMonth);

        if (balance.isPresent()) {
            if(balance.get().getSwapCount()<=0) {
                throw new RuntimeException("Swap balance exceeded for this month.");
            }
        }

        // Prevent conflict if user already has a shift on the requested swap date
        Optional<Shift> conflictingShift = shiftRepository.findByUserIdAndShiftDate(requester.getId(), request.getShiftDate());
        if (conflictingShift.isPresent()) {
            throw new RuntimeException("Cannot request swap — shift already exists on the desired date.");
        }

        if (!request.getShiftDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("Shift swap must be requested at least 1 day in advance.");
        }

        // Create swap request
        ShiftSwapRequest swapRequest = new ShiftSwapRequest();
        swapRequest.setUser(requester);
        swapRequest.setCreatedDate(LocalDateTime.now());
        swapRequest.setShift(currentShift);
        if(request.getShiftType()== ShiftType.DAY) {
            swapRequest.setShiftType(ShiftType.NIGHT);
        }else {
            swapRequest.setShiftType(ShiftType.DAY);
        }
        swapRequest.setShiftDate(request.getShiftDate());
        swapRequest.setStatus(Status.PENDING);

        shiftSwapRequestRepository.save(swapRequest);
    }

    public void updateSwapRequestStatus(UpdateShiftSwapRequest request) {
        ShiftSwapRequest swapRequest = shiftSwapRequestRepository.findById(request.getSwapId())
                .orElseThrow(() -> new RuntimeException("Shift swap request not found with id: " + request.getSwapId()));

        Status newStatus = request.getStatus();

        if (newStatus == Status.APPROVED) {
            Shift currentShift = swapRequest.getShift(); // The shift user currently has

            // Optionally decrease swap balance for the user
            YearMonth ym = YearMonth.from(currentShift.getShiftDate());
            ShiftBalance balance = shiftBalanceRepository
                    .findByUserIdAndYearAndMonth(swapRequest.getUser().getId(), ym.getYear(), ym.getMonthValue())
                    .orElseThrow(() -> new RuntimeException("Swap balance not found for user"));

            if(balance.getSwapCount() <= 0) {
                throw new RuntimeException("No swap balance left to approve this request.");
            }
            balance.setSwapCount(balance.getSwapCount() - 1);
            shiftBalanceRepository.save(balance);

            // Perform shift swap logic here
            currentShift.setShiftDate(swapRequest.getShiftDate());
            currentShift.setShiftType(swapRequest.getShiftType());
            swapRequest.setStatus(Status.APPROVED);

            shiftRepository.save(currentShift);
        }else{
            swapRequest.setStatus(Status.REJECTED);
        }

        shiftSwapRequestRepository.save(swapRequest);
    }

    public List<ShiftSwapRequestDTO> getSwapRequestsByUser(Long userId) {
        List<ShiftSwapRequest> requests = shiftSwapRequestRepository.findByUserId(userId);
        return requests.stream()
                .map(ShiftSwapRequest::getDto) // create getDto() in entity
                .collect(Collectors.toList());
    }

    public List<ShiftSwapRequestDTO> getShiftSwapRequestsForManagerOnStatus(String managerEmail, String status) {
        Status swapStatus;
        try {
            swapStatus = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid shift swap status: " + status);
        }

        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        List<User> subordinates = userRepository.findByManager(manager);

        List<ShiftSwapRequest> swapRequests = new ArrayList<>();

        for (User subordinate : subordinates) {
            List<ShiftSwapRequest> requests = shiftSwapRequestRepository
                    .findByUserIdAndStatus(subordinate.getId(), swapStatus);
            swapRequests.addAll(requests);
        }

        return swapRequests.stream()
                .map(ShiftSwapRequest::getDto)
                .collect(Collectors.toList());
    }


    public List<ShiftSwapRequestDTO> getSwapRequestsForManager(Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        List<User> employees = userRepository.findByManager(manager);
        if (employees.isEmpty()) return Collections.emptyList();

        List<ShiftSwapRequest> requests = new ArrayList<>();

        for (User employee : employees) {
            ShiftSwapRequest request = (ShiftSwapRequest) shiftSwapRequestRepository.findByUserIdAndStatus(employee.getId(), Status.PENDING);
            requests.add(request);
        }

        return requests.stream()
                .map(ShiftSwapRequest::getDto)
                .collect(Collectors.toList());
    }
}



