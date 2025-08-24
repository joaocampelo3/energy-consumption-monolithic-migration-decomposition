package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Transactional
@RequiredArgsConstructor
@Service
public class UserService {
    private final JwtService jwtService;

    public String getEmailFromAuthorizationString(String authorizationToken) {
        return jwtService.extractClaims(authorizationToken.substring(7), Claims::getSubject);
    }

    public String getRoleFromAuthorizationString(String authorizationToken) {
        return jwtService.extractRole(authorizationToken.substring(7));
    }

}
