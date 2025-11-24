package com.example.webchat.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisOtp {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void storeOtp(String email, String otp){
        redisTemplate.opsForValue().set(email, otp, 2, TimeUnit.MINUTES);
    }
    public String getOtp(String email){
        return redisTemplate.opsForValue().get(email);
    }

    public void deleteOtp(String email){
       redisTemplate.delete(email);
    }
}
