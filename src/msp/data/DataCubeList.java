/*
 * Copyright 2010 MSPMeter
 *
 * Licensed under the EUPL, Version 1.1 or � as soon they
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

package msp.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import msp.ProgressListener;
import org.apache.log4j.Logger;
import ui.panels.file.FileRow;

/**
 * <p>
 * Implementation of a DataCube based on lists, instead of hashmaps. Hashmaps
 * are not necessary, as MSP is always a value that is summed over all
 * categories/wordforms and lemmas.
 * </p>
 * 
 * @author joris
 */
public class DataCubeList {
	
	// ==========================================================================
	// Variables
	// ==========================================================================
	/**
	 * The data is structured as nested lists.
	 */
	protected List<List<List<Integer>>>	cube;
	/**
	 * List of span indices, keeps track of the order in the spans, lemmas and
	 * categories.
	 */
	protected CubeIndex					cubeIndex;
	/**
	 * List of objects listening to our progress.
	 */
	protected List<ProgressListener>	progressListener;
	
	// - The base of the logarithm (and exponential).
	private double						logBase			= 2;
	private double						log10LogBase	= Math.log10(2);
	
	// - Everywhere the same!
	private static String				newLine			= System.getProperty("line.separator");
	private static Logger				logger			= Logger.getLogger(DataCubeList.class);
	
	// ==========================================================================
	// Construction
	// ==========================================================================
	/**
	 * Constructor: initializes the cube.
	 */
	public DataCubeList() {
		cube = new ArrayList<List<List<Integer>>>();
		cubeIndex = new CubeIndex();
		progressListener = new LinkedList<ProgressListener>();
	}
	
	/**
	 * Reads a list of files into this datacube. Files with the same annotation
	 * are placed within the same span.
	 * 
	 * @param files				list of files
	 * @param firstSeparator	start of lemma
	 * @param secondSeparator	end of lemma/start of category
	 * @param terminator		end of category
	 * @throws DataFaultException
	 */
	public void fillCube(Vector<FileRow> files,
							String firstSeparator,
							String secondSeparator,
							String terminator) throws DataFaultException {
		/*
		 * For each line in each file: 1. Remove leading and trailing
		 * whitespaces. 2. Get the integer at the front: Frequency 3. Get the
		 * largest sequence of non-first-separator tokens 4. Get the largest
		 * sequence of non-second-separators tokens: Lemma 5. The remainder of
		 * the line (if larger than 0): Category
		 */
		
		try {
			// - Setting up the patterns
			Pattern pCorrectLine = Pattern
					.compile(".*[0-9]+.*[" + firstSeparator + "].+" + "(["
							+ secondSeparator + "].+)?");
			Pattern pFreq = Pattern.compile("[0-9]+");
			Pattern pFirst = Pattern.compile("[^" + firstSeparator + "]*");
			Pattern pSecond = Pattern.compile("[^" + secondSeparator + "]+");
			Pattern pTerminator = null;
			if (terminator != null && !terminator.isEmpty()) {
				pTerminator = Pattern.compile("["+ terminator +"]");
				logger.debug("Terminator: ["+ terminator +"]");
			}
			
			// - Setting up the cube and axises
			cube = new ArrayList<List<List<Integer>>>();
			cubeIndex = new CubeIndex();
			
			// - running through the list of files
			for (int i = 0; i < files.size(); i++) {
				FileRow fr = files.get(i);
				
				// opening the reader
				BufferedReader r = new BufferedReader(new FileReader(
						fr.getFile()));
				
				// - loop through the lines
				String line = "";
				while ((line = r.readLine()) != null)
					try {
						Matcher mCorrectLine = pCorrectLine.matcher(line);
						if (line.length() > 1 && mCorrectLine.matches()) {
							// remember the position
							int pos = 0;
							
							// 1. removing leading and trailing whitespace
							line = line.trim();
							
							// 2. get the integer out!
							Matcher mFreq = pFreq.matcher(line);
							if (!mFreq.find(pos))
								throw new DataFaultException("Frequency");
							int frequency = Integer.parseInt(mFreq.group());
							pos = mFreq.end();
							
							// 3. Get the piece between the frequency and the
							// lemma out
							Matcher mFirst = pFirst.matcher(line);
							if (!mFirst.find(pos))
								throw new DataFaultException(
										"Frequency -> Lemma");
							pos = mFirst.end() + 1;
							
							// 4. Get the lemma out
							Matcher mSecond = pSecond.matcher(line);
							if (!mSecond.find(pos))
								throw new DataFaultException("Lemma");
							String lemma = mSecond.group();
							pos = mSecond.end() + 1;
							
							// 5. Get the category out
							String category = "nil";
							if (pos < line.length()) {
								if (terminator != null && !terminator.isEmpty()) {
									// Copy up to a terminator token
									Matcher mTerminator = pTerminator.matcher(line);
									
									if (mTerminator.find(pos))
										category = line.substring(pos, mTerminator.start());
									else
										category = line.substring(pos);
								} else
									category = line.substring(pos);
							}
							
							// - Entering it in the cube
							// a. Span
							String span = fr.getDataSet();
							if (!cubeIndex.containsSpan(span)) {
								// Adding span to index and cube
								cubeIndex.add(new SpanIndex(span));
								cube.add(new ArrayList<List<Integer>>());
							}
							int spanPos = cubeIndex.getSpanPosition(span);
							
							// b. Lemma
							if (!cubeIndex.get(spanPos).containsLemma(lemma)) {
								cubeIndex.get(spanPos).addLemmaIndex(
										new LemmaIndex(lemma));
								cube.get(spanPos).add(new ArrayList<Integer>());
							}
							int lemmaPos = cubeIndex.get(spanPos)
									.getLemmaPosition(lemma);
							
							// c. Category
							if (!cubeIndex.get(spanPos).getLemma(lemmaPos)
									.containsCategory(category)) {
								// install
								cubeIndex.get(spanPos).getLemma(lemmaPos)
										.addCategory(category);
								cube.get(spanPos).get(lemmaPos)
										.add(new Integer(frequency));
							} else {
								// update
								int categoryPos = cubeIndex.get(spanPos)
										.getLemma(lemmaPos)
										.getCategoryPosition(category);
								Integer x = cube.get(spanPos).get(lemmaPos)
										.get(categoryPos);
								Integer y = x + frequency;
								
								// remove & re-insert
								cube.get(spanPos).get(lemmaPos)
										.remove(categoryPos);
								cube.get(spanPos).get(lemmaPos)
										.add(categoryPos, y);
							}
						}
					} catch (DataFaultException e) {
						// a faulty line, just ignore!
					}
				// closing the reader
				r.close();
			}
		} catch (Exception e) {
			logger.error("Fault while filling the cube: " + e.getMessage());
			throw new DataFaultException(e.getMessage());
		}
	}
	
	
	
