package ui.panels.equivalences;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import msp.defaults.Defaults;

/**
 * A panel that allows the user to specify a file and load/import that file 
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class FileLoadingPanel extends JPanel implements ActionListener {
	
	/** The one that requests a file */
	protected FileRequestor requestor = null;
	
	/** Path to the file */
	protected JTextField filePath;
	/** Where were we last time? */
	protected String workingDirectory = null;
	
	/**
	 * Construct a new FileLoadingPanel
	 * @param buttonLabel	the label that will be printed on the action button
	 */
	public FileLoadingPanel( FileRequestor requestor, String buttonLabel ) {
		// Defaults
		try {
			workingDirectory = Defaults.instance().getString("working directory");
		} catch( Exception e ) {
			workingDirectory = null;
		}
		
		// requestor
		this.requestor = requestor;
		
		// setting up the gui
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Choose an input file"));
		
		// button
		JButton action = new JButton(buttonLabel);
		action.addActionListener(this);
		action.setActionCommand("load");
		
		// textfield
		filePath = new JTextField(20);
		
		this.add(filePath);
		this.add(action);
	}
	
	/**
	 * Get the file path that was chosen.
	 * @return	file path
	 */
	public String getFilePath() {
		return filePath.getText();
	}
	
	/**
	 * Sets the file path. Override of asking the user.
	 * @param filePath	file path
	 */
	public void setFilePath( String filePath ) {
		if( filePath != null && !filePath.equals("null") ) {
			this.filePath.setText(filePath);
			requestor.receiveFile(new File(filePath));
		}
	}
	
	/**
	 * Resets the FileLoading.
	 */
	public void reset() {
		filePath.setText("");
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if( e.getActionCommand().equals("load") ) {
			if( requestor != null ) {
				// deliver the file to the requestor
				JFileChooser fc;
				if( workingDirectory != null )
					fc = new JFileChooser(workingDirectory);
				else
					fc = new JFileChooser();
				if( fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ) {
					// file selected
					filePath.setText(fc.getSelectedFile().getAbsolutePath());
					requestor.receiveFile(fc.getSelectedFile());
				}
			}
		}
	}
	
}
