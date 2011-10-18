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
import java.util.List;

import msp.data.MSPTriple;

import org.apache.log4j.Logger;

/**
 * Model backing a table with the MSP values of samples. 
 * @author joris
 */
public class SampleTableModel extends IteratingTableModel {
	
	/** The store: storing all the values. */
	private ArrayList<TableModelRow> rows = new ArrayList<TableModelRow>();
	/** Names of the columns in the table. */
	private String[] columnNames = new String[]{"Dataset", "Sample", "MSP"};
	
	// Logger
	private static Logger logger = Logger.getLogger( SampleTableModel.class );
	
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
		if( column >= 0 && column < columnNames.length )
			return columnNames[column];
		return "";
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	public Class<?> getColumnClass(int column) {
		if( column == 1 )
			return Integer.class;
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
	public Object getValueAt(int rowIndex, int columnIndex) {
		if( inRange(rowIndex) ) {
			SampleTableModelRow row = (SampleTableModelRow)rows.get(rowIndex);
			switch( columnIndex ) {
			case 0:
				return row.getDataset();
			case 1:
				return row.getSampleNR();
			case 2:
				return row.getMSP();
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
		// Do not allow insertions
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
    public boolean isCellEditable(int row, int col) {
    	return false;
    }
    
    /**
     * Checks if a given index is in the range of the table model. 
     * @param index	index
     * @return		true if inRange, false otherwise
     */
    public boolean inRange( int index ) {
    	return 0 <= index && index < rows.size();
    }
    
    
	/**
	 * Adds a new row to the table.
	 * @param msp	msp value of a sample
	 */
	public void addRow( String dataset, int sampleNR, double msp ) {
		rows.add( new SampleTableModelRow( dataset, sampleNR, msp ) );
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
	 * Sets the given results in the table. The results sets is used for the 
	 * dataset values, the sampleMSPs set is used for the MSP values of the
	 * samples. 
	 * @param results		results for the datasets
	 * @param sampleMSPs	MSP values
	 */
	public void setValues( MSPTriple[] results, List<List<Double>> sampleMSPs ) {
		if( sampleMSPs != null ) {
			//- 1. Processing the given information into the correct form
			if( sampleMSPs != null ) {
				if( results.length != sampleMSPs.size() )
					logger.error("The length of results and sampleMSPs is different!!");
				
				rows = new ArrayList<TableModelRow>( sampleMSPs.size() );
				for( int i = 0; i < sampleMSPs.size(); i++ ) {
					String dataset = results[i].getDataset();
					int sampleNR = 1;
					
					for( Double sample : sampleMSPs.get(i) ) {
						addRow( dataset, sampleNR, sample );
						sampleNR++;
					}
				}
			}
			
			//- 2. Notifying the interface that new values are available
			fireTableDataChanged();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<TableModelRow> iterator() {
		return rows.iterator();
	}
}
