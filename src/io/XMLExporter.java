package io;

import javax.swing.JFrame;

import ui.results.IteratingTableModel;
import ui.results.TableModelRow;

/**
 * Export data structures to XML format
 * @author Joris Gillis
 */
public class XMLExporter extends Exporter {
	
	private static String newLine = System.getProperty("line.separator");
	
	
	/**
	 * Construct a new XML Exporter
	 * @param parent
	 */
	public XMLExporter( JFrame parent ) {
		super(parent);
	}
	
	/**
	 * Generic method to export a table to XML. 
	 * @param table	table
	 */
	public void exportTable( IteratingTableModel table ) {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + newLine + newLine;
		
		// Making the tree
		xml += "<table>" + newLine;
		for( TableModelRow row : table ) {
			xml += "\t<row>" + newLine;
			xml += "\t\t" + row.printXML() + newLine;
			xml += "\t</row>" + newLine;
		}
		xml += "</table>" + newLine;
		
		// Writing the xml file out
		writeOut(xml);
	}
}
