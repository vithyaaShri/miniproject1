package com.project.miniproject1.service.Impl;

import com.project.miniproject1.dto.JwtAuthResponse;
import com.project.miniproject1.dto.LoginDto;
import com.project.miniproject1.dto.RegisterDto;
import com.project.miniproject1.entity.Employee;
import com.project.miniproject1.entity.Role;
import com.project.miniproject1.entity.UserSecurity;
import com.project.miniproject1.exception.EmployeeApiException;
import com.project.miniproject1.repository.RoleSecurityRepo;
import com.project.miniproject1.repository.UserSecurityRepo;
import com.project.miniproject1.security.JwtUtil;
import com.project.miniproject1.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    UserSecurityRepo userSecurityRepo;
    RoleSecurityRepo roleSecurityRepo;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    @Override
    public String register(RegisterDto registerDto) {

        if(userSecurityRepo.exsitsByUserName(registerDto.getUserName())){
            throw new EmployeeApiException(HttpStatus.BAD_REQUEST,"User name Already exsits");
        }
        if(userSecurityRepo.existsByEmail(registerDto.getEmail())){
            throw new EmployeeApiException(HttpStatus.BAD_REQUEST,"Email Already exsits");
        }
        UserSecurity userSecurity =new UserSecurity();
        userSecurity.setName(registerDto.getName());
        userSecurity.setUsername(registerDto.getUserName());
        userSecurity.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        userSecurity.setEmail(registerDto.getEmail());
        Set<Role> roles=new HashSet<>();
        Role userrole=roleSecurityRepo.findByName("ROLE_USER");
        roles.add(userrole);
        userSecurity.setRoleSet(roles);
        userSecurityRepo.save(userSecurity);
        return "User Registered Successfully";
    }

    @Override
    public JwtAuthResponse login(LoginDto loginDto)
    {
        Authentication authentication= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUserName(),
                loginDto.getPassword()
                ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token=jwtUtil.generate(loginDto.getUserName());
        Optional<UserSecurity> user=userSecurityRepo.findByUsernameOrEmail(loginDto.getUserName(),loginDto.getUserName());
        String role=null;
        if(user.isPresent()){
            UserSecurity loggedInUser=user.get();
           Optional<Role> optionalRoles= loggedInUser.getRoleSet().stream().findFirst();
           if(optionalRoles.isPresent()){
               Role userRole=optionalRoles.get();
               role=userRole.getName();
           }
        }
        JwtAuthResponse jwtAuthResponse=new JwtAuthResponse();
        jwtAuthResponse.setRole(role);
        jwtAuthResponse.setAccessToken(token);
        return jwtAuthResponse;
    }
}
