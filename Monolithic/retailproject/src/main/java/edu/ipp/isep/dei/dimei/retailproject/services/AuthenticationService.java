package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.auth.AuthenticationResponse;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.LoginDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.RegisterDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Account;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
import edu.ipp.isep.dei.dimei.retailproject.repositories.UserRepository;
import edu.ipp.isep.dei.dimei.retailproject.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final Logger logger = Logger.getLogger(getClass().getName());

    public AuthenticationResponse register(RegisterDTO registerDTO) {
        Account account = Account.builder()
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .role(RoleEnum.USER)
                .build();

        User user = User.builder()
                .firstname(registerDTO.getFirstname())
                .lastname(registerDTO.getLastname())
                .account(account)
                .build();

        User savedUser;

        try {
            userRepository.save(user);
            savedUser = user;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Cause: " + e.getCause() + "\nMessage: " + e.getMessage());
            throw e;
        }

        var jwtToken = jwtService.generateToken(savedUser.getAccount());

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse login(LoginDTO loginDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );

        var user = userRepository.findByAccountEmail(loginDTO.getEmail())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user.getAccount());

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
