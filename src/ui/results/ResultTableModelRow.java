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

import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import msp.data.MSPSpan;

/**
 * Row of the ResultTableModel. 
 * @author Joris Gillis
 */
public class ResultTableModelRow implements TableModelRow {
	
	protected MSPSpan value;
	
	/**
	 * Constructs new row. 
	 * @param value	MSPSpan
	 */
	public ResultTableModelRow( MSPSpan value ) {
		this.value = value;
	}
	
	public String getSpan() {
		return value.getSpan();
	}
	
	public double getMSP() {
		return value.getMSP();
	}
	
	public double getStdDev() {
		return value.getStdDev();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ui.results.TableModelRow#printDelimiting(java.lang.String)
	 */
	public String printDelimiting(String delimitor) {
		if( getStdDev() != -1 )
			return getSpan() + delimitor + getMSP() + delimitor + getStdDev();
		else
			return getSpan() + delimitor + getMSP();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ui.results.TableModelRow#printExcell(int)
	 */
	public WritableCell[] printExcell(int rowIndex) {
		if( getStdDev() != -1 ) {
			WritableCell[] cells = new WritableCell[3];
			rowIndex++;
			
			cells[0] = new Label(0, rowIndex, getSpan());
			cells[1] = new Number(1, rowIndex, getMSP());
			cells[2] = new Number(2, rowIndex, getStdDev());
			
			return cells;
		} else {
			WritableCell[] cells = new WritableCell[2];
			rowIndex++;
			
			cells[0] = new Label(0, rowIndex, getSpan());
			cells[1] = new Number(1, rowIndex, getMSP());
			
			return cells;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see ui.results.TableModelRow#printXML()
	 */
	public String printXML() {
		String xml = "";
		
		xml += "<dataset>"+ getSpan() + "</dataset>";
		xml += "<msp>" + getMSP() + "</msp>";
		if( getStdDev() != -1 )
			xml += "<stddev>" + getStdDev() + "</stddev>";
		
		return xml;
	}

}
