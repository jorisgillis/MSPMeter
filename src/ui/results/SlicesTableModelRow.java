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
