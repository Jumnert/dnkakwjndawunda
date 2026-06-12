package com.luysot.jobodia.service;

import com.luysot.jobodia.dto.RegisterRequestDto;
import com.luysot.jobodia.dto.UserResponseDto;
import com.luysot.jobodia.mapper.UserMapper;
import com.luysot.jobodia.model.Users;
import com.luysot.jobodia.repository.UserRepository;
import com.luysot.jobodia.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserUtil userUtil;
    private final EmailService emailService;

    public UserResponseDto register(RegisterRequestDto requestDto){

        if(userRepository.findByEmail(requestDto.email()).isPresent()){
            throw new RuntimeException("Email already exist.");
        }

        if(userRepository.findByUsername(requestDto.username()).isPresent()){
            throw new RuntimeException("User already exist.");
        }

        Users user = new Users();
        user.setEmail(requestDto.email());
        user.setUsername(requestDto.username());
        user.setRole(requestDto.role());
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        user.setUserId(UUID.randomUUID().toString());
        userRepository.save(user);

        sendVerifyOtp(user.getEmail());

        return userMapper.toDto(user);
    }

    public void sendResetOtp(String email){
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String otp = generateOtp();
        Long expireAt = System.currentTimeMillis() + 10 * 60 * 1000;

        user.setResetPasswordOtp(otp);
        user.setResetPasswordOtpExpireAt(expireAt);

        userRepository.save(user);

        try{
            emailService.sendResetOtp(user.getEmail(), otp);
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }

    public UserResponseDto verifyOtp(String email,String otp){
        Users existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(existingUser.getVerifyOtpExpireAt() < System.currentTimeMillis()){
            throw new RuntimeException("Otp expired");
        }

        if(!existingUser.getVerifyOtp().equals(otp)){
            throw new RuntimeException("Invalid Otp");
        }

        existingUser.setIsVerified(true);
        existingUser.setVerifyOtp(null);
        existingUser.setVerifyOtpExpireAt(0L);

        userRepository.save(existingUser);
        emailService.successOtp(existingUser.getEmail());
        return  userMapper.toDto(existingUser);
    }

    public void resetPassword(String otp, String email, String password){
        Users user =  userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(user.getResetPasswordOtpExpireAt() < System.currentTimeMillis()){
            throw new RuntimeException("Otp expired");
        }
        if(!user.getResetPasswordOtp().equals(otp)||user.getResetPasswordOtp() == null){
            throw new RuntimeException("Invalid Otp");
        }

        user.setPassword(passwordEncoder.encode(password));
        user.setResetPasswordOtpExpireAt(0L);
        user.setResetPasswordOtp(null);

        userRepository.save(user);
    }

    public String generateOtp(){
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));
    }



    public void sendVerifyOtp(String email){
        Long verifyOtpExpiration = System.currentTimeMillis() + 10 * 60 * 1000;
        String verifyOtp = userUtil.generateOtp();
        Users existingUser = userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found!"));

        if(existingUser.getIsVerified()){
            return ;
        }

        existingUser.setVerifyOtp(verifyOtp);
        existingUser.setVerifyOtpExpireAt(verifyOtpExpiration);

        userRepository.save(existingUser);

        try{
            emailService.sendOtp(existingUser.getEmail(),verifyOtp);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}

