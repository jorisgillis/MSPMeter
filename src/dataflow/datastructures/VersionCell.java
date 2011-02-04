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

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;
import msp.defaults.Defaults;
import msp.defaults.NoValueException;

/**
 * The cell containing all version information.
 * @author Joris Gillis
 */
public class VersionCell extends DefaultCell {
	
	//- Values
	private boolean weighting;
	private boolean entropy;
	private int mode = -1;
	private int subSampleMode = -1;
	private int subSampleSize = -1;
	private int numberOfSamplesMode = -1;
	private double numberOfSamples = -1.0;
	
	
	/**
	 * Construct the cell.
	 */
	public VersionCell() {
		try {
			weighting = Defaults.instance().getBoolean("weighting");
			entropy = Defaults.instance().getBoolean("entropy");
		} catch( NoValueException e ) {}
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#getName()
	 */
	public String getName() {
		return "version";
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(dataflow.datastructures.Cell)
	 */
	public void recalculate(Vector<String> children) 
		throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {}
	
	/**
	 * Sets the value of this cell and triggers a flow. 
	 */
	public void setValue( boolean weighting, boolean entropy, int mode, int subSampleMode, int subSampleSize,
			int numberOfSamplesMode, double numberOfSamples ) throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {
		if( this.weighting != weighting ||
				this.entropy != entropy ||
				this.mode != mode ||
				this.subSampleMode != subSampleMode || 
				this.subSampleSize != subSampleSize ||
				this.numberOfSamplesMode != numberOfSamplesMode || 
				this.numberOfSamples != numberOfSamples ||
				mode > 1 ) {
			this.weighting = weighting;
			this.entropy = entropy;
			this.mode = mode;
			this.subSampleMode = subSampleMode;
			this.subSampleSize = subSampleSize;
			this.numberOfSamplesMode = numberOfSamplesMode;
			this.numberOfSamples = numberOfSamples;
		}
	}
	
	/**
	 * @return the weighting
	 */
	public boolean isWeighting() {
		return weighting;
	}
	
	/**
	 * @return the entropy
	 */
	public boolean isEntropy() {
		return entropy;
	}
	
	/**
	 * @return the mode
	 */
	public int getMode() {
		return mode;
	}
	
	/**
	 * @return the subSampleMode
	 */
	public int getSubSampleMode() {
		return subSampleMode;
	}
	
	/**
	 * @return the subSampleSize
	 */
	public int getSubSampleSize() {
		return subSampleSize;
	}
	
	/**
	 * @return the numberOfSamplesMode
	 */
	public int getNumberOfSamplesMode() {
		return numberOfSamplesMode;
	}
	
	/**
	 * @return the numberOfSamples
	 */
	public double getNumberOfSamples() {
		return numberOfSamples;
	}
}
