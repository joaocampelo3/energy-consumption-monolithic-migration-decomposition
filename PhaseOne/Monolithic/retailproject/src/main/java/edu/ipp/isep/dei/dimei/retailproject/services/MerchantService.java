package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.MerchantDTO;
import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.UserDTO;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.MerchantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class MerchantService {

    private static final String NOTFOUNDEXCEPTIONMESSAGE = "Merchant not found.";
    private static final String BADPAYLOADEXCEPTIONMESSAGE = "Wrong merchant payload.";
    private final MerchantRepository merchantRepository;

    public Merchant getMerchantByUser(UserDTO userDTO) throws NotFoundException {
        return merchantRepository.findByEmail(userDTO.getEmail()).orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
    }

    public List<MerchantDTO> getAllMerchants() {
        List<MerchantDTO> merchants = new ArrayList<>();

        this.merchantRepository.findAll()
                .forEach(merchant -> merchants.add(new MerchantDTO(merchant)));

        return merchants;
    }

    public MerchantDTO getMerchant(int id) throws NotFoundException {
        Merchant merchant = getMerchantById(id);
        return new MerchantDTO(merchant);
    }

    private Merchant getMerchantById(int id) throws NotFoundException {
        return this.merchantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOTFOUNDEXCEPTIONMESSAGE));
    }

    public MerchantDTO createMerchant(MerchantDTO merchantDTO) {
        Merchant merchant = new Merchant(merchantDTO.getName(), merchantDTO.getEmail(), merchantDTO.getAddressDTO().getId());

        merchant = this.merchantRepository.save(merchant);

        return new MerchantDTO(merchant);
    }

    public MerchantDTO updateMerchant(int id, MerchantDTO merchantDTO) throws NotFoundException, BadPayloadException {
        Merchant merchant = getMerchantById(id);

        if (merchant.getId() != merchantDTO.getId() || !merchant.getEmail().equals(merchantDTO.getEmail())) {
            throw new BadPayloadException(BADPAYLOADEXCEPTIONMESSAGE);
        }

        merchant.setName(merchantDTO.getName());
        merchant.setAddressId(merchantDTO.getAddressDTO().getId());

        merchant = this.merchantRepository.save(merchant);
        return new MerchantDTO(merchant);
    }

    public MerchantDTO deleteMerchant(int id) throws NotFoundException {
        Merchant merchant = getMerchantById(id);

        this.merchantRepository.delete(merchant);

        return new MerchantDTO(merchant);
    }
}
