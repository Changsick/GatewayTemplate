package com.song.server1.service;

import com.song.server1.entity.UserEntity;
import com.song.server1.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveUser(UserEntity user) {
        userRepository.save(user);
    }
}
