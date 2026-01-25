package com.luxora.service;

import com.luxora.entity.User;

public interface UserServices {
    User findUserByJwtToken(String jwt) throws Exception;
    User findUserByEmail(String email) throws Exception;
}
