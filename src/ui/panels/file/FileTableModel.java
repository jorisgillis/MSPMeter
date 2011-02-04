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

import java.io.File;
import java.util.Collections;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * Table model support the file table.
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class FileTableModel extends DefaultTableModel {
	
	/** Contains all the data. One element per row. */
	private Vector<FileRow> rows = new Vector<FileRow>();
	
	/**
	 * Create a new model.
	 */
	public FileTableModel() {
		super();
		rows = new Vector<FileRow>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 3;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		if( column > -1 && column < 3 ) {
			switch(column) {
			case 0:
				return "Order";
			case 1:
				return "File Path";
			case 2:
				return "Dataset";
			}
		}
		return "";
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int column) {
		if( -1 < column && column < 3 ) {
			switch(column) {
			case 0:
				return Integer.class;
			case 1:
			case 2:
				return String.class;
			}
		}
		
		return super.getColumnClass(column);
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 2;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		if( rows != null )
			return rows.size();
		else
			return 0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int row, int column) {
		if( correctRowColumn( row, column ) ) {
			FileRow fr = rows.get(row);
			switch( column ) {
			case 0:
				return fr.getOrder();
			case 1:
				return fr.getFile();
			case 2:
				return fr.getDataSet();
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
		if( correctRowColumn(row, column) ) {
			FileRow fr = rows.get(row);
			switch(column) {
			case 0:
				fr.setOrder(((Integer)value).intValue());
				break;
			case 1:
				fr.setFile((File)value);
				break;
			case 2:
				fr.setDataSet((String)value);
				break;
			}
			fireTableCellUpdated(row, column);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.DefaultTableModel#addRow(java.lang.Object[])
	 */
	@Override
	public void addRow(Object[] rowData) {
		if( rowData != null && rowData.length == 3 && rowData[0] instanceof Integer &&
				rowData[1] instanceof File && rowData[2] instanceof String ) {
			FileRow fr = new FileRow((Integer)rowData[0], (File)rowData[1], (String)rowData[2]);
			rows.add(fr);
			Collections.sort(rows);
			int index = rows.indexOf(fr);
			
			fireTableRowsUpdated(index, index);
			fireTableRowsInserted(rows.size()-1, rows.size()-1);
		}
	}
	
	/**
	 * Set the table to a given list of rows/files
	 * @param rows	the files
	 */
	public void addRows( Vector<FileRow> rows ) {
		this.rows = rows;
		fireTableRowsInserted(0, rows.size()-1);
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.DefaultTableModel#removeRow(int)
	 */
	@Override
	public void removeRow(int row) {
		if( row > -1 && row < rows.size() ) { 
			rows.remove(row);
			fireTableRowsDeleted(row, row);
		}
	}
	
	
	/**
	 * Move all the elements between min and max (inclusive) one up.
	 * @param min	start point
	 * @param max	end point
	 */
	public void moveUp( int min, int max ) {
		// at least one row has to be in front of the selected rows
		if( min > 0 ) {
			// move the min - 1 to max
			rows.get(min-1).setOrder(max+1);
			
			// move all from min to max one down
			for( int i = min; i <= max; i++ )
				rows.get(i).oneDown();
			
			// sort it out
			Collections.sort(rows);
			
			// fire!
			fireTableRowsUpdated(min-1, max);
		}
	}
	
	/**
	 * Move all the elements between min and max (inclusive) one down.
	 * @param min	start point
	 * @param max	end point
	 */
	public void moveDown( int min, int max ) {
		// at least one row has to be at the back of the selected rows
		if( max < rows.size() - 1 ) {
			// move max + 1 to min
			rows.get(max+1).setOrder(min+1);
			
			// move all from min to max one up
			for( int i = min; i <= max; i++ )
				rows.get(i).oneUp();
			
			// sort it out
			Collections.sort(rows);
			
			// fire!
			fireTableRowsUpdated(min, max + 1);
		}
	}
	
	
	/**
	 * Get the list of filenames out!
	 */
	public Vector<FileRow> getFiles() {
		return rows;
	}
	
	
	
	/**
	 * Is a given row, column pair a correct combination?
	 * @param row		the row
	 * @param column	the column
	 * @return			correct pair?
	 */
	private boolean correctRowColumn( int row, int column ) {
		return row > -1 && row < rows.size() && column > -1 && column < 3;
	}
}
