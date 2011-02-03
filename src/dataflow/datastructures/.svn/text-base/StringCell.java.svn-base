package dataflow.datastructures;

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;


public abstract class StringCell extends DefaultCell {
	
	/** Value of the cell. */
	private String value;
	
	/**
	 * Gets the value of the cell.
	 * @return	value of the cell.
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Sets the value of the cell and triggers a flow of data.
	 * @param value	new value
	 */
	public void setValue( String value ) throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {
		if( this.value == null || !this.value.equals(value) )
			this.value = value;
	}
}
