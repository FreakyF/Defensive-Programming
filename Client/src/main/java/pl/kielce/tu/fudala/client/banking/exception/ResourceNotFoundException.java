package pl.kielce.tu.fudala.client.banking.exception;

/**
 * A custom exception that represents a resource not found error in the application.
 * <p>
 * This exception is typically thrown to indicate that a requested resource
 * could not be found. It extends {@code RuntimeException}, allowing it to be
 * used as an unchecked exception.
 * <p>
 * Common use cases include signaling missing data in a database or filesystem,
 * or when an entity is requested by an identifier that does not exist.
 */
public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(String message) {
		super(message);
	}
}
