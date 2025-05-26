package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import edu.ipp.isep.dei.dimei.retailproject.domain.model.Merchant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class MerchantDTO {
    private int id;
    private String name;
    private String email;

    public MerchantDTO(Merchant merchant) {
        this.id = merchant.getId();
        this.name = merchant.getName();
        this.email = merchant.getEmail();
    }

    public Merchant dtoToEntity() {
        return Merchant.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .build();
    }
}
