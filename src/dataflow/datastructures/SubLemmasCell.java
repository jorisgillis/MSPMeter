package dataflow.datastructures;

import java.util.HashMap;
import java.util.Vector;

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;

/**
 * Cell containing the subLemmas datastructure from the LemmaEquivalencesPanel.
 * @author Joris Gillis
 */
public class SubLemmasCell extends DefaultCell {
	
	private HashMap<String, Vector<String>> subLemmas = null;
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#getName()
	 */
	public String getName() {
		return "subLemmas";
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(dataflow.datastructures.Cell)
	 */
	public void recalculate(Vector<String> children) 
		throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {}
	
	/**
	 * Returns the value of this cell.
	 */
	public HashMap<String, Vector<String>> getValue() {
		return subLemmas;
	}
	
	/**
	 * Sets the value of this cell and triggers a flow of data.
	 */
	public void setValue( HashMap<String, Vector<String>> subLemmas ) throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {
		if( this.subLemmas == null || !this.subLemmas.equals(subLemmas) )
			this.subLemmas = subLemmas;
	}
}
