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

import io.project.ProjectReader;
import io.project.ProjectWriter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import msp.defaults.Defaults;
import msp.defaults.NoValueException;

import org.xml.sax.SAXException;

import ui.panels.Panel;
import ui.panels.equivalences.CategoryEquivalencesPanel;
import ui.panels.equivalences.LemmaEquivalencesPanel;
import ui.panels.file.FilePanel;
import ui.panels.file.LineParsingPanel;
import ui.panels.version.VersionPanel;
import ui.results.StatusBar;
import dataflow.Grid;

/**
 * The main window of the MSPMeter. It consists of multiple panels which have to be 
 * walked through in a certain order. 
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class MSPMeterWindow extends Window implements ActionListener, ChangeListener {
	
	
	//===========================================================================
	// Variables
	//===========================================================================
	/** Where is the project located? null means it has not yet been saved. */
	protected String projectFile = null;
	
	/** List of all the panels, in order */
	protected ArrayList<Panel> panels;
	/** At which position in the panel order are we? */
	protected int panelPosition;
	
	
	/** Go to previous panel */
	protected JButton prev;
	/** Go to next panel */
	protected JButton next;
	/** Apply the changes */
	protected JButton calculate;
	
	/** Go to the previous panel */
	protected JMenuItem mPrev;
	/** Go to the next panel */
	protected JMenuItem mNext;
	/** Apply the values of the current panel */
	protected JMenuItem mCalculate;
	
	/** Tabs */
	protected JTabbedPane tabPanel;
	/** Main panel */
	protected JPanel mainPanel;
	/** Status bar */
	protected StatusBar statusBar;
	
	/** Window showing help information */
	protected HelpWindow helpWindow;
	
	
	// PANELS
	protected FilePanel fp;
	protected LineParsingPanel lpp;
	protected LemmaEquivalencesPanel lep;
	protected CategoryEquivalencesPanel cep;
	protected VersionPanel vp;
	
	
	//===========================================================================
	// Construction
	//===========================================================================
	
	/**
	 * Construct a new window:
	 * - Initialize the Grid
	 * - Initialize the menu bar
	 * - Initialize the panels
	 * - Show the first panel
	 */
	public MSPMeterWindow() {
		//- Title
		super("MSPMeter");
		
		//- Panels
		setupPanels();
		
		//- Help
		helpWindow = new HelpWindow();
		
		//- making the GUI
		// main tab panel
		setupMainTabPanel();
		
		
		// menu bar
		setupMenuBar();
		
		// statusbar
		statusBar = StatusBar.getInstance();
		
		// putting it all together
		this.setLayout(new BorderLayout());
		this.add(mainPanel, BorderLayout.CENTER);
		this.add(statusBar, BorderLayout.SOUTH);
		
		
		//- Some Mac only stuff
//		if( System.getProperty("os.name").equals("Mac OS X") )
//			System.setProperty("apple.laf.useScreenMenuBar", "true");
		
		
		//- doing frame stuff
		tabPanel.setPreferredSize(new Dimension(600, 350));
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * Setting up the MenuBar
	 */
	private void setupMenuBar() {
		JMenuBar bar = new JMenuBar();
		this.setJMenuBar(bar);
		
		// file menu
		JMenu file = new JMenu("File");
		bar.add(file);
		
		mPrev = new JMenuItem("Previous");
		mPrev.setEnabled(false);
		mNext = new JMenuItem("Next");
		mCalculate = new JMenuItem("Calculate");
		
		JMenuItem mSaveProject = 
			new JMenuItem("Save Project");
		JMenuItem mSaveProjectAs = 
			new JMenuItem("Save Project As ...");
		JMenuItem mSaveLemmas = 
			new JMenuItem("Save Lemma Equivalences");
		JMenuItem mSaveLemmasAs = 
			new JMenuItem("Save Lemma Equivalences As ...");
		JMenuItem mSaveCategories = 
			new JMenuItem("Save Category Equivalences");
		JMenuItem mSaveCategoriesAs = 
			new JMenuItem("Save Category Equivalences As ...");
		
		JMenuItem mOpenProject = new JMenuItem("Open Project");
		
		JMenuItem mQuit = new JMenuItem("Quit");
		
		// adding shortcuts
		int actionKey = 0;
		int doubleActionKey = 0;
		if( System.getProperty("os.name").equals("Mac OS X") ) {
			// Mac: META key
			actionKey = ActionEvent.META_MASK;
			doubleActionKey = ActionEvent.META_MASK | ActionEvent.ALT_MASK;
		} else {
			// Other: CONTROL key
			actionKey = ActionEvent.CTRL_MASK;
			doubleActionKey = ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK;
		}
		
		mPrev.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, actionKey));
		mNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, actionKey));
		mCalculate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, actionKey | doubleActionKey));
		mSaveProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, actionKey));
		mSaveProjectAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, doubleActionKey));
		mSaveLemmas.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, actionKey));
		mSaveLemmasAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, doubleActionKey));
		mSaveCategories.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, actionKey));
		mSaveCategoriesAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, doubleActionKey));
		mOpenProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, actionKey));
		mQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, actionKey));
		
		// Adding the menuitems to the menu
		file.add(mPrev);
		file.add(mNext);
		file.add(mCalculate);
		file.add(new JSeparator());
		file.add(mOpenProject);
		file.add(mSaveProject);
		file.add(mSaveProjectAs);
		file.add(new JSeparator());
		file.add(mSaveLemmas);
		file.add(mSaveLemmasAs);
		file.add(mSaveCategories);
		file.add(mSaveCategoriesAs);
		file.add(new JSeparator());
		file.add(mQuit);
		
		// Adding command to the items
		mPrev.setActionCommand("previous");
		mNext.setActionCommand("next");
		mCalculate.setActionCommand("apply");
		mSaveProject.setActionCommand("save project");
		mSaveProjectAs.setActionCommand("save project as");
		mSaveLemmas.setActionCommand("save lemmas");
		mSaveLemmasAs.setActionCommand("save lemmas as");
		mSaveCategories.setActionCommand("save categories");
		mSaveCategoriesAs.setActionCommand("save categories as");
		mOpenProject.setActionCommand("open project");
		mQuit.setActionCommand("quit");
		
		// making this class listen to the items
		mPrev.addActionListener(this);
		mNext.addActionListener(this);
		mCalculate.addActionListener(this);
		mSaveProject.addActionListener(this);
		mSaveProjectAs.addActionListener(this);
		mSaveLemmas.addActionListener(this);
		mSaveLemmasAs.addActionListener(this);
		mSaveCategories.addActionListener(this);
		mSaveCategoriesAs.addActionListener(this);
		mOpenProject.addActionListener(this);
		mQuit.addActionListener(this);
		
		// Help menu
		JMenu helpMenu = new JMenu("Help");
		
		JMenuItem help = new JMenuItem("Help");
		help.setActionCommand("help");
		help.addActionListener(this);
		
		helpMenu.add(help);
		bar.add(helpMenu);
	}
	
	/**
	 * Setting up the MainTabPanel
	 */
	private void setupMainTabPanel() {
		String[] tabTitles = new String[]{
					"Files", 
					"Line Parsing", 
					"Lemma Equivalences", 
					"Category Equivalences", 
					"MSP Version"};
		tabPanel = new JTabbedPane();
		for( int i = 0; i < panels.size(); i++ )
			tabPanel.addTab(tabTitles[i], panels.get(i));
		
		tabPanel.addChangeListener(this);
		
		// main panel
		mainPanel = new JPanel();
		
		prev = new JButton("Previous");
		prev.setActionCommand("previous");
		prev.addActionListener(this);
		prev.setEnabled(false);
		calculate = new JButton("Calculate");
		calculate.setActionCommand("calculate");
		calculate.addActionListener(this);
		next = new JButton("Next");
		next.setActionCommand("next");
		next.addActionListener(this);
		
		JPanel buttonPanel = new JPanel();
		BorderLayout borderL = new BorderLayout();
		buttonPanel.setLayout(borderL);
		buttonPanel.add(prev, BorderLayout.WEST);
		buttonPanel.add(calculate, BorderLayout.CENTER);
		buttonPanel.add(next, BorderLayout.EAST);
		
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		mainPanel.add(tabPanel, BorderLayout.CENTER);
	}
	
	/**
	 * Setting up the panels, coming inside the MainTabPanel. 
	 */
	private void setupPanels() {
		panels = new ArrayList<Panel>();
		panelPosition = 0;
		
		fp = new FilePanel();
		lpp = new LineParsingPanel();
		lep = new LemmaEquivalencesPanel();
		cep = new CategoryEquivalencesPanel();
		vp = new VersionPanel();
		panels.add(fp);
		panels.add(lpp);
		panels.add(lep);
		panels.add(cep);
		panels.add(vp);
	}
	
	
	
	
	//===========================================================================
	// Main functionality
	//===========================================================================
	/**
	 * If an action is performed, find out which action and react to the action.
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if( command.equals("next") )
			nextPanel();
		else if( command.equals("previous") )
			prevPanel();
		else if( command.equals("calculate") )
			calculate();
		else if( command.equals("open project") )
			open();
		else if( command.equals("save project") )
			save();
		else if( command.equals("save project as") )
			saveAs();
		else if( command.equals("save lemmas") )
			lep.save();
		else if( command.equals("save lemmas as") )
			lep.saveAs();
		else if( command.equals("save categories") )
			cep.save();
		else if( command.equals("save categories as") )
			cep.saveAs();
		else if( command.equals("help") )
			helpWindow.setVisible(true);
		else if( command.equals("quit") ) {
			if( saveOrDiscard() )
				System.exit(0);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		//- 1. Applying the changes in the current tab
		panels.get(panelPosition).apply();
		
		//- 2. Moving to the choosen tab
		panelPosition = tabPanel.getSelectedIndex();
		
		//- 3. Setting the buttons
		buttonMenuControl();
	}
	
	
	/**
	 * Advance to next panel. Hide the current panel and make the next one 
	 * visible.
	 * Increment the panelPosition counter by one.
	 */
	protected synchronized void nextPanel() {
		if( panelPosition >= 0 && panelPosition < panels.size()-1 ) {
			((Panel)tabPanel.getSelectedComponent()).apply();
			panelPosition++;
			tabPanel.setSelectedIndex(panelPosition);
			buttonMenuControl();
		}
	}
	
	/**
	 * Decline to the previous panel. Hide the current panel and make the 
	 * previous one visible. 
	 * Decrement the panelPosition counter by one.
	 */
	protected synchronized void prevPanel() {
		if( panelPosition > 0 && panelPosition < panels.size() ) {
			((Panel)tabPanel.getSelectedComponent()).apply();
			panelPosition--;
			tabPanel.setSelectedIndex(panelPosition);
			buttonMenuControl();
		}
	}
	
	/**
	 * Controls the enabling and disabling of the next/previous button and 
	 * menu item.
	 */
	protected synchronized void buttonMenuControl() {
		// disable next button if necessary
		if( panelPosition == panels.size()-1 ) {
			next.setEnabled(false);
			mNext.setEnabled(false);
		} else if( !next.isEnabled() ) {
			next.setEnabled(true);
			mNext.setEnabled(true);
		}
		// disable previous button if necessary
		if( panelPosition == 0 ) {
			prev.setEnabled(false);
			mPrev.setEnabled(false);
		} else if( !prev.isEnabled() ) {
			prev.setEnabled(true);
			mPrev.setEnabled(true);
		}
	}
	
	
	/**
	 * Apply all the panels
	 */
	protected synchronized void calculate() {
		// Let the panels put there values in the cells.  
//		for( int i = 0; i < panels.size(); i++ )
//			panels.get(i).apply();
		
		// Only the currently selected panel can have changes
		// for the cells. 
		((Panel)tabPanel.getSelectedComponent()).apply();
		
		// Let the grid recalculate!
		Grid.instance().recalculate();
	}
	
	
	//===========================================================================
	// File Management
	//===========================================================================
	/**
	 * Ask the user for a project file, and read in the project file.
	 */
	public void open() {
		// Asking the user whether he knows that this will discard current changes
		int ok = JOptionPane.OK_OPTION;
		if( Grid.instance().changes() )
			ok = JOptionPane.showConfirmDialog(
					this, 
					"Are you sure you want to open a project?\n"+
					"Opening a project will discard the changes "+
					"to the current project.");
		
		
		if( ok == JOptionPane.OK_OPTION ) {
			// Getting a file to open
			File f;
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "Project File", "xml");
			if (projectFile == null) {
				// Defaults: working directory
				String workingDirectory = null;
				try {
					workingDirectory = 
						Defaults.instance().getString("working directory");
				} catch (NoValueException e1) {
					e1.printStackTrace();
				}
				f = askForOpenFile( workingDirectory, filter );
			} else
				// Start from file
				f = askForOpenFile( projectFile, filter );
			
			if( f != null ) {
				try {
					projectFile = f.getAbsolutePath();
					ProjectReader r = new ProjectReader(fp, lpp, lep, cep, vp);
					r.readProject(projectFile);
					
					StatusBar.getInstance().addStatus("Opened project "+ projectFile);
				} catch( SAXException e ) {
					showWarning("Fault while reading the project file:\n"+ 
							e.getMessage());
				} catch( IOException e ) {
					showWarning("Fault while reading the project file:\n"+ 
							e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Saves all the project information to a file. If the file is known, that file
	 * is used, otherwise the user is asked to input a file.
	 */
	public void save() {
		if( projectFile == null ) {
			// ask for file
			FileNameExtensionFilter filter = 
				new FileNameExtensionFilter("Project File", "xml");
			String workingDirectory = null;
			try {
				workingDirectory = 
					Defaults.instance().getString("working directory");
			} catch (NoValueException e1) {
				e1.printStackTrace();
			}
			File f = askForSaveFile(workingDirectory, filter);
			if( f != null )
				projectFile = f.getAbsolutePath();
			
			try {
				// Checking the proposed file
				f = checkProjectFile(f);
				projectFile = f.getAbsolutePath();
			} catch( ValidationException e ) {
				projectFile = null;
			}
		}
		
		// Saving the project!
		if( projectFile != null )
			// If there is a project file, save to that file!
			saveProject(new File(projectFile));
	}
	
	/**
	 * Ask the user for a file, and save the current project to that file.
	 */
	public void saveAs() {
		// ask for file
		File f;
		FileNameExtensionFilter filter = 
			new FileNameExtensionFilter("Project File", "xml");
		if (projectFile == null) {
			// Defaults: working directory
			String workingDirectory = null;
			try {
				workingDirectory = 
					Defaults.instance().getString("working directory");
			} catch (NoValueException e1) {
				e1.printStackTrace();
			}
			f = askForSaveFile( workingDirectory, filter );
		} else
			// Start from previous project file file
			f = askForSaveFile( projectFile, filter );
		
		try {
			// Checking
			f = checkProjectFile(f);
			
			// Saving
			projectFile = f.getAbsolutePath();
			saveProject(f);
		} catch(ValidationException e) {
			if (e.getMessage() != null)
				StatusBar.getInstance().addStatus(e.getMessage());
		}
	}

	/**
	 * Checks whether the given file is correct for saving a project to.
	 * @param f		project file to examine
	 * @return		Possibly adjusted file path
	 * @throws ValidationException
	 */
	protected File checkProjectFile(File f) throws ValidationException {
		// 1. No directory
		if (f.isDirectory())
			throw new ValidationException(
					"Choosen file is a directory! Nothing saved.");
		
		// 2. If f does not end in ".xml"
		if (!f.getName().endsWith(".xml"))
			f = new File(f.getAbsolutePath() + ".xml");
		
		// 3. Already exists?
		if (f.exists())
			if (!overwrite(f))
				throw new ValidationException(
						f.getName() +" NOT overwritten. Nothing saved.");
		return f;
	}
	
	/**
	 * Saves the project to the given file.  
	 * @param f		file to which we save
	 */
	private void saveProject(File f) {
		if( f != null ) {
			try {
				// opening the writer and writing it out
				calculate();	// get the latest information
				ProjectWriter w = new ProjectWriter(projectFile);
				w.writeProject();
				
				Grid.instance().saved();
				StatusBar.getInstance().addStatus("Saved project to "+ projectFile);
			} catch( FileNotFoundException e ) {
				showWarning("Fault while saving project: \n"+ e.getMessage());
			} catch( IOException e ) {
				showWarning("Fault while saving project: \n"+ e.getMessage());
			}
		}
	}
	
	/**
	 * Ask the user for a file to open, but don't give him a suggestion.
	 * @return	the file selected by the user
	 */
	protected File askForOpenFile() {
		return askForOpenFile(null, null);
	}
	
	
	/**
	 * Ask the user for a file to save to, but don't give him a suggestion.
	 * @return	the file selected by the user
	 */
	protected File askForSaveFile() {
		return askForSaveFile(null, null);
	}
	
	
	/**
	 * Ask the user for a file, the given string is a suggestion of a location 
	 * where the file could be. If the suggestion <code>== null</code>, then no 
	 * suggestion is made. 
	 * @param suggestion	the suggestion
	 * @param filter		filters files based on extension 
	 * @return				the file selected by the user
	 */
	protected File askForOpenFile(String suggestion, 
									FileNameExtensionFilter filter) {
		// Setting up the file chooser
		JFileChooser fc = null;
		if( suggestion == null )
			fc = new JFileChooser();
		else
			fc = new JFileChooser( suggestion );
		
		// Adding the filter
		if (filter != null)
			fc.setFileFilter(filter);
		
		// Opening a dialog
		if( fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
			return fc.getSelectedFile();
		
		return null;
	}
	
	
	/**
	 * Ask the user for a file, the given string is a suggestion of a location where the file could be.
	 * If the suggestion <code>== null</code>, then no suggestion is made. 
	 * @param suggestion	the suggestion
	 * @param filter		filters files based on extension
	 * @return				the file selected by the user
	 */
	protected File askForSaveFile(String suggestion, 
									FileNameExtensionFilter filter) {
		// Setting up the file chooser
		JFileChooser fc = null;
		if( suggestion == null )
			fc = new JFileChooser();
		else
			fc = new JFileChooser( suggestion );
		
		// Adding the filter
		if (filter != null)
			fc.setFileFilter( filter );
		
		// Opening a dialog
		if( fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION )
			return fc.getSelectedFile();
		
		return null;
	}
	
	/**
	 * Ask the user whether he wants to save his project or not.
	 */
	protected boolean saveOrDiscard() {
		int r = JOptionPane.showConfirmDialog(
					this, 
					"Do you want to save your project?",
					"Project Saving", 
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
		if( r == JOptionPane.YES_OPTION )
			// save and exit
			save();
		else if( r == JOptionPane.CANCEL_OPTION )
			// don't exit
			return false;
		
		// exit
		return true;
	}
	
	/**
	 * Checks with the user whether it is okay to overwrite the given file.
	 * @param f		file that will be overwritten
	 * @return		true if ok, false otherwise
	 */
	protected boolean overwrite(File f) {
		int r = JOptionPane.showConfirmDialog(
					this, 
					"Do you want to overwrite file "+ f.getName() +"?", 
					"Project Saving", 
					JOptionPane.YES_NO_OPTION, 
					JOptionPane.WARNING_MESSAGE);
		return r == JOptionPane.YES_OPTION;
	}
}
