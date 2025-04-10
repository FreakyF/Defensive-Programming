package pl.kielce.tu.fudala.server.banking.exception;

import lombok.Getter;

/**
 * Exception thrown when there's an issue with account validation,
 * such as invalid PESEL, IBAN, or student email.
 */
@Getter
public class AccountValidationException extends RuntimeException {
    private final String field;

    public AccountValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

}

