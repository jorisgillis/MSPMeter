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

import msp.RestrictionViolation;
import msp.data.MSPSpan;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
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
		
		((CategoryPlot)chart.getPlot()).getRangeAxis().setAutoRange(true);
		
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
		
		if ( results != null ) {
			// removing all the rows from the matrix
			dataset.clear();
			
			// Minimum and Maximum
			double minimum = Double.MAX_VALUE;
			double maximum = 1;
			
			// averages
			for( int i = 0; i < results.length; i++ )
				if( results[i].getMSP() > 1 ) {
					dataset.setValue(results[i].getMSP(), "msp", results[i].getSpan());
					
					if (results[i].getMSP() > maximum)
						maximum = results[i].getMSP();
					if (minimum > results[i].getMSP())
						minimum = results[i].getMSP();
				} else {
					dataset.setValue(1.0, "msp", results[i].getSpan());
					minimum = 1.0;
				}
			
			// standard deviations (if present)
			if( results.length > 1 && results[0].getStdDev() >= 0 ) {
				for( int i = 0; i < results.length; i++ )
					if( results[i].getMSP() + 2*results[i].getStdDev() > 1 ) {
						double v = results[i].getMSP() + 2*results[i].getStdDev();
						dataset.setValue(v, "upper", results[i].getSpan());
						
						if (v > maximum)
							maximum = v;
					} else
						dataset.setValue(1.0, "upper", results[i].getSpan());
				
				for( int i = 0; i < results.length; i++ )
					if( results[i].getMSP() - 2*results[i].getStdDev() > 1 ) {
						double v = results[i].getMSP() - 2*results[i].getStdDev();
						dataset.setValue(v, "lower", results[i].getSpan());
						
						if (minimum > v)
							minimum = v;
					} else {
						dataset.setValue(1.0, "lower", results[i].getSpan());
						minimum = 1.0;
					}
			}
			
			// Add 5% of the range above and below
			minimum = minimum - ((maximum - minimum) / 10);
			maximum = maximum + ((maximum - minimum) / 10);
			((CategoryPlot)chart.getPlot()).getRangeAxis().setLowerBound(minimum);
			((CategoryPlot)chart.getPlot()).getRangeAxis().setUpperBound(maximum);
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
