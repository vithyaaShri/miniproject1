package com.project.miniproject1.controller;

import com.project.miniproject1.dto.EmployeeDto;
import com.project.miniproject1.dto.JwtAuthResponse;
import com.project.miniproject1.dto.LoginDto;
import com.project.miniproject1.dto.RegisterDto;
import com.project.miniproject1.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@CrossOrigin("*")
@RequestMapping("api/auth")
public class AuthController {
    AuthService authService;
    @PostMapping
    public String register(@RequestBody @Valid RegisterDto registerDto){
        String registermessage=authService.register(registerDto);
        return registermessage;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody @Valid LoginDto loginDto){
        JwtAuthResponse jwtAuthResponse =authService.login(loginDto);
        return new ResponseEntity<>(jwtAuthResponse,HttpStatus.OK);
    }
}
