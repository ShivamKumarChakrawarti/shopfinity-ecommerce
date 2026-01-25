package com.luxora.service;

import com.luxora.request.LoginRequest;
import com.luxora.response.AuthResponse;
import com.luxora.response.SignupRequest;

public interface AuthService {

    void sentLoginOtp(String email) throws Exception;

    String createUser(SignupRequest req) throws Exception;

    AuthResponse signing(LoginRequest request) throws Exception;
}
