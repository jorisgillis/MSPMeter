package msp.data;

/**
 * This exception captures everything that can go wrong while processing
 * the input files.
 * @author Joris Gillis
 */
public class DataFaultException extends Exception {
	
	private static final long serialVersionUID = -7387001409657406837L;

	/**
	 * Construct a new exception with the given message.
	 * @param msg	message
	 */
	public DataFaultException( String msg ) {
		super(msg);
	}
	
}
