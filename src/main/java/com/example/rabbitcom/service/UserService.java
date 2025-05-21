package com.example.rabbitcom.service;


import com.example.rabbitcom.dto.UserRequest;
import com.example.rabbitcom.dto.UserResponse;
import com.example.rabbitcom.entity.User;
import com.example.rabbitcom.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.beans.Transient;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final RabbitTemplate rabbitTemplate;

    private final UserRepository userRepository;

    @Transactional
    public UserResponse createUser(UserRequest userRequest) throws Exception {
        Optional<User> checkUserIsExist=userRepository.findByUsername(userRequest.getName());
        if(checkUserIsExist.isPresent()) {
            throw  new Exception("user already exist");
        }
        else{
            User user=User.builder()
                    .age(userRequest.getAge())
                    .name(userRequest.getName())
                    .address(userRequest.getAddress())
                    .build();
            return new UserResponse(user.getName(),user.getAddress());
        }
    }

}
