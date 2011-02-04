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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import msp.RestrictionViolation;
import msp.data.MSPSpan;

import org.apache.log4j.Logger;

import ui.FractionTableCellRenderer;
import dataflow.Grid;
import dataflow.datastructures.Cell;
import dataflow.datastructures.ResultsCell;

/**
 * Seperate frame which contains a table with the results of the msp calculation
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class ResultTablePanel extends JPanel implements Cell {
	
	protected ResultChartPanel parent = null;
	
	protected MSPSpan[] results = null;
	
	private JScrollPane scroller;
	private JTable table;
	private ResultTableModel model;
	
	private Logger logger = Logger.getLogger(ResultTablePanel.class);
	
	/**
	 * Construct a new frame for results.
	 */
	public ResultTablePanel() {
		// main panel
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Results Table"));
		
		// tabling it
		model = new ResultTableModel();
		table = new JTable(model);
		scroller = new JScrollPane(table);
		add(scroller);
		
		// renderers for the table
		FractionTableCellRenderer fractionRenderer = new FractionTableCellRenderer();
		table.getColumn("MSP").setCellRenderer(fractionRenderer);
		table.getColumn("StdDev").setCellRenderer(fractionRenderer);
		
		// size stuff
		setPreferredSize(new Dimension(200, 400));
	}
	
	
	/**
	 * Redraw the table containing the results.
	 */
	protected void refreshTable() {
		model.setResults(results);	
	}
	
	/**
	 * Returns the results that are displayed.
	 * @return	the results
	 */
	public MSPSpan[] getResults() {
		return results;
	}
	
	/**
	 * Returns the table model of the table on this panel.
	 */
	public IteratingTableModel getTableModel() {
		return model;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see ui.panels.result.ResultListener#newResults(msp.MSPMonth[])
	 */
	public void recalculate(Vector<String> children) throws RestrictionViolation {
		logger.info("New results have arrived!");
		results = ((ResultsCell)Grid.instance().getCell("results")).getValue();
		refreshTable();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.Component#getName()
	 */
	@Override
	public String getName() {
		return "resultsTable";
	}
}
