package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
import edu.ipp.isep.dei.dimei.retailproject.repositories.UserRepository;
import edu.ipp.isep.dei.dimei.retailproject.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public User getUserByToken(String authorizationToken) {
        String email = getEmailFromAuthorizationString(authorizationToken);
        return findByEmail(email).get();
    }

    public String getEmailFromAuthorizationString(String authorizationToken) {
        return jwtService.extractClaims(authorizationToken.substring(7), Claims::getSubject);
    }

    public Optional<User> findByEmail(String email) {
        return this.userRepository.findByAccountEmail(email);
    }


}
