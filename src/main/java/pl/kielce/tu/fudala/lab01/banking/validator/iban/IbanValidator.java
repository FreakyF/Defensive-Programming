package pl.kielce.tu.fudala.lab01.banking.validator.iban;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashMap;
import java.util.Map;

public class IbanValidator implements ConstraintValidator<ValidIban, String> {
	private static final Map<String, Integer> IBAN_LENGTHS = new HashMap<>();

	static {
		IBAN_LENGTHS.put("AL", 28);
		IBAN_LENGTHS.put("AD", 24);
		IBAN_LENGTHS.put("AT", 20);
		IBAN_LENGTHS.put("AZ", 28);
		IBAN_LENGTHS.put("BH", 22);
		IBAN_LENGTHS.put("BE", 16);
		IBAN_LENGTHS.put("BA", 20);
		IBAN_LENGTHS.put("BR", 29);
		IBAN_LENGTHS.put("BG", 22);
		IBAN_LENGTHS.put("CR", 21);
		IBAN_LENGTHS.put("HR", 21);
		IBAN_LENGTHS.put("CY", 28);
		IBAN_LENGTHS.put("CZ", 24);
		IBAN_LENGTHS.put("DK", 18);
		IBAN_LENGTHS.put("DO", 28);
		IBAN_LENGTHS.put("EE", 20);
		IBAN_LENGTHS.put("FO", 18);
		IBAN_LENGTHS.put("FI", 18);
		IBAN_LENGTHS.put("FR", 27);
		IBAN_LENGTHS.put("GE", 22);
		IBAN_LENGTHS.put("DE", 22);
		IBAN_LENGTHS.put("GI", 23);
		IBAN_LENGTHS.put("GR", 27);
		IBAN_LENGTHS.put("GL", 18);
		IBAN_LENGTHS.put("GT", 28);
		IBAN_LENGTHS.put("HU", 28);
		IBAN_LENGTHS.put("IS", 26);
		IBAN_LENGTHS.put("IE", 22);
		IBAN_LENGTHS.put("IL", 23);
		IBAN_LENGTHS.put("IT", 27);
		IBAN_LENGTHS.put("JO", 30);
		IBAN_LENGTHS.put("KZ", 20);
		IBAN_LENGTHS.put("KW", 30);
		IBAN_LENGTHS.put("LV", 21);
		IBAN_LENGTHS.put("LB", 28);
		IBAN_LENGTHS.put("LI", 21);
		IBAN_LENGTHS.put("LT", 20);
		IBAN_LENGTHS.put("LU", 20);
		IBAN_LENGTHS.put("MK", 19);
		IBAN_LENGTHS.put("MT", 31);
		IBAN_LENGTHS.put("MR", 27);
		IBAN_LENGTHS.put("MU", 30);
		IBAN_LENGTHS.put("MC", 27);
		IBAN_LENGTHS.put("MD", 24);
		IBAN_LENGTHS.put("ME", 22);
		IBAN_LENGTHS.put("NL", 18);
		IBAN_LENGTHS.put("NO", 15);
		IBAN_LENGTHS.put("PK", 24);
		IBAN_LENGTHS.put("PS", 29);
		IBAN_LENGTHS.put("PL", 28);
		IBAN_LENGTHS.put("PT", 25);
		IBAN_LENGTHS.put("QA", 29);
		IBAN_LENGTHS.put("RO", 24);
		IBAN_LENGTHS.put("SM", 27);
		IBAN_LENGTHS.put("SA", 24);
		IBAN_LENGTHS.put("RS", 22);
		IBAN_LENGTHS.put("SK", 24);
		IBAN_LENGTHS.put("SI", 19);
		IBAN_LENGTHS.put("ES", 24);
		IBAN_LENGTHS.put("SE", 24);
		IBAN_LENGTHS.put("CH", 21);
		IBAN_LENGTHS.put("TN", 24);
		IBAN_LENGTHS.put("TR", 26);
		IBAN_LENGTHS.put("AE", 23);
		IBAN_LENGTHS.put("GB", 22);
		IBAN_LENGTHS.put("VG", 24);
	}

	@Override
	public boolean isValid(String iban, ConstraintValidatorContext context) {
		if (iban == null) {
			return false;
		}
		iban = iban.replaceAll("\\s+", "").toUpperCase();

		if (iban.length() < 2) {
			return false;
		}
		String countryCode = iban.substring(0, 2);
		Integer expectedLength = IBAN_LENGTHS.get(countryCode);
		if (expectedLength == null || iban.length() != expectedLength) {
			return false;
		}

		String rearranged = iban.substring(4) + iban.substring(0, 4);

		StringBuilder numericIban = new StringBuilder();
		for (int i = 0; i < rearranged.length(); i++) {
			char ch = rearranged.charAt(i);
			if (Character.isDigit(ch)) {
				numericIban.append(ch);
			} else if (Character.isLetter(ch)) {
				int value = ch - 'A' + 10;
				numericIban.append(value);
			} else {
				return false;
			}
		}

		String numStr = numericIban.toString();
		int mod = 0;
		for (int i = 0; i < numStr.length(); i++) {
			int digit = numStr.charAt(i) - '0';
			mod = (mod * 10 + digit) % 97;
		}
		return mod == 1;
	}
}
