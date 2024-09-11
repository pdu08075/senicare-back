package com.korit.senicare.service;

import org.springframework.http.ResponseEntity;

import com.korit.senicare.dto.request.auth.IdCheckRequestDto;
import com.korit.senicare.dto.request.auth.TelAuthRequestDto;
import com.korit.senicare.dto.response.ResponseDto;

public interface AuthService {
    ResponseEntity<ResponseDto> idCheck(IdCheckRequestDto dto);
    ResponseEntity<ResponseDto> telAuth(TelAuthRequestDto dto);
}
