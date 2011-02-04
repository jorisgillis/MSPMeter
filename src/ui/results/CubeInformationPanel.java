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


package ui.results;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;

import org.apache.log4j.Logger;

import dataflow.Grid;
import dataflow.datastructures.Cell;
import dataflow.datastructures.DataCubeCell;

/**
 * A panel displaying information about the cubes. Size of the cube, size of the slices,
 * basic and cumulated.
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class CubeInformationPanel extends JPanel implements Cell {
	
	/** The panel displaying information of the workingDC */
	protected CubePanel basicCubeInformation;
	/** The panel displaying information of the cumulatedDC */
	protected CubePanel cumulatedCubeInformation;
	
	// Logger
	private static Logger logger = Logger.getLogger(CubeInformationPanel.class);
	
	/**
	 * Construct a new CubeInformationPanel.
	 */
	public CubeInformationPanel() {
		// necessary evil classes
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1; c.weighty = 1;
		
		// creating the panels
		basicCubeInformation = new CubePanel("Basic");
		cumulatedCubeInformation = new CubePanel("Cumulated");
		
		// adding it all together
		c.gridx = 0; c.gridy = 0;
		panel.add(basicCubeInformation, c);
		c.gridx = 1;
		panel.add(cumulatedCubeInformation, c);
		
		add(panel);
	}
	
	/**
	 * Returns the model of the chose table, this function raises a 
	 * dialogbox! 
	 */
	public IteratingTableModel getTableModel() {
		// Keeping the result of the dialog with the user
		boolean basic;
		
		// Asking the user which table he/she wants
		int answer = JOptionPane.showConfirmDialog(
				this, 
				"Export the Basic table?", 
				"Choose a table to export", 
				JOptionPane.YES_NO_OPTION);
		basic = answer == 0;
		
		// Based on the user's choice, we return the appropriate model
		if( basic )
			return basicCubeInformation.getTableModel();
		else
			return cumulatedCubeInformation.getTableModel();
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(dataflow.datastructures.Cell)
	 */
	public void recalculate(Vector<String> children) throws DataFaultException,
			ImpossibleCalculationException, RestrictionViolation {
		// Logging
		logger.info("Recalculating ...");
		
		// Recalculating
		Grid grid = Grid.instance();
		for( String cellName : children ) {
			if( cellName.equals("workingDC") )
				// the basic version
				basicCubeInformation.refresh(((DataCubeCell)grid.getCell(cellName)).getCube());
			else if( cellName.equals("cumulatedDC") )
				// the cumulated version
				cumulatedCubeInformation.refresh(((DataCubeCell)grid.getCell(cellName)).getCube());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.Component#getName()
	 */
	@Override
	public String getName() {
		return "cubeInformation";
	}
}
