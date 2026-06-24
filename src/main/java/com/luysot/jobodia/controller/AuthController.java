package com.luysot.jobodia.controller;

import com.luysot.jobodia.dto.UsersDTOs.AuthRequest;
import com.luysot.jobodia.mapper.UserMapper;
import com.luysot.jobodia.model.Users;
import com.luysot.jobodia.repository.UserRepository;
import com.luysot.jobodia.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@Valid @RequestBody AuthRequest authRequest){
        Users isVerifiedUser = userRepository.findByEmail(authRequest.email())
                .orElseThrow(()->new UsernameNotFoundException("User not found!"));

        if(isVerifiedUser.getIsVerified()){
            try {
                UsernamePasswordAuthenticationToken user =
                        new UsernamePasswordAuthenticationToken(authRequest.email(),authRequest.password());

                authenticationManager.authenticate(user);
                return ResponseEntity.ok(jwtUtil.generateToken(isVerifiedUser));
            }
            catch (Exception e){
                e.printStackTrace();
                throw e;
            }
        }
        else{
            return ResponseEntity.badRequest().body("User is not verify.");
        }
    }
}
