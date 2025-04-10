package pl.kielce.tu.fudala.client.banking.exception;

/**
 * A custom exception that represents server-side errors in the application.
 * <p>
 * This exception is typically used to indicate unexpected conditions encountered
 * on the server. It extends {@code RuntimeException}, allowing it to be used
 * as an unchecked exception.
 * <p>
 * The {@code ServerException} may be thrown to signal issues such as internal
 * server processing errors, unavailable resources, or similar problems that
 * fall under the category of server-side issues.
 */
public class ServerException extends RuntimeException {
	public ServerException(String message) {
		super(message);
	}
}
