package msp;

import javax.swing.UIManager;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * Main class of the project. It boots up the main GUI window.
 * @author Joris Gillis
 */
public class MSPMeter {
	
	/**
	 * Main function: booting it up!
	 * @param args
	 */
	public static void main( String[] args ) {
		// Setting up logging
		BasicConfigurator.configure();
		
		// Look 'n' Feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e) {
			System.err.println("Error while loading the System Look And Feel.");
			Logger logger = Logger.getLogger(MSPMeter.class);
			logger.error("Error while loading the System Look And Feel: "+ e.getMessage());
		}
		
		// Making a new meter
		new MSPMeter();
	}
	
	
	/**
	 * Construct a new MSPMeter
	 */
	public MSPMeter() {
		//- Initialize a new Grid
		new Architect();
	}
	
}
