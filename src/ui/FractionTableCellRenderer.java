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


package ui;

import java.text.NumberFormat;

import javax.swing.table.DefaultTableCellRenderer;

/**
 * Cell renderer that renderers Double's into an appropriate 3-floating-points notation
 * @author Joris Gillis
 */
public class FractionTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -7724463174977429789L;
	private NumberFormat formatter;
	
	/**
	 * Construct a new renderer
	 */
	public FractionTableCellRenderer() {
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.DefaultTableCellRenderer#setValue(java.lang.Object)
	 */
	public void setValue(Object value) {
		if ( formatter == null ) {
			formatter = NumberFormat.getInstance();
			formatter.setMaximumFractionDigits(3);
			formatter.setMinimumFractionDigits(3);
		}
		
		if( value instanceof Double && ((Double)value).doubleValue() >= -0.0001 )
			setText(formatter.format(value));
		else 
			setText("");
	}
	
}
