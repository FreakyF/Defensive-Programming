package pl.kielce.tu.fudala.client.banking.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Error response class representing the structure of error responses
 * sent to clients when exceptions occur.
 */
@Getter
public class ErrorResponse {
	private final LocalDateTime timestamp;
	private final int status;
	private final String error;
	private final String message;
	@Setter
	private String path;
	@Setter
	private Map<String, String> details;

	public ErrorResponse(int status, String error, String message) {
		this.timestamp = LocalDateTime.now();
		this.status = status;
		this.error = error;
		this.message = message;
	}

}
