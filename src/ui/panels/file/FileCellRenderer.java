package ui.panels.file;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Render the contents of a cell. Files are stored in the model. The filenames will be shown in the list.
 * @author Joris Gillis
 */
public class FileCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 990281092689321123L;

	/**
	 * Render a File into a cell
	 */
	public FileCellRenderer() {
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
		setText(((File)value).getName());
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
