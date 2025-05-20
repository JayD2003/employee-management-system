package com.emp_mgmt_sys.service;

import com.emp_mgmt_sys.dto.requestDTO.CreateShiftSwapRequest;
import com.emp_mgmt_sys.dto.requestDTO.ShiftAssignmentRequest;
import com.emp_mgmt_sys.dto.requestDTO.UpdateShiftSwapRequest;
import com.emp_mgmt_sys.dto.responseDTO.ShiftResponseDTO;
import com.emp_mgmt_sys.dto.responseDTO.ShiftSwapResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface ShiftService {

    void assignShift(ShiftAssignmentRequest request);

    void assignWeeklyShifts(ShiftAssignmentRequest request);

    List<ShiftResponseDTO> getEmployeeShifts(Long userId, LocalDate referenceDate);

    ShiftResponseDTO getEmployeeShiftByDate(Long userId, LocalDate date);

    int getSwapBalance(Long userId);

    List<ShiftResponseDTO> getShiftsByDateForManager(Long managerId, LocalDate date);

    void createSwapRequest(CreateShiftSwapRequest request);

    void updateSwapRequestStatus(UpdateShiftSwapRequest request);

    List<ShiftSwapResponseDTO> getSwapRequestsByUser(Long userId);

    List<ShiftSwapResponseDTO> getShiftSwapRequestsForManagerOnStatus(String managerEmail, String status);

    List<ShiftSwapResponseDTO> getSwapRequestsForManager(Long managerId);
}