	/**
	 * Constructs a new datacube by applying the given lemma equivalences to the
	 * data.
	 * 
	 * @param lemmaEquivalences lemma equivalences
	 * @return new datacube with equivalences applied
	 */
	public
			DataCubeList
			lemmaEquivalences(HashMap<String, String> lemmaEquivalences) {
		// Copy the cube
		DataCubeList dc = copyCube();
		
		// For each span:
		for (int i = 0; i < dc.cube.size(); i++) {
			// Span & Index
			List<List<Integer>> span = dc.cube.get(i);
			SpanIndex si = dc.cubeIndex.get(i);
			
			// Check each equivalence
			for (String l2 : lemmaEquivalences.keySet()) {
				// The equivalent lemma
				String l1 = lemmaEquivalences.get(l2);
				
				if (si.containsLemma(l2))
					// The equivalent lemma is present:
					// a. The base lemma (l1) is also present: merge
					// b. The base lemma (l1) is not present: rename
					if (si.containsLemma(l1))
						// a. Merge
						try {
							// Getting the respective positions
							int pos1 = si.getLemmaPosition(l1);
							int pos2 = si.getLemmaPosition(l2);
							
							// Getting the lemma indices to be able to merge the
							// categories correctly
							LemmaIndex li1 = si.getLemma(pos1);
							LemmaIndex li2 = si.getLemma(pos2);
							List<String> cats2 = li2.getCategories();
							
							for (String cat : cats2) {
								// Determining the position of the category in
								// the second lemma.
								int cPos2 = li2.getCategoryPosition(cat);
								
								if (li1.containsCategory(cat)) {
									// i. Category is present, so add up!
									int cPos1 = li1.getCategoryPosition(cat);
									Integer x = span.get(pos1).get(cPos1);
									Integer y = span.get(pos2).get(cPos2);
									
									// Removing and re-inserting
									span.get(pos1).remove(cPos1);
									span.get(pos1).add(cPos1, (x + y));
								} else {
									// ii. Category is not present: add to cube
									// and add to LemmaIndex
									span.get(pos1).add(
											span.get(pos2).get(cPos2));
									li1.addCategory(cat);
								}
							}
							
							// Removing the second lemma
							span.remove(pos2);
							si.removeLemmaIndex(pos2);
						} catch (NoSuchLemmaException e) {
							// Lemmas are present!
						} catch (NoSuchCategoryException e) {
							// Categories are present!
						}
					else
						// b. Rename
						try {
							si.rename(l2, l1);
						} catch (DataFaultException e) {
							// We know l2 is present ...
							logger.error(e.getMessage());
						}
			}
		}
		
		return dc;
	}
	
	/**
	 * Constructs a new datacube by applying the given category equivalences to
	 * the data.
	 * 
	 * @param categoryEquivalences category equivalences
	 * @param useInMSP use the lemma equivalences in the calculation or just in
	 *            this step
	 * @param lemmaEquivalences lemma equivalences
	 * @return
	 */
	public DataCubeList categoryEquivalences(
				HashMap<String, HashMap<String, String>> categoryEquivalences,
				boolean useInMSP,
				HashMap<String, String> lemmaEquivalences) {
		// Creating the new cube and index
		List<List<List<Integer>>> newCube = new ArrayList<List<List<Integer>>>();
		CubeIndex newSpanIndex = new CubeIndex();
		
		// Copying the spans and creating space for storing the new spans
		for (int i = 0; i < cubeIndex.size(); i++) {
			newCube.add(new ArrayList<List<Integer>>());
			newSpanIndex.add(new SpanIndex(cubeIndex.get(i).getSpan()));
		}
		
		// Go through the spans and lemmas
		for (int i = 0; i < cube.size(); i++) {
			// Current span
			List<List<Integer>> curSpan = cube.get(i);
			
			// Index of the current span
			SpanIndex si = cubeIndex.get(i);
			SpanIndex newSI = newSpanIndex.get(i);
			
			// Go through the lemmas
			for (int j = 0; j < curSpan.size(); j++) {
				// Index of the current lemma
				LemmaIndex li = si.getLemma(j);
				LemmaIndex newLI = new LemmaIndex(li.getLemma());
				newSI.addLemmaIndex(newLI);
				
				// The current lemma and new space
				List<Integer> curLemma = curSpan.get(j);
				newCube.get(i).add(new ArrayList<Integer>());
				
				// What is the current lemma?
				String lemma = li.getLemma();
				if (!useInMSP && lemmaEquivalences.containsKey(lemma))
					// Use the equivalent lemma
					lemma = lemmaEquivalences.get(lemma);
				
				// Getting the equivalences
				HashMap<String, String> equivs;
				if (categoryEquivalences.containsKey(lemma))
					equivs = categoryEquivalences.get(lemma);
				else
					equivs = new HashMap<String, String>();
				
				// Load the counts
				List<String> categories = li.getCategories();
				for (int k = 0; k < curLemma.size(); k++) {
					// Compute the correct category
					String cat = categories.get(k);
					
					if (equivs.containsKey(cat))
						// Take the equivalent one
						cat = equivs.get(cat);
					
					// Insert or update the count
					if (newLI.containsCategory(cat))
						// Update
						try {
							int catPos = newLI.getCategoryPosition(cat);
							Integer x = newCube.get(i).get(j).get(catPos);
							Integer y = x + curLemma.get(k);
							newCube.get(i).get(j).set(catPos, y);
						} catch (NoSuchCategoryException e) {
							// Category is always present, just checked it!
						}
					else {
						// Insert
						newLI.addCategory(cat);
						newCube.get(i).get(j).add(curLemma.get(k));
					}
				}
			}
		}
		
		// Constructing & Returning the cube
		DataCubeList dc = new DataCubeList();
		dc.setCube(newCube);
		dc.setSpanIndex(newSpanIndex);
		return dc;
	}
	
