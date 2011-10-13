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
 * Cell of the Lemma Equivalences.
 * @author Joris Gillis
 */
public class LemmaEquivalencesCell extends DefaultCell {
	
	/** Mapping from sublemma onto generic lemma. This one only contains which
	 * sublemmas have to be remapped in the DataCube. */
	protected HashMap<String, String> lemmaEquivalences;
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#getName()
	 */
	public String getName() {
		return "lemmaEquivalences";
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(dataflow.datastructures.Cell)
	 */
	public void recalculate(Vector<String> children) 
		throws DataFaultException, 
				ImpossibleCalculationException, 
				RestrictionViolation {}
	
	/**
	 * @return	lemmaEquivalences 
	 */
	public HashMap<String, String> getValue() {
		return lemmaEquivalences;
	}
	
	/**
	 * Sets the value of this cell.
	 */
	public void setValue( HashMap<String, String> lemmaEquivalences ) 
		throws DataFaultException, 
				ImpossibleCalculationException, 
				RestrictionViolation {
		if( this.lemmaEquivalences == null || 
				!this.lemmaEquivalences.equals(lemmaEquivalences) )
			this.lemmaEquivalences = lemmaEquivalences;
	}
}
