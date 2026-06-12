package com.luysot.jobodia.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class UserUtil {

    public String generateOtp(){
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));
    }
}
