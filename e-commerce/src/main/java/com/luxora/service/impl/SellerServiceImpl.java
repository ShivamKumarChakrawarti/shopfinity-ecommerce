package com.luxora.service.impl;

import com.luxora.config.JwtProvider;
import com.luxora.domain.AccountStatus;
import com.luxora.entity.Seller;
import com.luxora.entity.VerificationCode;
import com.luxora.exception.InvalidOtpException;
import com.luxora.exception.OtpNotFoundException;
import com.luxora.exception.SellerNotFoundException;
import com.luxora.repository.SellerRepository;
import com.luxora.repository.VerificationCodeRepository;
import com.luxora.service.SellerServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerServices {

    private final SellerRepository sellerRepository;
    private final JwtProvider jwtProvider;
    private final VerificationCodeRepository verificationCodeRepository;

    @Override
    public Seller getSellerProfile(String jwt) throws Exception {
        String email = jwtProvider .getEmailFromJwtToken(jwt);
        log.info("Fetching seller profile for email={}", email);
        Seller seller = sellerRepository.findByEmail(email);
        validateSellerExists(seller);
        validateSellerNotDeleted(seller);
        return seller;
    }

    @Override
    public Seller createSellerProfile(Seller seller) {
        log.info("Creating seller profile for email={} ", seller.getEmail());

        if(sellerRepository.existsByEmail(seller.getEmail())){
            log.warn("Seller already exists with this email={} ", seller.getEmail());
        }
        seller.setAccountStatus(AccountStatus.PENDING_VERIFICATION);
        seller.setCreatedAt(LocalDateTime.now());

        return sellerRepository.save(seller);
    }

    @Override
    public Seller getSellerById(Long id) throws Exception {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Seller not found with id={}", id);
                    return new SellerNotFoundException("Seller not find");
                });

        validateSellerNotDeleted(seller);
        return seller;
    }

    @Override
    public Seller getSellerByEmail(String email) throws Exception {
        Seller seller = sellerRepository.findByEmail(email);

        validateSellerExists(seller);
        validateSellerNotDeleted(seller);
        return seller;
    }

    @Override
    public List<Seller> getAllSeller(AccountStatus status) {
        log.info("Fetching sellers with status={}", status);

        if(status == null){
            return sellerRepository.findAll()
                    .stream()
                    .filter(s -> s.getAccountStatus() != AccountStatus.DELETED)
                    .toList();
        }
        return sellerRepository.findByAccountStatus(status);
    }

    @Override
    public Seller updateSeller(Long id, Seller updatedData) throws Exception {
        Seller existingSeller = getSellerById(id);

        validateSellerUpdatable(existingSeller);

        existingSeller.setBusinessDetails(updatedData.getBusinessDetails());
        existingSeller.setMobileNumber(updatedData.getMobileNumber());
        existingSeller.setPickupAddress(updatedData.getPickupAddress());

        log.info("Updating seller id={}", id);
        return sellerRepository.save(existingSeller);
    }

    @Override
    public void deleteSeller(Long id) throws Exception {
        Seller seller = getSellerById(id);

        seller.setAccountStatus(AccountStatus.DELETED);
        sellerRepository.save(seller);

        log.warn("Seller soft deleted with id={}", id);

    }

    @Override
    public Seller verifyEmail(String email, String otp) throws Exception {
        log.info("Verifying seller email={}", email);

        VerificationCode code = verificationCodeRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("OTP not found for email={}", email);
                    return new OtpNotFoundException("OTP not found");
                });

        if (!code.getOtp().equals(otp)) {
            log.warn("Invalid OTP for email={}", email);
            throw new InvalidOtpException("Invalid OTP");
        }

        if (code.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new Exception("OTP expired");
        }

        Seller seller = getSellerByEmail(email);

        if (seller.getAccountStatus() != AccountStatus.PENDING_VERIFICATION) {
            throw new Exception("Seller already verified");
        }

        seller.setAccountStatus(AccountStatus.VERIFIED);
        sellerRepository.save(seller);

        verificationCodeRepository.delete(code);

        log.info("Seller email verified for email={}", email);
        return seller;
    }

    @Override
    public Seller updateSellerAccountStatus(Long id, AccountStatus status) throws Exception {

        Seller seller = getSellerById(id);

        validateStatusTransition(seller.getAccountStatus(), status);

        seller.setAccountStatus(status);
        sellerRepository.save(seller);

        log.info("Seller id={} status updated to {}", id, status);
        return seller;
    }

    private void validateSellerExists(Seller seller) throws Exception {
        if (seller == null) {
            throw new SellerNotFoundException("Seller not found");
        }
    }

    private void validateSellerNotDeleted(Seller seller) throws Exception {
        if (seller.getAccountStatus() == AccountStatus.DELETED) {
            throw new Exception("Seller is deleted");
        }
    }

    private void validateSellerUpdatable(Seller seller) throws Exception {
        if (seller.getAccountStatus() == AccountStatus.DELETED) {
            throw new Exception("Cannot update deleted seller");
        }
        if (seller.getAccountStatus() == AccountStatus.SUSPENDED) {
            throw new Exception("Seller is suspended");
        }
    }

    private void validateStatusTransition(AccountStatus current, AccountStatus next)
            throws Exception {

        if (current == AccountStatus.DELETED) {
            throw new Exception("Cannot update deleted seller");
        }

        if (current == AccountStatus.PENDING_VERIFICATION && next == AccountStatus.ACTIVE) {
            throw new Exception("Seller must be verified before activation");
        }
    }
}
