package msp.data;

/**
 * Exception thrown if the asked for span is not found.
 * @author joris
 */
public class NoSuchSpanException extends Exception {
	
	/**
	 * Default constructor.
	 */
	public NoSuchSpanException() {
		super();
	}
	
	/**
	 * Constructor with a message
	 * @param message	message
	 */
	public NoSuchSpanException(String message) {
		super(message);
	}
}
