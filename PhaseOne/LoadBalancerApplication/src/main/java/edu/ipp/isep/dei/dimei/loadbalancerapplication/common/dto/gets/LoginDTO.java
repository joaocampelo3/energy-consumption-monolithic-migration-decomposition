package edu.ipp.isep.dei.dimei.loadbalancerapplication.common.dto.gets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@JsonPropertyOrder({"email", "password"})
public class LoginDTO {
    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;
}
