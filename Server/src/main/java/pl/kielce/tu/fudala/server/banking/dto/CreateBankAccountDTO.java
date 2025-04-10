package pl.kielce.tu.fudala.server.banking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pl.kielce.tu.fudala.server.banking.validator.pesel.ValidPesel;
import pl.kielce.tu.fudala.server.banking.validator.email.ValidEmail;
import pl.kielce.tu.fudala.server.banking.validator.iban.ValidIban;

import java.math.BigDecimal;

@Data
public class CreateBankAccountDTO {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @ValidIban
    @NotBlank
    private String accountNumber;
    @NotNull
    private BigDecimal balance = BigDecimal.ZERO;
    @ValidPesel
    @NotBlank
    private String pesel;
    @ValidEmail
    @NotBlank
    private String email;
}