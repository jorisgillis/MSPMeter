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

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;

import org.apache.log4j.Logger;

import dataflow.Grid;
import dataflow.datastructures.Cell;
import dataflow.datastructures.ResultsCell;

/**
 * A panel displaying information about the MSP of samples. 
 * @author joris
 */
@SuppressWarnings("serial")
public class SampleInformationPanel extends JPanel implements Cell {
	
	// Parts
	protected JScrollPane scrollPane;
	protected JTable table;
	protected SampleTableModel model;
	
	// Logger
	private static Logger logger = Logger.getLogger(SampleInformationPanel.class);
	
	/**
	 * Construct a new SampleInformationPanel. 
	 */
	public SampleInformationPanel() {
		// JPanel constructor
		super();
		
		// Setting up the interface elements
		setup();
	}
	
	/**
	 * Does the setting up of the interface elements. 
	 */
	protected void setup() {
		// setting the border
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), 
				"MSP values of Samples"));
		
		// the table
		model = new SampleTableModel();
		table = new JTable(model);
		scrollPane = new JScrollPane(table);
		tidyUpTable();
		
		// adding it together
		this.add(scrollPane);
	}
	
	/**
	 * Set the column widths of the table
	 */
	private void tidyUpTable() {
		scrollPane.setPreferredSize(new Dimension(660, 400));
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(0).setWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(60);
		table.getColumnModel().getColumn(1).setWidth(60);
		table.getColumnModel().getColumn(2).setPreferredWidth(400);
		table.getColumnModel().getColumn(2).setWidth(400);
	}
	
	
	/**
	 * Returns the table model of the table on this pane.  
	 */
	public IteratingTableModel getTableModel() {
		return model;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.Component#getName()
	 */
	@Override
	public String getName() {
		return "sampleInformationPanel";
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(dataflow.datastructures.Cell)
	 */
	public void recalculate(Vector<String> children) throws DataFaultException,
			ImpossibleCalculationException, RestrictionViolation {
		// Logging
		logger.debug("New results have arrived!");
		
		// Populating the table
		ResultsCell c = ((ResultsCell)Grid.instance().getCell("results"));
		model.setValues( c.getValue(), c.getSampleValues() );
	}
}
