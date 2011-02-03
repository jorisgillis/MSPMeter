package dataflow.datastructures;

/**
 * Cell containing an integer value.
 * @author Joris Gillis
 *
 */
public abstract class IntegerCell extends DefaultCell {
	
	/**
	 * Returns the integer value of the cell.
	 * @return	integer value
	 */
	public abstract int intValue();
	
	/**
	 * Sets the value of the cell and results in a flow of data.
	 */
	public abstract void setValue( int value );
	
}
