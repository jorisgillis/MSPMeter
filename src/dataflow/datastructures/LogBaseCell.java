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

import org.apache.log4j.Logger;

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;
import msp.defaults.Defaults;
import msp.defaults.NoValueException;

/**
 * Which base should MSP use for calculating the entropy?
 * @author Joris Gillis
 */
public class LogBaseCell extends IntegerCell {
	/** Base of the log used in computing the entropy. */
	protected int base;
	// Logger
	protected static Logger logger = Logger.getLogger(LogBaseCell.class);
	
	
	public LogBaseCell() {
		try {
			base = Defaults.instance().getInteger("logBase");
		} catch( NoValueException e ) {
			logger.error("No Value for \"logBase\".");
			base = 2;
		}
	}
	
	@Override
	public void recalculate(Vector<String> cells) throws DataFaultException,
			ImpossibleCalculationException, RestrictionViolation {}

	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#getName()
	 */
	@Override
	public String getName() {
		return "logBase";
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.IntegerCell#intValue()
	 */
	@Override
	public int intValue() {
		return base;
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.IntegerCell#setValue(int)
	 */
	@Override
	public void setValue(int value) {
		if( 1 < value )
			base = value;
	}
}
