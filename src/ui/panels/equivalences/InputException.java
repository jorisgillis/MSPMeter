package ui.panels.equivalences;

/**
 * Exception thrown whenever an input is not correct.
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class InputException extends Exception {
	
	/**
	 * A default input exception.
	 */
	public InputException() {
		super("Input went wrong!");
	}
	
	/**
	 * A customized exception.
	 * @param msg	message
	 */
	public InputException( String msg ) {
		super(msg);
	}
}
