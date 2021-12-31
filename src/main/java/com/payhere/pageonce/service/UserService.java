package com.payhere.pageonce.service;

import com.payhere.pageonce.dto.request.LoginRequestDto;
import com.payhere.pageonce.dto.request.SignUpRequestDto;
import com.payhere.pageonce.dto.response.LoginResponseDto;
import com.payhere.pageonce.dto.response.SignUpResponseDto;
import com.payhere.pageonce.jwt.JwtTokenProvider;
import com.payhere.pageonce.model.User;
import com.payhere.pageonce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Value("{$etc.salt}")
    private String salt;

    @Transactional
    public SignUpResponseDto register(SignUpRequestDto signUpRequestDto){
        SignUpResponseDto signUpResponseDto;
        String requestEmail = signUpRequestDto.getEmail();
        Optional<User> existUser = userRepository.findByEmail(requestEmail);
        if (existUser.isPresent()) {
            signUpResponseDto = SignUpResponseDto.builder()
                    .success(false)
                    .message("이미 존재하는 이메일입니다.")
                    .build();
        } else {
            String password = passwordEncoder.encode(signUpRequestDto.getPassword());
            User user = User.builder()
                    .email(requestEmail)
                    .password(password)
                    .build();
            userRepository.save(user);
            signUpResponseDto = SignUpResponseDto.builder()
                    .success(true)
                    .email(requestEmail)
                    .message("성공적으로 가입되었습니다.")
                    .build();
        }
        return signUpResponseDto;
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto){
        LoginResponseDto loginResponseDto;
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();
        Optional<User> user = userRepository.findByEmail(email);
        if(!user.isPresent()){
            loginResponseDto = LoginResponseDto.builder()
                    .success(false)
                    .message("아이디 또는 비밀번호가 올바르지않습니다.")
                    .build();
        } else {
            String realPass = user.get().getPassword();
            if (!password.equals(realPass)){
                loginResponseDto = LoginResponseDto.builder()
                        .success(false)
                        .message("아이디 또는 비밀번호가 올바르지않습니다.")
                        .build();
            } else {
                Authentication emailPassword = new UsernamePasswordAuthenticationToken(email,password);
                Authentication authentication = authenticationManager.authenticate(emailPassword);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String token = jwtTokenProvider.createToken(user.get().getId().toString(),user.get().getEmail());
                loginResponseDto = LoginResponseDto.builder()
                        .success(true)
                        .message("성공적으로 로그인 되었습니다.")
                        .email(email)
                        .token(token)
                        .build();
            }
        }
        return loginResponseDto;
    }
}
