/*
 * Copyright 2010 MSPMeter
 *
 * Licensed under the EUPL, Version 1.1 or � as soon they
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

import java.util.List;
import java.util.Vector;

import msp.RestrictionViolation;
import msp.data.DataCubeList;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;
import msp.data.MSPResult;
import msp.data.MSPTriple;
import dataflow.Grid;

/**
 * Cell containing the results.
 * @author Joris Gillis
 */
public class ResultsCell extends DefaultCell {
	
	/* Different modes of computation */
	public final int MODE_BASIC 			= 0;
	public final int MODE_CUMULATE			= 1;
	public final int MODE_RESAMPLE			= 2;
	public final int MODE_CUMULATE_RESAMPLE	= 3;
	public final int MODE_RESAMPLE_CUMULATE	= 4;
	
	
	/* Results of the calculation */
	private MSPTriple[] results = null;
	private List<List<Double>> samples = null;
	
	/* Objects needed for the calculation */
	private DataCubeList workingDC = null;
	private DataCubeList cumulatedDC = null;
	private boolean weighting;
	private boolean entropy;
	private int mode = -1;
	private int subSampleMode = -1;
	private int subSampleSize = -1;
	private int numberOfSamplesMode = -1;
	private double numberOfSamples = -1.0;
	private int logBase = 2;
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#getName()
	 */
	public String getName() {
		return "results";
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(dataflow.datastructures.Cell)
	 */
	public void recalculate(Vector<String> children) throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {
		Grid grid = Grid.instance();
		
		//- Receiving
		for( String cellName : children ) {
			if( cellName.equals("workingDC") )
				workingDC = ((WorkingDataCubeCell)grid.getCell(cellName)).getCube();
			else if( cellName.equals("cumulatedDC") )
				cumulatedDC = ((CumulatedDataCubeCell)grid.getCell(cellName)).getCube();
			else if( cellName.equals("version") ) {
				VersionCell cell = (VersionCell)grid.getCell(cellName);
				weighting = cell.isWeighting();
				entropy = cell.isEntropy();
				mode = cell.getMode();
				subSampleMode = cell.getSubSampleMode();
				subSampleSize = cell.getSubSampleSize();
				numberOfSamplesMode = cell.getNumberOfSamplesMode();
				numberOfSamples = cell.getNumberOfSamples();
			} else if( cellName.equals("logBase") )
				logBase = ((LogBaseCell)grid.getCell(cellName)).intValue();
		}
		
		//- Recalculate
		if( mode == 4 && subSampleMode == 0 )
			throw new RestrictionViolation("The combination \"Resample & Cumulate\" " +
					"with \"One Span\" is impossible!");
		
		if( workingDC != null && cumulatedDC != null && mode > -1 && 
				subSampleMode > -1 && subSampleSize > -1 && 
				numberOfSamplesMode > -1 && numberOfSamples > 0 ) {
			// Temporary storage
			MSPResult pair = null;
			
			// Setting the base
			workingDC.setLogBase(logBase);
			cumulatedDC.setLogBase(logBase);
			
			switch( mode ) {
			case MODE_BASIC:
				pair = workingDC.MSP( weighting, entropy );
				break;
			case MODE_CUMULATE:
				pair = cumulatedDC.MSP( weighting, entropy );
				break;
			case MODE_RESAMPLE:
				pair = workingDC.resampleMSP(
							weighting, 
							entropy, 
							subSampleMode, 
							subSampleSize, 
							numberOfSamplesMode, 
							numberOfSamples );
				break;
			case MODE_CUMULATE_RESAMPLE:
				pair = cumulatedDC.resampleMSP( 
							weighting, 
							entropy, 
							subSampleMode, 
							subSampleSize, 
							numberOfSamplesMode, 
							numberOfSamples );
				break;
			case MODE_RESAMPLE_CUMULATE:
				pair = workingDC.resampleCumulateMSP( 
							weighting, 
							entropy, 
							subSampleSize, 
							numberOfSamplesMode, 
							numberOfSamples );
				break;
			}
			
			// Extracting the parts
			results = pair.getResults();
			samples = pair.getSampleValues();
		}
	}
	
	/**
	 * Gets the value of this cell. 
	 */
	public MSPTriple[] getValue() {
		return results;
	}
	
	/**
	 * Returns the list of MSP values of the samples. The list is empty
	 * if no sampling is applied. 
	 * @return	list of sample values
	 */
	public List<List<Double>> getSampleValues() {
		return samples;
	}
}
