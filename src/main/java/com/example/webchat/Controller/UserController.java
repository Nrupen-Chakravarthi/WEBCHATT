package com.example.webchat.Controller;

import com.example.webchat.Model.LoginResponse;
import com.example.webchat.Model.OtpRequest;
import com.example.webchat.Model.User;
import com.example.webchat.Model.loginUser;
import com.example.webchat.Repository.UserRepository;
import com.example.webchat.Service.UserService;
import org.apache.coyote.Response;
import org.hibernate.annotations.ColumnTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserRepository repo;

    @Autowired
    private UserService service;
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user){
         return service.registerUser(user);
    }

    @PostMapping("/verifyOTP")
    public ResponseEntity<String> validateOTP(@RequestBody OtpRequest rqst){
        return service.validateOTP(rqst);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody loginUser loginUser){
        return service.login(loginUser);
    }
}
