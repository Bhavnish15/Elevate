package com.project.Elevate.userService.service;

import com.project.Elevate.userService.dto.LoginRequestDto;
import com.project.Elevate.userService.dto.SignupRequestDto;
import com.project.Elevate.userService.dto.UserDto;
import com.project.Elevate.userService.entity.User;
import com.project.Elevate.userService.exception.BadRequestException;
import com.project.Elevate.userService.exception.ResourceNotFoundException;
import com.project.Elevate.userService.repository.UserRepository;
import com.project.Elevate.userService.utils.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;

    public UserDto signUp(SignupRequestDto signupRequestDto) {
        log.info("Sign up a user with email {}", signupRequestDto.getEmail());
        boolean exists = userRepository.existsByEmail(signupRequestDto.getEmail());
        if(exists) {
            throw new BadRequestException("User is already registered with us you may please login.");
        }
        User user = modelMapper.map(signupRequestDto, User.class);
        user.setPassword(BCrypt.hash(signupRequestDto.getPassword()));
        user = userRepository.save(user);

        return modelMapper.map(user, UserDto.class);
    }

    public String login(LoginRequestDto loginRequestDto) {
        log.info("Login request for user with email: {}", loginRequestDto.getEmail());
        User user = userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User is not present with email {} "+loginRequestDto.getEmail()));
        boolean isPasswordMatch = BCrypt.match(loginRequestDto.getPassword(), user.getPassword());
        if(!isPasswordMatch) {
            throw new BadRequestException("Incorrect Password");
        }

        return jwtService.generateAccessToken(user);

    }
}
