package io;

import javax.swing.JFrame;

import ui.results.IteratingTableModel;
import ui.results.TableModelRow;

/**
 * This class exports data structure to a delimited format. The delimiter
 * is specified on construction.
 * @author Joris Gillis
 */
public abstract class DelimitedExporter extends Exporter {
	
	private String newLine = System.getProperty("line.separator");
	private String delimiter;
	
	/**
	 * Construct a new delimited-exporter
	 * @param parent	GUI parent
	 * @param delimiter	the delimiter
	 */
	public DelimitedExporter( JFrame parent, String delimiter ) {
		super(parent);
		this.delimiter = delimiter;
	}
	
	/**
	 * Generic method to export a table model. 
	 * @param table		the rows
	 */
	public void exportTable( IteratingTableModel table ) {
		if( table != null && table.getRowCount() > 0 ) {
			String text = "";
			
			//- 1. Making the header
			for( int i = 0; i < table.getColumnCount(); i++ )
				text += table.getColumnName(i) + delimiter;
			text = text.substring(0, text.length() - delimiter.length() );
			text += newLine;
			
			//- 2. Row per Row
			for( TableModelRow row : table )
				text += row.printDelimiting(delimiter) + newLine;
			
			writeOut(text);
		}
	}
}
