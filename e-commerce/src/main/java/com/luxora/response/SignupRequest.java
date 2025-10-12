package com.luxora.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SignupRequest {
    private String fullName;
    private String email;
    private String otp;
}
