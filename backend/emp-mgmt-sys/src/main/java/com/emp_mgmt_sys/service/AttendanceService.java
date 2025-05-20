package com.emp_mgmt_sys.service;

import com.emp_mgmt_sys.dto.responseDTO.AttendanceResponseDTO;
import com.emp_mgmt_sys.dto.requestDTO.AttendanceRequest;

import java.util.List;

public interface AttendanceService {

    void clockIn(AttendanceRequest request);

    void clockOut(AttendanceRequest request);

    List<AttendanceResponseDTO> getAttendanceHistory(Long userId);

    List<AttendanceResponseDTO> getAttendanceHistoryForManager(String managerEmail);
}

