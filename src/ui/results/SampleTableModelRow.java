package ui.results;

import java.text.NumberFormat;

import jxl.write.Label;
import jxl.write.WritableCell;

/**
 * A row in the SampleTableModel: (Dataset, Sample Number, MSP) 
 * @author joris
 */
public class SampleTableModelRow implements TableModelRow {
	
	protected String dataset;
	protected int sampleNR;
	protected double MSP;
	
	// Number Formatter
	private static NumberFormat format = NumberFormat.getInstance();
	static {
		format.setMaximumFractionDigits(10);
		format.setMinimumFractionDigits(10);
		format.setMinimumIntegerDigits(1);
	}
	
	/**
	 * Constructs a new row. 
	 * @param dataset	dataset
	 * @param sampleNR	sample number
	 * @param MSP		MSP value of sample
	 */
	public SampleTableModelRow( String dataset, int sampleNR, double MSP ) {
		this.dataset = dataset;
		this.sampleNR = sampleNR;
		this.MSP = MSP;
	}
	
	
	public String getDataset() {
		return dataset;
	}
	
	public int getSampleNR() {
		return sampleNR;
	}
	
	/**
	 * Formatted MSP value.
	 * @return	MSP value
	 */
	public String getMSP() {
		return format.format(MSP);
	}
	
	/*
	 * (non-Javadoc)
	 * @see ui.results.TableModelRow#printDelimiting(java.lang.String)
	 */
	public String printDelimiting(String delimitor) {
		return getDataset() + delimitor + getSampleNR() + delimitor + getMSP();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ui.results.TableModelRow#printXML(java.util.List)
	 */
	public String printXML() {
		String xml = "";
		
		// dataset
		xml += "<dataset>" + getDataset() + "</dataset>";
		xml += "<samplenr>" + getSampleNR() + "</samplenr>";
		xml += "<msp>" + getMSP() + "</msp>";
		
		return xml;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ui.results.TableModelRow#printExcell(int)
	 */
	public WritableCell[] printExcell(int rowIndex) {
		WritableCell[] cells = new WritableCell[3];
		rowIndex++;
		
		cells[0] = new Label(0, rowIndex, getDataset());
		cells[1] = new jxl.write.Number(1, rowIndex, getSampleNR());
		cells[2] = new jxl.write.Number(2, rowIndex, MSP);
		
		return cells;
	}
}
