package pl.kielce.tu.fudala.server.banking.exception;

/**
 * Exception thrown when a bank account is not found.
 * This exception is used when attempting to retrieve or manipulate an account
 * that does not exist in the system.
 */
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(String pesel, String message) {
        super("Account with PESEL " + pesel + " not found: " + message);
    }
}
