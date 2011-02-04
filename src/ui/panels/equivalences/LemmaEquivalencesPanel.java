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


package ui.panels.equivalences;

import io.project.LemmaWriter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;

import org.apache.log4j.Logger;

import ui.results.StatusBar;
import dataflow.Grid;
import dataflow.datastructures.AllLemmasCell;
import dataflow.datastructures.Cell;
import dataflow.datastructures.LemmaEquivalencesCell;
import dataflow.datastructures.LemmaFileCell;
import dataflow.datastructures.OriginalDataCubeCell;
import dataflow.datastructures.SubLemmasCell;
import dataflow.datastructures.UseInMSPCell;


/**
 * Load a file defining lemma equivalences. The user can then edit the equivalences
 * by checking and unchecking checkboxes representing other lemmas.
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class LemmaEquivalencesPanel extends EquivalencesPanel 
	implements FileRequestor, ItemListener, ActionListener, ChangeListener, Cell {
	
	/** Which lemma? */
	protected JComboBox lemmaBox;
	/** Model of the lemmaBox */
	protected ComboBoxModel comboModel;
	/** Use the lemma equivalences for defining category equivalences or for calculation? */
	protected JCheckBox useInMSP;
	/** All the checkboxes */
	protected JPanel checkPanel;
	/** Panel used for editing widgets */
	protected JPanel editPanel;
	/** Contains checkPanel */
	protected JScrollPane scrollPane;
	
	/** All the checkboxes */
	protected Vector<JCheckBox> checkBoxes = null;
	/** The previous lemma */
	protected String prevLemma = null;
	
	/** Indicates whether this component is blocked, due to bad input. */
	protected boolean blocked = false;
	/** Explanation about the blocking state. */
	protected String blockingMessage = null;
	
	
	/** Mapping from sublemma onto generic lemma. This one only contains which
	 * sublemmas have to be remapped in the DataCube. */
	protected HashMap<String, String> lemmaEquivalences;
	/** Mapping from generic lemma onto all its sublemmas. This one contains, as keys,
	 * at least all the lemmas that are available in the DataCube (due to Dependencies) */
	protected HashMap<String, Vector<String>> subLemmas;
	/** All the lemmas */
	protected HashSet<String> allLemmas;
	/** The file  in which the equivalences can be found. */
	protected String lemmaFile = null;
	
	/** The cell in the grid corresponding to the lemma equivalences. */
	protected LemmaEquivalencesCell lemmaEquivalencesCell;
	/** The cell in the grid corresponding to the data structure subLemmas */
	protected SubLemmasCell subLemmasCell;
	/** The cell in the grid corresponding to the data structure allLemmas */
	protected AllLemmasCell allLemmasCell;
	/** The cell in the grid corresponding to the useInMSP boolean. */
	protected UseInMSPCell useInMSPCell;
	/** The cell in the grid corresponding to the lemmaFile variable */
	protected LemmaFileCell lemmaFileCell;
	
	
	/** Remember whether something has changed */
	protected boolean changed = false;
	
	/** Used to load lemma from files. */
	protected Vector<String> lemmas = null;
	
	
	// Everywhere the same
	private Logger logger = Logger.getLogger(LemmaEquivalencesPanel.class);
	
	/**
	 * Construct a new panel for importing and editing lemma equivalences.
	 */
	public LemmaEquivalencesPanel() {
		//- Cells
		lemmaEquivalencesCell = new LemmaEquivalencesCell();
		subLemmasCell = new SubLemmasCell();
		allLemmasCell = new AllLemmasCell();
		useInMSPCell = new UseInMSPCell();
		lemmaFileCell = new LemmaFileCell();
		
		Grid.instance().addCell(lemmaEquivalencesCell);
		Grid.instance().addCell(subLemmasCell);
		Grid.instance().addCell(allLemmasCell);
		Grid.instance().addCell(useInMSPCell);
		Grid.instance().addHotCell(this);
		Grid.instance().addCell(lemmaFileCell);
		
		//- gui
		this.setLayout(new BorderLayout());
		fileLoadingPanel = new FileLoadingPanel(this, "Load Lemma Equivalences file");
		this.add(fileLoadingPanel, BorderLayout.NORTH);
		
		//- edit framework
		editPanel = new JPanel();
		editPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Edit Equivalences"));
		editPanel.setLayout(new BorderLayout());
		this.add(editPanel, BorderLayout.CENTER);
		
		//- editing tools
		// lemma choosing and updating
		JPanel lemmaChoicePanel = new JPanel();
		lemmaChoicePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH; c.gridx = 0; c.gridy = 0; c.weightx = 2; c.weighty = 1;
		
		comboModel = new ComboBoxModel();
		lemmaBox = new JComboBox(comboModel);
		lemmaBox.setPreferredSize(new Dimension(150, 30));
		lemmaBox.addItemListener(this);
		
		JButton update = new JButton("Update File");
		update.addActionListener(this);
		update.setActionCommand("update");
		
		lemmaChoicePanel.add(lemmaBox, c);
		c.gridx = 1; c.weightx = 1;
		lemmaChoicePanel.add(update, c);
		
		// option selection
		JPanel optionSelectionPanel = new JPanel();
		optionSelectionPanel.setLayout(new BorderLayout());
		
		JButton scratch = new JButton("From Scratch");
		scratch.addActionListener(this);
		scratch.setActionCommand("scratch");
		
		useInMSP = new JCheckBox("Use in MSP");
		useInMSP.setSelected(true);
		
		optionSelectionPanel.add(useInMSP, BorderLayout.WEST);
		optionSelectionPanel.add(scratch, BorderLayout.EAST);
		
		
		// option and lemma choosing together
		JPanel optionAndChoosingPanel = new JPanel();
		optionAndChoosingPanel.setLayout(new BorderLayout());
		optionAndChoosingPanel.add(optionSelectionPanel, BorderLayout.NORTH);
		optionAndChoosingPanel.add(lemmaChoicePanel, BorderLayout.SOUTH);
		
		// Checkbox-field
		checkPanel = new JPanel();	// empty for now
		scrollPane = new JScrollPane(checkPanel);
		
		// adding it all together
		editPanel.add(optionAndChoosingPanel, BorderLayout.NORTH);
		editPanel.add(scrollPane, BorderLayout.CENTER);
		
		// data structures
		lemmaEquivalences = new HashMap<String, String>();
		subLemmas = new HashMap<String, Vector<String>>();
		allLemmas = new HashSet<String>();
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see ui.panels.Panel#apply()
	 */
	@Override
	public void apply() {
		if( changed ) {
			// Download and save the current situation of the board
			downloadAndSave();
			
			// notify
			try {
				useInMSPCell.setValue(useInMSP.isSelected());
				lemmaEquivalencesCell.setValue(lemmaEquivalences);
				subLemmasCell.setValue(subLemmas);
				allLemmasCell.setValue(allLemmas);
				
				Grid.instance().cellChanged(new Cell[]{
						useInMSPCell,
						lemmaEquivalencesCell,
						subLemmasCell,
						allLemmasCell} );
			} catch( Exception e ) {
				showWarning(e.getMessage());
			}
		}
	}
	
	
	/**
	 * Ask the user where to save the file, and write it out!
	 */
	public void save() {
		if( changed ) {
			if( lemmaFile == null ) {
				// no file known, ask the user about it
				File f = askForFile();
				try {
					if( f != null ) {
						lemmaFile = f.getAbsolutePath();
						lemmaFileCell.setValue(lemmaFile);
					}
				} catch( Exception e ) {
					logger.error("An error occurred while setting the lemmaFileCell: "+ e.getMessage());
					showWarning(e.getMessage());
				} finally {
					lemmaFile = null;
				}
			}
			
			// TODO applying before saving, but not getting in an infinite loop!
			
			if( lemmaFile != null ) {
				try {
					// send the datastructures to the correct function
					LemmaWriter w = new LemmaWriter();
					w.writeLemmas(lemmaFile, subLemmas, allLemmas);
					
					changed = false;
					StatusBar.getInstance().addStatus("Saved the lemma equivalences to "+ lemmaFile);
				} catch( FileNotFoundException e ) {
					showWarning("Fault while saving lemma equivalences: \n"+ e.getMessage());
				} catch( IOException e ) {
					showWarning("Fault while saving lemma equivalences: \n"+ e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Ask the user for a file, and save the data structures to that file.
	 */
	public void saveAs() {
		File f = askForFile( lemmaFile );
		if( f != null ) {
			try {
				// getting the filename
				lemmaFile = f.getAbsolutePath();
				lemmaFileCell.setValue(lemmaFile);
				
				// send the datastructures to the correct function
				LemmaWriter w = new LemmaWriter();
				w.writeLemmas(lemmaFile, subLemmas, allLemmas);
				
				changed = false;
				StatusBar.getInstance().addStatus("Saved the lemma equivalences to "+ lemmaFile);
			} catch( FileNotFoundException e ) {
				showWarning("Fault while saving lemma equivalences: \n"+ e.getMessage());
			} catch( IOException e ) {
				showWarning("Fault while saving lemma equivalences: \n"+ e.getMessage());
			} catch (DataFaultException e) {
				logger.error("An error occurred while setting the lemmaFileCell: "+ e.getMessage());
				showWarning(e.getMessage());
			} catch (ImpossibleCalculationException e) {
				logger.error("An error occurred while setting the lemmaFileCell: "+ e.getMessage());
				showWarning(e.getMessage());
			} catch (RestrictionViolation e) {
				logger.error("An error occurred while setting the lemmaFileCell: "+ e.getMessage());
				showWarning(e.getMessage());
			}
		}
	}
	
	//===========================================================================
	// Reactional functions
	//===========================================================================
	/*
	 * (non-Javadoc)
	 * @see ui.panels.equivalences.FileRequestor#receiveFile(java.io.File)
	 */
	public void receiveFile( File file ) {
		// Receive the file:
		// 	1. read in the list in the file
		//	2. add a mapping from each sublemma into the generic lemma
		//	3. add the sublemma to the lemma equivalences of the generic lemma
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			
			// clean out the combobox
			comboModel.removeAllElements();
			
			// clearing out the data structures
			lemmaEquivalences.clear();
			subLemmas.clear();
			allLemmas.clear();
			
			
			String line = "";
			while( (line = reader.readLine()) != null ) {
				// generic_lemma TAB lemma1_lemma2_...
				// get the generic lemma out of there
				String[] split = line.split("\t");
				if( split.length != 2 )
					throw new InputException("Each line must contain two lemmas. In the simplest case, twice the same to indicate" +
							" that the lemma is generic.");
				
				String generic = split[0];
				allLemmas.add(generic);
				
				// for each generic lemma add a new vector
				if( !subLemmas.containsKey(generic) )
					subLemmas.put(generic, new Vector<String>());
				
				// get the sublemmas out of there
				String[] equivs = split[1].split("_");
				
				for( int i = 0; i < equivs.length; i++ )
					if( !equivs[i].equals(generic) ) {
						lemmaEquivalences.put(equivs[i], generic);			// from sub to generic
						subLemmas.get(generic).add(equivs[i]);				// from generic to sub
						allLemmas.add(equivs[i]);
					}
			}
			
			changed = false;
			reader.close();
			
			lemmaFile = file.getAbsolutePath();
			lemmaFileCell.setValue(lemmaFile);
			
			blocked = false;
			blockingMessage = null;
			fillUpComboBox();
			
//			logger.info("Equivalences: ");
//			logger.info(lemmaEquivalences);
//			logger.info(subLemmas);
		} catch( FileNotFoundException e ) {
			showWarning("The requested file was not found: \n"+ e.getMessage());
		} catch( IOException e ) {
			showWarning("An error occurred while reading the file "+ file.getName() +": \n" + e.getMessage());
		} catch( InputException e ) { 
			blocked = true;
			clearCheckPanel();
			logger.warn("Wrong input: " + e.getMessage());
			showWarning(e.getMessage());
		} catch (DataFaultException e) {
			logger.error("An error occurred while setting the lemmaFileCell: "+ e.getMessage());
			showWarning(e.getMessage());
		} catch (ImpossibleCalculationException e) {
			logger.error("An error occurred while setting the lemmaFileCell: "+ e.getMessage());
			showWarning(e.getMessage());
		} catch (RestrictionViolation e) {
			logger.error("An error occurred while setting the lemmaFileCell: "+ e.getMessage());
			showWarning(e.getMessage());
		} finally {
			try {
				if( reader != null )
					reader.close();
			} catch( IOException e ) {}
		}
	}
	
	
	/**
	 * Get the list of all generic lemmas.
	 * @return	all generic lemmas
	 */
	protected Vector<String> getGenerics() {
		Vector<String> generics = new Vector<String>(subLemmas.keySet());
		return generics;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		if( e.getStateChange() == ItemEvent.SELECTED ) {
			logger.info("Lemma = "+ e.getItem());
			fillUpCheckPanel((String)e.getItem());
			changed = true;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		changed = true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if( command.equals("update") ) {
			// update!
			downloadAndSave();
			changed = true;
		}
		else if( command.equals("scratch") ) {
			// load all the lemmas from the files
			loadFromFiles();
			changed = true;
		}
	}
	
	
	//===========================================================================
	// GUI related
	//===========================================================================
	/**
	 * When a new set of equivalences has arrived, the combobox and checkPanel have to be adjusted.
	 */
	protected void fillUpComboBox() {
		// fill up the combobox
		Vector<String> lemmas = getGenerics();
		for( int i = 0; i < lemmas.size(); i++ )
			comboModel.addElement(lemmas.get(i));
	}
	
	/**
	 * When a lemma has been selected by the combobox, the checkPanel should be updated.
	 * @param lemma	newly selected lemma
	 */
	protected void fillUpCheckPanel( String lemma ) {
		// download and save
		downloadAndSave();
		
		// fill up for the new lemma
		if( lemmaEquivalences.containsKey(lemma) ) {
			// this is a sublemma (a lemma contained in a generic lemma)
			JLabel subLemma = new JLabel("Lemma is contained in another lemma");
			checkPanel.add(subLemma);
			checkPanel.remove(scrollPane);
		} else {
			// generate all the checkboxes
			checkPanel = new JPanel();
			
			// layouting
			checkPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			int row = 0;
			int col = 0;
			
			
			Iterator<String> it = new TreeSet<String>(allLemmas).iterator();
			checkBoxes = new Vector<JCheckBox>();
			TreeSet<String> generics = new TreeSet<String>(subLemmas.keySet());
			
			while( it.hasNext() ) {
				String l = it.next();
				if( !l.equals(lemma)																		// not the lemma itself
						&& (!lemmaEquivalences.containsKey(l) || lemmaEquivalences.get(l).equals(lemma)) 	// not a sublemma of another lemma
						&& (!generics.contains(l) || subLemmas.get(l).size() == 0 ) )  {					// not a generic lemma
					// new checkbox
					JCheckBox b = new JCheckBox(l);
					checkBoxes.add(b);
					
					// selected?
					if( lemmaEquivalences.containsKey(l) && lemmaEquivalences.get(l).equals(lemma) )
						b.setSelected(true);
					
					// adding the checkbox
					c.gridy = row++ / 4 + 1;
					c.gridx = col++ % 4 + 1;
							
					checkPanel.add(b, c);
				}
			}
			
			editPanel.remove(scrollPane);
			scrollPane = new JScrollPane(checkPanel);
			editPanel.add(scrollPane, BorderLayout.CENTER);
			
			scrollPane.revalidate();
			editPanel.revalidate();
			editPanel.repaint();
		}
		
		prevLemma = lemma;
	}
	
	/**
	 * Clear the checkboxPanel
	 */
	private void clearCheckPanel() {
		prevLemma = null;
		checkBoxes = new Vector<JCheckBox>();
		
		checkPanel = new JPanel();
		
		editPanel.remove(scrollPane);
		scrollPane = new JScrollPane(checkPanel);
		editPanel.add(scrollPane, BorderLayout.CENTER);
		
		scrollPane.revalidate();
		editPanel.revalidate();
		editPanel.repaint();
	}
	
	
	/**
	 * Download the information from the checkboxes into the data structures
	 */
	protected void downloadAndSave() {
		// if a previous lemma and checkboxes exist, we need to update the data structures
		if( prevLemma != null && checkBoxes != null ) {
			// reconstruct the list of sublemmas
			Vector<String> subs = subLemmas.get(prevLemma);
			subs.clear();
			
			// reconstruction
			for( int i = 0; i < checkBoxes.size(); i++ ) {
				if( checkBoxes.get(i) != null ) {
					String subL = checkBoxes.get(i).getText();
					
					if( checkBoxes.get(i).isSelected() ) {
						// add to list of the generic lemma
						subs.add(subL);
						
						// save the mapping
						lemmaEquivalences.put(subL, prevLemma);
						
						// remove the generic
						if( subLemmas.get(subL) != null && subLemmas.get(subL).size() == 0 )
							subLemmas.remove(subL);
						
						// remove it from the combobox
						comboModel.removeElement(subL);
					} else if( lemmaEquivalences.get(subL) != null && lemmaEquivalences.get(subL).equals(prevLemma) ) {
						// remove the mapping
						lemmaEquivalences.remove(subL);
						
						// make the "used-to-be" sublemma generic
						subLemmas.put(subL, new Vector<String>());
						
						// add the sublemma to the combobox
						comboModel.addElement(subL);
					}
				}
			}
			changed = true;
		}
		
		if( allLemmas.size() > 0 )
			save();
	}
	
	
	/**
	 * Loads the lemma from the files. Every file is contained in it's own equivalence class
	 */
	protected void loadFromFiles() {
		// set the FileLoadingPanel to no file path
		fileLoadingPanel.reset();
		comboModel.removeAllElements();
		
		// empty the existing data structures
		lemmaEquivalences = new HashMap<String, String>();
		subLemmas = new HashMap<String, Vector<String>>();
		allLemmas = new HashSet<String>();
		
		// stroll through the list of all lemmas filling up the data structures
		if( lemmas != null ) {
			for( int i = 0; i < lemmas.size(); i++ ) {
				subLemmas.put(lemmas.get(i), new Vector<String>());
				allLemmas.add(lemmas.get(i));
			}
		}
		changed = true;
		
		// filling up the combobox, to see the results of our loading
		fillUpComboBox();
		clearCheckPanel();
		
		// saving it as
		saveAs();
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(dataflow.datastructures.Cell)
	 */
	public void recalculate(Vector<String> children) throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {
		for( String cellName : children )
			if( cellName.equals("originalDC") )
				lemmas = ((OriginalDataCubeCell)Grid.instance().getCell(cellName)).
							getCube().getLemmas();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.Component#getName()
	 */
	@Override
	public String getName() {
		return "lemmaEquivalencesPanel";
	}
}
