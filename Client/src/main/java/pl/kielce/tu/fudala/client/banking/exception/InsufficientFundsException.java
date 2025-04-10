package pl.kielce.tu.fudala.client.banking.exception;

/**
 * A custom exception that represents an insufficient funds error in the application.
 * <p>
 * This exception is typically used to indicate that a financial operation cannot
 * be completed due to insufficient funds in an account or similar scenarios.
 * <p>
 * It extends {@code RuntimeException}, enabling it to be used as an unchecked exception.
 * Applications may catch and handle this exception to provide meaningful error
 * messages or take corrective actions.
 */
public class InsufficientFundsException extends RuntimeException {
	public InsufficientFundsException(String message) {
		super(message);
	}
}