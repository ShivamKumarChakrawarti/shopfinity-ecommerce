package com.luxora.controller;

import com.luxora.entity.Seller;
import com.luxora.response.ApiResponse;
import com.luxora.response.SellerResponse;
import com.luxora.service.SellerServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sellers")
public class SellerController {

    private final SellerServices sellerSvc;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<SellerResponse>> registerSeller(
            @Valid @RequestBody Seller req
            ) throws Exception{
        log.info("Seller registration request received for email={}", req.getEmail());

        Seller seller = new Seller();
        seller.setEmail(req.getEmail());
        seller.setPassword(req.getPassword());
        seller.setBusinessDetails(req.getBusinessDetails());
        seller.setMobileNumber(req.getMobileNumber());

        Seller createdSeller = sellerSvc.createSellerProfile(seller);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        "Seller registered successfully. Please verify email.",
                        true,
                        mapToResponse(createdSeller)
                ));
    }

    @GetMapping("/getprofile")
    public ResponseEntity<ApiResponse<SellerResponse>> getSellerProfile(
            @RequestHeader("Authorization") String jwt) throws Exception{

        Seller seller = sellerSvc.getSellerProfile(jwt);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Seller profile fetched successfully",
                        true,
                        mapToResponse(seller)
                )
        );
    }

    @PutMapping("/updateprofile")
    public ResponseEntity<ApiResponse<SellerResponse>> updateSellerProfile(
            @RequestHeader("Authorization") String jwt,
            @Valid @RequestBody Seller req) throws Exception {

        Seller existingSeller = sellerSvc.getSellerProfile(jwt);

        Seller updateData  = new Seller();
        updateData.setBusinessDetails(req.getBusinessDetails());
        updateData.setMobileNumber(req.getMobileNumber());
        updateData.setPickupAddress(req.getPickupAddress());

        Seller updatedSeller = sellerSvc.updateSeller(existingSeller.getId(), updateData);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Seller profile updated successfully",
                        true,
                        mapToResponse(updatedSeller)
                )
        );
    }

    @PostMapping("/verifyemail")
    public ResponseEntity<ApiResponse<SellerResponse>> verifySellerEmail(
            @RequestParam String email,
            @RequestParam String otp) throws Exception {

        Seller seller = sellerSvc.verifyEmail(email, otp);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Email verification successfully",
                        true,
                        mapToResponse(seller)
                )
        );
    }

    private SellerResponse mapToResponse( Seller seller){
        return new SellerResponse(
                seller.getId(),
                seller.getEmail(),
                String.valueOf(seller.getBusinessDetails()),
                seller.getMobileNumber(),
                seller.getAccountStatus(),
                seller.getPickupAddress()
        );
    }
}
