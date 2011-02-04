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

import java.util.HashMap;
import java.util.Vector;

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;

/**
 * Cell containing the subLemmas datastructure from the LemmaEquivalencesPanel.
 * @author Joris Gillis
 */
public class SubLemmasCell extends DefaultCell {
	
	private HashMap<String, Vector<String>> subLemmas = null;
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#getName()
	 */
	public String getName() {
		return "subLemmas";
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(dataflow.datastructures.Cell)
	 */
	public void recalculate(Vector<String> children) 
		throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {}
	
	/**
	 * Returns the value of this cell.
	 */
	public HashMap<String, Vector<String>> getValue() {
		return subLemmas;
	}
	
	/**
	 * Sets the value of this cell and triggers a flow of data.
	 */
	public void setValue( HashMap<String, Vector<String>> subLemmas ) throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {
		if( this.subLemmas == null || !this.subLemmas.equals(subLemmas) )
			this.subLemmas = subLemmas;
	}
}
