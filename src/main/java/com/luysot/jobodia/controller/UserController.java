package com.luysot.jobodia.controller;

import com.luysot.jobodia.dto.RegisterRequestDto;
import com.luysot.jobodia.dto.ResetPasswordRequest;
import com.luysot.jobodia.dto.UserResponseDto;
import com.luysot.jobodia.dto.VerifyUserDto;
import com.luysot.jobodia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    ResponseEntity<UserResponseDto> register(@RequestBody RegisterRequestDto dto){
        return ResponseEntity.ok(userService.register(dto));
    }

    @PostMapping("/verify-otp")
    void sendVerifyOtp(@RequestBody VerifyUserDto verifyUserDto){
        userService.verifyOtp(verifyUserDto.email(),verifyUserDto.otp());
    }


    @PostMapping("/send-reset-otp")
    public void sendResetOtp(@RequestParam String email) {
        try{
            userService.sendResetOtp(email);
        }
        catch(Exception e){
            throw new RuntimeException("Failed to send reset otp");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(
                    request.otp(),
                    request.email(),
                    request.password()
            );
            return ResponseEntity.ok("Password reset successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
