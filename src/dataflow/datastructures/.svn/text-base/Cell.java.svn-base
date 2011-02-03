package dataflow.datastructures;

import java.util.Vector;

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;

/**
 * The basic unit of the Grid. This class contains a piece of information that is updated
 * based on incoming cells.
 * @author Joris Gillis
 */
public interface Cell {
	
	/**
	 * Recalculate the value of this cell based on changed children cells. 
	 */
	public abstract void recalculate( Vector<String> cells ) 
		throws DataFaultException, ImpossibleCalculationException, RestrictionViolation;
	
	/**
	 * Returns the name of the cell.
	 * @return	name
	 */
	public abstract String getName();
}
