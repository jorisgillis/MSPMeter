package msp.data;

/**
 * Exception thrown if asked for a non-existing lemma.
 * @author joris
 */
public class NoSuchLemmaException extends Exception {
	
	/**
	 * Default constructor.
	 */
	public NoSuchLemmaException() {
		super();
	}
	
	/**
	 * Constructor with a message.
	 * @param message message
	 */
	public NoSuchLemmaException(String message) {
		super(message);
	}
}
