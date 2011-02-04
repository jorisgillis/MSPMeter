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

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;

/**
 * Cell containing a boolean value.
 * @author Joris Gillis
 */
public abstract class BooleanCell extends DefaultCell {

	/**
	 * Get the boolean value of this cell.
	 * @return	boolean value
	 */
	public abstract boolean boolValue();
	
	/**
	 * Sets the value of this cell and results in a flow of Data
	 */
	public abstract void setValue( boolean value ) throws DataFaultException, ImpossibleCalculationException, RestrictionViolation;
}
