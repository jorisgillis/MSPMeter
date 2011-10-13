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

import dataflow.Grid;

import msp.RestrictionViolation;
import msp.data.DataCube;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;

/**
 * The datacube derived from the original one, possible engaging lemma equivalences in the action
 * @author Joris Gillis
 */
public class LemmadDataCubeCell extends DataCubeCell {
	
	private boolean useInMSP = true;
	private HashMap<String, String> lemmaEquivalences = null;
	private DataCube originalDC = null;
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#getName()
	 */
	public String getName() {
		return "lemmadDC";
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(dataflow.datastructures.Cell)
	 */
	public void recalculate( Vector<String> children ) 
		throws DataFaultException, 
				ImpossibleCalculationException, 
				RestrictionViolation {
		Grid grid = Grid.instance();
		for( String cellName : children ) {
			//- Which cell has been changed?
			if( cellName.equals("useInMSP") )
				useInMSP = ((BooleanCell)grid.getCell(cellName)).boolValue();
			else if( cellName.equals("lemmaEquivalences") )
				lemmaEquivalences = ((LemmaEquivalencesCell)grid.getCell(cellName)).getValue();
			else if( cellName.equals("originalDC") )
				originalDC = ((OriginalDataCubeCell)grid.getCell(cellName)).getCube();
		}
		
		//- recalculate
		if( originalDC != null ) {
			if( useInMSP && lemmaEquivalences != null )
				cube = originalDC.lemmaEquivalences(lemmaEquivalences);
			else
				cube = originalDC; 
		}
	}

}
