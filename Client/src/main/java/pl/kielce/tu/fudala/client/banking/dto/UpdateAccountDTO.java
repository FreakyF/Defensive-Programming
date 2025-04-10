package pl.kielce.tu.fudala.client.banking.dto;

import lombok.Data;
import pl.kielce.tu.fudala.client.banking.validator.email.ValidEmail;

@Data
public class UpdateAccountDTO {
	private String firstName;
	private String lastName;

	@ValidEmail
	private String email;
}
