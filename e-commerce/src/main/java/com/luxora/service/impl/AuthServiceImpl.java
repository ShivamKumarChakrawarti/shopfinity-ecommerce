package com.luxora.service.impl;

import com.luxora.config.JwtProvider;
import com.luxora.domain.USER_ROLE;
import com.luxora.entity.Cart;
import com.luxora.entity.User;
import com.luxora.entity.VerificationCode;
import com.luxora.repository.CartRepository;
import com.luxora.repository.UserRepository;
import com.luxora.repository.VerificationCodeRepository;
import com.luxora.response.SignupRequest;
import com.luxora.service.AuthService;
import com.luxora.service.EmailService;
import com.luxora.utils.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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

    @Override
    public void sentLoginOtp(String email) throws Exception {
        String SIGNING_PREFIX="signin_";

        if(email.startsWith(SIGNING_PREFIX)){
            email=email.substring(SIGNING_PREFIX.length());

            User user=userRepository.findByEmail(email);
            if(user==null){
                throw new Exception("user not exist with provded emal");
            }
        }
        VerificationCode isExist=verificationCodeRepository.findByEmail(email);
        if(isExist!=null){
            verificationCodeRepository.delete(isExist);
        }

        String otp= OtpUtil.generateOtp();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(email);
        verificationCodeRepository.save(verificationCode);

        String subject="luxora login/signup otp";
        String text="your login/signup otp is - " + otp;

        emailService.sendVerificationOtpEmail(email,otp,subject,text);

    }

    @Override
    public String createUser(SignupRequest req) throws Exception {

//      we are checking that if verification code is present in our database. If it presents then will move forward
        VerificationCode verificationCode=verificationCodeRepository.findByEmail(req.getEmail());
        if(verificationCode==null || !verificationCode.getOtp().equals(req.getOtp())){
            throw new Exception("wrong otp...");
        }

//      Need to check user is an existing customer or new one
        User user = userRepository.findByEmail(req.getEmail());
        if(user==null){
            User createdUser = new User();
            createdUser.setEmail(req.getEmail());
            createdUser.setFullName(req.getFullName());
            createdUser.setRole(USER_ROLE.ROLE_CUSTOMER);
            createdUser.setMobileNumber("8810905435");
            createdUser.setPassword(passwordEncoder.encode(req.getOtp()));
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
}
