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
	public void recalculate( Vector<String> children ) throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {
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
