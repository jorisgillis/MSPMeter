package io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

/**
 * Base for the exporters.
 * @author Joris Gillis
 */
public abstract class Exporter {
	
	/** GUI parent: is used to show warnings */
	protected JFrame parent;
	
	// Everywhere the same
	protected Logger logger = Logger.getLogger(Exporter.class);
	
	
	/**
	 * Construct a new basic Exporter.
	 * @param parent	GUI parent
	 */
	public Exporter( JFrame parent ) {
		this.parent = parent;
	}
	
	/**
	 * Write out a piece of text, the user is requested to choose a file.
	 * @param text	the text to be written out.
	 */
	protected void writeOut(String text) {
		BufferedWriter writer = null;
		try {
			// request a filename
			File file = requestFileName();
			
			
			if( file != null ) {
				// open the given file, if one is provided
				logger.debug("Exporting table: " + file.getName() + ".");
				if( file != null ) {
					writer = new BufferedWriter(new FileWriter(file));
					writer.write(text);
					writer.close();
				}
			}
		} catch( FileNotFoundException e ) {
			showWarning(e);
		} catch( IOException e ) {
			showWarning(e);
		} finally {
			if( writer != null )
				try {
					writer.close();
				} catch( IOException e ) {}
		}
	}

	/**
	 * Request the user to enter a file.
	 * @return	file provided by the user
	 */
	protected File requestFileName() {
		File file = null;
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) 
			file = fc.getSelectedFile();
		return file;
	}
	
	
	/**
	 * Show a warning, based on the given exception
	 * @param e	exception
	 */
	public void showWarning( Exception e ) {
		showWarning(e.getMessage());
	}
	
	/**
	 * Show a warning message.
	 * @param message	message to be shown
	 */
	public void showWarning( String message ) {
		JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
	}
	
}
