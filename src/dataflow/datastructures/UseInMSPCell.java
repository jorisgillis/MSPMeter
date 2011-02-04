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

import java.util.Vector;

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;
import msp.defaults.Defaults;
import msp.defaults.NoValueException;

/**
 * Cell containing the boolean useInMSP.
 * @author Joris Gillis
 */
public class UseInMSPCell extends BooleanCell {
	
	private boolean value;
	
	/**
	 * Construct the cell.
	 */
	public UseInMSPCell() {
		try {
			value = Defaults.instance().getBoolean("useInMSP");
		} catch( NoValueException e ) {
			value = true;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.BooleanCell#boolValue()
	 */
	@Override
	public boolean boolValue() {
		return value;
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.BooleanCell#setValue(boolean)
	 */
	@Override
	public void setValue(boolean value) throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {
		if( this.value != value )
			this.value = value;
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#getName()
	 */
	public String getName() {
		return "useInMSP";
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(dataflow.datastructures.Cell)
	 */
	public void recalculate(Vector<String> children) 
		throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {}
}
