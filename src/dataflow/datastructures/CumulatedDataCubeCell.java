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

import dataflow.Grid;

import msp.RestrictionViolation;
import msp.data.DataCube;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;

/**
 * Cell containing the cumulated datacube.
 * @author Joris Gillis
 */
public class CumulatedDataCubeCell extends DataCubeCell {
	
	private DataCube workingDC = null;
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#getName()
	 */
	public String getName() {
		return "cumulatedDC";
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(dataflow.datastructures.Cell)
	 */
	public void recalculate(Vector<String> children) throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {
		for( String cellName : children ) {
			//- Incoming
			if( cellName.equals("workingDC") )
				this.workingDC = 
					((WorkingDataCubeCell)Grid.instance().getCell(cellName)).getCube();
			
			//- Recalculate
			if( workingDC != null )
				cube = workingDC.cumulate();
		}
	}

}
