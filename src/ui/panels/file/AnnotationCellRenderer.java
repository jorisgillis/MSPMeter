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
