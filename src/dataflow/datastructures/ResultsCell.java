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

import java.util.List;
import java.util.Vector;

import msp.RestrictionViolation;
import msp.data.DataCube;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;
import msp.data.MSPResult;
import msp.data.MSPSpan;
import dataflow.Grid;

/**
 * Cell containing the results.
 * @author Joris Gillis
 */
public class ResultsCell extends DefaultCell {
	
	/* Results of the calculation */
	private MSPSpan[] results = null;
	private List<List<Double>> samples = null;
	
	/* Objects needed for the calculation */
	private DataCube workingDC = null;
	private DataCube cumulatedDC = null;
	private boolean weighting;
	private boolean entropy;
	private int mode = -1;
	private int subSampleMode = -1;
	private int subSampleSize = -1;
	private int numberOfSamplesMode = -1;
	private double numberOfSamples = -1.0;
	
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
			}
		}
		
		//- Recalculate
		if( mode == 4 && subSampleMode == 0 )
			throw new RestrictionViolation("The combination \"Resample & Cumulate\" " +
					"with \"One Span\" is impossible!");
		
		if( workingDC != null && cumulatedDC != null && mode > -1 && 
				subSampleMode > -1 && subSampleSize > -1 && 
				numberOfSamplesMode > -1 && numberOfSamples > -0.9 ) {
			// Temporary storage
			MSPResult pair = null;
			
			switch( mode ) {
			case 0:
				pair = workingDC.MSP( weighting, entropy );
				break;
			case 1:
				pair = cumulatedDC.MSP( weighting, entropy );
				break;
			case 2:
				pair = workingDC.resampleMSP( weighting, entropy, 
						subSampleMode, subSampleSize, numberOfSamplesMode, numberOfSamples );
				break;
			case 3:
				pair = cumulatedDC.resampleMSP( weighting, entropy, 
						subSampleMode, subSampleSize, numberOfSamplesMode, numberOfSamples );
				break;
			case 4:
				pair = workingDC.resampleCumulateMSP( weighting, entropy, 
						subSampleSize, numberOfSamplesMode, numberOfSamples );
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
	public MSPSpan[] getValue() {
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
