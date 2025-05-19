package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.User;
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

    public UserDTO(User user) {
        this.userId = user.getId();
        this.email = user.getAccount().getEmail();
        this.role = user.getAccount().getRole();
    }
}
