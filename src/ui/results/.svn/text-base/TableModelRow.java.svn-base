package ui.results;

import jxl.write.WritableCell;

/**
 * What should a row be like and how should it behave? This interface is more
 * like a tag, tagging classes that are rows in a TableModel. 
 * @author joris
 */
public interface TableModelRow {
	
	/**
	 * Transforms this row into a string in which the values of the row
	 * are separated by a give delimitor. 
	 * @param delimitor	the delimitor separating values
	 * @return			delimited string representation
	 */
	public String printDelimiting( String delimitor );
	
	/**
	 * Transforms this row into a string in which the values of the row
	 * are encapsulated in tags. 
	 * @return		XML representation of the row
	 */
	public String printXML();
	
	/**
	 * Transforms this row into a list of Excell sheet cells. The 
	 * given row index is used to position the cell in the sheet. 
	 * @param rowIndex	index of this row
	 * @return			list of cells representing the row
	 */
	public WritableCell[] printExcell( int rowIndex );
}
