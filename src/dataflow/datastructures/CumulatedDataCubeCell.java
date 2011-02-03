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
				this.workingDC = ((WorkingDataCubeCell)Grid.instance().getCell(cellName)).getCube();
			
			//- Recalculate
			if( workingDC != null )
				cube = workingDC.cumulate();
		}
	}

}
