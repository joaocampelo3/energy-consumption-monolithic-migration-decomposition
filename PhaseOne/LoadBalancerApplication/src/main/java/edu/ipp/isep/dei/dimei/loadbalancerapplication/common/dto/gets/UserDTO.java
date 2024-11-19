package edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import edu.ipp.isep.dei.dimei.loadbalancerapplication.domain.enums.RoleEnum;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@JsonPropertyOrder({"userId", "email"})
public class UserDTO {
    @JsonProperty("userId")
    private int userId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("role")
    private RoleEnum role;
}
