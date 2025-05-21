package com.example.rabbitcom.service;


import com.example.rabbitcom.dto.UserRequest;
import com.example.rabbitcom.dto.UserResponse;
import com.example.rabbitcom.entity.User;
import com.example.rabbitcom.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.beans.Transient;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final RabbitTemplate rabbitTemplate;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;


    @Transactional
    public UserResponse createUser(UserRequest userRequest) throws Exception {
        if (userRepository.findByUsername(userRequest.getName()).isPresent()) {
            throw new Exception("User already exists");
        }

        User user = User.builder()
                .name(userRequest.getName())
                .age(userRequest.getAge())
                .address(userRequest.getAddress())
                .build();

        userRepository.save(user);

        redisTemplate.opsForValue().set(user.getName(), user.getAddress());

        return new UserResponse(user.getName(), user.getAddress());
    }

    public UserResponse getUser(String name) throws Exception {
        String addressFromRedis = redisTemplate.opsForValue().get(name);

        if (addressFromRedis != null) {
            return new UserResponse(name, addressFromRedis);
        }

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new Exception("User not found"));
        redisTemplate.opsForValue().set(name, user.getAddress());

        return new UserResponse(user.getName(), user.getAddress());
    }
}
