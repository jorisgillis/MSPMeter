package ui;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Provides the basic warning mechanismes used in all the windows and panels.
 * @author Joris Gillis
 */
public class Window extends JFrame {
	
	private static final long serialVersionUID = -1030336475457432617L;

	public Window() throws HeadlessException {
		super();
	}
	
	public Window(GraphicsConfiguration gc) {
		super(gc);
	}
	
	public Window(String title) throws HeadlessException {
		super(title);
	}
	
	public Window(String title, GraphicsConfiguration gc) {
		super(title, gc);
	}
	
	/**
	 * Show a warning message.
	 * @param message	message to be shown
	 */
	public void showWarning(String message) {
		JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Show a warning message.
	 * @param e		exception containing the errormessage
	 */
	public void showWarning(Exception e) {
		JOptionPane.showMessageDialog(this, "A fault occurred: \n"+ e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
	}

}