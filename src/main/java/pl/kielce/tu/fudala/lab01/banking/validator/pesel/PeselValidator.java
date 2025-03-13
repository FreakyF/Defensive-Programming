package pl.kielce.tu.fudala.lab01.banking.validator.pesel;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PeselValidator implements ConstraintValidator<ValidPesel, String> {
	@Override
	public boolean isValid(String pesel, ConstraintValidatorContext context) {
		if (pesel == null) {
			return false;
		}

		if (!pesel.matches("\\d{11}")) {
			return false;
		}

		int[] weights = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};
		int sum = 0;
		for (int i = 0; i < weights.length; i++) {
			int digit = Character.getNumericValue(pesel.charAt(i));
			sum += digit * weights[i];
		}
		int modulo = sum % 10;
		int checkDigit = modulo == 0 ? 0 : (10 - modulo);

		int lastDigit = Character.getNumericValue(pesel.charAt(10));
		return checkDigit == lastDigit;
	}
}
