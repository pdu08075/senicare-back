package com.korit.senicare.common.util;

import java.util.Random;

public class AuthNumberCreator {
    
    // 0~9의 4자리 인증번호
    public static String number4() {
        String authNumber = "";
        
        Random random = new Random();
        for ( int i = 0; i < 5; i ++)
        authNumber += random.nextInt(10);
        
        return authNumber;
    }

}
