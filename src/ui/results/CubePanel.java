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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import msp.data.DataCubeList;

/**
 * Panel displaying all kinds of information about a DataCube.
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class CubePanel extends JPanel {
	
	protected JLabel lSize, lLemmas, lTime;
	protected JLabel size, lemmas, time;
	
	protected JScrollPane scrollPane;
	protected JTable slicesTable;
	protected SlicesTableModel model;
	
	/**
	 * Create a new CubePanel for displaying DataCube information.
	 * @param title	name of the cube, will be displayed in the border
	 */
	public CubePanel( String title ) {
		// super
		super();
		
		// setting the border
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title));
		
		//- the labels
		lSize = new JLabel("Size: ");
		lLemmas = new JLabel("Number of Lemmas: ");
		lTime = new JLabel("Number of Datasets: ");
		
		size = new JLabel("");
		lemmas = new JLabel("");
		time = new JLabel("");
		
		//- the table
		model = new SlicesTableModel();
		slicesTable = new JTable(model);
		scrollPane = new JScrollPane(slicesTable);
		tidyUpTable();
		
		//- Placing it on the panel
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = 1; c.fill = GridBagConstraints.BOTH;
		c.weightx = 1; c.weighty = 1;
		
		c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.FIRST_LINE_END;
		add(lSize, c);
		c.gridx = 1; c.anchor = GridBagConstraints.FIRST_LINE_START;
		add(size, c);
		
		c.gridx = 0; c.gridy = 1; c.anchor = GridBagConstraints.FIRST_LINE_END;
		add(lLemmas, c);
		c.gridx = 1; c.anchor = GridBagConstraints.FIRST_LINE_START;
		add(lemmas, c);
		
		c.gridx = 0; c.gridy = 2; c.anchor = GridBagConstraints.FIRST_LINE_END;
		add(lTime, c);
		c.gridx = 1; c.anchor = GridBagConstraints.FIRST_LINE_START;
		add(time, c);
		
		c.gridx = 0; c.gridy = 3;
		c.gridwidth = 2;
		add(scrollPane, c);
	}
	
	/**
	 * Set the column widths of the table
	 */
	private void tidyUpTable() {
		scrollPane.setPreferredSize(new Dimension(330, 360));
		slicesTable.getColumnModel().getColumn(0).setPreferredWidth(150);
		slicesTable.getColumnModel().getColumn(0).setWidth(200);
		slicesTable.getColumnModel().getColumn(1).setPreferredWidth(30);
		slicesTable.getColumnModel().getColumn(1).setWidth(30);
		slicesTable.getColumnModel().getColumn(2).setPreferredWidth(30);
		slicesTable.getColumnModel().getColumn(2).setWidth(30);
	}
	
	
	/**
	 * Introduces a new DataCube to the panel. All information will be updated.
	 * @param cube	the new cube
	 */
	public void refresh( DataCubeList cube ) {
		if( cube != null ) {
			// Size
			size.setText(""+cube.numberOfTokens());
			
			// Lemmas
			lemmas.setText(""+cube.getLemmas().size());
			
			// Datasets
			time.setText(""+cube.getDataSets().size());
			
			// Table
			model.removeAll();
			List<String> dataSets = cube.getDataSets();
			for( int i = 0; i < dataSets.size(); i++ ) {
				String sliceName = dataSets.get(i);
				model.addRow(sliceName, cube.numberOfTokens(sliceName), cube.numberOfLemmas(sliceName));
			}
		}
	}
	
	/**
	 * Returns the model of the table on this panel. 
	 */
	public IteratingTableModel getTableModel() {
		return model;
	}
}
