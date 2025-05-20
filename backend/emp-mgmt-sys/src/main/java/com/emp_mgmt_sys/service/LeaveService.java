package com.emp_mgmt_sys.service;

import com.emp_mgmt_sys.dto.requestDTO.CreateLeaveRequest;
import com.emp_mgmt_sys.dto.requestDTO.UpdateLeaveRequest;
import com.emp_mgmt_sys.dto.responseDTO.LeaveBalanceResponseDTO;
import com.emp_mgmt_sys.dto.responseDTO.LeaveRequestResponseDTO;

import java.util.List;

public interface LeaveService {

    void createLeaveRequest(CreateLeaveRequest leave);

    void updateLeaveStatus(UpdateLeaveRequest dto);

    List<LeaveRequestResponseDTO> getLeaveRequests(Long userId);

    List<LeaveRequestResponseDTO> getLeaveRequestsForManagerOnLeaveStatus(String managerEmail, String status);

    List<LeaveRequestResponseDTO> getLeaveRequestHistoryForManager(String managerEmail);

    void createLeaveBalance(Long userId);

    LeaveBalanceResponseDTO getLeaveBalance(Long userId);

    List<LeaveBalanceResponseDTO> getLeaveBalancesForManager(String managerEmail);
}
