package msp.data;

/**
 * Exception which indicates the impossibility of calculating some value.
 * For example: o_{.l}(t)
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class ImpossibleCalculationException extends Exception {
	
	/**
	 * Create a new exception telling the system that the requested calculation with the
	 * specific arguments is impossible.
	 * @param arguments	arguments causing for the impossibility
	 */
	public ImpossibleCalculationException( String arguments ) {
		super("Impossible calculation! Arguments: "+ arguments);
	}
	
}
