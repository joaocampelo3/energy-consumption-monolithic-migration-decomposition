package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.MerchantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final UserService userService;

    public Merchant getMerchantByUser(User user) throws NotFoundException {
        return merchantRepository.findByEmail(user.getAccount().getEmail()).orElseThrow(() -> new NotFoundException("Merchant not found."));
    }

}
