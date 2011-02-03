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
