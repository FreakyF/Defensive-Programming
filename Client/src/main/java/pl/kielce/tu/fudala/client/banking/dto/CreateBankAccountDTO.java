package pl.kielce.tu.fudala.client.banking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pl.kielce.tu.fudala.client.banking.validator.email.ValidEmail;
import pl.kielce.tu.fudala.client.banking.validator.iban.ValidIban;
import pl.kielce.tu.fudala.client.banking.validator.pesel.ValidPesel;

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