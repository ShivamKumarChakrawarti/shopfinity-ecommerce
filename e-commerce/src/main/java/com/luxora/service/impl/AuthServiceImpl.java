package com.luxora.service.impl;

import com.luxora.config.JwtProvider;
import com.luxora.domain.USER_ROLE;
import com.luxora.entity.Cart;
import com.luxora.entity.User;
import com.luxora.entity.VerificationCode;
import com.luxora.exception.InvalidOtpException;
import com.luxora.exception.OtpNotFoundException;
import com.luxora.repository.CartRepository;
import com.luxora.repository.UserRepository;
import com.luxora.repository.VerificationCodeRepository;
import com.luxora.request.LoginRequest;
import com.luxora.response.AuthResponse;
import com.luxora.response.SignupRequest;
import com.luxora.service.AuthService;
import com.luxora.service.EmailService;
import com.luxora.utils.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final JwtProvider jwtProvider;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final CustomUserServiceImpl customUserService;

    @Override
    public void sentLoginOtp(String email) throws Exception {
        String SIGNING_PREFIX="signin_";

        if(email.startsWith(SIGNING_PREFIX)){
            email=email.substring(SIGNING_PREFIX.length());

            User user=userRepository.findByEmail(email);
            if(user==null){
                throw new Exception("user not exist with provided email");
            }
        }
        verificationCodeRepository.findByEmail(email)
                .ifPresent(verificationCodeRepository::delete);

        String otp= OtpUtil.generateOtp();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(email);
        verificationCode.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        verificationCodeRepository.save(verificationCode);

        String subject="luxora login/signup otp";
        String text="your login/signup otp is - " + otp;

        emailService.sendVerificationOtpEmail(email,otp,subject,text);

    }

    @Override
    public String createUser(SignupRequest req) throws Exception {

//      we are checking that if verification code is present in our database. If it presents then will move forward
        VerificationCode verificationCode =
                verificationCodeRepository.findByEmail(req.getEmail())
                        .orElseThrow(() -> new Exception("OTP not found"));

        if (!verificationCode.getOtp().equals(req.getOtp())) {
            throw new Exception("Wrong OTP");
        }

        if (verificationCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new Exception("OTP expired");
        }

        // ✅ OTP USED → DELETE IT
        verificationCodeRepository.delete(verificationCode);

//      Need to check user is an existing customer or new one
        User user = userRepository.findByEmail(req.getEmail());
        if(user==null){
            User createdUser = new User();
            createdUser.setEmail(req.getEmail());
            createdUser.setFullName(req.getFullName());
            createdUser.setRole(USER_ROLE.ROLE_CUSTOMER);
            createdUser.setMobileNumber("8810905435");
            createdUser.setPassword(
                    passwordEncoder.encode(UUID.randomUUID().toString())
            );

            // To save data in a database
            user=userRepository.save(createdUser);

            Cart cart=new Cart();
            cart.setUser(user);
            cartRepository.save(cart);

        }

//      Generate Jwt Token
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_CUSTOMER.toString()));

        Authentication authentication = new UsernamePasswordAuthenticationToken(req.getEmail(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);


        return jwtProvider.generateToken(authentication) ;
    }

    @Override
    public AuthResponse signing(LoginRequest request) throws Exception {
        String username = request.getEmail();
        String otp = request.getOtp();

        Authentication authentication = authenticate(username, otp);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Login Success");

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();

        authResponse.setRole(USER_ROLE.valueOf(roleName));
        return authResponse;
    }

    private Authentication authenticate(String username, String otp) throws Exception {
        UserDetails userDetails = customUserService.loadUserByUsername(username);

        if(userDetails==null){
            throw new BadCredentialsException("invalid username or password");
        }

        VerificationCode verificationCode =
                verificationCodeRepository.findByEmail(username)
                .orElseThrow(() -> new OtpNotFoundException("OTP not found. Please request a new OTP."));;

        if (!verificationCode.getOtp().equals(otp)) {
            throw new InvalidOtpException("Wrong OTP");
        }

        verificationCodeRepository.delete(verificationCode);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}
