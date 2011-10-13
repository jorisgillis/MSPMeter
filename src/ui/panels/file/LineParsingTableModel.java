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

package ui.panels.file;

import java.util.LinkedList;

import javax.swing.table.DefaultTableModel;

/**
 * The model underlying the live line parsing table. 
 * @author Joris Gillis
 */
public class LineParsingTableModel extends DefaultTableModel {
	
	protected LinkedList<ParseRow> rows;
	
	
	public LineParsingTableModel() {
		super();
		rows = new LinkedList<ParseRow>();
	}
	
}
