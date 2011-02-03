package dataflow.datastructures;

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;

/**
 * Cell containing a boolean value.
 * @author Joris Gillis
 */
public abstract class BooleanCell extends DefaultCell {

	/**
	 * Get the boolean value of this cell.
	 * @return	boolean value
	 */
	public abstract boolean boolValue();
	
	/**
	 * Sets the value of this cell and results in a flow of Data
	 */
	public abstract void setValue( boolean value ) throws DataFaultException, ImpossibleCalculationException, RestrictionViolation;
}
