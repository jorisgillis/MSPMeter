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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import msp.RestrictionViolation;
import msp.data.MSPSpan;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;

import dataflow.Grid;
import dataflow.datastructures.Cell;
import dataflow.datastructures.ResultsCell;

/**
 * This panel displays the information of the MSP calculation
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class ResultChartPanel extends JPanel implements Cell, ActionListener {
	
	// UI
	protected DefaultStatisticalCategoryDataset dataset;
	protected DefaultCategoryDataset upper, lower;
	protected ChartPanel panel;
	protected JFreeChart chart;
	
	// Renderers
	protected LineAndShapeRenderer lineRenderer0, lineRenderer1, lineRenderer2;
	protected StatisticalBarRenderer barRenderer;
	
	// Colors
	protected final Color lowerColor 	= new Color(0.8f, 0.2f, 0.2f);
	protected final Color mspColor		= new Color(0.2f, 0.8f, 0.2f);
	protected final Color upperColor	= new Color(0.2f, 0.2f, 0.8f);
	
	// Results
	protected MSPSpan[] results;
	
	private Logger logger = Logger.getLogger(ResultChartPanel.class);
	
	/**
	 * Construct new Result panel
	 */
	public ResultChartPanel() {
		// The charting objects
		dataset = new DefaultStatisticalCategoryDataset();
		upper = new DefaultCategoryDataset();
		lower = new DefaultCategoryDataset();
		
		// The Renderers
		lineRenderer0	= new LineAndShapeRenderer();
		lineRenderer1	= new LineAndShapeRenderer();
		lineRenderer2	= new LineAndShapeRenderer();
		lineRenderer0.setSeriesPaint(0, lowerColor);
		lineRenderer1.setSeriesPaint(0, mspColor);
		lineRenderer2.setSeriesPaint(0, upperColor);
		lineRenderer0.setSeriesShapesVisible(0, false);
		lineRenderer1.setSeriesShapesVisible(0, false);
		lineRenderer2.setSeriesShapesVisible(0, false);
		
		barRenderer		= new StatisticalBarRenderer();
		barRenderer.setSeriesPaint(0, mspColor);
		
		// The Plot
		CategoryPlot plot = new CategoryPlot();
		plot.setDomainAxis(new CategoryAxis("Datasets"));
		plot.setRangeAxis(new NumberAxis("MSP"));
		plot.setRenderer(0, lineRenderer0);
		plot.setRenderer(1, lineRenderer1);
		plot.setRenderer(2, lineRenderer2);
		plot.setDataset(0, lower);
		plot.setDataset(1, dataset);
		plot.setDataset(2, upper);
		
		// Adding the new plot
		chart = new JFreeChart(plot);
		panel = new ChartPanel(chart);
		
		// Listening to changes
		dataset.addChangeListener(chart.getPlot());
		chart.getPlot().addChangeListener(chart);
		chart.addChangeListener(panel);
		
		((CategoryPlot)chart.getPlot()).getRangeAxis().setAutoRange(true);
		
		// Choice panel: choicing between line and bar chart
		JPanel choicePanel = createChoicePanel();
		
		// Doing the interface
		setLayout(new BorderLayout());
		add(choicePanel, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
		setBorder(
				BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(), "Chart"));
		
		// size stuff
		//setPreferredSize(new Dimension(500, 400));
	}


	/**
	 * Creates the choice panel, that is placed above the chart.
	 * @return	the choice panel
	 */
	protected JPanel createChoicePanel() {
		// The radiobuttons
		JRadioButton line		= new JRadioButton("Line Chart", true);
		JRadioButton bar		= new JRadioButton("Bar Chart", false);
		
		line.setActionCommand("line chart");
		bar.setActionCommand("bar chart");
		
		line.addActionListener(this);
		bar.addActionListener(this);
		
		// Let the radiobuttons form a group: i.e. only one can be selected.
		ButtonGroup choiceGroup	= new ButtonGroup();
		choiceGroup.add(line);
		choiceGroup.add(bar);
		
		// Joining it all on the panel
		JPanel choicePanel		= new JPanel();
		choicePanel.add(line);
		choicePanel.add(bar);
		
		return choicePanel;
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
					dataset.add(results[i].getMSP(), 
								results[i].getStdDev(), 
								"msp", 
								results[i].getSpan());
					
					if (results[i].getMSP() > maximum)
						maximum = results[i].getMSP();
					if (minimum > results[i].getMSP())
						minimum = results[i].getMSP();
				} else {
					dataset.add(1.0, 0.0, "msp", results[i].getSpan());
					minimum = 1.0;
				}
			
			// standard deviations (if present)
			if( results.length > 1 && results[0].getStdDev() >= 0 ) {
				for( int i = 0; i < results.length; i++ )
					if( results[i].getMSP() + 2*results[i].getStdDev() > 1 ) {
						double v = results[i].getMSP() + 2*results[i].getStdDev();
						upper.addValue(v, "upper", results[i].getSpan());
						
						if (v > maximum)
							maximum = v;
					} else
						upper.addValue(1.0, "upper", results[i].getSpan());
				
				for( int i = 0; i < results.length; i++ )
					if( results[i].getMSP() - 2*results[i].getStdDev() > 1 ) {
						double v = results[i].getMSP() - 2*results[i].getStdDev();
						lower.addValue(v, "lower", results[i].getSpan());
						
						if (minimum > v)
							minimum = v;
					} else {
						lower.addValue(1.0, "lower", results[i].getSpan());
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

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = ((JRadioButton)e.getSource()).getActionCommand();
		CategoryPlot plot = (CategoryPlot)chart.getPlot(); 
		
		if( command.equals("line chart") || command.equals("bar chart") ) {
			// Change the chart by a line chart
			if( command.equals("line chart") ) {
				plot.setRenderer(0, lineRenderer0);
				plot.setRenderer(1, lineRenderer1);
				plot.setRenderer(2, lineRenderer2);
				plot.setDataset(0, lower);
				plot.setDataset(1, dataset);
				plot.setDataset(2, upper);
			}
			else if( command.equals("bar chart") ) {
				plot.setRenderer(0, barRenderer);
				plot.setDataset(0, dataset);
				plot.setDataset(1, null);
				plot.setDataset(2, null);
			}
		}
	}
}
