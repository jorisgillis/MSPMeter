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
