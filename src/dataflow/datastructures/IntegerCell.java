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
