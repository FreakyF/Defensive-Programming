package pl.kielce.tu.fudala.client.banking.validator.pesel;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PeselValidator implements ConstraintValidator<ValidPesel, String> {

	private static final int PESEL_LENGTH = 11;
	private static final int[] WEIGHT_FACTORS = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};

	@Override
	public boolean isValid(String pesel, ConstraintValidatorContext context) {
		if (pesel == null) {
			return false;
		}

		final String trimmedPesel = pesel.trim();
		if (!hasCorrectFormat(trimmedPesel)) {
			return false;
		}

		final int expectedCheckDigit = calculateExpectedCheckDigit(trimmedPesel);
		final int actualCheckDigit = Character.getNumericValue(trimmedPesel.charAt(10));
		return expectedCheckDigit == actualCheckDigit;
	}

	private boolean hasCorrectFormat(final String pesel) {
		return pesel.matches("\\d{" + PESEL_LENGTH + "}");
	}

	private int calculateExpectedCheckDigit(final String pesel) {
		int weightedSum = 0;
		for (int i = 0; i < WEIGHT_FACTORS.length; i++) {
			final int digit = Character.getNumericValue(pesel.charAt(i));
			weightedSum += digit * WEIGHT_FACTORS[i];
		}
		final int remainder = weightedSum % 10;
		return remainder == 0 ? 0 : (10 - remainder);
	}
}
