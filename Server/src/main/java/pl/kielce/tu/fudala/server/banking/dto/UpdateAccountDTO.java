package pl.kielce.tu.fudala.server.banking.dto;

import lombok.Data;
import pl.kielce.tu.fudala.server.banking.validator.email.ValidEmail;

@Data
public class UpdateAccountDTO {
    private String firstName;
    private String lastName;

    @ValidEmail
    private String email;
}
