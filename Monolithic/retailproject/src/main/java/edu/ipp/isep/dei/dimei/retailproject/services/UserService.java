package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.UserRepository;
import edu.ipp.isep.dei.dimei.retailproject.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public User getUserByToken(String authorizationToken) throws NotFoundException {
        String email = getEmailFromAuthorizationString(authorizationToken);
        return findByEmail(email);
    }

    public String getEmailFromAuthorizationString(String authorizationToken) {
        return jwtService.extractClaims(authorizationToken.substring(7), Claims::getSubject);
    }

    public User findByEmail(String email) throws NotFoundException {
        return this.userRepository.findByAccountEmail(email).orElseThrow(() -> new NotFoundException("User not found."));
    }


}
