package edu.ipp.isep.dei.dimei.retailproject.common.dto.gets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@JsonPropertyOrder({"firstname", "lastname", "email", "password"})
public class RegisterDTO {
    @JsonProperty("firstname")
    private String firstname;

    @JsonProperty("lastname")
    private String lastname;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;


}
