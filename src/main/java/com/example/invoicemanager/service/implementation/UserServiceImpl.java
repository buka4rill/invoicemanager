package com.example.invoicemanager.service.implementation;

import com.example.invoicemanager.domain.User;
import com.example.invoicemanager.dto.UserDTO;
import com.example.invoicemanager.dto.dtomapper.UserDTOMapper;
import com.example.invoicemanager.repository.UserRepository;
import com.example.invoicemanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository<User> userRepository;
    @Override
    public UserDTO createUser(User user) {
        return UserDTOMapper.fromUser(userRepository.create(user));
    }
}
