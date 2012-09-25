package msp.data;

/**
 * Exception thrown if asked for a non-existing category.
 * @author joris
 */
public class NoSuchCategoryException extends Exception {
	/**
	 * Default constructor. 
	 */
	public NoSuchCategoryException() {
		super();
	}
	
	/**
	 * Constructor with a message.
	 * @param message message
	 */
	public NoSuchCategoryException(String message) {
		super(message);
	}
}
