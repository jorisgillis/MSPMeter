/*
 * Copyright 2010 MSPMeter
 *
 * Licensed under the EUPL, Version 1.1 or � as soon they
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

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;
import dataflow.datastructures.Cell;

/**
 * A panel showing a table with the result of the line parsing.
 * @author Joris Gillis
 */
public class LiveLineParsingPanel extends JPanel implements Cell {
	
	/** The Table */
	protected JTable table;
	/** The Model */
	protected LineParsingTableModel model;
	
	
	/**
	 * Constructor.
	 */
	public LiveLineParsingPanel() {
		model = new LineParsingTableModel();
		
		table = new JTable();
		table.setModel(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); 
		//table.setPreferredSize(new Dimension(380, 100));
		tidyTable();
		
		// Adding the table to a scrollpane and adding the scrollpane to the
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(550,230));
		this.add(scrollPane);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(java.util.Vector)
	 */
	@Override
	public void recalculate(Vector<String> cells) throws DataFaultException,
			ImpossibleCalculationException, RestrictionViolation {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Returns the name of this cell.
	 * @return the name of this cell
	 */
	public String getName() {
		return "LiveLineParsingCell";
	}
	
	/**
	 * 
	 */
	private void tidyTable() {
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(80);
		table.getColumnModel().getColumn(3).setPreferredWidth(80);
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(200, 100);
	}
}