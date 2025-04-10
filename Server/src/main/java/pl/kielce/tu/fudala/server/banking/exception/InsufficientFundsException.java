package pl.kielce.tu.fudala.server.banking.exception;

import lombok.Getter;

/**
 * Exception thrown when there are insufficient funds in a bank account
 * to complete a requested operation, such as a withdrawal or transfer.
 */
@Getter
public class InsufficientFundsException extends RuntimeException {
    private final String accountNumber;
    private final String operation;

    public InsufficientFundsException(String accountNumber, String operation, String message) {
        super(message);
        this.accountNumber = accountNumber;
        this.operation = operation;
    }

}