	/**
	 * Loads information about categories into the given datastructures. First
	 * of all, every category is in its own equivalence class. Secondly, an
	 * empty list is created for each lemma and category. Thirdly, for each
	 * lemma the set of categories is defined.
	 * 
	 * @param categoryEquivalences making a category equivalences set in which
	 *            each category is in its own equivalence
	 * @param subCategories for each lemma and for each category an empty list
	 * @param allCategories set of all the categories in each lemma
	 */
	public void loadSubAllCategories(
				HashMap<String, HashMap<String, String>> categoryEquivalences,
				HashMap<String, HashMap<String, Vector<String>>> subCategories,
				HashMap<String, HashSet<String>> allCategories) {
		for (SpanIndex si : cubeIndex)
			for (LemmaIndex li : si.getLemmas()) {
				// This is the current lemma
				String lemma = li.getLemma();
				
				// Creating if necessary
				if (!categoryEquivalences.containsKey(lemma))
					categoryEquivalences.put(lemma,
							new HashMap<String, String>());
				if (!subCategories.containsKey(lemma))
					subCategories.put(lemma,
							new HashMap<String, Vector<String>>());
				if (!allCategories.containsKey(lemma))
					allCategories.put(lemma, new HashSet<String>());
				
				// Adding the categories
				for (String cat : li.getCategories()) {
					// Adding to the set
					allCategories.get(lemma).add(cat);
					
					// Creating a list if necessary
					if (!subCategories.get(lemma).containsKey(cat))
						subCategories.get(lemma).put(cat, new Vector<String>());
				}
			}
	}
	
	/**
	 * Sets the new base of the logarithm to be used.
	 * 
	 * @param logBase base of the logarithm
	 */
	public void setLogBase(int logBase) {
		this.logBase = logBase;
		this.log10LogBase = Math.log(logBase);
	}
	
	/**
	 * Copies this cube into a new cube.
	 * 
	 * @return copied cube
	 */
	public DataCubeList copyCube() {
		DataCubeList newCube = new DataCubeList();
		
		// Copy data
		newCube.cube		= new ArrayList<List<List<Integer>>>(cube.size());
		newCube.cubeIndex	= new CubeIndex();
		for (int i = 0; i < cube.size(); i++) {
			// Recreate span
			newCube.cube.add(new ArrayList<List<Integer>>(cube.get(i).size()));
			for (int j = 0; j < cube.get(i).size(); j++) {
				// Recreate lemma
				newCube.cube.get(i).add(
						new ArrayList<Integer>(cube.get(i).get(j).size()));
				
				// Adding categories
				for (Integer c : cube.get(i).get(j))
					newCube.cube.get(i).get(j).add(c);
			}
			
			// Copy SpanIndex
			SpanIndex si = cubeIndex.get(i);
			newCube.cubeIndex.add(new SpanIndex(si.getSpan(), si.getLemmas()));
		}
		
		return newCube;
	}
	
	// ==========================================================================
	// Cumulation & Resampling
	// ==========================================================================
	/**
	 * Computes the cumulated version of this datacube.
	 * 
	 * @return cumulated datacube
	 */
	public DataCubeList cumulate() {
		// Contents of the new cube
		List<List<List<Integer>>> newCube = new ArrayList<List<List<Integer>>>(
				cube.size());
		CubeIndex newIndex = new CubeIndex();
		
		// Creating spans
		for (SpanIndex si : cubeIndex) {
			newCube.add(new ArrayList<List<Integer>>(si.getLemmas().size()));
			newIndex.add(new SpanIndex(si.getSpan()));
		}
		
		/*
		 * Add the i^th span to all i <= j^th spans of the new cube. Creating
		 * lemmas and categories if needed.
		 */
		
		for (int i = 0; i < cube.size(); i++) {
			// The current values
			List<List<Integer>> curSpan = cube.get(i);
			SpanIndex si = cubeIndex.get(i);
			
			// For self and all successors
			for (int j = i; j < cube.size(); j++) {
				List<List<Integer>> newSpan = newCube.get(j);
				SpanIndex newSI = newIndex.get(j);
				
				// Foreach lemma
				for (int k = 0; k < curSpan.size(); k++) {
					// Current values
					List<Integer> curLemma = curSpan.get(k);
					LemmaIndex li = si.getLemma(k);
					String lemma = li.getLemma();
					
					// Does the lemma exist?
					if (!newSI.containsLemma(lemma)) {
						newSI.addLemmaIndex(new LemmaIndex(lemma));
						newSpan.add(new ArrayList<Integer>());
					}
					
					// Find the place of the lemma
					int newLemmaPos = 0;
					try {
						newLemmaPos = newSI.getLemmaPosition(lemma);
					} catch (NoSuchLemmaException e) {
						// Just added the lemma, it has to be there!
					}
					List<Integer> newLemma = newSpan.get(newLemmaPos);
					LemmaIndex newLI = newSI.getLemma(newLemmaPos);
					
					// For each category
					for (int m = 0; m < curLemma.size(); m++) {
						// Current values
						String cat = li.getCategories().get(m);
						Integer value = curLemma.get(m);
						
						// Does the category exist?
						if (newLI.containsCategory(cat)) {
							// Update
							int newCatPos = 0;
							try {
								newCatPos = newLI.getCategoryPosition(cat);
							} catch (NoSuchCategoryException e) {
								// We know it exists...
							}
							int updated = newLemma.get(newCatPos) + value;
							newLemma.set(newCatPos, updated);
						} else {
							// Insert
							newLI.addCategory(cat);
							newLemma.add(value);
						}
					}
				}
			}
		}
		
		// Constructing the DataCube
		DataCubeList dc = new DataCubeList();
		dc.setCube(newCube);
		dc.setSpanIndex(newIndex);
		return dc;
	}
	
