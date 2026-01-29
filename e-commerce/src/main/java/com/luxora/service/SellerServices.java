package com.luxora.service;

import com.luxora.domain.AccountStatus;
import com.luxora.entity.Seller;

import java.util.List;

public interface SellerServices {
     Seller getSellerProfile(String jwt) throws Exception;

     Seller createSellerProfile(Seller seller) throws Exception;

     Seller getSellerById(Long id) throws Exception;

     Seller getSellerByEmail(String email) throws Exception;

     List<Seller> getAllSeller(AccountStatus status) throws Exception;

     Seller updateSeller(Long id, Seller seller) throws Exception;

     void deleteSeller(Long id) throws Exception;

     Seller verifyEmail(String email, String otp) throws Exception;

     Seller updateSellerAccountStatus(Long id, AccountStatus  status) throws Exception;
}
