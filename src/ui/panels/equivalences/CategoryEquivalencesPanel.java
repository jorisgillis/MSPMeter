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

import io.project.CategoryWriter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import msp.RestrictionViolation;
import msp.data.DataCube;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;

import org.apache.log4j.Logger;

import ui.results.StatusBar;
import dataflow.Grid;
import dataflow.datastructures.AllLemmasCell;
import dataflow.datastructures.CategoryEquivalencesCell;
import dataflow.datastructures.CategoryFileCell;
import dataflow.datastructures.Cell;
import dataflow.datastructures.LemmaEquivalencesCell;
import dataflow.datastructures.LemmadDataCubeCell;
import dataflow.datastructures.SubLemmasCell;

/**
 * Load a file defining category equivalences. The user can then edit the equivalences
 * by checking and unchecking checkboxes representing other categories.
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class CategoryEquivalencesPanel extends EquivalencesPanel 
		implements FileRequestor, ItemListener, ActionListener, Cell {
	
	/**
	 * Mapping from lemmas to their category equivalences.<br />
	 * In each lemma is a mapping from subcategory to generic category,
	 */
	protected HashMap<String, HashMap<String, String>> categoryEquivalences;
	/**
	 * Mapping from lemmas to their generic categories.<br />
	 * Generic lemmas are mapped onto the list of subcategories.
	 */
	protected HashMap<String, HashMap<String, Vector<String>>> subCategories;
	/** Keeps track of all the categories that the lemmas can have. */
	protected HashMap<String, HashSet<String>> allCategories;
	/** File in which the equivalences reside */
	protected String categoryFile;
	
	/** The cell in the grid corresponding to the categoryEquivalences */
	protected CategoryEquivalencesCell categoryEquivalencesCell;
	/** The cell in the grid corresponding to the categoryFile variable */
	protected CategoryFileCell categoryFileCell;
	
	
	protected DataCube dataCube = null;
	protected HashMap<String, String> lemmaEquivalences = null;
	protected HashMap<String, Vector<String>> subLemmas = null;
	protected HashSet<String> allLemmas = null;
	
	
	
	/** The lemma that has been selected. */
	protected String lemma = null;
	/** The category that has been selected. */
	protected String category = null;
	
	/** Previous lemma */
	protected String prevLemma = null;
	/** Previous category */
	protected String prevCategory = null;
	
	/** Is this component blocked? A component gets blocked if its input went wrong. */
	protected boolean blocked = false;
	/** Remember whether there was a change. */
	protected boolean changed = false;
	
	/** List of all checkboxes */
	protected Vector<JCheckBox> checkBoxes;
	
	// Panels
	protected JPanel editPanel, checkPanel;
	protected JScrollPane scrollPane;
	
	protected ComboBoxModel lemmaModel, categoryModel;
	protected JComboBox lemmaBox, categoryBox;
	
	// everywhere the same
	private Logger logger = Logger.getLogger(CategoryEquivalencesPanel.class);
	
	
	
	/**
	 * Construct a new CategoryEquivalencesPanel.
	 */
	public CategoryEquivalencesPanel() {
		//- Cells
		categoryEquivalencesCell = new CategoryEquivalencesCell();
		categoryFileCell = new CategoryFileCell();

		Grid.instance().addCell(categoryEquivalencesCell);
		Grid.instance().addCell(categoryFileCell); // TODO waarvoor nodig????
		Grid.instance().addHotCell(this);
		
		//- gui
		this.setLayout(new BorderLayout());
		fileLoadingPanel = new FileLoadingPanel(this, "Load Category Equivalences file");
		this.add(fileLoadingPanel, BorderLayout.NORTH);
		checkBoxes = new Vector<JCheckBox>();
		
		//- edit framework
		editPanel = new JPanel();
		editPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Edit Equivalences"));
		editPanel.setLayout(new BorderLayout());
		this.add(editPanel, BorderLayout.CENTER);
		
		//- editing tools
		// lemmas
		lemmaModel = new ComboBoxModel();
		lemmaBox = new JComboBox(lemmaModel);
		lemmaBox.setPreferredSize(new Dimension(150, 30));
		lemmaBox.addItemListener(this);
		lemmaBox.setActionCommand("lemma");
		
		// categories
		categoryModel = new ComboBoxModel();
		categoryBox = new JComboBox(categoryModel);
		categoryBox.setPreferredSize(new Dimension(150, 30));
		categoryBox.addItemListener(this);
		categoryBox.setActionCommand("category");
		
		// update button
		JPanel selectionPanel = new JPanel();
		selectionPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH; c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
		selectionPanel.add(lemmaBox, c);
		c.gridx = 1;
		selectionPanel.add(categoryBox, c);
		
		// Option Selection
		JPanel optionPanel = new JPanel();
		optionPanel.setLayout(new GridLayout(1, 3));
		
		// loading from scratch
		JButton scratch = new JButton("From Scratch");
		scratch.addActionListener(this);
		scratch.setActionCommand("scratch");
		
		// updating and saving to file
		JButton update = new JButton("Update File");
		update.addActionListener(this);
		update.setActionCommand("update");
		
		// updating from lemmaEquivalences
		JButton updateLemmas = new JButton("Use Lemma Equivalences");
		updateLemmas.addActionListener(this);
		updateLemmas.setActionCommand("lemmas");
		
		optionPanel.add(scratch);
		optionPanel.add(updateLemmas);
		optionPanel.add(update);
		
		// Option and Selection panel together
		JPanel optionAndSelectionPanel = new JPanel();
		optionAndSelectionPanel.setLayout(new BorderLayout());
		optionAndSelectionPanel.add(optionPanel, BorderLayout.NORTH);
		optionAndSelectionPanel.add(selectionPanel, BorderLayout.SOUTH);
		
		// panels for the editing facilities
		checkPanel = new JPanel();	// empty for now
		scrollPane = new JScrollPane(checkPanel);
		
		//- adding it all together
		editPanel.add(optionAndSelectionPanel, BorderLayout.NORTH);
		editPanel.add(scrollPane, BorderLayout.CENTER);
		
		//- data structures
		categoryEquivalences = new HashMap<String, HashMap<String,String>>();
		subCategories = new HashMap<String, HashMap<String,Vector<String>>>();
		allCategories = new HashMap<String, HashSet<String>>();
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see ui.panels.Panel#apply()
	 */
	@Override
	public void apply() {
		if( !blocked ) {
			// download and save information from the checkboxes
			downloadAndSave();
			
			// notify the listeners about new category equivalences data
			try {
				categoryEquivalencesCell.setValue(categoryEquivalences);
				Grid.instance().cellChanged(new Cell[]{categoryEquivalencesCell});
				changed = false;
			} catch( Exception e ) {
				logger.error(e);
				showWarning(e.getMessage());
			}
		}
	}
	
	/**
	 * Save the data structures to a file. The current file if it is available,
	 * ask the user otherwise.
	 */
	public void save() {
		if( changed ) {
			if( categoryFile == null ) {
				try {
					File f = askForFile();
					if( f != null ) {
						categoryFile = f.getAbsolutePath();
						categoryFileCell.setValue(categoryFile);
					}
				} catch( Exception e ) {
					logger.error("An error occurred while setting the categoryFileCell: "+ e.getMessage());
					showWarning(e.getMessage());
				} finally {
					categoryFile = null;
				}
			}
			
			if( categoryFile != null ) {
				try {
					// send the datastructures to the correct function
					CategoryWriter w = new CategoryWriter();
					w.writeCategories(categoryFile, subCategories, allCategories);
					
					changed = false;
					StatusBar.getInstance().addStatus("Saved the category equivalences to "+ categoryFile);
				} catch( FileNotFoundException e ) {
					showWarning("Fault while saving category equivalences: \n"+ e.getMessage());
				} catch( IOException e ) {
					showWarning("Fault while saving category equivalences: \n"+ e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Ask the user for a file and save the data structures to thta location.
	 */
	public void saveAs() {
		File f = askForFile( categoryFile );
		if( f != null ) {
			try {
				// getting the file name
				categoryFile = f.getAbsolutePath();
				categoryFileCell.setValue(categoryFile);
				fileLoadingPanel.updateFilePath(categoryFile);
				
				// send the datastructures to the correct function
				CategoryWriter w = new CategoryWriter();
				w.writeCategories(categoryFile, subCategories, allCategories);
				
				changed = false;
				StatusBar.getInstance().addStatus("Saved the category equivalences to "+ categoryFile);
			} catch( FileNotFoundException e ) {
				showWarning("Fault while saving category equivalences: \n"+ e.getMessage());
			} catch( IOException e ) {
				showWarning("Fault while saving category equivalences: \n"+ e.getMessage());
			} catch( DataFaultException e ) {
				logger.error("An error occurred while setting the categoryFileCell: "+ e.getMessage());
				showWarning(e.getMessage());
			} catch( ImpossibleCalculationException e ) {
				logger.error("An error occurred while setting the categoryFileCell: "+ e.getMessage());
				showWarning(e.getMessage());
			} catch (RestrictionViolation e) {
				logger.error("An error occurred while setting the categoryFileCell: "+ e.getMessage());
				showWarning(e.getMessage());
			}
		}
	}
	
	/**
	 * Load the category equivalences from the files.
	 */
	protected void loadFromFiles() {
		if( dataCube != null ) {
			// Resetting the data structures
			categoryEquivalences = new HashMap<String, HashMap<String,String>>();
			subCategories = new HashMap<String, HashMap<String,Vector<String>>>();
			allCategories = new HashMap<String, HashSet<String>>();
			
			// ask the dataCube to fill it up!
			dataCube.loadSubAllCategories(categoryEquivalences, subCategories, allCategories);
			changed = true;
			
			// load into the GUI
			fillUpLemmaBox();
			clearCheckPanel();
			
			// saving it
			saveAs();
		}
	}
	
	/**
	 * Updates the lemmas used in this panel. It uses the lemma equivalence data structures
	 * to modify the category equivalences data structures in order to bring the second in accordance 
	 * to the first.
	 */
	protected void updateLemmas() {
		if( lemmaEquivalences != null && subLemmas != null && allLemmas != null ) {
			//- 1. Combination: if a lemma occurs in lemmaEquivalences and in allCategories
			// 			then we must collapse the lemma into it's generic lemma.
			Iterator<String> it = lemmaEquivalences.keySet().iterator();
			while( it.hasNext() ) {
				String lemma = it.next();
				
				if( allCategories.containsKey(lemma) ) {
					String generic = lemmaEquivalences.get(lemma);
					
					// making sure everything is in place
					if( !categoryEquivalences.containsKey(generic) )
						categoryEquivalences.put(generic, new HashMap<String, String>());
					if( !subCategories.containsKey(generic) )
						subCategories.put(generic, new HashMap<String, Vector<String>>());
					if( !allCategories.containsKey(generic) )
						allCategories.put(generic, new HashSet<String>());
					
					
					// all categories in the lemma categories that do not occur in generic categories,
					// have to be placed in the data structures for the generic lemma
					Iterator<String> subIt = allCategories.get(lemma).iterator();
					while( subIt.hasNext() ) {
						String subCat = subIt.next();
						
						if( !allCategories.get(generic).contains(subCat) ) {
							allCategories.get(generic).add(subCat);
							subCategories.get(generic).put(subCat, new Vector<String>());
						}
					}
					
					// removing all evidence of lemma
					categoryEquivalences.remove(lemma);
					subCategories.remove(lemma);
					allCategories.remove(lemma);
				}
			}
			
			
			
			//- 2. Split: if a lemma occurs in subLemmas and not in allCategories
			//			then we must split this lemma of, and add information about it's categories
			//			to the appropriate data structures
			// we need the dataCube in order to ask it some questions
			if( dataCube != null ) {
				it = subLemmas.keySet().iterator();
				while( it.hasNext() ) {
					String generic = it.next();
					
					// if the generic lemma is not found in the lemmas, then we need to add it
					if( !allCategories.containsKey(generic) ) {
						Vector<String> categories = new Vector<String>(dataCube.getCategories( generic ));
						if( categories.size() > 0 ) {
							// making sure everything is in place
							categoryEquivalences.put(generic, new HashMap<String, String>());
							subCategories.put(generic, new HashMap<String, Vector<String>>());
							allCategories.put(generic, new HashSet<String>());
							
							// adding
							for( int i = 0; i < categories.size(); i++ ) {
								subCategories.get(generic).put(categories.get(i), new Vector<String>());
								allCategories.get(generic).add(categories.get(i));
							}
						}
						
						// TODO what about categories not used by the original generic lemma?
					}
				}
			}
			
			// filling it up
			changed = true;
			fillUpLemmaBox();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(dataflow.datastructures.Cell)
	 */
	public void recalculate( Vector<String> children ) 
		throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {
		Grid grid = Grid.instance();
		for( String cellName : children ) {
			if( cellName.equals("lemmaEquivalences") )
				lemmaEquivalences = ((LemmaEquivalencesCell)grid.getCell(cellName)).getValue();
			else if( cellName.equals("subLemmas") )
				subLemmas = ((SubLemmasCell)grid.getCell(cellName)).getValue();
			else if( cellName.equals("allLemmas") )
				allLemmas = ((AllLemmasCell)grid.getCell(cellName)).getValue();
			else if( cellName.equals("lemmadDC") )
				dataCube = ((LemmadDataCubeCell)grid.getCell(cellName)).getCube();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.Component#getName()
	 */
	@Override
	public String getName() {
		return "categoryEquivalencesPanel";
	}
	
	//===========================================================================
	// Reactional functions
	//===========================================================================
	/*
	 * (non-Javadoc)
	 * @see ui.panels.equivalences.FileRequestor#receiveFile(java.io.File)
	 */
	public void receiveFile(File file) {
		if( file != null ) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				
				// clean out the combobox
				lemmaModel.removeAllElements();
				categoryModel.removeAllElements();
				
				// initializing the data structures
				categoryEquivalences.clear();
				subCategories.clear();
				allCategories.clear();
				
				String line = "";
				while( (line = reader.readLine()) != null ) {
					// read the file one line at a time
					String[] mainParts = line.split("\t");
					
					// check
					if( mainParts.length < 2 )
						throw new InputException("At least one category per lemma is required.");
					
					String lemma = mainParts[0];
					
					if( !allCategories.containsKey(lemma) )
						allCategories.put(lemma, new HashSet<String>());
					HashSet<String> knownCategories = allCategories.get(lemma);
					
					if( !categoryEquivalences.containsKey(lemma) )
						categoryEquivalences.put(lemma, new HashMap<String, String>());
					HashMap<String, String> equivalences = categoryEquivalences.get(lemma);
					
					if( !subCategories.containsKey(lemma) )
						subCategories.put(lemma, new HashMap<String, Vector<String>>());
					
					for( int i = 1; i < mainParts.length; i++ ) {
						// run through the generics
						String[] parts = mainParts[i].split("_");
						
						// check
						if( parts.length < 1 )
							throw new InputException("At least one category per lemma is required.");
						
						String generic = parts[0];
						knownCategories.add(generic);
						
						// check: lemma != category
						if( generic.equals(lemma) )
							throw new InputException("A category cannot be the same as the lemma.");
						
						subCategories.get(lemma).put(generic, new Vector<String>());
						
						// run through the subcategories
						for( int j = 1; j < parts.length; j++ ) {
							// run through the equivalents
							knownCategories.add(parts[j]);
							equivalences.put(parts[j], generic);
							subCategories.get(lemma).get(generic).add(parts[j]);
							
							// check
							if( parts[j].equals(lemma) )
								throw new InputException("A category cannot be the same as the lemma.");
						}
					}
				}
				
				// storing the file path
				categoryFile = file.getAbsolutePath();
				categoryFileCell.setValue(categoryFile);
				
				// setting the null
				lemma = null;
				category = null;
				
				// non-blocking
				blocked = false;
				changed = false;
				
				// filling up the GUI
				fillUpLemmaBox();
				
	//			logger.info("CategoryEquivalences: ");
	//			logger.info(categoryEquivalences);
	//			logger.info(subCategories);
	//			logger.info(allCategories);
			} catch( FileNotFoundException e ) {
				showWarning("The requested file was not found: \n"+ e.getMessage());
			} catch( IOException e ) {
				showWarning("An error occurred while reading the file "+ file.getName() +": \n" + e.getMessage());
			} catch( InputException e ) {
				blocked = true;
				logger.warn("Wrong input: "+ e.getMessage());
				showWarning(e.getMessage());
			} catch( DataFaultException e ) {
				logger.error("An error occurred while setting the value of the categoryFileCell: "+ e.getMessage());
				showWarning(e.getMessage());
			} catch (ImpossibleCalculationException e) {
				logger.error("An error occurred while setting the value of the categoryFileCell: "+ e.getMessage());
				showWarning(e.getMessage());
			} catch (RestrictionViolation e) {
				logger.error("An error occurred while setting the value of the categoryFileCell: "+ e.getMessage());
				showWarning(e.getMessage());
			} finally {
				try {
					if( reader != null )
						reader.close();
				} catch( IOException e ) {}
			}
		}
	}
	
	/**
	 * Returns a list of all categories (in the given lemma) that are generic, i.e.
	 * are not a subcategory of another category within the same lemma.
	 * @param lemma	lemma within which we search
	 * @return		list of all generic categories
	 */
	private Vector<String> getGenerics( String lemma ) {
		if( lemma != null ) {
			Vector<String> generics = new Vector<String>();
			
			Iterator<String> it = subCategories.get(lemma).keySet().iterator();
			while( it.hasNext() )
				generics.add(it.next());
			
			return generics;
		}
		return null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		if( !blocked ) {
			if( e.getStateChange() == ItemEvent.SELECTED ) {
				if( e.getSource().equals(lemmaBox) ) {
					logger.info("Lemma = "+ e.getItem());
					lemma = (String)e.getItem();
					clearCheckPanel();
					fillUpCategoryBox();
				}
				else if( e.getSource().equals(categoryBox) ) {
					logger.info("Category = "+ e.getItem());
					category = (String)e.getItem();
					fillUpCheckPanel();
					changed = true;
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if( !blocked ) {
			changed = true;
			String command = e.getActionCommand();
			
			if( command.equals("update") ) {
				// update!
				downloadAndSave();
			} else if( command.equals("scratch")) {
				// from scratch
				loadFromFiles();
			} else if( command.equals("lemmas") ) {
				// update from the lemma equivalences panel
				updateLemmas();
			}
		}
	}
	
	
	//===========================================================================
	// GUI related
	//===========================================================================
	/**
	 * Fill up the lemma combobox with lemmas.
	 */
	private void fillUpLemmaBox() {
		lemmaModel.removeAllElements();
		Iterator<String> it = allCategories.keySet().iterator();
		
		while( it.hasNext() )
			lemmaModel.addElement(it.next());
	}
	
	/**
	 * Fill up the category combobox, based on the chosen lemma.
	 */
	private void fillUpCategoryBox() {
		categoryModel.removeAllElements();
		Vector<String> generics = getGenerics(lemma);
		logger.info("Generics: "+ generics);
		for( int i = 0; i < generics.size(); i++ )
			categoryModel.addElement(generics.get(i));
	}
	
	/**
	 * Fill up the CheckPanel using checkboxes necessary for the chosen (lemma, category) pair
	 */
	private void fillUpCheckPanel() {
		// download and save
		downloadAndSave();
		
		if( lemma != null ) {
			if( categoryEquivalences.get(lemma).containsKey(category) ) {
				// this is a subcategory (a category contained in a generic category)
				JLabel subCategory = new JLabel("Category is contained in another category");
				checkPanel.add(subCategory);
				checkPanel.remove(scrollPane);
			}
			else {
				// generate all the checkboxes
				checkBoxes.clear();
				checkPanel = new JPanel();
				
				// layouting
				checkPanel.setLayout(new GridBagLayout());
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				int row = 0;
				int col = 0;
				
				// show all the non-generics and non-subcategories
				Iterator<String> it = allCategories.get(lemma).iterator();
				while( it.hasNext() ) {
					String cat = it.next();
					
					if( !cat.equals(category)
							&& (!categoryEquivalences.get(lemma).containsKey(cat) || categoryEquivalences.get(lemma).get(cat).equals(category))
							&& (!subCategories.get(lemma).containsKey(cat) || subCategories.get(lemma).get(cat).size() == 0) ) {
						// making the checkbox
						JCheckBox b = new JCheckBox(cat);
						checkBoxes.add(b);
						
						// selected?
						if( categoryEquivalences.get(lemma).containsKey(cat) && categoryEquivalences.get(lemma).get(cat).equals(category) )
							b.setSelected(true);
						
						// adding the checkbox
						c.gridy = row++ / 3 + 1;
						c.gridx = col++ % 3 + 1;
						
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
			prevCategory = category;
		}
	}
	
	/**
	 * Clear the checkboxPanel
	 */
	private void clearCheckPanel() {
		prevLemma = null;
		prevCategory = null;
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
	 * Download the information from the checkbox-field into the data structures.
	 */
	private void downloadAndSave() {
		if( prevLemma != null && prevCategory != null && checkBoxes != null ) {
			// clear out the subCategories vector, we'll reconstruct it while running
			// through the checkboxes
			subCategories.get(prevLemma).get(prevCategory).clear();
			
			for( int i = 0; i < checkBoxes.size(); i++ ) {
				if( checkBoxes.get(i) != null ) {
					JCheckBox b = checkBoxes.get(i);
					String subCat = b.getText();
					if( b.isSelected() ) {
						// selected: add to the subCategories and categoryEquivalences
						subCategories.get(prevLemma).get(prevCategory).add(subCat);
						categoryEquivalences.get(prevLemma).put(b.getText(), prevCategory);
						
						// removing it from the combobox
						categoryModel.removeElement(subCat);
						
						// remove from subCategories
						if( subCategories.get(prevLemma).containsKey(subCat) )
							subCategories.get(prevLemma).remove(subCat);
					} else if( categoryEquivalences.get(prevLemma).containsKey(subCat) ) {
						// not selected: remove from categoryEquivalences if necessary
						categoryEquivalences.get(prevLemma).remove(subCat);
						
						// adding it to the combobox
						categoryModel.addElement(subCat);
						
						// add the category back the subCategories, such that it can appear in the combobox
						subCategories.get(prevLemma).put(subCat, new Vector<String>());
					}
				}
			}
			changed = true;
		}
		
		// saving it
		if( allCategories.size() > 0 )
			save();
	}
	
	
}
