package ui.panels.file;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.log4j.Logger;

import msp.defaults.Defaults;
import ui.panels.Panel;
import dataflow.Grid;
import dataflow.datastructures.Cell;
import dataflow.datastructures.FilesCell;

/**
 * Make a time line of files. 
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class FilePanel extends Panel implements ActionListener, TableModelListener {
	
	/** The Table */
	protected JTable table;
	/** The Model */
	protected FileTableModel model;
	/** The cell of the files */
	protected FilesCell filesCell;
	
	/** Directory in which the last file was selected */
	private String workingDirectory = null;
	/** Tracking changes. */
	private boolean changed = false;
	
	// Logger
	private static Logger logger = Logger.getLogger(FilePanel.class);
	
	/**
	 * Construct a new FilePanel
	 */
	public FilePanel() {
		//- Calling the super
		super();
		
		//- Defaults
		try {
			workingDirectory = Defaults.instance().getString("working directory");
		} catch( Exception e ) {
			workingDirectory = null;
		}
		
		// TODO working directory 
		
		//- Cell
		filesCell = new FilesCell();
		Grid.instance().addCell(filesCell);
		
		
		//- Setting up the GUI
		JPanel buttonPanel = setupButtons();
		
		// file table
		model = new FileTableModel();
		model.addTableModelListener(this);
		table = new JTable();
		table.setModel(model);
		tidyUpTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		model.addTableModelListener(table);
		JScrollPane listPane = new JScrollPane(table);
		
		
		this.setLayout(new BorderLayout());
		this.add(buttonPanel, BorderLayout.EAST);
		this.add(listPane, BorderLayout.CENTER);
	}
	
	/**
	 * Set up the button panel. 
	 * @return	button panel
	 */
	private JPanel setupButtons() {
		// buttons
		JButton addFile = new JButton("Add File");
		JButton remFile = new JButton("Remove File");
		JButton remAllFiles = new JButton("Remove All");
		JButton up = new JButton("Move Up");
		JButton down = new JButton("Move Down");
		
		addFile.setActionCommand("add file");
		remFile.setActionCommand("remove file");
		remAllFiles.setActionCommand("remove all");
		up.setActionCommand("move up");
		down.setActionCommand("move down");
		
		addFile.addActionListener(this);
		remFile.addActionListener(this);
		remAllFiles.addActionListener(this);
		up.addActionListener(this);
		down.addActionListener(this);
		
		// button panel
		JPanel buttonPanel = new JPanel();
		GridBagLayout l = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		buttonPanel.setLayout(l);
		c.gridy = 0;
		buttonPanel.add(addFile, c);
		c.gridy = 1;
		buttonPanel.add(remFile, c);
		c.gridy = 2;
		buttonPanel.add(remAllFiles, c);
		c.gridy = 3;
		buttonPanel.add(up, c);
		c.gridy = 4;
		buttonPanel.add(down, c);
		return buttonPanel;
	}
	
	/**
	 * Set the column widths of the table
	 */
	private void tidyUpTable() {
		table.getColumnModel().getColumn(0).setPreferredWidth(20);
		table.getColumnModel().getColumn(1).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setCellRenderer(new FileCellRenderer());
		table.getColumnModel().getColumn(2).setPreferredWidth(120);
		table.getColumnModel().getColumn(2).setCellRenderer(new AnnotationCellRenderer());
	}
	
	
	//===========================================================================
	// Main functionality
	//===========================================================================
	/*
	 * (non-Javadoc)
	 * @see ui.panels.Panel#continu()
	 */
	public void apply() {
		// creating the flow!
		try {
			if( changed ) {
				changed = false;
				filesCell.setValue(model.getFiles());
				Grid.instance().cellChanged( new Cell[]{filesCell} );
			}
		} catch( Exception e ) {
			showWarning(e.getMessage());
		}
	}
	
	
	//===========================================================================
	// Interactivity
	//===========================================================================
	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.TableModelListener
	 * #tableChanged(javax.swing.event.TableModelEvent)
	 */
	public void tableChanged(TableModelEvent e) {
		// Something changed in the table, thus something changed
		// with respect to the files
		changed = true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if( command.equals("add file") )
			addFiles();
		else if( command.equals("remove file") )
			removeFiles();
		else if( command.equals("remove all") )
			removeAllFiles();
		else if( command.equals("move up") )
			moveFilesUp();
		else if( command.equals("move down") )
			moveFilesDown();
	}
	
	/**
	 * Add a file to the model.
	 * Summon a file chooser and add the selected file to the model.
	 */
	private void addFiles() {
		// making the file chooser
		JFileChooser chooser = null;
		if( workingDirectory == null )
			chooser = new JFileChooser();
		else
			chooser = new JFileChooser(workingDirectory);
		chooser.setMultiSelectionEnabled(true);
		
		// filtering files
	    FreqFileFilter filter = new FreqFileFilter();
	    chooser.setFileFilter(filter);
	    
	    // summon the filechooser
	    int returnVal = chooser.showOpenDialog(this);
	    
	    // if a file has been selected
	    if( returnVal == JFileChooser.APPROVE_OPTION && 
	    		chooser.getSelectedFiles() != null ) {
	    	File[] selected = chooser.getSelectedFiles();
	    	addFiles(selected);
	    }
	}
	
	/**
	 * Add files to the panel. (Internal usage)
	 * @param files	new files
	 */
	private void addFiles(File[] files) {
		if( files != null && files.length > 0 ) {
			int bias = model.getRowCount();
			for( int i = 0; i < files.length; i++ )
				model.addRow(new Object[]{(bias+i+1), files[i], 
								files[i].getName()});
		}
	}
	
	/**
	 * Let the ProjectReader deliver its files.
	 * @param files	the new files
	 */
	public void addFiles( Vector<FileRow> files ) {
		model.addRows(files);
	}
	
	/**
	 * Delete a file from the model.
	 * Get the indices from the list and loop through the indices, 
	 * removing them one by one.
	 */
	private void removeFiles() {
		ListSelectionModel selector = table.getSelectionModel();
		int min = selector.getMinSelectionIndex();
		int max = selector.getMaxSelectionIndex();
		
		if( min != -1 && max != -1 ) {
			// removing the rows
			for( int i = max; i >= min; i-- )
				model.removeRow(i);
			
			// adjusting the order numbers of the trailing rows
			for( int i = min; i < model.getRowCount(); i++ )
				model.setValueAt(i+1, i, 0);
		}
	}
	
	/**
	 * Removes all the files from the model.
	 */
	private void removeAllFiles() {
		for( int i = table.getRowCount() - 1; i >= 0; i-- )
			model.removeRow(i);
	}
	
	/**
	 * Move a batch of files up a notch
	 * Get the indices from the list and loop through them. 
	 * Move each item from index to index-1.
	 */
	private void moveFilesUp() {
		ListSelectionModel selector = table.getSelectionModel();
		int min = selector.getMinSelectionIndex();
		int max = selector.getMaxSelectionIndex();
		
		if( min != -1 && max != -1 && 0 < min ) {
			model.moveUp(min, max);
			selector.setSelectionInterval(min-1, max-1);
		}
	}
	
	/**
	 * Move a batch of files down a notch
	 * Get the indices from the list and loop through them. 
	 * Move each item from index to index+1.
	 */
	private void moveFilesDown() {
		ListSelectionModel selector = table.getSelectionModel();
		int min = selector.getMinSelectionIndex();
		int max = selector.getMaxSelectionIndex();
		
		if( min != -1 && max != -1 && max < model.getRowCount() - 1 ) {
			model.moveDown(min, max);
			selector.setSelectionInterval(min+1, max+1);
		}
	}
}