	/**
	 * Take random samples from this datacube. This method constructs a set of
	 * subcorpora.
	 * 
	 * @param S number of tokens per subcorpus
	 * @param xMode sampling mode: 0 = sampling factor, 1 = fixed number of
	 *            samples
	 * @param X sampling factor
	 * @return
	 */
	public DataCubeList[] resample(int S, int xMode, double X) {
		// Determine number of samples
		int N = numberOfTokens();
		int numberOfSamples = 2;
		switch (xMode) {
		case 0:
			numberOfSamples = (int) Math.ceil(N * X / S);
			break;
		case 1:
			numberOfSamples = (int) Math.ceil(X);
			break;
		}
		
		// We need to take at least two samples
		if (numberOfSamples < 2)
			numberOfSamples = 2;
		
		// Making space for the samples
		DataCubeList[] samples = new DataCubeList[numberOfSamples];
		for (int i = 0; i < numberOfSamples; i++)
			samples[i] = new DataCubeList();
		
		if (N >= S) {
			// Constructing the list of keys
			ArrayList<DataCubeKey> keyList = constructKeyList();
			
			// For each sample
			for (int i = 0; i < numberOfSamples; i++) {
				// Copy the key list
				ArrayList<DataCubeKey> curKeyList = new ArrayList<DataCubeKey>(
						keyList);
				
				// Take a sample
				samples[i] = sampleCube(S, curKeyList);
				
				// // Spans need to be ordered in the same way as the original
				// cube
				// List<Integer> spanPositions = new ArrayList<Integer>();
				// for (SpanIndex si : index)
				// try {
				// spanPositions.add(getSpanPosition(si.getSpan()));
				// } catch (NoSuchSpanException e) {
				// logger.error("Span in sample not found in original.");
				// }
				//
				// // Sorting
				// List<List<List<Integer>>> sortedCube = new
				// ArrayList<List<List<Integer>>>();
				// List<SpanIndex> sortedIndex = new ArrayList<SpanIndex>();
				// while (!index.isEmpty()) {
				// // 1. Find smallest number in spanPositions
				// int minPos = 0;
				// int min = spanPositions.get(0);
				// for (int k = 1; k < spanPositions.size(); k++)
				// if (spanPositions.get(k) < min) {
				// minPos = k;
				// min = spanPositions.get(k);
				// }
				//
				// // 2. Adding the smallest to the sorted list
				// sortedCube.add(cube.get(minPos));
				// sortedIndex.add(index.get(minPos));
				//
				// // 3. Remove sorted element from to sort list
				// cube.remove(minPos);
				// index.remove(minPos);
				// spanPositions.remove(minPos);
				// }
				
				// Storing the sampled cube
				samples[i].setCube(cube);
				samples[i].setSpanIndex(new CubeIndex(cubeIndex));
			}
		}
		
		return samples;
	}
	
	/**
	 * Constructs a key list from this cube.
	 * 
	 * @return key list
	 */
	private ArrayList<DataCubeKey> constructKeyList() {
		// Construct a list of all (span, lemma, category) triples
		ArrayList<DataCubeKey> keyList = new ArrayList<DataCubeKey>();
		for (int i = 0; i < cube.size(); i++) {
			String span = cubeIndex.get(i).getSpan();
			for (int j = 0; j < cube.get(i).size(); j++) {
				String lemma = cubeIndex.get(i).getLemma(j).getLemma();
				for (int k = 0; k < cube.get(i).get(j).size(); k++) {
					String cat = cubeIndex.get(i).getLemma(j).getCategories()
							.get(k);
					DataCubeKey key = new DataCubeKey(span, lemma, cat, 1);
					for (int m = 0; m < cube.get(i).get(j).get(k); m++)
						keyList.add(key);
				}
			}
		}
		return keyList;
	}
	
	
	/**
	 * Takes a sample from the this cube.  Note: do not oversample!
	 * @param S number of elements
	 * @param curKeyList the current list of keys
	 * @return a new cube: sampling of the cube with ALL SPANS in order!
	 */
	private DataCubeList sampleCube(int S, ArrayList<DataCubeKey> curKeyList) {
		// Creating space for cube and temporary index
		List<List<List<Integer>>> cube = new ArrayList<List<List<Integer>>>();
		List<List<List<Integer>>> sortedCube = null;
		CubeIndex index = new CubeIndex();
		CubeIndex sortedIndex = null;
		
		if (S <= curKeyList.size()) {
			// Keeping track of chosen tokens
			boolean[] chosen = new boolean[curKeyList.size()];
			
			// Select elements at random
			for (int j = 0; j < S; j++) {
				// Random position
				int randPosOriginal = (int) (Math.random() * curKeyList.size());
				
				// Find free token
				int inc = 1;
				int randPos = randPosOriginal;
				while (chosen[randPos]) {
					if (randPos == chosen.length-1) {
						// End of the array, thus we need to search for a token
						// before randPosOriginal that is still free to be 
						// chosen.
						// Because there can be no oversampling, this will not
						// go out of bounds.
						inc = -1;
						randPos = randPosOriginal - 1;
					} else
						randPos += inc;
				}
				
				// Get the random token
				chosen[randPos] = true;
				DataCubeKey key = curKeyList.get(randPos);
				
				// Adding to the cube
				// a. Span
				if (!index.containsSpan(key.getMonth())) {
					index.add(new SpanIndex(key.getMonth()));
					cube.add(new ArrayList<List<Integer>>());
				}
				
				int spanPos = 0;
				try {
					spanPos = index.getSpanPosition(key.getMonth());
				} catch (NoSuchSpanException e) {
					// Either just added, or it was present
				}
				
				// b. Lemma
				if (!index.get(spanPos).containsLemma(key.getLemma())) {
					index.get(spanPos)
							.addLemmaIndex(new LemmaIndex(key.getLemma()));
					cube.get(spanPos).add(new ArrayList<Integer>());
				}
				
				int lemmaPos = 0;
				try {
					lemmaPos = index.get(spanPos).getLemmaPosition(key.getLemma());
				} catch (NoSuchLemmaException e) {
					// Either just added, or it was present
				}
				
				// c. Category
				if (!index.get(spanPos).getLemma(lemmaPos)
						.containsCategory(key.getCategory())) {
					// Insert
					index.get(spanPos).getLemma(lemmaPos)
							.addCategory(key.getCategory());
					cube.get(spanPos).get(lemmaPos).add(1);
				} else
					// Update
					try {
						int catPos = index.get(spanPos).getLemma(lemmaPos)
								.getCategoryPosition(key.getCategory());
						int x = cube.get(spanPos).get(lemmaPos).get(catPos) + 1;
						cube.get(spanPos).get(lemmaPos).set(catPos, x);
					} catch (NoSuchCategoryException e) {
						// Is present!
					}
			}
			
			// Sorting the spans and adding missing spans
			sortedCube = new ArrayList<List<List<Integer>>>(cubeIndex.size());
			sortedIndex = new CubeIndex();
			try {
				for (SpanIndex si : cubeIndex) {
					if (index.containsSpan(si.getSpan())) {
						// Span present: put in place
						int spanPos = index.getSpanPosition(si.getSpan());
						sortedCube.add(cube.get(spanPos));
						sortedIndex.add(index.get(spanPos));
					} else {
						// Span not present: add empty span
						sortedCube.add(new ArrayList<List<Integer>>());
						sortedIndex.add(new SpanIndex(si.getSpan()));
					}
				}
			} catch (NoSuchSpanException e) {
				logger.error("Did not find a span that is present!");
			}
		}
		
		// Constructing the sample cube
		DataCubeList sampledCube = new DataCubeList();
		sampledCube.setCube(sortedCube);
		sampledCube.setSpanIndex(sortedIndex);
		
		return sampledCube;
	}
	
