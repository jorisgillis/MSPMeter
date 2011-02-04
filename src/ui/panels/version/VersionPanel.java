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


package ui.panels.version;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import msp.RestrictionViolation;
import msp.data.DataCube;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;
import msp.defaults.Defaults;

import org.apache.log4j.Logger;

import ui.panels.Panel;
import dataflow.Grid;
import dataflow.datastructures.Cell;
import dataflow.datastructures.DataCubeCell;
import dataflow.datastructures.VersionCell;

/**
 * Panel that let's the user specify several options of the MSP calculation
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class VersionPanel extends Panel 
	implements ItemListener, Cell, ChangeListener, ActionListener {
	
	// TODO
	/*
	 * Geen controle meer uitoefenen op de sample grootte. Als de sample grootte groter is dan 
	 * de grootte van een span, dan wordt er geen waarde berekent voor die span. 
	 * Daarvoor gaan we een soort empty-span nodig hebben. 
	 */
	
	//- Components
	protected JCheckBox weightC, entropyC;
	protected JComboBox modeCB, numberOfSamplesModeCB, subSampleModeCB;
	protected JTextField subSampleSizeText, numberOfSamplesText;
	
	//- sample size related
	protected int maxSampleSizeAllSpan = 1;
	protected int maxSampleSizeOneSpan = 1;
	
	//- number of samples
	protected int maxSampleSizeOneSpanCumulated = 1;
	protected int defaultNumberOfSamples = 1;
	
	protected int maxSampleSizeAllSpanCumulated = 1;
	protected int defaultNumberOfSamplesCumulated = 1;
	
	//- The cell corresponding to the version values
	protected VersionCell versionCell;
	
	/** Did something change? */
	protected boolean changed = false;
	
	//- Everywhere the same
	protected static Logger logger = Logger.getLogger(VersionPanel.class);
	
	/**
	 * Construct a new VersionPanel
	 */
	public VersionPanel() {
		//- Cell
		versionCell = new VersionCell();
		Grid.instance().addCell(versionCell);
		Grid.instance().addHotCell(this);
		
		//-- build interface
		//- Components
		JLabel weighting		= new JLabel("Weighting? ");
		JLabel entropy			= new JLabel("Entropy? ");
		JLabel operationMode	= new JLabel("Operation Mode: ");
		JLabel sampleMode		= new JLabel("Sample Mode: ");
		JLabel subSampleMode	= new JLabel("Number of Samples: ");
		JLabel sampleSize		= new JLabel("Subsample Size: ");
		
		// combobox content
		String[] modeStrings = new String[]{"Basic", "Cumulate", "Resample", "Cumulate & Resample", "Resample & Cumulate"};
		String[] numberOfSubSamplesStrings = new String[]{"Sampling Factor", "Fixed Value"};
		String[] sampleModeStrings = new String[]{"One Span", "All Spans"};
		
		// elements
		try {
			// setting the controllers
			Defaults d = Defaults.instance();
			weightC	 				= new JCheckBox();					weightC.setSelected(d.getBoolean("weighting"));
			entropyC 				= new JCheckBox();					entropyC.setSelected(d.getBoolean("entropy"));
			modeCB					= new JComboBox(modeStrings);		modeCB.setSelectedIndex(d.getInteger("mode"));
			subSampleSizeText		= new JTextField(6);
			subSampleModeCB			= new JComboBox(sampleModeStrings);	subSampleModeCB.setSelectedIndex(0);
			numberOfSamplesModeCB	= new JComboBox(numberOfSubSamplesStrings);	numberOfSamplesModeCB.setSelectedIndex(0);
			numberOfSamplesText		= new JTextField(6);
			
			// Setting values
			subSampleSizeText.setText("" + 1);
			numberOfSamplesText.setText("" + 1);
			
			// adding the listeners to guide the changes
			modeCB.addItemListener(this);
			numberOfSamplesModeCB.addItemListener(this);
			subSampleModeCB.addItemListener(this);
			
			// adding changelisteners to capture change
			weightC.addChangeListener(this);
			entropyC.addChangeListener(this);
			subSampleSizeText.addActionListener(this);
			numberOfSamplesText.addActionListener(this);
			
		} catch( Exception e ) {
			logger.error("Defaults not loaded. "+ e.getMessage());
			showWarning("Defaults not loaded.");
		}
		
		// disable the sampling controllers, we are in basic mode!
		disableSamplingControllers();
		
		//- Layout
		// Panel for the numberOfSamples
		JPanel numberPanel = new JPanel();
		numberPanel.setLayout(new BorderLayout());
		numberPanel.add(numberOfSamplesModeCB, BorderLayout.EAST);
		numberPanel.add(numberOfSamplesText, BorderLayout.WEST);
		
		// Panel for everything
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		
		mainPanel.add(weighting, c);
		c.gridx = 1;
		mainPanel.add(weightC, c);
		
		c.gridx = 0; c.gridy = 1;
		mainPanel.add(entropy, c);
		c.gridx = 1;
		mainPanel.add(entropyC, c);
		
		c.gridx = 0; c.gridy = 2;
		mainPanel.add(operationMode, c);
		c.gridx = 1;
		mainPanel.add(modeCB, c);
		
		c.gridx = 0; c.gridy = 3;
		mainPanel.add(sampleMode, c);
		c.gridx = 1;
		mainPanel.add(subSampleModeCB, c);
		
		
		c.gridx = 0; c.gridy = 4;
		mainPanel.add(subSampleMode, c);
		c.gridx = 1;
		mainPanel.add(numberPanel, c);
		
		c.gridx = 0; c.gridy = 5;
		mainPanel.add(sampleSize, c);
		c.gridx = 1;
		mainPanel.add(subSampleSizeText, c);
		
		this.add(mainPanel);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see ui.panels.Panel#continu()
	 */
	public void apply() {
		boolean weighting		= weightC.isSelected();
		boolean entropy			= entropyC.isSelected();
		int mode				= modeCB.getSelectedIndex();
		int subSampleMode		= subSampleModeCB.getSelectedIndex();
		int numberOfSamplesMode	= numberOfSamplesModeCB.getSelectedIndex();
		int subSampleSize		= Integer.parseInt(subSampleSizeText.getText());
		double numberOfSamples	= Integer.parseInt(numberOfSamplesText.getText());
		
		try {
			versionCell.setValue(weighting, 
									entropy, 
									mode, 
									subSampleMode, 
									subSampleSize, 
									numberOfSamplesMode, 
									numberOfSamples);
			Grid.instance().cellChanged( new Cell[]{versionCell} );
			changed = false;
		} catch( Exception e ) {
			showWarning(e.getMessage());
		}
	}
	
	
	/**
	 * Disables all the controllers related to sampling.
	 */
	private void disableSamplingControllers() {
		subSampleSizeText.setEnabled(false);
		numberOfSamplesText.setEnabled(false);
		numberOfSamplesModeCB.setEnabled(false);
		subSampleModeCB.setEnabled(false);
	}
	
	/**
	 * Enables all the controllers related to sampling.
	 */
	private void enableSamplingControllers() {
		subSampleSizeText.setEnabled(true);
		numberOfSamplesText.setEnabled(true);
		numberOfSamplesModeCB.setEnabled(true);
		subSampleModeCB.setEnabled(true);
	}
	
	
	
	//===========================================================================
	// Setters
	//===========================================================================
	/**
	 * Sets the weighting checkbox
	 * @param weighting	weighting?
	 */
	public void setWeighting( boolean weighting ) {
		weightC.setSelected(weighting);
	}
	
	/**
	 * Sets the entropy checkbox
	 * @param entropy	entropy?
	 */
	public void setEntropy( boolean entropy ) {
		entropyC.setSelected(entropy);
	}
	
	/**
	 * Sets the operation mode combobox
	 * @param mode	mode
	 */
	public void setMode( int mode ) {
		modeCB.setSelectedIndex(mode);
	}
	
	/**
	 * Sets the subSampleSizeMode widget
	 * @param subSampleSizeMode	subsample size
	 */
	public void setSubSampleSizeMode( int subSampleSizeMode ) {
		subSampleModeCB.setSelectedIndex(subSampleSizeMode);
	}
	
	/**
	 * Sets the subsample size widgets
	 * @param subSampleSize	subsample size
	 */
	public void setSubSampleSize( int subSampleSize ) {
		subSampleSizeText.setText(""+subSampleSize);
	}
	
	/**
	 * Sets the number of samples mode widget
	 * @param numberOfSamplesMode	number of samples mode
	 */
	public void setNumberOfSamplesMode( int numberOfSamplesMode ) {
		numberOfSamplesModeCB.setSelectedIndex(numberOfSamplesMode);
	}
	
	/**
	 * Sets the number of samples widget
	 * @param numberOfSamples	number of samples
	 */
	public void setNumberOfSamples( double numberOfSamples ) {
		numberOfSamplesText.setText(""+numberOfSamples);
	}
	
	
	//===========================================================================
	// Sample Size
	//===========================================================================
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(dataflow.datastructures.Cell)
	 */
	public void recalculate( Vector<String> children ) 
		throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {
		
		Grid grid = Grid.instance();
		
		for( String cellName : children ) {
			if( cellName.equals("workingDC") ) {
				// getting the values out the cube
				DataCube cube = ((DataCubeCell)grid.getCell("workingDC")).getCube();
				maxSampleSizeOneSpan = cube.maxSampleSizeOneSpan();
				maxSampleSizeAllSpan = cube.maxSampleSizeAllSpan();
//				int defaultNumberOfSamples = cube.defaultNumberOfTokens(
//						Integer.parseInt(subSampleSizeText.getText()));
				
				// setting and resetting the sliders
//				if( subSampleModeCB.getSelectedIndex() == 0 && modeCB.getSelectedIndex() != 3 )
//					subSampleSizeText.setMaximum(maxSampleSizeOneSpan);
//				else if( subSampleModeCB.getSelectedIndex() == 1 && modeCB.getSelectedIndex() != 3 )
//					subSampleSizeText.setMaximum(maxSampleSizeAllSpan);
				
				// if there is a change in the default number of samples
//				if( this.defaultNumberOfSamples != defaultNumberOfSamples ) {
//					// if in fixed mode, set it to the default number of subsamples
//					// the program would take
//					if( numberOfSamplesModeCB.getSelectedIndex() == 1 &&
//							(int)numberOfSamplesText.getValue() == this.defaultNumberOfSamples )
//						numberOfSamplesText.setValue(defaultNumberOfSamples);
//					
//					this.defaultNumberOfSamples = defaultNumberOfSamples;
//				}
			} else if( cellName.equals("cumulatedDC") ) {
				// getting the values out of the cube
				DataCube cube = ((DataCubeCell)grid.getCell("cumulatedDC")).getCube();
				maxSampleSizeOneSpanCumulated = cube.maxSampleSizeOneSpan();
				maxSampleSizeAllSpanCumulated = cube.maxSampleSizeAllSpan();
//				int defaultNumberOfSamplesCumulated = cube.defaultNumberOfTokens(
//						Integer.parseInt(subSampleSizeText.getText()));
				
				// setting and resetting the sliders
//				if( subSampleModeCB.getSelectedIndex() == 0 && 
//						modeCB.getSelectedIndex() == 3 )
//					subSampleSizeText.setMaximum(maxSampleSizeOneSpanCumulated);
//				else if( subSampleModeCB.getSelectedIndex() == 1 && 
//						modeCB.getSelectedIndex() == 3 )
//					subSampleSizeText.setMaximum(maxSampleSizeAllSpanCumulated);
				
				// if there is a change in the default number of samples
//				if( this.defaultNumberOfSamplesCumulated != defaultNumberOfSamplesCumulated ) {
//					// if in fixed mode, set it to the default number of subsamples
//					// the program would take
//					if( numberOfSamplesModeCB.getSelectedIndex() == 1 && 
//							modeCB.getSelectedIndex() == 3 &&
//							(int)numberOfSamplesText.getValue() == this.defaultNumberOfSamplesCumulated )
//						numberOfSamplesText.setValue(defaultNumberOfSamplesCumulated);
//					
//					this.defaultNumberOfSamplesCumulated = defaultNumberOfSamplesCumulated;
//				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.Component#getName()
	 */
	@Override
	public String getName() {
		return "versionPanel";
	}
	
	//===========================================================================
	// State changes
	//===========================================================================
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		changed = true;
		
		if( e.getSource().equals(modeCB) ) {
			// if the order is changed
			if( modeCB.getSelectedIndex() >= 2 ) {
				// enabling
				enableSamplingControllers();
				
				// setting the maximum sizes of the subsamples size
//				if( modeCB.getSelectedIndex() != 3 ) {
//					if( subSampleModeCB.getSelectedIndex() == 0 )
//						subSampleSizeText.setMaximum(maxSampleSizeOneSpan);
//					else if( subSampleModeCB.getSelectedIndex() == 1 )
//						subSampleSizeText.setMaximum(maxSampleSizeAllSpan);
//				} else {
//					if( subSampleModeCB.getSelectedIndex() == 0 )
//						subSampleSizeText.setMaximum(maxSampleSizeOneSpanCumulated);
//					else if( subSampleModeCB.getSelectedIndex() == 1 )
//						subSampleSizeText.setMaximum(maxSampleSizeAllSpanCumulated);
//				}
				
				// setting the defaults for the sampling factor
//				if( numberOfSamplesModeCB.getSelectedIndex() == 0 ) {
//					numberOfSamplesText.setScale(10);
//					numberOfSamplesText.setDefault(10);
//				} else {
//					numberOfSamplesText.setScale(1);
//					if( modeCB.getSelectedIndex() != 3 )
//						numberOfSamplesText.setDefault(defaultNumberOfSamples);
//					else
//						numberOfSamplesText.setDefault(defaultNumberOfSamplesCumulated);
//				}
				
			} else
				disableSamplingControllers();
		}
		else if( e.getSource().equals(numberOfSamplesModeCB) ) {
			// another number of subsamples is chosen
//			if( numberOfSamplesModeCB.getSelectedIndex() == 0 ) {
//				// factor: B = B x factor
//				numberOfSamplesText.setScale(10.0);
//				numberOfSamplesText.setDefault(10);
//			} else if( numberOfSamplesModeCB.getSelectedIndex() == 1 ) {
//				numberOfSamplesText.setScale(1.0);
//				if( modeCB.getSelectedIndex() != 3 )
//					numberOfSamplesText.setDefault(defaultNumberOfSamples);
//				else
//					numberOfSamplesText.setDefault(defaultNumberOfSamplesCumulated);
//			}
		}
		else if( e.getSource().equals(subSampleModeCB) ) {
			// if the sampling mode has been changed
//			if( subSampleModeCB.getSelectedIndex() == 0 )
//				if( modeCB.getSelectedIndex() != 3 )
//					subSampleSizeText.setMaximum(maxSampleSizeOneSpan);
//				else
//					subSampleSizeText.setMaximum(maxSampleSizeOneSpanCumulated);
//			else
//				if( modeCB.getSelectedIndex() != 3 )
//					subSampleSizeText.setMaximum(maxSampleSizeAllSpan);
//				else
//					subSampleSizeText.setMaximum(maxSampleSizeAllSpanCumulated);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		changed = true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		// An action on the samplesize and number of samples field has occurred
		logger.info("Action Performed!");
		logger.info( e.getActionCommand() );
	}
}
