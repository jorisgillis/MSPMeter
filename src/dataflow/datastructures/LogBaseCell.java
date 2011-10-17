package dataflow.datastructures;

import java.util.Vector;

import org.apache.log4j.Logger;

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;
import msp.defaults.Defaults;
import msp.defaults.NoValueException;

/**
 * Which base should MSP use for calculating the entropy?
 * @author Joris Gillis
 */
public class LogBaseCell extends IntegerCell {
	/** Base of the log used in computing the entropy. */
	protected int base;
	// Logger
	protected static Logger logger = Logger.getLogger(LogBaseCell.class);
	
	
	public LogBaseCell() {
		try {
			base = Defaults.instance().getInteger("logBase");
		} catch( NoValueException e ) {
			logger.error("No Value for \"logBase\".");
			base = 2;
		}
	}
	
	@Override
	public void recalculate(Vector<String> cells) throws DataFaultException,
			ImpossibleCalculationException, RestrictionViolation {}

	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#getName()
	 */
	@Override
	public String getName() {
		return "logBase";
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.IntegerCell#intValue()
	 */
	@Override
	public int intValue() {
		return base;
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.IntegerCell#setValue(int)
	 */
	@Override
	public void setValue(int value) {
		if( 1 < value )
			base = value;
	}
}