	/**
	 * Constructs a list of lemma-category pairs.
	 * 
	 * @param span span to construct key list for
	 * @param si index of the span
	 * @return key list
	 */
	private List<SpanKey> constructSpanKeyList(
													List<List<Integer>> span,
													SpanIndex si) {
		// Make room
		List<SpanKey> keys = new ArrayList<SpanKey>();
		
		// Searching through the index and span
		for (int i = 0; i < span.size(); i++) {
			List<Integer> lemma = span.get(i);
			LemmaIndex li = si.getLemma(i);
			
			for (int j = 0; j < lemma.size(); j++) {
				// Make key
				SpanKey sk = new SpanKey(li.getLemma(), li.getCategories().get(
						j), 1);
				
				// Adding freq times
				for (int k = 0; k < lemma.get(j); k++)
					keys.add(sk);
			}
		}
		
		return keys;
	}
	
	
	/**
	 * Draw a sample from a given list of keys of the given size S: Alternative
	 * approach.
	 * @param S size of the sample
	 * @param keys lemma-category pairs
	 * @return a sample of size S
	 */
	private List<List<Integer>> sampleSpan(int S, List<SpanKey> keys) {
		// Making room
		List<List<Integer>> sample = new ArrayList<List<Integer>>(S);
		SpanIndex si = new SpanIndex("tmp");
		
		// Keeping track of already sampled tokens
		boolean[] chosen = new boolean[keys.size()];
		
		if (chosen.length >= S) {
			// NO oversampling
			for (int i = 0; i < S; i++) {
				// Random lemma-category combination
				int randPosOriginal = (int) (Math.random() * keys.size());
				
				// Find a free token
				int inc = 1;
				int randPos = randPosOriginal;
				while (chosen[randPos]) {
					if (randPos == chosen.length-1) {
						inc = -1;
						randPos = randPosOriginal-1;
					} else
						randPos += inc;
				}
				
				// Get random token
				chosen[randPos] = true;
				SpanKey sk = keys.get(randPos);
				
				// Lemma present?
				if (!si.containsLemma(sk.getLemma())) {
					sample.add(new ArrayList<Integer>());
					si.addLemmaIndex(new LemmaIndex(sk.getLemma()));
				}
				try {
					int lemmaPos = si.getLemmaPosition(sk.getLemma());
					LemmaIndex li = si.getLemma(lemmaPos);
					
					// Category present or not?
					if (!li.containsCategory(sk.getCategory())) {
						// Insert
						li.addCategory(sk.getCategory());
						sample.get(lemmaPos).add(1);
					} else {
						// Update
						int catPos = li.getCategoryPosition(sk.getCategory());
						Integer x = sample.get(lemmaPos).get(catPos);
						sample.get(lemmaPos).set(catPos, x + 1);
					}
				} catch (NoSuchLemmaException e) {
					logger.error("Could not find lemma that was just added.");
				} catch (NoSuchCategoryException e) {
					logger.error("Could not find category that was just added.");
				}
			}
		}
		
		return sample;
	}
	
	// ==========================================================================
	// Mean Size Of Paradigm
	// ==========================================================================
	/**
	 * Computes the MSP values of this cube.
	 * 
	 * @param weighting do we weight?
	 * @param entropy do we use entropy?
	 * @return MSP values of this cube
	 */
	public MSPResult MSP(boolean weighting, boolean entropy) {
		// Storing the results
		MSPTriple[] result = new MSPTriple[cube.size()];
		
		if (!weighting && !entropy) {
			for (int i = 0; i < cube.size(); i++)
				result[i] = new MSPTriple(mspVarietyUnweighted(cube.get(i)),
						cubeIndex.get(i).getSpan());
		} else if (weighting && !entropy) {
			for (int i = 0; i < cube.size(); i++)
				result[i] = new MSPTriple(mspVarietyWeighted(cube.get(i)),
						cubeIndex.get(i).getSpan());
		} else if (!weighting && entropy) {
			for (int i = 0; i < cube.size(); i++)
				result[i] = new MSPTriple(mspEntropyUnweighted(cube.get(i)),
						cubeIndex.get(i).getSpan());
		} else {
			for (int i = 0; i < cube.size(); i++)
				result[i] = new MSPTriple(mspEntropyWeighted(cube.get(i)),
						cubeIndex.get(i).getSpan());
		}
		
		return new MSPResult(result, null);
	}
	
	/**
	 * Do resampling and output the MSP values.
	 * 
	 * @param weighting apply weighting?
	 * @param entropy apply entropy?
	 * @param subSampleMode 0 = one span, 1 = all spans
	 * @param subSampleSize size of the subsamples
	 * @param numberOfSamplesMode how to adjust the number of subsamples?
	 * @param numberOfSamples how many subsamples?
	 * @return resampled MSP values
	 */
	public MSPResult resampleMSP(
									boolean weighting,
									boolean entropy,
									int subSampleMode,
									int subSampleSize,
									int numberOfSamplesMode,
									double numberOfSamples) {
		// Making space for the result and the samples
		MSPTriple[] result = new MSPTriple[cubeIndex.size()];
		List<List<Double>> sampleMSPs = new ArrayList<List<Double>>(
				cubeIndex.size());
		
		// Depending on the sampling mode
		if (subSampleMode == 0) {
			// ONE SPAN
			resampleMSPOneSpan(weighting, entropy, subSampleSize,
					numberOfSamplesMode, numberOfSamples, result, sampleMSPs);
		} else if (subSampleMode == 1) {
			// ALL SPAN
			resampleMSPAllSpan(weighting, entropy, subSampleSize,
					numberOfSamplesMode, numberOfSamples, result, sampleMSPs);
		}
		
		return new MSPResult(result, sampleMSPs);
	}
	
