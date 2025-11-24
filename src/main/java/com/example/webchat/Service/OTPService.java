package com.example.webchat.Service;

import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.random.RandomGenerator;

@Service
public class OTPService {
    public String generateOtp() {
        Random rand = new Random();
        return String.valueOf(1000 + rand.nextInt(9000));
    }

}
