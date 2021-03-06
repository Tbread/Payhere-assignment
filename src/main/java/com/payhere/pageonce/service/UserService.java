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
import org.springframework.validation.BindingResult;

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
    public SignUpResponseDto register(SignUpRequestDto signUpRequestDto, BindingResult bindingResult) {
        SignUpResponseDto signUpResponseDto;
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            signUpResponseDto = SignUpResponseDto.builder()
                    .success(false)
                    .message(message)
                    .build();
        } else {
            String requestEmail = signUpRequestDto.getEmail();
            Optional<User> existUser = userRepository.findByEmail(requestEmail);
            if (existUser.isPresent()) {
                signUpResponseDto = SignUpResponseDto.builder()
                        .success(false)
                        .message("?????? ???????????? ??????????????????.")
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
                        .message("??????????????? ?????????????????????.")
                        .build();
            }
        }
        return signUpResponseDto;
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto, BindingResult bindingResult) {
        LoginResponseDto loginResponseDto;
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            loginResponseDto = LoginResponseDto.builder()
                    .success(false)
                    .message(message)
                    .build();
        } else {
            String email = loginRequestDto.getEmail();
            String password = loginRequestDto.getPassword();
            Optional<User> user = userRepository.findByEmail(email);
            if (!user.isPresent()) {
                loginResponseDto = LoginResponseDto.builder()
                        .success(false)
                        .message("????????? ?????? ??????????????? ????????????????????????.")
                        .build();
            } else {
                String realPass = user.get().getPassword();
                if (!passwordEncoder.matches(password, realPass)) {
                    loginResponseDto = LoginResponseDto.builder()
                            .success(false)
                            .message("????????? ?????? ??????????????? ????????????????????????.")
                            .build();
                } else {
                    Authentication emailPassword = new UsernamePasswordAuthenticationToken(email, password);
                    Authentication authentication = authenticationManager.authenticate(emailPassword);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    String token = jwtTokenProvider.createToken(user.get().getId().toString(), user.get().getEmail());
                    loginResponseDto = LoginResponseDto.builder()
                            .success(true)
                            .message("??????????????? ????????? ???????????????.")
                            .email(email)
                            .token(token)
                            .build();
                }
            }
        }
        return loginResponseDto;
    }
}
