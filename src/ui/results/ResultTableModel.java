package ui.results;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import msp.data.MSPSpan;

/**
 * Model for showing the results of an MSP calculation in a table
 * @author Joris Gillis
 *
 */
public class ResultTableModel extends IteratingTableModel {
	
	private List<TableModelRow> rows = new ArrayList<TableModelRow>();
	private String[] columnNames = new String[]{"Data Set", "MSP", "StdDev"};
	
	
	/**
	 * Set new results!
	 * @param results
	 */
	public void setResults( MSPSpan[] results ) {
		// cleaning out the table
		removeAll();
		
		// adding each span
		for( MSPSpan span : results )
			rows.add( new ResultTableModelRow( span ) );
		
		// letting the table know about the new values
		fireTableDataChanged();
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
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName( int col ) {
        if( inColumnRange( col ) )
        	return columnNames[col];
        else
        	return "";
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
	public Object getValueAt(int rowIndex, int columnIndex) {
		if( inRange( rowIndex, columnIndex ) ) {
			ResultTableModelRow row = (ResultTableModelRow)rows.get(rowIndex);
			switch( columnIndex ) {
			case 0:
				return row.getSpan();
			case 1:
				return row.getMSP();
			case 2:
				return row.getStdDev();
			}
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
    public boolean isCellEditable(int row, int col) {
    	return false;
    }
	
    /**
     * Checks whether the given column index is within the
     * column bounds. 
     * @param column	column index
     * @return			within bounds
     */
    private boolean inColumnRange( int column ) {
    	return 0 <= column && column < columnNames.length;
    }
    
    /**
     * Checks whether the given row and column index are within
     * the table bounds. 
     * @param row		row index
     * @param column	column index
     * @return			within bounds?
     */
    private boolean inRange( int row, int column ) {
    	return 0 <= row && row < rows.size() && inColumnRange( column );
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<TableModelRow> iterator() {
    	return rows.iterator();
    }
}
