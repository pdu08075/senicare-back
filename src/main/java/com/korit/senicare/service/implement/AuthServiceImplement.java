package com.korit.senicare.service.implement;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.korit.senicare.common.util.AuthNumberCreator;
import com.korit.senicare.dto.request.auth.IdCheckRequestDto;
import com.korit.senicare.dto.request.auth.SignInRequestDto;
import com.korit.senicare.dto.request.auth.SignUpRequestDto;
import com.korit.senicare.dto.request.auth.TelAuthCheckRequestDto;
import com.korit.senicare.dto.request.auth.TelAuthRequestDto;
import com.korit.senicare.dto.response.ResponseDto;
import com.korit.senicare.dto.response.auth.SignInResponseDto;
import com.korit.senicare.entity.NurseEntity;
import com.korit.senicare.entity.TelAuthNumberEntity;
import com.korit.senicare.provider.JwtProvider;
import com.korit.senicare.provider.SmsProvider;
import com.korit.senicare.repository.NurseRepository;
import com.korit.senicare.repository.TelAuthNumberRepository;
import com.korit.senicare.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImplement implements AuthService {

    private final SmsProvider smsProvider;
    private final JwtProvider jwtProvider;

    private final NurseRepository nurseRepository;
    private final TelAuthNumberRepository telAuthNumberRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
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

        return ResponseDto.success();

    }

    @Override
    public ResponseEntity<ResponseDto> telAuth(TelAuthRequestDto dto) {
        
        String telNumber = dto.getTelNumber();

        try {

            boolean isExistedTelNumber = nurseRepository.existsByTelNumber(telNumber);
            if (isExistedTelNumber) return ResponseDto.duplicatedTelNumber();

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        String authNumber = AuthNumberCreator.number4();

        boolean isSendSuccess = smsProvider.sendMessage(telNumber, authNumber);
        if (!isSendSuccess) return ResponseDto.messageSendFail();

        try {

            TelAuthNumberEntity telAuthNumberEntity = new TelAuthNumberEntity(telNumber, authNumber);
            telAuthNumberRepository.save(telAuthNumberEntity);
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return ResponseDto.success();
    }

    @Override
    public ResponseEntity<ResponseDto> telAuthCheck(TelAuthCheckRequestDto dto) {
        
        String telNumber = dto.getTelNumber();
        String authNumber = dto.getAuthNumber();

        try {
            
            boolean isMatched = telAuthNumberRepository.existsByTelNumberAndAuthNumber(telNumber, authNumber);
            if (!isMatched) return ResponseDto.telAuthFail();

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return ResponseDto.success();

    }

    @Override
    public ResponseEntity<ResponseDto> signUp(SignUpRequestDto dto) {

        String userId = dto.getUserId();
        String password = dto.getPassword();
        String telNumber = dto.getTelNumber();
        String authNumber = dto.getAuthNumber();

        try {

            boolean isExistedUserId = nurseRepository.existsByUserId(userId);
            if (isExistedUserId) return ResponseDto.duplicatedUserId();

            boolean isExistedTelNumber = nurseRepository.existsByTelNumber(telNumber);
            if (isExistedTelNumber) return ResponseDto.duplicatedTelNumber();

            boolean isMatched = telAuthNumberRepository.existsByTelNumberAndAuthNumber(telNumber, authNumber);
            if (!isMatched) return ResponseDto.telAuthFail();

            String encodedPassword = passwordEncoder.encode(password);
            dto.setPassword(encodedPassword);

            NurseEntity nurseEntity = new NurseEntity(dto);
            nurseRepository.save(nurseEntity);

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return ResponseDto.success();


    }

    @Override
    public ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto dto) {

        String userId = dto.getUserId();
        String password = dto.getPassword();        // 평문의 password를 Dto로 부터 꺼내옴

        String accessToken = null;

        try {
            
            NurseEntity nurseEntity = nurseRepository.findByUserId(userId);
            if (nurseEntity == null) return ResponseDto.signInFail();

            String encodedPassword = nurseEntity.getPassword();         // nurses에 있는 암호화된 password를 꺼내옴
            boolean isMached = passwordEncoder.matches(password, encodedPassword);      // 암호화된 password가 평문의 password로부터 만들어진 것이라면 true
            if (!isMached) return ResponseDto.signInFail();     // 위가 false라면 로그인 실패 반환

            accessToken = jwtProvider.create(userId);
            if (accessToken == null) return ResponseDto.tokenCreateFail(); 

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return SignInResponseDto.success(accessToken);

    }
    
}
