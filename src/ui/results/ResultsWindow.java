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

import io.CSVExporter;
import io.DelimitedExporter;
import io.ExcelExporter;
import io.TabExporter;
import io.XMLExporter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import ui.Window;
import dataflow.Grid;


/**
 * The window in which the results of the MSPCalculation are shown.
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class ResultsWindow extends Window implements ActionListener, ProgressController {
	
	//- The panels
	protected JTabbedPane tabs;
	
	// Charting
	protected JPanel chartTablePanel;
	protected ResultChartPanel resultChart;
	protected ResultTablePanel resultTable;
	
	// Data Information
	protected CubeInformationPanel cubeInformation;
	
	// Sample Information
	protected SampleInformationPanel sampleInformation;
	
	//- Progressbar
	protected JProgressBar progressBar;
	
	//- Everywhere the same
	private static Logger logger = Logger.getLogger(ResultsWindow.class);
	
	/**
	 * Construct a new results window, containing a chart and a table.
	 */
	public ResultsWindow() {
		// Title
		super("MSPMeter Results");
		
		// Menu
		setupMenuBar();
		
		// a tab panel
		tabs = new JTabbedPane();
		
		// three panels, a chart panel, a table panel, and a sampleinformation panel
		setupTabs();
		
		setLayout(new BorderLayout());
		add(tabs, BorderLayout.CENTER);
		
		// framing stuff
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * Making the tabs. 
	 */
	private void setupTabs() {
		chartTablePanel = new JPanel();
		resultChart = new ResultChartPanel();
		resultTable = new ResultTablePanel();
		
		Grid.instance().addCell(resultChart);
		Grid.instance().addCell(resultTable);
		
		chartTablePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH; c.weightx = 1; c.weighty = 1;
		c.gridx = 0; c.gridy = 0; c.gridwidth = 1; c.anchor = GridBagConstraints.FIRST_LINE_START;
		chartTablePanel.add(resultChart, c);
		c.gridx = 1; c.weightx = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_END; c.fill = GridBagConstraints.VERTICAL;
		chartTablePanel.add(resultTable, c);
		
		// the cube information panel
		cubeInformation = new CubeInformationPanel();
		Grid.instance().addHotCell(cubeInformation);
		
		
		// Showing individual sample results
		sampleInformation = new SampleInformationPanel();
		Grid.instance().addCell(sampleInformation);
		
		// Putting it together in the tabPanel
		tabs.addTab("MSP Results", chartTablePanel);
		tabs.addTab("Data Information", cubeInformation);
		tabs.addTab("Samples", sampleInformation);
	}
	
	/**
	 * Making the menubar. 
	 */
	private void setupMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu export = new JMenu("Export");
		
		JMenuItem exportAsCSV 	= new JMenuItem("Export Table As CSV");
		JMenuItem exportAsTab 	= new JMenuItem("Export Table As Tab-delimited");
		JMenuItem exportAsExcel	= new JMenuItem("Export Table As Excel");
		JMenuItem exportAsXML 	= new JMenuItem("Export Table As XML");
		
		exportAsCSV.setActionCommand("csv");
		exportAsTab.setActionCommand("tab");
		exportAsExcel.setActionCommand("excel");
		exportAsXML.setActionCommand("xml");
		
		exportAsCSV.addActionListener(this);
		exportAsTab.addActionListener(this);
		exportAsExcel.addActionListener(this);
		exportAsXML.addActionListener(this);
		
		export.add(exportAsCSV);
		export.add(exportAsTab);
		export.add(exportAsExcel);
		export.add(exportAsXML);
		
		menuBar.add(export);
		
		setJMenuBar(menuBar);
	}
	
	
	//===========================================================================
	// Progress Monitoring.
	//===========================================================================
	/* (non-Javadoc)
	 * @see ui.results.ProgressController#startProgressing()
	 */
	public void startProgressing() {
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		add( progressBar, BorderLayout.SOUTH );
		progressBar.setVisible(true);
		pack();
	}
	
	/* (non-Javadoc)
	 * @see ui.results.ProgressController#stopProgressing()
	 */
	public void stopProgressing() {
		remove(progressBar);
		progressBar.setVisible(false);
		progressBar = null;
		pack();
	}
	
	
	//===========================================================================
	// User Interaction
	//===========================================================================
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if( command.equals("csv") ) {
			exportCSV();
		} else if( command.equals("tab") ) {
			exportTab();
		} else if( command.equals("excel") ) {
			exportExcel();
		} else if( command.equals("xml") ) {
			exportXML();
		}
	}
	
	/**
	 * Returns the table model on which currently the focus is directed. 
	 * @return	focused table
	 */
	protected IteratingTableModel getFocusedTable() {
		Component selected = tabs.getSelectedComponent();
		
		// which tab is selected? 
		logger.debug("Selecting the selected tab");
		if( selected.equals(chartTablePanel) ) {
			logger.debug("Tab Selected: Results");
			return resultTable.getTableModel();
		}
		else if( selected.equals(sampleInformation) ) {
			logger.debug("Tab Selected: Samples");
			return sampleInformation.getTableModel();
		}
		else if( selected.equals(cubeInformation) ) {
			logger.debug("Tab Selected: CubeInformation");
			return cubeInformation.getTableModel();
		}
		
		return null;
	}
	
	//===========================================================================
	// Exporting
	//===========================================================================
	/**
	 * Export the table to CSV.
	 */
	protected void exportCSV() {
		IteratingTableModel table = getFocusedTable();
		
		if( table != null ) {
			// Making the exporter export
			DelimitedExporter exporter = new CSVExporter(this);
			exporter.exportTable(table);
			
			// Updating the statusbar
			StatusBar.getInstance().addStatus("Exported the data table to CSV format");
		} else
			showWarning("No results available.");
	}
	
	/**
	 * Export the table to tab-delimited format.
	 */
	protected void exportTab() {
		IteratingTableModel table = getFocusedTable();
		
		if( table != null ) {
			// Making the exporter export
			DelimitedExporter exporter = new TabExporter(this);
			exporter.exportTable(table);
			
			// Updating the statusbar
			StatusBar.getInstance().addStatus("Exported the data table to Tab-delimited format");
		} else
			showWarning("No results available.");
	}
	
	/**
	 * Export the table to XML format.
	 */
	protected void exportXML() {
		IteratingTableModel table = getFocusedTable();
		
		if( table != null ) {
			// Making the exporter export
			XMLExporter exporter = new XMLExporter(this);
			exporter.exportTable(table);
			
			// Updating the statusbar
			StatusBar.getInstance().addStatus("Exported the data table to XML format");
		} else
			showWarning("No results available.");
	}
	
	/**
	 * Export the table to Excel format.
	 */
	protected void exportExcel() {
		IteratingTableModel table = getFocusedTable();
		
		if( table != null ) {
			// Making the exporter export
			ExcelExporter exporter = new ExcelExporter(this);
			exporter.exportTable(table);
			
			// Updating the statusbar
			StatusBar.getInstance().addStatus("Exported the data table to Excel format");
		} else
			showWarning("No results available.");
	}
}
