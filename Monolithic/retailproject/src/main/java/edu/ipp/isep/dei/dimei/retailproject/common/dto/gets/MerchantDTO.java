package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MerchantDTO {
    private int id;
    private String name;
    private String email;
    private AddressDTO address;

    public MerchantDTO(Merchant merchant) {
        this.id = merchant.getId();
        this.name = merchant.getName();
        this.email = merchant.getEmail();
        this.address = new AddressDTO(merchant.getAddress());
    }

    public Merchant dtoToEntity() {
        return Merchant.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .address(this.address.dtoToEntity())
                .build();
    }
}
