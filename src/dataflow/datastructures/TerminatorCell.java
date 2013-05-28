package dataflow.datastructures;

import java.util.Vector;

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;

/**
 * Characters terminating the category.
 * @author Joris Gillis
 */
public class TerminatorCell extends StringCell {
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(java.util.Vector)
	 */
	@Override
	public void recalculate(Vector<String> cells) throws DataFaultException,
			ImpossibleCalculationException, RestrictionViolation {
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#getName()
	 */
	@Override
	public String getName() {
		return "terminator";
	}

}
