/*
 * Copyright 2010 MSPMeter
 *
 * Licensed under the EUPL, Version 1.1 or Ð as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl.html
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */


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
