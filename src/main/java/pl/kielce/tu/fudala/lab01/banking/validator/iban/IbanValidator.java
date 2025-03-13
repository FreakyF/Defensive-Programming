package pl.kielce.tu.fudala.lab01.banking.validator.iban;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;

public class IbanValidator implements ConstraintValidator<ValidIban, String> {

    private static final int IBAN_MIN_LENGTH = 4;

    @Override
    public boolean isValid(String iban, ConstraintValidatorContext context) {
        if (iban == null) {
            return false;
        }
        final String normalizedIban = normalizeIban(iban);
        if (!hasValidCountryCodeAndLength(normalizedIban)) {
            return false;
        }
        final String rearrangedIban = rearrangeIban(normalizedIban);
        final String numericIban = convertIbanToNumeric(rearrangedIban);
        if (numericIban == null) {
            return false;
        }
        return computeModulo97(numericIban) == 1;
    }

    private String normalizeIban(String iban) {
        return iban.replaceAll("\\s+", "").toUpperCase();
    }

    private boolean hasValidCountryCodeAndLength(String iban) {
        if (iban.length() < IBAN_MIN_LENGTH) {
            return false;
        }
        final String countryCode = iban.substring(0, 2);
        final Map<String, Integer> ibanLengths = IbanLengthRegistry.getIbanLengths();
        final Integer expectedLength = ibanLengths.get(countryCode);
        return expectedLength != null && iban.length() == expectedLength;
    }


    private String rearrangeIban(String iban) {
        return iban.substring(4) + iban.substring(0, 4);
    }

    private String convertIbanToNumeric(String rearrangedIban) {
        final StringBuilder numericIban = new StringBuilder();
        for (int i = 0; i < rearrangedIban.length(); i++) {
            final char ch = rearrangedIban.charAt(i);
            final String convertedValue = switch (ch) {
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> String.valueOf(ch);
                default -> {
                    if (Character.isLetter(ch)) {
                        yield String.valueOf(ch - 'A' + 10);
                    }
                    yield null;
                }
            };
            if (convertedValue == null) {
                return null;
            }
            numericIban.append(convertedValue);
        }
        return numericIban.toString();
    }

    private int computeModulo97(String numericIban) {
        int mod = 0;
        for (int i = 0; i < numericIban.length(); i++) {
            int digit = numericIban.charAt(i) - '0';
            mod = (mod * 10 + digit) % 97;
        }
        return mod;
    }
}
