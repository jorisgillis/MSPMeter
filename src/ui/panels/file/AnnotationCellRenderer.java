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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Rendering an Annotation cell.
 * @author Joris Gillis
 */
public class AnnotationCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -6242311119112572638L;

	/**
	 * Render an Annotation into a cell
	 */
	public AnnotationCellRenderer() {
		super();
		setOpaque(true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
		setText((String)value);
		if( isSelected ) {
			setBackground(isSelected ? Color.DARK_GRAY : Color.WHITE);
			setForeground(isSelected ? Color.WHITE : Color.BLACK);
		} else {
			setBackground(row % 2 == 0 ? new Color(240, 240, 240) : Color.WHITE);
			setForeground(row % 2 == 0 ? Color.BLACK : Color.BLACK);
		}

		return this;
	}
	
}
