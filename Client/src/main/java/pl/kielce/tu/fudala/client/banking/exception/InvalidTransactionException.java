package pl.kielce.tu.fudala.client.banking.exception;

/**
 * Exception thrown when an invalid transaction is attempted,
 * such as a negative deposit amount or an invalid transfer.
 */
public class InvalidTransactionException extends RuntimeException {
	public InvalidTransactionException(String message) {
		super(message);
	}
}
