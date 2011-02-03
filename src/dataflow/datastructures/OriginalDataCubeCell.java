package dataflow.datastructures;

import java.util.Vector;

import dataflow.Grid;

import msp.RestrictionViolation;
import msp.data.DataCube;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;
import ui.panels.file.FileRow;

/**
 * The original DataCube. The one derived directly from the files.
 * @author Joris Gillis
 */
public class OriginalDataCubeCell extends DataCubeCell {
	
	/** The files */
	private Vector<FileRow> files = null;
	/** Tokens just before lemma */
	private String firstSeparator = null;
	/** Tokens between lemma and category */
	private String secondSeparator = null;
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#getName()
	 */
	public String getName() {
		return "originalDC";
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(dataflow.datastructures.Cell)
	 */
	public void recalculate( Vector<String> children ) 
		throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {
		/*
		 * 1. Check which cell has been changed.
		 * 2. Store that data!
		 * 3. Check whether we can recalculate
		 * 4. Recalculate
		 */
		
		// Changes? Store!
		Grid grid = Grid.instance();
		for( String cellName : children ) {
			if( cellName.equals("files") )
				files = ((FilesCell)grid.getCell(cellName)).getValue();
			else if( cellName.equals("firstSeparator") )
				firstSeparator = ((FirstSeparatorCell)grid.getCell(cellName)).getValue();
			else if( cellName.equals("secondSeparator") )
				secondSeparator = ((SecondSeparatorCell)grid.getCell(cellName)).getValue();
		}
		
		// Recalculate?
		if( files != null && firstSeparator != null && secondSeparator != null ) {
			// New DataCube
			cube = new DataCube();
			cube.fillCube(files, firstSeparator, secondSeparator);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.DataCubeCell#getCube()
	 */
	@Override
	public DataCube getCube() {
		try {
			return cube.copy();
		} catch( CloneNotSupportedException e ) {
		}
		return null;
	}

}
