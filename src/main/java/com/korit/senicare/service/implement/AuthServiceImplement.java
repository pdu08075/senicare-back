package com.korit.senicare.service.implement;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.korit.senicare.dto.request.IdCheckRequestDto;
import com.korit.senicare.dto.response.ResponseDto;
import com.korit.senicare.repository.NurseRepository;
import com.korit.senicare.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImplement implements AuthService {

    private final NurseRepository nurseRepository;
    
    @Override
    public ResponseEntity<ResponseDto> idCheck(IdCheckRequestDto dto) {

        String userId = dto.getUserId();

        try {
            boolean isExistedId = nurseRepository.existsById(userId);
            if (isExistedId) return ResponseDto.duplicatedUserId();
            
        } catch (Exception exception) {
            exception.printStackTrace();        // 데이터베이스에서 나는 에러 잡기
            return ResponseDto.databaseError();
        }

        // return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("SU", "Success"));      // -> 가독성과 유지보수성 향상을 위해 ResponseDto에 메서드를 만든 후 아래에 사용
        return ResponseDto.success();

    }
    
}
