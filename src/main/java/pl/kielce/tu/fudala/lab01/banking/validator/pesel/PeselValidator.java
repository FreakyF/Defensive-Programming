package pl.kielce.tu.fudala.lab01.banking.validator.pesel;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PeselValidator implements ConstraintValidator<ValidPesel, String> {

    @Override
    public boolean isValid(String pesel, ConstraintValidatorContext context) {
        if (pesel == null) {
            return false;
        }

        if (!hasCorrectFormat(pesel)) {
            return false;
        }

        int expectedCheckDigit = calculateExpectedCheckDigit(pesel);
        int actualCheckDigit = Character.getNumericValue(pesel.charAt(10));
        return expectedCheckDigit == actualCheckDigit;
    }

    private boolean hasCorrectFormat(String pesel) {
        return pesel.matches("\\d{11}");
    }

    private int calculateExpectedCheckDigit(String pesel) {
        int[] weightFactors = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};
        int weightedSum = 0;
        for (int i = 0; i < weightFactors.length; i++) {
            int digit = Character.getNumericValue(pesel.charAt(i));
            weightedSum += digit * weightFactors[i];
        }
        int remainder = weightedSum % 10;
        return remainder == 0 ? 0 : (10 - remainder);
    }
}