	/**
	 * @param weighting weighting or not?
	 * @param entropy entropy or not?
	 * @param subSampleSize size of the samples
	 * @param numberOfSamplesMode how to interpret the number of samples?
	 * @param numberOfSamples number of samples
	 * @param result the results array
	 * @param sampleMSPs list of all sample values
	 */
	protected void resampleMSPOneSpan(
										boolean weighting,
										boolean entropy,
										int subSampleSize,
										int numberOfSamplesMode,
										double numberOfSamples,
										MSPTriple[] result,
										List<List<Double>> sampleMSPs) {
		// for each span
		//   Resample the span
		//   Compute the msp for each sample
		//   Compute the average and standard deviation
		for (int i = 0; i < cubeIndex.size(); i++) {
			// Sampling
			
			// 1. Compute the number of samples
			List<List<Integer>> span = cube.get(i);
			int N = numberOfTokens(span);
			int B = 1;
			if (numberOfSamplesMode == 0)
				B = (int) Math.ceil(N * numberOfSamples / subSampleSize);
			else
				B = (int) Math.ceil(numberOfSamples);
			
			// Making room for samples
			sampleMSPs.add(new ArrayList<Double>(B));
			
			if (N >= subSampleSize) {
				// Construct key list
				List<SpanKey> keys = constructSpanKeyList(span, cubeIndex.get(i));
				
				// 2. Sample and Compute MSPs
				for (int j = 0; j < B; j++) {
					// Sample
					List<List<Integer>> sample = 
							sampleSpan(subSampleSize, keys);
					
					// Compute MSP
					if (!weighting && !entropy)
						sampleMSPs.get(i).add(mspVarietyUnweighted(sample));
					else if (weighting && !entropy)
						sampleMSPs.get(i).add(mspVarietyWeighted(sample));
					else if (!weighting && entropy)
						sampleMSPs.get(i).add(mspEntropyUnweighted(sample));
					else
						sampleMSPs.get(i).add(mspEntropyWeighted(sample));
				}
			}
				
			// Compute the average and deviation
			double average = 0.0;
			if (sampleMSPs.get(i).size() > 0) {
				for (int j = 0; j < sampleMSPs.get(i).size(); j++)
					average += sampleMSPs.get(i).get(j);
				average /= sampleMSPs.get(i).size();
			}
			
			double variance = 0.0;
			double stddev = 0.0;
			if (sampleMSPs.get(i).size() > 0) {
				for (int j = 0; j < sampleMSPs.get(i).size(); j++)
					variance = Math.pow(sampleMSPs.get(i).get(j) - average, 2);
				variance /= sampleMSPs.get(i).size();
				stddev = Math.sqrt(variance);
			}
			
			result[i] = new MSPTriple(average, stddev, cubeIndex.get(i)
					.getSpan());
		}
	}
	
	/**
	 * @param weighting				weighting or not?
	 * @param entropy				entropy or not?
	 * @param subSampleSize 		size of the samples
	 * @param numberOfSamplesMode 	how to interpret the number of samples
	 * @param numberOfSamples 		number of samples
	 * @param result 				array with results
	 * @param sampleMSPs 			list with all sample values
	 */
	protected void resampleMSPAllSpan(
										boolean weighting,
										boolean entropy,
										int subSampleSize,
										int numberOfSamplesMode,
										double numberOfSamples,
										MSPTriple[] result,
										List<List<Double>> sampleMSPs) {
		// Drawing samples one by one
		int N = numberOfTokens();
		int B = numberOfSamples(subSampleSize, N, numberOfSamplesMode,
				numberOfSamples);
		
		// Constructing the key list of this cube
		ArrayList<DataCubeKey> keys = constructKeyList();
		
		// Run through the samples, calculating the MSPs
		MSPTriple[][] msps = new MSPTriple[B][];
		boolean overSampled = N < subSampleSize;
		
		for (int j = 0; j < B; j++)
			if (overSampled)
				msps[j] = null;
			else {
				// Draw sample
				DataCubeList sample = sampleCube(subSampleSize, keys);
				
				// Compute MSP
				msps[j] = sample.MSP(weighting, entropy).getResults();
			}
		
		// Calculate the averages and standard deviations of each span
		for (int i = 0; i < cube.size(); i++) {
			// Averaging
			String span = cubeIndex.get(i).getSpan();
			double avg = 0.0;
			double stddev = 0.0;
			int count = 0;
			
			if (!overSampled) {
				// Sample bookkeeping
				sampleMSPs.add(new ArrayList<Double>(B));
				for (int j = 0; j < msps.length; j++) {
					boolean found = false;
					for (int k = 0; !found && k < msps[j].length; k++)
						if (msps[j][k].getDataset().equals(span)) {
							if (msps[j][k].getMSP() > 0) {
								avg += msps[j][k].getMSP();
								sampleMSPs.get(i).add(msps[j][k].getMSP());
								count++;
							}
							found = true;
						}
				}
				if (count > 0)
					avg /= count;
				
				// standard deviation
				double variance = 0.0;
				for (int j = 0; j < B; j++) {
					boolean found = false;
					for (int k = 0; !found && k < msps[j].length; k++)
						if (msps[j][k].getDataset().equals(span)) {
							variance += Math.pow(msps[j][k].getMSP() - avg, 2);
							found = true;
						}
				}
				if (count > 0)
					variance /= count;
				stddev = Math.sqrt(variance);
			} else
				// Adding an empty list of samples
				sampleMSPs.add(new ArrayList<Double>());
			
			// Adding to the results
			result[i] = new MSPTriple(avg, stddev, span);
		}
	}
	
	/**
	 * Computes the number of samples to be drawn.
	 * 
	 * @param S sample size
	 * @param numTokens number of tokens in the cube
	 * @param mode mode of computing number of samples: 0 = sampling factor, 1 =
	 *            fixed value
	 * @param numSamples number of samples
	 * @return number of samples to be drawn
	 */
	protected int numberOfSamples(
									int sampleSize,
									int numTokens,
									int mode,
									double numSamples) {
		int numberOfSamples = 0;
		if (mode == 0)
			numberOfSamples = (int) Math.ceil(numTokens * numSamples
					/ sampleSize);
		else
			numberOfSamples = (int) Math.ceil(numSamples);
		
		// At least two samples
		if (numberOfSamples < 2)
			numberOfSamples = 2;
		
		return numberOfSamples;
	}
	
