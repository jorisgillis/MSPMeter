package dataflow.datastructures;

import java.util.HashMap;
import java.util.Vector;

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;

/**
 * Cell containing the categoryEquivalences
 * @author Joris Gillis
 */
public class CategoryEquivalencesCell extends DefaultCell {
	
	private HashMap<String, HashMap<String, String>> categoryEquivalences = null;
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#getName()
	 */
	public String getName() {
		return "categoryEquivalences";
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(dataflow.datastructures.Cell)
	 */
	public void recalculate( Vector<String> children ) 
		throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {}
	
	/**
	 * Sets the value of this cell and triggers a data flow.
	 */
	public void setValue( HashMap<String, HashMap<String, String>> categoryEquivalences ) throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {
		if( this.categoryEquivalences == null || !this.categoryEquivalences.equals(categoryEquivalences) )
			this.categoryEquivalences = categoryEquivalences;
	}
	
	/**
	 * Returns the value of this cell.
	 * @return	value
	 */
	public HashMap<String, HashMap<String, String>> getValue() {
		return categoryEquivalences;
	}
}
