package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.auth.AuthenticationResponse;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.LoginDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.RegisterDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Account;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.UserRepository;
import edu.ipp.isep.dei.dimei.retailproject.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse registerAdmin(RegisterDTO registerDTO) {
        return registerUser(registerDTO, RoleEnum.ADMIN);
    }

    public AuthenticationResponse registerMerchant(RegisterDTO registerDTO) {
        return registerUser(registerDTO, RoleEnum.MERCHANT);
    }

    public AuthenticationResponse register(RegisterDTO registerDTO) {
        return registerUser(registerDTO, RoleEnum.USER);
    }

    private AuthenticationResponse registerUser(RegisterDTO registerDTO, RoleEnum role) {
        Account account = Account.builder()
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .role(role)
                .build();

        User user = User.builder()
                .firstname(registerDTO.getFirstname())
                .lastname(registerDTO.getLastname())
                .account(account)
                .build();

        User savedUser = this.userRepository.save(user);

        var jwtToken = jwtService.generateToken(savedUser.getAccount(), savedUser.getId());

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse login(LoginDTO loginDTO) throws NotFoundException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );

        var user = userRepository.findByAccountEmail(loginDTO.getEmail())
                .orElseThrow(() -> new NotFoundException("User or Password not correct"));

        var jwtToken = jwtService.generateToken(user.getAccount(), user.getId());

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
