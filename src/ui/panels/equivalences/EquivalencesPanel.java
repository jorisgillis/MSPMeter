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