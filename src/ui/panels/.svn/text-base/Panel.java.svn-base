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
