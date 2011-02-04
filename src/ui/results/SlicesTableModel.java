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

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

/**
 * Model of the slices table in the CubePanel.
 * @author Joris Gills
 */
public class SlicesTableModel extends IteratingTableModel {
	
	private ArrayList<TableModelRow> rows = new ArrayList<TableModelRow>();
	
	private String[] columnNames = new String[]{"Dataset", "Size", "Lemmas"};
	private Class<?>[] columnClasses = new Class<?>[]{String.class, Integer.class, Integer.class};
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		if( inColumnRange(column) )
			return columnNames[column];
		return "";
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int column) {
		if( inColumnRange(column) )
			return columnClasses[column];
		return String.class;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return columnNames.length;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return rows.size();
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int row, int column) {
		if( inRange(row, column) ) {
			SlicesTableModelRow r = (SlicesTableModelRow)rows.get(row);
			switch(column) {
			case 0:
				return r.getDataset();
			case 1:
				return r.getSize();
			case 2:
				return r.getLemmas();
			}
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object value, int row, int column) {
		// No editing!
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
    public boolean isCellEditable(int row, int col) {
    	return false;
    }
	
	/**
	 * Adds a new row to the table.
	 * @param dataSet	dataset
	 * @param size		size of the dataset
	 * @param lemma		number of lemmas in the dataset
	 */
	public void addRow( String dataSet, int size, int lemma ) {
		rows.add( new SlicesTableModelRow( dataSet, size, lemma ) );
		fireTableRowsInserted(rows.size()-1, rows.size()-1);
	}
	
	/**
	 * Removes all data from the table.
	 */
	public void removeAll() {
		int size = rows.size();
		rows = new ArrayList<TableModelRow>();
		
		if( size > 0 )
			fireTableRowsDeleted(0, size-1);
	}
	
	/**
	 * Checks whether a given couple of coordinates falls within the ranges.
	 * @param row		row coordinate
	 * @param column	column coordinate
	 * @return			in range?
	 */
	private boolean inRange( int row, int column ) {
		return 0 <= row && row < rows.size() && 0 <= column && column < columnNames.length;
	}
	
	/**
	 * Checks whether a given column index falls within the range of columns. 
	 * @param columnIndex	column index
	 * @return				in range? 
	 */
	private boolean inColumnRange( int columnIndex ) {
		return 0 <= columnIndex && columnIndex < columnNames.length;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<TableModelRow> iterator() {
		return rows.iterator();
	}
}
