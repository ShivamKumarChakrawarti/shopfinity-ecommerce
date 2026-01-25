package com.luxora.controller;

import com.luxora.domain.USER_ROLE;
import com.luxora.entity.VerificationCode;
import com.luxora.repository.UserRepository;
import com.luxora.request.LoginRequest;
import com.luxora.response.ApiResponse;
import com.luxora.response.AuthResponse;
import com.luxora.response.SignupRequest;
import com.luxora.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@Valid @RequestBody SignupRequest req) throws Exception {
        String jwt = authService.createUser(req);

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setMessage("register success");
        res.setRole(USER_ROLE.ROLE_CUSTOMER);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/sent/login-signup-otp")
    public ResponseEntity<ApiResponse<String>> sentOtpHandler(
            @RequestBody VerificationCode req) throws Exception {
       authService.sentLoginOtp(req.getEmail());
       ApiResponse<String> res = new ApiResponse<>(
               "OTP sent successfully", true, null
       );

       return ResponseEntity.ok(res);
    }

    @PostMapping("/signing")
    public ResponseEntity<AuthResponse> loginHandler(
            @RequestBody LoginRequest request) throws Exception{
        AuthResponse authResponse = authService.signing(request);
        return ResponseEntity.ok(authResponse);
    }
//    video pause at 5:23
}
