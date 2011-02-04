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