	/**
	 * First do resampling, then accumulate the samples (only applicable on all
	 * spans).
	 * 
	 * @param weighting use weighting?
	 * @param entropy use entropy?
	 * @param S sample size
	 * @param numberOfSamplesMode mode of number of sample: 0 = sampling factor,
	 *            1 = fixed value.
	 * @param numberofSamples number of samples or sampling factor
	 * @return MSP results
	 * @throws ImpossibleCalculationException
	 */
	public MSPResult resampleCumulateMSP(
								boolean weighting,
								boolean entropy,
								int S,
								int numberOfSamplesMode,
								double numberOfSamples)
				throws ImpossibleCalculationException {
		// Compute number of samples
		int N = numberOfTokens();
		int B = numberOfSamples(S, N, numberOfSamplesMode, numberOfSamples);
		
		// Making room for the results and the sample values
		MSPTriple[] result = new MSPTriple[cube.size()];
		List<List<Double>> sampleMSPs = new ArrayList<List<Double>>(cube.size());
		for (int i = 0; i < cube.size(); i++)
			sampleMSPs.add(new ArrayList<Double>());
		
		// Checking for oversampling
		boolean overSampled = N < S;
		if (!overSampled) {
			// Construct key list
			ArrayList<DataCubeKey> keys = constructKeyList();
			
			// For each sample
			//   Draw sample
			//   Cumulate
			//   Compute MSP
			for (int i = 0; i < B; i++) {
				// Draw sample
				ArrayList<DataCubeKey> curKeys = new ArrayList<DataCubeKey>(
						keys);
				DataCubeList sample = sampleCube(S, curKeys);
				
				// Cumulate
				DataCubeList cumulSample = sample.cumulate();
				
				// Compute MSP
				MSPTriple[] values = cumulSample.MSP(weighting, entropy)
						.getResults();
				
				// Storing the MSP values
				for (int j = 0; j < values.length; j++)
					if (values[j].getMSP() > 0)
						sampleMSPs.get(j).add(values[j].getMSP());
			}
			
			// Calculate average and standard deviation
			// For each span:
			for (int i = 0; i < sampleMSPs.size(); i++) {
				int count = 0;
				double average = 0.0;
				List<Double> values = sampleMSPs.get(i);
				
				// For each sample:
				for (int j = 0; j < values.size(); j++) {
					if (values.get(j) > 0) {
						average += values.get(j);
						count++;
					}
				}
				if (count > 0)
					average /= count;
				
				double variance = 0.0;
				double stddev = 0.0;
				if (count > 0) {
					for (int j = 0; j < values.size(); j++)
						if (values.get(j) > 0)
							variance += Math.pow(values.get(j) - average, 2);
					variance /= count;
					stddev = Math.sqrt(variance);
				}
				
				result[i] = new MSPTriple(average, stddev, cubeIndex.get(i)
						.getSpan());
			}
		} else {
			// Oversampled: thus making empty result datastructures
			// a. Results
			for (int i = 0; i < B; i++)
				result[i] = new MSPTriple(0, 0, "" + (i + 1));
			
			// b. Sample values
			for (int i = 0; i < B; i++)
				sampleMSPs.add(new ArrayList<Double>());
		}
		
		// Return the results and the sample values
		return new MSPResult(result, sampleMSPs);
	}
	
	/**
	 * Computes the unweighted variety-based MSP value of the given span.
	 * 
	 * @param span span to compute msp for
	 * @return MSP
	 */
	protected double mspVarietyUnweighted(List<List<Integer>> span) {
		if (span.size() > 0) {
			double msp = 0;
			for (List<Integer> lemma : span)
				msp += lemma.size();
			return msp / span.size();
		} else
			return 0;
	}
	
	/**
	 * Computes the weighted variety-based MSP value of the given span.
	 * 
	 * @param span span to compute msp for
	 * @return MSP
	 */
	protected double mspVarietyWeighted(List<List<Integer>> span) {
		// Compute variety and frequency per lemma
		double msp = 0;
		
		if (span.size() > 0) {
			for (List<Integer> lemma : span) {
				// a. Total number of tokens in lemma
				double tokensInLemma = tokensInLemma(lemma);
				
				// b. MSP
				msp += tokensInLemma * lemma.size();
			}
			
			msp /= totalTokensInSpan(span);
		}
		
		return msp;
	}
	
	/**
	 * Computes the unweighted, entropy-based MSP of the given span.
	 * 
	 * @param span span to compute MSP for
	 * @return MSP
	 */
	protected double mspEntropyUnweighted(List<List<Integer>> span) {
		double msp = 0;
		
		if (span.size() > 0) {
			for (List<Integer> lemma : span)
				msp += Math.pow(logBase, lemmaEntropy(lemma));
			
			msp /= span.size();
		}
		
		return msp;
	}
	
	/**
	 * Computes the weighted, entropy-based MSP of the given span.
	 * 
	 * @param span span to compute MSP for
	 * @return MSP
	 */
	protected double mspEntropyWeighted(List<List<Integer>> span) {
		// Compute entropy and weigh
		double msp = 0;
		
		if (span.size() > 0) {
			for (List<Integer> lemma : span) {
				// a. Total number of tokens in lemma
				double tokensInLemma = tokensInLemma(lemma);
				
				// b. MSP
				msp += tokensInLemma * Math.pow(logBase, lemmaEntropy(lemma));
			}
			
			msp /= totalTokensInSpan(span);
		}
		
		return msp;
	}
	
	/**
	 * Compute the entropy of a lemma.
	 * 
	 * @param lemma the lemma to compute the entropy of
	 * @return entropy
	 */
	protected double lemmaEntropy(List<Integer> lemma) {
		// 1. Total number of tokens
		double tokensInLemma = tokensInLemma(lemma);
		
		// 2. Computing, for each category, the frequency and entropy
		double entropy = 0;
		for (Integer i : lemma) {
			double freq = ((double) i) / tokensInLemma;
			entropy += freq * (Math.log10(freq) / log10LogBase);
		}
		
		return entropy * -1;
	}
	
	/**
	 * Compute the total number of tokens in the given lemma.
	 * 
	 * @param lemma lemma
	 * @return number of tokens
	 */
	protected double tokensInLemma(List<Integer> lemma) {
		// a. Total number of tokens in lemma
		double tokensInLemma = 0;
		for (Integer i : lemma)
			tokensInLemma += i;
		return tokensInLemma;
	}
	
	/**
	 * Computes the total number of tokens in the given span.
	 * 
	 * @param span span
	 * @return total number of tokens
	 */
	protected double totalTokensInSpan(List<List<Integer>> span) {
		// Compute total number of tokens in span
		double tokensInSpan = 0;
		for (List<Integer> lemma : span)
			tokensInSpan += tokensInLemma(lemma);
		return tokensInSpan;
	}
	
	/**
	 * Method used for testing purposes. Sets the data of this cube. Note that
	 * there is no SpanIndex!
	 * 
	 * @param cube data
	 */
	public void setCube(List<List<List<Integer>>> cube) {
		this.cube = cube;
	}
	
	/**
	 * Method used for testing purposes. Sets the span index of this cube.
	 * 
	 * @param spanIndex
	 */
	public void setSpanIndex(CubeIndex spanIndex) {
		this.cubeIndex = spanIndex;
	}
	
	// ==========================================================================
	// Measuring the cube
	// ==========================================================================
	/**
	 * Returns the number of tokens in this cube.
	 * 
	 * @return number of tokens
	 */
	public int numberOfTokens() {
		int num = 0;
		
		for (List<List<Integer>> span : cube)
			for (List<Integer> lemma : span)
				for (Integer n : lemma)
					num += n;
		
		return num;
	}
	
