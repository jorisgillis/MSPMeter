package dataflow.datastructures;

import java.util.Vector;

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;
import msp.defaults.Defaults;
import msp.defaults.NoValueException;

/**
 * Cell containing the boolean useInMSP.
 * @author Joris Gillis
 */
public class UseInMSPCell extends BooleanCell {
	
	private boolean value;
	
	/**
	 * Construct the cell.
	 */
	public UseInMSPCell() {
		try {
			value = Defaults.instance().getBoolean("useInMSP");
		} catch( NoValueException e ) {
			value = true;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.BooleanCell#boolValue()
	 */
	@Override
	public boolean boolValue() {
		return value;
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.BooleanCell#setValue(boolean)
	 */
	@Override
	public void setValue(boolean value) throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {
		if( this.value != value )
			this.value = value;
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#getName()
	 */
	public String getName() {
		return "useInMSP";
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(dataflow.datastructures.Cell)
	 */
	public void recalculate(Vector<String> children) 
		throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {}
}
