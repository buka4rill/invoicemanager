package com.example.invoicemanager.service;

import com.example.invoicemanager.domain.User;
import com.example.invoicemanager.dto.UserDTO;

public interface UserService {
    UserDTO createUser(User user);
}
