package com.vertex.stockflow.common.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
//Random mk
@Component
public class PasswordGenerator {
    private static final String CHARS =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$";
    private final SecureRandom random = new SecureRandom();

    public String generate(){
        StringBuilder sb = new StringBuilder(10);
        for(int i = 0; i < 10; i++){
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
