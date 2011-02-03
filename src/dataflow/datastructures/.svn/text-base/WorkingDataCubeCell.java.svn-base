package dataflow.datastructures;

import java.util.HashMap;
import java.util.Vector;

import msp.RestrictionViolation;
import msp.data.DataCube;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;
import dataflow.Grid;

/**
 * Cell containing the workingDC.
 * @author Joris Gillis
 */
public class WorkingDataCubeCell extends DataCubeCell {
	
	private boolean useInMSP;
	private DataCube lemmadDC = null;
	private HashMap<String, String> lemmaEquivalences = null;
	private HashMap<String, HashMap<String, String>> categoryEquivalences = null;
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#getName()
	 */
	public String getName() {
		return "workingDC";
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(dataflow.datastructures.Cell)
	 */
	public void recalculate(Vector<String> children) throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {
		Grid grid = Grid.instance();
		
		//- Check and Store
		for( String cellName : children ) {
			if( cellName.equals("useInMSP") )
				useInMSP = ((BooleanCell)grid.getCell(cellName)).boolValue();
			else if( cellName.equals("lemmadDC") )
				lemmadDC = ((DataCubeCell)grid.getCell(cellName)).getCube();
			else if( cellName.equals("lemmaEquivalences") )
				lemmaEquivalences = ((LemmaEquivalencesCell)grid.getCell(cellName)).getValue();
			else if( cellName.equals("categoryEquivalences") )
				categoryEquivalences = ((CategoryEquivalencesCell)grid.getCell(cellName)).getValue();
		}
		
		//- Recalculate
		if( lemmadDC != null ) {
			if( categoryEquivalences != null )
				cube = lemmadDC.categoryEquivalences(categoryEquivalences, useInMSP, lemmaEquivalences);
			else
				cube = lemmadDC;
		}
	}

}
