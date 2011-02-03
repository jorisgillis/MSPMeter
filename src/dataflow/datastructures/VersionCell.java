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
