package com.example.webchat.Service;

import com.example.webchat.Model.LoginResponse;
import com.example.webchat.Model.OtpRequest;
import com.example.webchat.Model.User;
import com.example.webchat.Model.loginUser;
import com.example.webchat.Repository.UserRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisOtp redisOtp;


    @Autowired
    private JwtService service;
    public ResponseEntity<String> registerUser(User user){
        User uEmail = repo.findByEmail(user.getEmail());
        Optional<User> uUsername = repo.findByUsername(user.getUsername());
        if(uEmail != null){
            return ResponseEntity
                    .badRequest()
                    .body("Email Already Registered");
        } else if(uUsername.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("Username is already Taken");
        }
        String otp = otpService.generateOtp();
        emailService.sendMail(user.getEmail(), otp);
        redisOtp.storeOtp(user.getEmail(), otp);
        return ResponseEntity
                .ok()
                .body("OTP sent to email. please enter the otp");
    }

    public ResponseEntity<String> validateOTP(OtpRequest rqst) {

        String otp = redisOtp.getOtp(rqst.getEmail());
        if(otp.equals(rqst.getOtp())){
            User user = new User();
            user.setEmail(rqst.getEmail());
            user.setPassword(passwordEncoder.encode(rqst.getPassword()));
            user.setUsername(rqst.getUsername());
            repo.save(user);
            System.out.println("User registered Successfully");
            return ResponseEntity
                    .ok()
                    .body("User registered successfully");
        }
        return ResponseEntity
                .badRequest()
                .body("OTP doesnt match");
    }

    public void updateLastSeen(String username) {
        // Fetch the user and update the timestamp
        repo.findByUsername(username).ifPresent(user -> {
            user.setLastSeen(LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES));
            repo.save(user);
        });
    }

    public ResponseEntity<?> login(loginUser loginUser) {

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));

            if (authentication.isAuthenticated()) {
                String token = service.generateToken(loginUser.getUsername());
//                return ResponseEntity.ok(Map.of("token", token));
                User user = repo.findByUsername(loginUser.getUsername()).get();
                return ResponseEntity
                        .ok(new LoginResponse(token, user.getUsername()));
            }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
    }
}
