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


package ui.panels.file;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import msp.defaults.Defaults;

import org.apache.log4j.Logger;

import ui.panels.Panel;
import dataflow.Grid;
import dataflow.datastructures.Cell;
import dataflow.datastructures.FirstSeparatorCell;
import dataflow.datastructures.SecondSeparatorCell;
import dataflow.datastructures.TerminatorCell;

/**
 * This panel let's the user construct a regular expression, expressing the 
 * structure of a line.
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class LineParsingPanel extends Panel {
	
	// The interface elements
	protected JTextField firstSeparator;
	protected JTextField secondSeparator;
	protected JTextField terminator;
	protected LiveLineParsingPanel preview;
	
	// Internal use
	protected String[] metaChars = 
			new String[]{"\\","(","[","{","^","$","|","-","]","}",")","?","*","+","."};
	
	// The cells
	protected FirstSeparatorCell firstSeparatorCell;
	protected SecondSeparatorCell secondSeparatorCell;
	protected TerminatorCell terminatorCell;
	
	// The previous values, used to track changes
	protected String prevFirstSep;
	protected String prevSecondSep;
	protected String prevTerminator;
	
	// The logger
	private static Logger logger = Logger.getLogger(LineParsingPanel.class);
	
	
	/**
	 * Construct a new LineParsingPanel
	 */
	public LineParsingPanel() {
		//-- The cells
		firstSeparatorCell = new FirstSeparatorCell();
		secondSeparatorCell = new SecondSeparatorCell();
		terminatorCell = new TerminatorCell();
		
		Grid.instance().addCell(firstSeparatorCell);
		Grid.instance().addCell(secondSeparatorCell);
		Grid.instance().addCell(terminatorCell);
		
		//-- Building the interface
		setLayout(new BorderLayout());
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		// labels
		JLabel separator1 = new JLabel("Token indicating lemma: ");
		JLabel separator2 = new JLabel("Token between lemma and category: ");
		JLabel separator3 = new JLabel("Token indicating end of category: ");
		
		// textfields and defaults
		try {
			Defaults d = Defaults.instance();
			firstSeparator = new JTextField(d.getString("firstSeparator"), 15);
			secondSeparator = new JTextField(d.getString("secondSeparator"), 15);
			terminator = new JTextField(d.getString("terminator"), 15);
			
			prevFirstSep = escapeString(firstSeparator.getText());
			prevSecondSep = escapeString(secondSeparator.getText());
			prevTerminator = escapeString(terminator.getText());
		} catch( Exception e ) {
			logger.error("Error on defaulting: "+ e.getMessage());
			showWarning("Defaults not loaded.");
		}
		
		// adding it to mainPanel
		mainPanel.add( separator1, c );
		c.gridx = 1;
		mainPanel.add( firstSeparator, c );
		c.gridx = 0; c.gridy = 1;
		mainPanel.add( separator2, c );
		c.gridx = 1;
		mainPanel.add( secondSeparator, c );
		c.gridx = 0; c.gridy = 2;
		mainPanel.add( separator3, c );
		c.gridx = 1;
		mainPanel.add( terminator, c );
		
		// adding it to the panel
		add( mainPanel, BorderLayout.NORTH );
		
		// Preview
		preview = new LiveLineParsingPanel();
		Grid.instance().addHotCell(preview);
		add(preview, BorderLayout.CENTER);
	}
	
	
	
	//===========================================================================
	// Panel functionality
	//===========================================================================
	/*
	 * (non-Javadoc)
	 * @see ui.panels.Panel#continu()
	 */
	@Override
	public void apply() {
		// getting in from the textfield
		String firstSep = firstSeparator.getText();
		String secondSep = secondSeparator.getText();
		String terminatorTokens = terminator.getText();
		
		// escaping: adding backslashes
		firstSep = escapeString(firstSep);
		secondSep = escapeString(secondSep);
		terminatorTokens = escapeString(terminatorTokens);
		
		// sending it through
		try {
			if( !(prevFirstSep.equals(firstSep) && prevSecondSep.equals(secondSep)) ) {
				// Setting the value
				firstSeparatorCell.setValue(firstSep);
				secondSeparatorCell.setValue(secondSep);
				terminatorCell.setValue(terminatorTokens);
				
				// Storing the value
				prevFirstSep = firstSep;
				prevSecondSep = secondSep;
				prevTerminator = terminatorTokens;
				
				Grid.instance().cellChanged(new Cell[]{
						firstSeparatorCell, 
						secondSeparatorCell,
						terminatorCell});
			}
		} catch( Exception e ) {
			showWarning(e.getMessage());
		}
	}
	
	/**
	 * Escapes the special characters in the given string. 
	 * @param s	string
	 * @return	string with special characters escaped
	 */
	private String escapeString(String s) {
		for( int i = 0; i < metaChars.length; i++ ) {
			String metaChar = metaChars[i];
			s = s.replace(metaChar, "\\" + metaChar);
		}
		
		return s;
	}
	
	/**
	 * Sets the first separator.
	 */
	public void setFirstSeparator( String firstSeparator ) {
		this.firstSeparator.setText(firstSeparator);
	}
	
	/**
	 * Sets the second separator.
	 */
	public void setSecondSeparator( String secondSeparator ) {
		this.secondSeparator.setText(secondSeparator);
	}
	
	/**
	 * Sets the terminator.
	 * @param terminator	new terminator tokens
	 */
	public void setTerminator( String terminator ) {
		this.terminator.setText(terminator);
	}
}
