package ui.results;

import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import msp.data.MSPSpan;

/**
 * Row of the ResultTableModel. 
 * @author joris
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
		return getSpan() + delimitor + getMSP() + delimitor + getStdDev();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ui.results.TableModelRow#printExcell(int)
	 */
	public WritableCell[] printExcell(int rowIndex) {
		WritableCell[] cells = new WritableCell[3];
		rowIndex++;
		
		cells[0] = new Label(0, rowIndex, getSpan());
		cells[1] = new Number(1, rowIndex, getMSP());
		cells[2] = new Number(2, rowIndex, getStdDev());
		
		return cells;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ui.results.TableModelRow#printXML()
	 */
	public String printXML() {
		String xml = "";
		
		xml += "<dataset>"+ getSpan() + "</dataset>";
		xml += "<msp>" + getMSP() + "</msp>";
		xml += "<stddev>" + getStdDev() + "</stddev>";
		
		return xml;
	}

}
