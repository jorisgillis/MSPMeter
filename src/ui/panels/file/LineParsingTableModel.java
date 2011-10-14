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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.table.DefaultTableModel;


/**
 * The model underlying the live line parsing table. 
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class LineParsingTableModel extends DefaultTableModel {
	
	/** The rows of the table model. */
	protected List<ParseRow> rows;
	
	/**
	 * Constructs a new, empty table model.
	 */
	public LineParsingTableModel() {
		super();
		rows = new LinkedList<ParseRow>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.DefaultTableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 5;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int i) {
		return String.class;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.DefaultTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int i) {
		String name = "";
		switch(i) {
		case 0:
			name = "Dataset";
			break;
		case 1:
			name = "Frequency";
			break;
		case 2:
			name = "Ante";
			break;
		case 3:
			name = "Lemma";
			break;
		case 4:
			name = "Category";
			break;
		}
		return name;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.DefaultTableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		if( rows != null )
			return rows.size();
		else
			return 0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int col) {
		if( correctPosition(row, col) ) {
			ParseRow pr = rows.get(row);
			switch(col) {
			case 0:
				return pr.getDataSet();
			case 1:
				return pr.getFrequency();
			case 2:
				return pr.getAnte();
			case 3:
				return pr.getLemma();
			case 4:
				return pr.getCategory();
			}
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.DefaultTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object value, int row, int col) {
		if( correctPosition(row, col) ) {
			ParseRow pr = rows.get(row);
			switch(col) {
			case 0:
				pr.setDataSet((String)value);
				break;
			case 1:
				pr.setFrequency((String)value);
				break;
			case 2:
				pr.setAnte((String)value);
				break;
			case 3:
				pr.setLemma((String)value);
				break;
			case 4:
				pr.setCategory((String)value);
				break;
			}
			fireTableCellUpdated(row, col);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.DefaultTableModel#addRow(java.lang.Object[])
	 */
	@Override
	public void addRow(Object[] row) {
		if( row != null && row.length == 5 &&
				row[0] instanceof String &&
				row[1] instanceof String && 
				row[2] instanceof String &&
				row[3] instanceof String &&
				row[4] instanceof String ) {
			// Make the row
			ParseRow pr = new ParseRow();
			pr.setDataSet((String)row[0]);
			pr.setFrequency((String)row[1]);
			pr.setAnte((String)row[2]);
			pr.setLemma((String)row[3]);
			pr.setCategory((String)row[4]);
			rows.add(pr);
			
			// Sort the row
			Collections.sort(rows);// TODO can we do better? Sort at the end or something?
			int index = rows.indexOf(pr);
			
			// Fire updates to the GUI
			fireTableRowsUpdated(index, index);
			fireTableRowsInserted(rows.size()-1, rows.size()-1);
		}
	}
	
	/**
	 * Sets the table to a given list of rows.
	 * @param rows	new content of the table
	 */
	public void setRows( List<ParseRow> rows ) {
		this.rows = rows;
		fireTableRowsInserted(0, rows.size()-1);
	}
	
	
	
	/**
	 * Decides whether a given combination of a row and column index forms
	 * a correct position indication in this table. 
	 * @param row	row index
	 * @param col	column index
	 * @return		correct position indication?
	 */
	private boolean correctPosition(int row, int col) {
		return 0 <= row && row < getRowCount() && 0 <= col && col < 5; 
	}
}