	/**
	 * Returns the number of tokens in the given span.
	 * 
	 * @param span span
	 * @return number of tokens in this span
	 */
	protected int numberOfTokens(List<List<Integer>> span) {
		int tokens = 0;
		
		for (List<Integer> lemma : span)
			for (Integer c : lemma)
				tokens += c;
		
		return tokens;
	}
	
	/**
	 * Returns the number of tokens in the span with the given name
	 * 
	 * @param spanName span name
	 * @return
	 */
	public int numberOfTokens(String spanName) {
		try {
			int pos = cubeIndex.getSpanPosition(spanName);
			return numberOfTokens(cube.get(pos));
		} catch (NoSuchSpanException e) {
			logger.error("Span not found: " + spanName);
		}
		return 1;
	}
	
	/**
	 * Returns the number of lemmas in the given span.
	 * 
	 * @param span span
	 * @return number of lemmas in span
	 */
	public int numberOfLemmas(String span) {
		int numberOfLemmas = 0;
		if (cubeIndex.containsSpan(span))
			try {
				int pos = cubeIndex.getSpanPosition(span);
				numberOfLemmas = cubeIndex.get(pos).getLemmas().size();
			} catch (NoSuchSpanException e) {
				logger.error("Span not found: " + span);
			}
		return numberOfLemmas;
	}
	
	/**
	 * Calculates the biggest value one can choose as sample size, when doing
	 * resampling.
	 * 
	 * @return maximum sample size
	 * @see test.msp.DataCubeHashTest#maxSampleSizeCumulateResample()
	 */
	public int maxSampleSizeOneSpan() {
		int maxSampleSize = 1;
		if (cube.size() > 0) {
			int min = numberOfTokens(cube.get(0));
			for (List<List<Integer>> span : cube) {
				int x = numberOfTokens(span);
				if (numberOfTokens(span) < min)
					min = x;
			}
			min--;
			maxSampleSize = min;
		}
		return maxSampleSize;
	}
	
	/**
	 * Computes the biggest value one can choose as sample size, when doing
	 * cumulation and resampling.
	 * 
	 * @return maximum sample size
	 */
	public int maxSampleSizeAllSpan() {
		if (cube.size() > 0)
			return numberOfTokens() - 1;
		return 1;
	}
	
	/**
	 * Returns a list with all spans.
	 * 
	 * @return list with all spans
	 */
	public Vector<String> getDataSets() {
		Vector<String> spans = new Vector<String>();
		
		for (SpanIndex si : cubeIndex)
			spans.add(si.getSpan());
		
		return spans;
	}
	
	/**
	 * Returns a list of all lemmas in this cube.
	 * 
	 * @return a list of all lemmas in this cube
	 */
	public Vector<String> getLemmas() {
		HashSet<String> lemmas = new HashSet<String>();
		
		for (SpanIndex si : cubeIndex)
			for (LemmaIndex li : si.getLemmas())
				lemmas.add(li.getLemma());
		
		// Converting to vector add returning
		return new Vector<String>(lemmas);
	}
	
	/**
	 * Returns the collection of all categories of the given lemma.
	 * 
	 * @param lemma lemma
	 * @return categories in the lemma
	 */
	public Collection<String> getCategories(String lemma) {
		HashSet<String> cats = new HashSet<String>();
		
		try {
			for (SpanIndex si : cubeIndex) {
				if (si.containsLemma(lemma)) {
					int lemmaPos = si.getLemmaPosition(lemma);
					for (String cat : si.getLemma(lemmaPos).getCategories())
						cats.add(cat);
				}
			}
		} catch (NoSuchLemmaException e) {
			logger.error("Could not find lemma that is present.");
		}
		
		return cats;
	}
	
	// ==========================================================================
	// Object Functionality
	// ==========================================================================
	/**
	 * Checks whether this cube is equal to the given object
	 * 
	 * @param o the other object
	 * @return equal to this cube?
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof DataCubeList) {
			// Of the same type: check
			DataCubeList cube2 = (DataCubeList) o;
			
			return equalCubes(cube, cube2.cube)
					&& cubeIndex.equals(cube2.cubeIndex);
		} else
			// Of different types: non-equal!
			return false;
	}
	
	/**
	 * Test whether the given cubes are equal.
	 * 
	 * @param c1 cube 1
	 * @param c2 cube 2
	 * @return equal?
	 */
	protected boolean equalCubes(
									List<List<List<Integer>>> c1,
									List<List<List<Integer>>> c2) {
		// 1. Same number of spans?
		if (c1.size() != c2.size())
			return false;
		
		// 2. Compare the spans:
		for (int i = 0; i < c1.size(); i++) {
			List<List<Integer>> span1 = c1.get(i);
			List<List<Integer>> span2 = c2.get(i);
			
			// 3. Same number of lemmas?
			if (span1.size() != span2.size())
				return false;
			
			// 4. Compare the lemmas
			for (int j = 0; j < span1.size(); j++) {
				List<Integer> lemma1 = span1.get(j);
				List<Integer> lemma2 = span2.get(j);
				
				// 5. Same number of categories?
				if (lemma1.size() != lemma2.size())
					return false;
				
				// 6. Compare the data
				for (int k = 0; k < lemma1.size(); k++)
					if (lemma1.get(k) != lemma2.get(k))
						return false;
			}
		}
		
		// Checked everything and got here: all ok!
		return true;
	}
	
	/**
	 * Creates a String representation of this cube.
	 * 
	 * @return String representation
	 */
	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < cube.size(); i++)
			s += convertSpan(cube.get(i), cubeIndex.get(i));
		return s;
	}
	
	/**
	 * Converts a span (= data + index) to a String
	 * 
	 * @param span span data
	 * @param si span index
	 * @return
	 */
	protected String convertSpan(List<List<Integer>> span, SpanIndex si) {
		String s = "";
		
		s += si.getSpan() + "\n";
		for (int i = 0; i < span.size(); i++)
			s += convertLemma(span.get(i), si.getLemma(i));
		
		return s;
	}
	
	/**
	 * Converts a lemma (= data + index) to a String.
	 * 
	 * @param lemma the lemma data
	 * @param li the index
	 * @return String representation
	 */
	protected String convertLemma(List<Integer> lemma, LemmaIndex li) {
		String s = "";
		DecimalFormat formatter = new DecimalFormat("######");
		
		s += "\t" + li.getLemma() + "\n";
		for (int i = 0; i < lemma.size(); i++)
			s += "\t\t" + li.getCategories().get(i) + ": "
					+ formatter.format(lemma.get(i)) + "\n";
		
		return s;
	}
}
