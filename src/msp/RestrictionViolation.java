package msp;

/**
 * Exception thrown when not all restrictions hold.
 * @author Joris Gillis
 */
public class RestrictionViolation extends Exception {
	
	/**
	 * Default constructor
	 */
	public RestrictionViolation() {
		super("A restriction was violated.");
	}
	
	/**
	 * Construct an exception with the given message.
	 * @param msg	message
	 */
	public RestrictionViolation( String msg ) {
		super(msg);
	}
	
}
