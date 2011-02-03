package dataflow.datastructures;

import java.util.HashSet;
import java.util.Vector;

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;

/**
 * The cell containing the allLemmas datastructure from the LemmaEquivalencesPanel.
 * @author Joris Gillis
 */
public class AllLemmasCell extends DefaultCell {
	
	private HashSet<String> allLemmas = null;
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#getName()
	 */
	public String getName() {
		return "allLemmas";
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
	public HashSet<String> getValue() {
		return allLemmas;
	}
	
	/**
	 * Sets the value of this cell and triggers a flow of data.
	 */
	public void setValue( HashSet<String> allLemmas ) throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {
		if( this.allLemmas == null || !this.allLemmas.equals(allLemmas) )
			this.allLemmas = allLemmas;
	}
}
