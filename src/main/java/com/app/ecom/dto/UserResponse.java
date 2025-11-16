package com.app.ecom.dto;

import com.app.ecom.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private AddressDTO address;
}
