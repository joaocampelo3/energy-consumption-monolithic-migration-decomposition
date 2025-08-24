package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class MerchantDTO {
    private int id;
    private String name;
    private String email;
    private int addressId;
    private UserDTO userDTO;
    @JsonProperty("address")
    private AddressDTO addressDTO;

    public MerchantDTO(Merchant merchant) {
        this.id = merchant.getId();
        this.name = merchant.getName();
        this.email = merchant.getEmail();
        this.addressId = merchant.getAddressId();
    }

    public MerchantDTO(int id, String name, String email, int addressId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.addressId = addressId;
    }

    public Merchant dtoToEntity() {
        return Merchant.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .addressId(this.addressId)
                .build();
    }
}
