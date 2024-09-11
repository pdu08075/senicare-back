package com.korit.senicare.dto.response;

// ResponseDto의 code 상수

public interface ResponseCode {

    String SUCCESS = "SU";

    String VALIDATION_FAIL = "VF";
    String DUPLICATED_USER_ID = "DI";
    String DUPLICATED_TEL_NUMBER = "DT";

    String MESSAGE_SEND_FAIL = "TF";
    String DATABASE_ERROR = "DBE";

}
