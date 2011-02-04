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

/**
 * The basic unit of the Grid. This class contains a piece of information that is updated
 * based on incoming cells.
 * @author Joris Gillis
 */
public interface Cell {
	
	/**
	 * Recalculate the value of this cell based on changed children cells. 
	 */
	public abstract void recalculate( Vector<String> cells ) 
		throws DataFaultException, ImpossibleCalculationException, RestrictionViolation;
	
	/**
	 * Returns the name of the cell.
	 * @return	name
	 */
	public abstract String getName();
}
