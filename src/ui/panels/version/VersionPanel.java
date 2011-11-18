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
import dataflow.datastructures.LogBaseCell;
import dataflow.datastructures.VersionCell;

/**
 * Panel that let's the user specify several options of the MSP calculation
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class VersionPanel extends Panel 
	implements ItemListener, Cell, ChangeListener {
	
	//- Components
	protected JCheckBox weightC, entropyC;
	protected JComboBox modeCB, numberOfSamplesModeCB, subSampleModeCB;
	protected JTextField subSampleSizeText, numberOfSamplesText, logBaseText;
	
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
	protected LogBaseCell logBaseCell;
	
	/**
	 * Construct a new VersionPanel
	 */
	public VersionPanel() {
		//- Cell
		versionCell = new VersionCell();
		Grid.instance().addCell(versionCell);
		Grid.instance().addHotCell(this);
		
		logBaseCell = new LogBaseCell();
		Grid.instance().addCell(logBaseCell);
		
		//-- build interface
		//- Components
		JLabel weighting		= new JLabel("Weighting? ");
		JLabel entropy			= new JLabel("Entropy? ");
		JLabel operationMode	= new JLabel("Operation Mode: ");
		JLabel sampleMode		= new JLabel("Sample Scope: ");
		JLabel subSampleMode	= new JLabel("Sample Mode: ");
		JLabel sampleSize		= new JLabel("Subsample Size: ");
		JLabel logBase			= new JLabel("Log Base");
		
		// combobox content
		String[] modeStrings = new String[]{"Basic", 
											"Cumulate", 
											"Resample", 
											"Cumulate & Resample", 
											"Resample & Cumulate"};
		String[] numberOfSubSamplesStrings = new String[]{	"Sampling Factor", 
															"Fixed Value"};
		String[] sampleModeStrings = new String[]{"One Dataset", "All Datasets"};
		
		// elements
		try {
			// setting the controllers
			Defaults d = Defaults.instance();
			weightC	 				= new JCheckBox();					
			weightC.setSelected(d.getBoolean("weighting"));
			entropyC 				= new JCheckBox();					
			entropyC.setSelected(d.getBoolean("entropy"));
			modeCB					= new JComboBox(modeStrings);
			modeCB.setSelectedIndex(d.getInteger("mode"));
			subSampleSizeText		= new JTextField(6);
			subSampleModeCB			= new JComboBox(sampleModeStrings);	
			subSampleModeCB.setSelectedIndex(0);
			numberOfSamplesModeCB	= new JComboBox(numberOfSubSamplesStrings);	
			numberOfSamplesModeCB.setSelectedIndex(0);
			numberOfSamplesText		= new JTextField(6);
			logBaseText				= new JTextField(6);
			
			// Setting values
			subSampleSizeText.setText("" + 1);
			numberOfSamplesText.setText("" + 1);
			logBaseText.setText(""+ Defaults.instance().getInteger("logBase"));
			
			// adding the listeners to guide the changes
			modeCB.addItemListener(this);
			
			// adding changelisteners to capture change
			weightC.addChangeListener(this);
			entropyC.addChangeListener(this);
			
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
		mainPanel.add(logBase, c);
		c.gridx = 1;
		mainPanel.add(logBaseText, c);
		
		c.gridx = 0; c.gridy = 3;
		mainPanel.add(operationMode, c);
		c.gridx = 1;
		mainPanel.add(modeCB, c);
		
		c.gridx = 0; c.gridy = 4;
		mainPanel.add(sampleMode, c);
		c.gridx = 1;
		mainPanel.add(subSampleModeCB, c);
		
		
		c.gridx = 0; c.gridy = 5;
		mainPanel.add(subSampleMode, c);
		c.gridx = 1;
		mainPanel.add(numberPanel, c);
		
		c.gridx = 0; c.gridy = 6;
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
		try {
			boolean weighting		= weightC.isSelected();
			boolean entropy			= entropyC.isSelected();
			int mode				= modeCB.getSelectedIndex();
			int subSampleMode		= subSampleModeCB.getSelectedIndex();
			int numberOfSamplesMode	= numberOfSamplesModeCB.getSelectedIndex();
			int subSampleSize		= Integer.parseInt(subSampleSizeText.getText());
			double numberOfSamples	= Double.parseDouble(numberOfSamplesText.getText());
			
			try {
				versionCell.setValue(weighting, 
										entropy, 
										mode, 
										subSampleMode, 
										subSampleSize, 
										numberOfSamplesMode, 
										numberOfSamples);
				logBaseCell.setValue(Integer.parseInt(logBaseText.getText()));
				Grid.instance().cellChanged( new Cell[]{versionCell,logBaseCell} );
				changed = false;
			} catch( NumberFormatException e ) {
				showWarning("Log Base must be a number!");
			} catch( Exception e ) {
				showWarning(e.getMessage());
			}
		} catch( NumberFormatException e ) {
			showWarning("\"Sample Size\" and \"Number of Samples\" must be " +
					"numbers.");
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
	
	/**
	 * Sets the log base to be used.
	 * @param logBase	base of the logarithm
	 */
	public void setLogBase( int logBase ) {
		logBaseText.setText(""+logBase); 
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
				if( cube != null ) {
					maxSampleSizeOneSpan = cube.maxSampleSizeOneSpan();
					maxSampleSizeAllSpan = cube.maxSampleSizeAllSpan();
				}
				
				
				
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
				if( cube != null ) {
					maxSampleSizeOneSpanCumulated = cube.maxSampleSizeOneSpan();
					maxSampleSizeAllSpanCumulated = cube.maxSampleSizeAllSpan();
				}
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
			if( modeCB.getSelectedIndex() >= 2 )
				// enabling
				enableSamplingControllers();
			else
				disableSamplingControllers();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		changed = true;
	}
}
