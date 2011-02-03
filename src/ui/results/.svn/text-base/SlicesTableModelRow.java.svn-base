package ui.results;

import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;

/**
 * Row in the SlicesTableModel.
 * @author joris
 */
public class SlicesTableModelRow implements TableModelRow {
	
	private String dataset;
	private int size, lemmas;
	
	/**
	 * Constructs a new row. 
	 * @param dataset	dataset
	 * @param size		size
	 * @param lemmas	number of lemmas
	 */
	public SlicesTableModelRow( String dataset, int size, int lemmas ) {
		this.dataset = dataset;
		this.size = size;
		this.lemmas = lemmas;
	}
	
	public String getDataset() {
		return dataset;
	}
	
	public int getLemmas() {
		return lemmas;
	}
	
	public int getSize() {
		return size;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ui.results.TableModelRow#printDelimiting(java.lang.String)
	 */
	public String printDelimiting(String delimitor) {
		return getDataset() + delimitor + getLemmas() + delimitor + getSize();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ui.results.TableModelRow#printExcell(int)
	 */
	public WritableCell[] printExcell(int rowIndex) {
		WritableCell[] cells = new WritableCell[3];
		rowIndex++;
		
		cells[0] = new Label(0, rowIndex, getDataset());
		cells[1] = new Number(1, rowIndex, getSize());
		cells[2] = new Number(2, rowIndex, getLemmas());
		
		return cells;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ui.results.TableModelRow#printXML()
	 */
	public String printXML() {
		String xml = "";
		
		xml += "<dataset>" + getDataset() + "</dataset>";
		xml += "<size>" + getSize() + "</size>";
		xml += "<lemmas>" + getLemmas() + "</lemmas>";
		
		return xml;
	}
}
