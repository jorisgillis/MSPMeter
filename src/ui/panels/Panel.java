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


package ui.panels;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Base class for panels.
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public abstract class Panel extends JPanel {
	
	/**
	 * Construct a new panel
	 */
	public Panel() {
		super();
	}
	
	
	//===========================================================================
	// Continuation
	//===========================================================================
	/**
	 * Let the panel save its values to the controller.
	 */
	public abstract void apply();
	
	
	//===========================================================================
	// Standard GUI functionality
	//===========================================================================
	/**
	 * Show a warning message.
	 * @param message	message to be shown
	 */
	public void showWarning( String message ) {
		JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
	}
}
