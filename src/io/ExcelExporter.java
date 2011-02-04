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


package io;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import ui.results.IteratingTableModel;
import ui.results.TableModelRow;

/**
 * Export data structures to Excel.
 * @author Joris Gillis
 */
public class ExcelExporter extends Exporter {
	
	
	/**
	 * Construct a new ExcelExporter
	 * @param parent	GUI parent
	 */
	public ExcelExporter( JFrame parent ) {
		super(parent);
	}
	
	/**
	 * Generic method for exporting a table to Excell. 
	 */
	public void exportTable( IteratingTableModel table ) {
		// let the user choose a file
		File file = requestFileName();
		
		if( file != null ) {
			try {
				// setting up the Excel workbook
				WritableWorkbook workbook = Workbook.createWorkbook(file);
				
				// setting up the sheet
				WritableSheet sheet = workbook.createSheet("MSP", 0); 
				
				// headers
				for( int i = 0; i < table.getColumnCount(); i++ ) {
					Label headerCell = new Label(i, 0, table.getColumnName(i));
					sheet.addCell(headerCell);
				}
				
				// data
				int rowIndex = 0;
				for( TableModelRow row : table ) {
					WritableCell[] cells = row.printExcell(rowIndex);
					
					for( WritableCell c : cells )
						sheet.addCell( c );
					
					rowIndex++;
				}
				
				// All sheets and cells added. Now write out the workbook
				workbook.write();
				workbook.close(); 
			} catch( IOException e ) {
				showWarning("Fault while writing out the Excel workbook: \n"+ e.getMessage());
			} catch( WriteException e ) {
				showWarning("Fault while writing out the Excel workbook: \n"+ e.getMessage());
			}
		}
	}
}
