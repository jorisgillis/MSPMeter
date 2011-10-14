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
import java.awt.Dimension;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.FrequencyFile;
import msp.data.FrequencyLineParse;
import msp.data.ImpossibleCalculationException;

import org.apache.log4j.Logger;

import dataflow.Grid;
import dataflow.datastructures.Cell;
import dataflow.datastructures.FilesCell;
import dataflow.datastructures.FirstSeparatorCell;
import dataflow.datastructures.SecondSeparatorCell;

/**
 * A panel showing a table with the result of the line parsing.
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class LiveLineParsingPanel extends JPanel implements Cell {
	
	/** The Table */
	protected JTable table;
	/** The Model */
	protected LineParsingTableModel model;
	
	/** The input files */
	protected Vector<FileRow> files;
	/** The first separator separates the ante from the lemma */
	protected String firstSeparator;
	/** The second separator separates the lemma from the category */
	protected String secondSeparator;
	
	// Logger
	private static Logger logger = Logger.getLogger(LiveLineParsingPanel.class);
	
	/**
	 * Constructor.
	 */
	public LiveLineParsingPanel() {
		model = new LineParsingTableModel();
		
		table = new JTable();
		table.setModel(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setColumnSelectionAllowed(false);
		table.setCellSelectionEnabled(false);
		table.setRowSelectionAllowed(false);
		tidyTable();
		
		// Adding the table to a scrollpane and adding the scrollpane to the
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(550,230));
		
		// Layouting
		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(java.util.Vector)
	 */
	@Override
	public void recalculate(Vector<String> cells) throws DataFaultException,
			ImpossibleCalculationException, RestrictionViolation {
		// Get the new data
		Grid grid = Grid.instance();
		for( String cellName : cells ) {
			if( cellName == "files" )
				files = ((FilesCell)grid.getCell("files")).getValue();
			else if( cellName == "firstSeparator" )
				firstSeparator = 
					((FirstSeparatorCell)grid.getCell("firstSeparator")).getValue();
			else if( cellName == "secondSeparator" )
				secondSeparator = 
					((SecondSeparatorCell)grid.getCell("secondSeparator")).getValue();
		}
		
		// Update the table
		updateTable();
	}
	
	/**
	 * Updates the table with the values at hand.
	 */
	protected void updateTable() {
		if( files != null && firstSeparator != null && secondSeparator != null ) {
			try {
				LinkedList<ParseRow> rows = new LinkedList<ParseRow>();
				for( FileRow fr : files ) {
					FrequencyFile fFile = new FrequencyFile(fr.getFile(), 
															firstSeparator, 
															secondSeparator);
					
					for( FrequencyLineParse parse : fFile ) {
						ParseRow pr = new ParseRow();
						pr.setDataSet(fr.getDataSet());
						pr.setFrequency(parse.getFrequency());
						pr.setAnte(parse.getAnte());
						pr.setLemma(parse.getLemma());
						pr.setCategory(parse.getCategory());
						rows.add(pr);
					}
				}
				model.setRows(rows);
			} catch( DataFaultException e ) {
				logger.error(e);
			}
		}
	}
	
	/**
	 * Returns the name of this cell.
	 * @return the name of this cell
	 */
	public String getName() {
		return "LiveLineParsing";
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
