package ui.panels.equivalences;

import java.io.File;

import javax.swing.JFileChooser;

import ui.panels.Panel;

@SuppressWarnings("serial")
public abstract class EquivalencesPanel extends Panel {
	
	/** Panel for loading a file */
	protected FileLoadingPanel fileLoadingPanel;
	
	/**
	 * New equivalances panel
	 */
	public EquivalencesPanel() {
		super();
	}
	
	/**
	 * Ask the user for a file, but don't give him a suggestion.
	 * @return	the file selected by the user
	 */
	protected File askForFile() {
		return askForFile(null);
	}
	
	/**
	 * Ask the user for a file, the given string is a suggestion of a location where the file could be.
	 * If the suggestion <code>== null</code>, then no suggestion is made. 
	 * @param suggestion	the suggestion
	 * @return				the file selected by the user
	 */
	protected File askForFile(String suggestion) {
		JFileChooser fc = null;
		if( suggestion == null )
			fc = new JFileChooser();
		else
			fc = new JFileChooser( suggestion );
		
		if( fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION )
			return fc.getSelectedFile();
		
		return null;
	}

	/**
	 * Gives the file to the FileLoadingPanel.
	 */
	public void setFile(String filePath) {
		fileLoadingPanel.setFilePath(filePath);
	}
}