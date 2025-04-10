package pl.kielce.tu.fudala.client.banking.exception;

/**
 * Exception thrown when there's an issue with file operations,
 * such as reading from or writing to account files.
 */
public class FileOperationException extends RuntimeException {
	public FileOperationException(String message, Throwable cause) {
		super(message, cause);
	}
}
