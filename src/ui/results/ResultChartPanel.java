package ui.results;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import msp.RestrictionViolation;
import msp.data.MSPSpan;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.DefaultKeyedValues2DDataset;

import dataflow.Grid;
import dataflow.datastructures.Cell;
import dataflow.datastructures.ResultsCell;

/**
 * This panel displays the information of the MSP calculation
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class ResultChartPanel extends JPanel implements Cell {
	
	// UI
	protected DefaultKeyedValues2DDataset dataset;
	protected ChartPanel panel;
	protected JFreeChart chart;
	
	// Results
	protected MSPSpan[] results;
	
	private Logger logger = Logger.getLogger(ResultChartPanel.class);
	
	/**
	 * Construct new Result panel
	 */
	public ResultChartPanel() {
		// the charting objects
		dataset = new DefaultKeyedValues2DDataset();
		chart = ChartFactory.createLineChart("MSPMeter", "Datasets", "MSP", dataset, PlotOrientation.VERTICAL, true, true, false);
		panel = new ChartPanel(chart);
		
		// listening to changes
		dataset.addChangeListener(chart.getPlot());
		chart.getPlot().addChangeListener(chart);
		chart.addChangeListener(panel);
		
		// Doing the interface
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Chart"));
		
		// size stuff
		setPreferredSize(new Dimension(500, 400));
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see ui.panels.result.ResultListener#newResults(msp.MSPMonth[])
	 */
	public void recalculate( Vector<String> children ) throws RestrictionViolation {
		results = ((ResultsCell)Grid.instance().getCell("results")).getValue();
		logger.debug("Refresh the chart");
		
		// removing all the rows from the matrix
		dataset.clear();
		
		// averages
		for( int i = 0; i < results.length; i++ )
			if( results[i].getMSP() > 1 )
				dataset.setValue(results[i].getMSP(), "msp", results[i].getSpan());
			else
				dataset.setValue(1.0, "msp", results[i].getSpan());
		
		// standard deviations (if present)
		if( results.length > 1 && results[0].getStdDev() >= 0 ) {
			for( int i = 0; i < results.length; i++ )
				if( results[i].getMSP() + 2*results[i].getStdDev() > 1 )
					dataset.setValue(results[i].getMSP() + 2*results[i].getStdDev(), "upper", results[i].getSpan());
				else
					dataset.setValue(1.0, "upper", results[i].getSpan());
			
			for( int i = 0; i < results.length; i++ )
				if( results[i].getMSP() - 2*results[i].getStdDev() > 1 )
					dataset.setValue(results[i].getMSP() - 2*results[i].getStdDev(), "lower", results[i].getSpan());
				else
					dataset.setValue(1.0, "lower", results[i].getSpan());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.Component#getName()
	 */
	@Override
	public String getName() {
		return "resultsChart";
	}
	
}
