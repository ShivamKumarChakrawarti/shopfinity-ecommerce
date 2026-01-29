package com.luxora.response;

import com.luxora.domain.AccountStatus;
import com.luxora.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SellerResponse {
    private Long id;
    private String email;
    private String businessDetails;
    private String phone;
    private AccountStatus accountStatus;
    private Address pickupAddress;
}
