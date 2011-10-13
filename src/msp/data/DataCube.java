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


package msp.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import msp.ProgressListener;
import msp.Progressor;

import org.apache.log4j.Logger;

import ui.panels.file.FileRow;


//TODO more accurate progress tracking?


/**
 * The DataCube contains all the data available in the samples. Along the x-axis
 * are the categories, along the y-axis are the lemmas and along the z-axis the
 * samples are located.<br />
 * This class offers certain functionality in order to interact with the DataCube.
 * @author Joris Gillis
 */
public class DataCube implements Progressor, ProgressListener, Cloneable {
	
	//===========================================================================
	// Constants
	//===========================================================================
	public final int ONE_SPAN	= 0;
	public final int ALL_SPAN	= 1;
	
	
	//===========================================================================
	// Variables
	//===========================================================================
	/*
	 * Possibly it is advantageous to redesign the cube as a hashmap for the time, 
	 */

	/** 
	 * The Cube: implemented as a 3D hashmap 
	 */
	protected HashMap<String, HashMap<String, HashMap<String, Integer>>> cube;
	
	/** 
	 * Contains the number of counts in a slice. Used extensively for 
	 * calculating the frequency of a lemma. 
	 */
	protected HashMap<String, Integer> sliceTokenCounts;
	
	// The axis markers
	/** All the time elements present in the cube */
	protected Vector<String> time = new Vector<String>();
	/** All the lemmas present in the cube */
	protected Vector<String> lemmas = new Vector<String>();
	/** All the categories present in the cube */
	protected Vector<String> categories = new Vector<String>();
	
	private Vector<ProgressListener> progressListeners;
	
	//- Everywhere the same!
	private static double log2 = Math.log10(2);
	private static String newLine = System.getProperty("line.separator");
	private static Logger logger = Logger.getLogger(DataCube.class);
	
	
	
	
	//===========================================================================
	// Construction
	//===========================================================================
	/**
	 * Constructor
	 */
	public DataCube() {
		// Making room for the data
		cube = new HashMap<String, HashMap<String,HashMap<String,Integer>>>(100);
		sliceTokenCounts = new HashMap<String, Integer>(100);
		
		// The listeners
		progressListeners = new Vector<ProgressListener>();
	}
	
	
	
	
	//===========================================================================
	// Cube Operations
	//===========================================================================
	//== Filling up the cube!
	/**
	 * Fill up the cube with the given files and separators. This function takes care 
	 * off the spans. If multiple files have the same annotation (= dataset), they 
	 * will be grouped in the resulting datacube. 
	 * @param files				the files
	 * @param firstSeparator	the token(s) announcing the start of the lemma
	 * @param secondSeparator	the token(s) announcing the start of the category
	 */
	public void fillCube( Vector<FileRow> files, String firstSeparator, String secondSeparator ) 
		throws DataFaultException {
		/*
		 * For each line in each file:
		 * 1. Remove leading and trailing whitespaces.
		 * 2. Get the integer at the front: Frequency
		 * 3. Get the largest sequence of non-first-separator tokens
		 * 4. Get the largest sequence of non-second-separators tokens: Lemma
		 * 5. The remainder of the line (if larger than 0): Category
		 */
		
		try {
			//- Setting up the patterns
			Pattern pCorrectLine = Pattern.compile(".*[0-9]+.*["+ firstSeparator +"].+" +
													"(["+ secondSeparator +"].+)?");
			Pattern pFreq = Pattern.compile("[0-9]+");
			Pattern pFirst = Pattern.compile("[^"+ firstSeparator +"]*");
			Pattern pSecond = Pattern.compile("[^"+ secondSeparator +"]+");
			
			//- Setting up the cube and axises
			cube = new HashMap<String, HashMap<String,HashMap<String,Integer>>>();
			time = new Vector<String>();
			lemmas = new Vector<String>();
			categories = new Vector<String>();
			
			//- running through the list of files
			for( int i = 0; i < files.size(); i++ ) {
				FileRow fr = files.get(i);
				
				// opening the reader
				BufferedReader r = new BufferedReader(new FileReader(fr.getFile()));
				
				//- loop through the lines
				String line = "";
				int tokenCount = 0;
				while( (line = r.readLine()) != null ) {
					try {
						Matcher mCorrectLine = pCorrectLine.matcher(line);
						if( line.length() > 1 && mCorrectLine.matches() ) {
							// remember the position
							int pos = 0;
							
							// 1. removing leading and trailing whitespace
							line = line.trim();
							
							// 2. get the integer out!
							Matcher mFreq = pFreq.matcher(line);
							if( !mFreq.find(pos) )
								throw new DataFaultException("Frequency");
							int frequency =  Integer.parseInt(mFreq.group());
							tokenCount += frequency;
							pos = mFreq.end();
							
							// 3. Get the piece between the frequency and the lemma out
							Matcher mFirst = pFirst.matcher(line);
							if( !mFirst.find(pos) )
								throw new DataFaultException("Frequency -> Lemma");
							pos = mFirst.end() + 1;
							
							// 4. Get the lemma out
							Matcher mSecond = pSecond.matcher(line);
							if( !mSecond.find(pos) )
								throw new DataFaultException("Lemma");
							String lemma = mSecond.group();
							pos = mSecond.end() + 1;
							
							// 5. Get the category out
							String category = "nil";
							if( pos < line.length() )
								category = line.substring(pos);
							
//							System.out.println(frequency +" "+ lemma +" "+ category);
							
							//- Entering it in the cube
							if( !cube.containsKey(fr.getDataSet()) )
								cube.put(fr.getDataSet(), new HashMap<String, HashMap<String,Integer>>());
							if( !cube.get(fr.getDataSet()).containsKey(lemma) )
								cube.get(fr.getDataSet()).put(lemma, new HashMap<String, Integer>());
							if( !cube.get(fr.getDataSet()).get(lemma).containsKey(category) ) {
								// install
								cube.get(fr.getDataSet()).get(lemma).put(category, new Integer(frequency));
							} else {
								// update
								Integer x = cube.get(fr.getDataSet()).get(lemma).get(category);
								Integer y = x + frequency;
								cube.get(fr.getDataSet()).get(lemma).remove(category);
								cube.get(fr.getDataSet()).get(lemma).put(category, y);
							}
							
							//- Entering it in the axis
							if( !time.contains(fr.getDataSet()) )
								time.add(fr.getDataSet());
							if( !lemmas.contains(lemma) )
								lemmas.add(lemma);
							if( !categories.contains(category) )
								categories.add(category);
						}
					} catch( DataFaultException e ) {
						// a faulty line, just ignore!
					} 
				}
				// closing the reader
				r.close();
				
				// Storing tokenCount
				sliceTokenCounts.put(fr.getDataSet(), tokenCount);
			}
		} catch( Exception e ) {
			logger.error("Fault while filling the cube: "+ e.getMessage());
			throw new DataFaultException(e.getMessage());
		}
	}
	
	
	//== Equivalences
	/**
	 * Add the frequencies of the sublemmas to the frequency of the corresponding generic lemma.
	 * Remove the sublemmas from the DataCube. 
	 * @param lemmaEquivalences	mapping from sublemmas into generic lemmas 
	 */
	public DataCube lemmaEquivalences( HashMap<String, String> lemmaEquivalences ) {
		// axes
		Vector<String> newTime = new Vector<String>(time);				// stays the same
		Vector<String> newLemmas = new Vector<String>(lemmas);			// start with the same, remove the sublemmas
		Vector<String> newCategories = new Vector<String>(categories);	// stays the same
		
		// remove sublemmas from newLemmas
		Iterator<String> it = lemmaEquivalences.keySet().iterator();
		while( it.hasNext() )
			newLemmas.remove(it.next());
		
		// make a secure copy of the cube
		HashMap<String, HashMap<String, HashMap<String, Integer>>> newCube = copyCube();
		
		// remove each sublemma from the cube, and add the frequency of the sublemma
		// to the frequency of the generic lemma.
		it = lemmaEquivalences.keySet().iterator();
		while( it.hasNext() ) {
			String subLemma = it.next();
			String generic = lemmaEquivalences.get(subLemma);
			
			// run through all the months
			for( int i = 0; i < newTime.size(); i++ ) {
				HashMap<String, HashMap<String, Integer>> timeMap = newCube.get(newTime.get(i));
				if( timeMap.containsKey(subLemma) ) {
					// 1. is the generic present?
					if( !timeMap.containsKey(generic) ) {
						// if not, we need to make a new HashMap
						timeMap.put(generic, new HashMap<String, Integer>());
					}
					
					// 2. for each category, add the frequency to the generic
					Iterator<String> cit = timeMap.get(subLemma).keySet().iterator();
					while( cit.hasNext() ) {
						String category = cit.next();
						
						// 1. Has the generic the category?
						HashMap<String, Integer> genericCats = timeMap.get(generic);
						Integer subFreq = timeMap.get(subLemma).get(category);
						if( !genericCats.containsKey(category) ) {
							// add the category
							genericCats.put(category, subFreq);
						} else {
							// update the category
							Integer genericFreq = genericCats.get(category);
							Integer freq = genericFreq + subFreq;
							genericCats.remove(category);
							genericCats.put(category, freq);
						}
					}
					
					// removing the sublemma
					timeMap.remove(subLemma);
				}
			}
		}
		
		// putting it all together
		DataCube dc = new DataCube();
		dc.time = newTime;
		dc.lemmas = newLemmas;
		dc.categories = newCategories;
		dc.cube = newCube;
		
		return dc;
	}
	
	
	/**
	 * For each lemma, add the frequency of subcategories to the frequency of generic categories.
	 * @param categoryEquivalences	category equivalences
	 * @param useInMSP				use the lemma equivalences in the calculation or just for category equivalences?
	 * @param lemmaEquivalences		the lemma equivalences
	 * @return						a new cube with the category equivalences applied
	 */
	public DataCube categoryEquivalences( HashMap<String, HashMap<String, String>> categoryEquivalences, 
			boolean useInMSP, HashMap<String, String> lemmaEquivalences ) {
		// axes
		Vector<String> newTime = new Vector<String>(time);				// stays the same
		Vector<String> newLemmas = new Vector<String>(lemmas);			// stays the same
		HashSet<String> usedCategories = new HashSet<String>();			// keeps track of all the categories that are being used
		
		// make a secure copy of the cube
		HashMap<String, HashMap<String, HashMap<String, Integer>>> newCube = copyCube();
		
		// run through the cube, check for each lemma which categories have to be "folded"
		for( int i = 0; i < newTime.size(); i++ ) {
			String month = newTime.get(i);
			HashMap<String, HashMap<String, Integer>> tSlice = newCube.get(month);
			
			for( int j = 0; j < newLemmas.size(); j++ ) {
				// lemma used in the cube
				String lemma = newLemmas.get(j);
				
				/*
				 * If we are using the lemma equivalences just for the sake of category equivalences,
				 * then this is the time and place to use the lemma equivalences! If useInMSP is false
				 * and we have lemma equivalences, then we need to use a different lemma to look up 
				 * category equivalences.
				 */
				// lemma used to look up category equivalences
				String ceqLemma = lemma;
				
				// possible we have to replace the lemma with another lemma to do the lookups
				if( !useInMSP && lemmaEquivalences != null && lemmaEquivalences.containsKey(lemma) )
					ceqLemma = lemmaEquivalences.get(lemma);
				
				
				if( tSlice.containsKey(lemma) ) {
					HashMap<String, Integer> lSlice = tSlice.get(lemma);
					
					for( int k = 0; k < categories.size(); k++ ) {
						String subCategory = categories.get(k);
						if( lSlice.containsKey(subCategory) ) {
							// to business: check out the categories
							if( categoryEquivalences.get(ceqLemma) != null && 		// the lemma must exist!
									categoryEquivalences.get(ceqLemma).containsKey(subCategory) ) {
								// category is a subcategory, add it's frequency to the frequency of the generic category
								String generic = categoryEquivalences.get(ceqLemma).get(subCategory);
								usedCategories.add(generic);
								Integer subFreq = lSlice.get(subCategory);
								
								if( lSlice.containsKey(generic) ) {
									// update
									Integer current = lSlice.get(generic);
									Integer x = current + subFreq;
									lSlice.remove(generic);
									lSlice.put(generic, x);
								} else {
									// install
									lSlice.put(generic, subFreq);
								}
								
								// remove the subcategory
								lSlice.remove(subCategory);
							} else
								usedCategories.add(subCategory);
						}
					}
				}
			}
		}
		
		// Assembling the new DataCube
		DataCube dc = new DataCube();
		dc.setCategories(new Vector<String>(usedCategories));
		dc.setCube(newCube);
		dc.setLemmas(newLemmas);
		dc.setTime(newTime);
		
		return dc;
	}
	
	
	/**
	 * Loads the data structure up. Run through the cube searching for new lemmas and categories.
	 * @param categoryEquivalences	the empty data structure (in-out)
	 * @param subCategories			the empty data structure (in-out)
	 * @param allCategories			the empty data structure (in-out)
	 */
	public void loadSubAllCategories( HashMap<String, HashMap<String, String>> categoryEquivalences,
			HashMap<String, HashMap<String,Vector<String>>> subCategories,
			HashMap<String, HashSet<String>> allCategories ) {
		// Running through the dataCube
		Iterator<String> sIt = time.iterator();
		while( sIt.hasNext() ) {
			String span = sIt.next();
			
			if( cube.containsKey(span) && cube.get(span) != null ) {
				// run through lemmas
				Iterator<String> lIt = lemmas.iterator();
				while( lIt.hasNext() ) {
					String lemma = lIt.next();
					
					// adding lemmas
					if( !subCategories.containsKey(lemma) )
						subCategories.put(lemma, new HashMap<String, Vector<String>>());
					if( !allCategories.containsKey(lemma) )
						allCategories.put(lemma, new HashSet<String>());
					if( !categoryEquivalences.containsKey(lemma) )
						categoryEquivalences.put(lemma, new HashMap<String, String>());
					
					
					if( cube.get(span).containsKey(lemma) && cube.get(span).get(lemma) != null ) {
						// run through categories
						Iterator<String> cIt = categories.iterator();
						while( cIt.hasNext() ) {
							String category = cIt.next();
							
							if( cube.get(span).get(lemma).containsKey(category) ) {
								// this cell exists!
								// add it to the data structures
								if( !subCategories.get(lemma).containsKey(category) )
									subCategories.get(lemma).put(category, new Vector<String>());
								allCategories.get(lemma).add(category);
							}
						}
					}
				}
			}
		}
	}
	
	
	
	/**
	 * Make a secure copy of the cube. That means, when an adjustment is made in the copy
	 * the original stays unchanged (invariant).
	 * @return	secure copy
	 */
	private HashMap<String, HashMap<String, HashMap<String, Integer>>> copyCube() {
		HashMap<String, HashMap<String, HashMap<String, Integer>>> newCube = 
			new HashMap<String, HashMap<String,HashMap<String,Integer>>>();
		
		for( int i = 0; i < time.size(); i++ ) {
			String month = time.get(i);
			if( cube.get(month) != null ) {
				if( newCube.get(month) == null )
					newCube.put(month, new HashMap<String, HashMap<String, Integer>>());
				
				for( int j = 0; j < lemmas.size(); j++ ) {
					String lemma = lemmas.get(j);
					if( cube.get(month).get(lemma) != null ) {
						if( newCube.get(month).get(lemma) == null )
							newCube.get(month).put(lemma, new HashMap<String, Integer>());
						
						for( int k = 0; k < categories.size(); k++ ) {
							String category = categories.get(k);
							
							if( cube.get(month).get(lemma).get(category) != null )
								newCube.get(month).get(lemma).put(category, new Integer(cube.get(month).get(lemma).get(category).intValue()));
						}
					}
				}
			}
		}
		
		return newCube;
	}
	
	
	
	/**
	 * <p>
	 * The preferred sampling procedure: run over the array of entries and 
	 * select a token with dynamic probability, based on the number of already 
	 * selected tokens and the number of tokens still remaining in the array. 
	 * The advantage of this procedure is that we do not have to take a copy of 
	 * the entries for each sample, thus saving both time and memory.
	 * </p>
	 * <p>
	 * This function returns an array of samples.
	 * </p>
	 * @param S		Sample size
	 * @param xMode	Sampling mode (Sampling factor or Fixed Value)
	 * @param X		Sampling factor or Fixed Value
	 * @return		An array of samples
	 */
	public DataCube[] resample( int S, int xMode, double X ) {
		// The numbers
		int N = numberOfTokens();
		int B = numberOfSamples(N, S, xMode, X);
		
		// the subcorpora
		DataCube[] subCorpora = new DataCube[B];
		for( int i = 0; i < B; i++ )
			subCorpora[i] = null;
		
		/*
		 * If we need to take more samples from the cube, than there are
		 * elements in the cube, we need to return empty subCorpora. 
		 */
		if( numElements() < S )
			return subCorpora;
		
		// Constructing the array of tokens
		DataCubeEntry[] entries = createEntryArray(N);
		
		// Run over array
		for( int j = 0; j < B; j++ )
			subCorpora[j] = sampleCube(S, N, entries);
		
		return subCorpora;
	}




	/**
	 * <p>
	 * Function to perform a single all-span sampling of a datacube. The 
	 * preferred sampling procedure: run over the array of entries and 
	 * select a token with dynamic probability, based on the number of already 
	 * selected tokens and the number of tokens still remaining in the array. 
	 * The advantage of this procedure is that we do not have to take a copy of 
	 * the entries for each sample, thus saving both time and memory.
	 * </p>
	 * <p>
	 * This function returns only one sample!
	 * </p>
	 * @param S			sample size
	 * @param N			number of tokens in cube
	 * @param entries	entry array of this cube
	 */
	protected DataCube sampleCube(int S, int N, DataCubeEntry[] entries) {
		//- keep track of the axes
		TreeSet<String> usedTime = new TreeSet<String>();
		TreeSet<String> usedLemmas = new TreeSet<String>();
		TreeSet<String> usedCategories = new TreeSet<String>();
		HashMap<String, HashMap<String, HashMap<String, Integer>>> cubicle 
			= new HashMap<String, HashMap<String,HashMap<String,Integer>>>();
		
		int m = 0;	// Chosen
		for( int i = 0; i < N && m < S; i++) {
			double chance = ((double)(S-m))/(N-i);
			if (chance >= Math.random()) {
				String month = entries[i].getMonth();
				String lemma = entries[i].getLemma();
				String category = entries[i].getCategory();
				
				// Updating axes
				usedTime.add(month);
				usedLemmas.add(lemma);
				usedCategories.add(category);
				
				// Updating cubicle
				if( cubicle.get(month) == null )
					cubicle.put(month, 
							new HashMap<String, HashMap<String, Integer>>());
				if( cubicle.get(month).get(lemma) == null )
					cubicle.get(month).put(lemma, new HashMap<String, Integer>());
				if( cubicle.get(month).get(lemma).get(category) == null )
					cubicle.get(month).get(lemma).put(category, 1);
				else {
					int x = cubicle.get(month).get(lemma).get(category);
					x++;
					cubicle.get(month).get(lemma).remove(category);
					cubicle.get(month).get(lemma).put(category, x);
				}
				
				// Increment chosen counter
				m++;
			}
		}
		
		// Converting sets to vectors
		Vector<String> ts = new Vector<String>(usedTime.size());
		Vector<String> ls = new Vector<String>(usedLemmas);
		Vector<String> cs = new Vector<String>(usedCategories);
		
		// keep the time vector in order: add missing times
		for( int i = 0; i < time.size(); i++ )
			if( usedTime.contains(time.get(i)) )
				ts.add(time.get(i));
		
		// Storing the sample
		DataCube c = new DataCube();
		c.setCube(cubicle);
		c.setCategories(cs);
		c.setLemmas(ls);
		c.setTime(ts);
		
		return c;
	}




	/**
	 * Creates an array out of the datacube. There are as many spaces occupied
	 * by an entry as the frequency of that entry.
	 * @param N		number of tokens
	 * @return		array of DataCubeEntries
	 */
	protected DataCubeEntry[] createEntryArray(int N) {
		DataCubeEntry[] entries = new DataCubeEntry[N];
		int i = 0;
		Iterator<String> i1 = cube.keySet().iterator();
		while( i1.hasNext() ) {
			String month = i1.next();
			Iterator<String> i2 = cube.get(month).keySet().iterator();
			while( i2.hasNext() ) {
				String lemma = i2.next();
				Iterator<String> i3 = cube.get(month).get(lemma).keySet().iterator();
				while( i3.hasNext() ) {
					String category = i3.next();
					
					// Adding the entries freq times
					int freq = cube.get(month).get(lemma).get(category);
					for( int k = 0; k < freq; k++ ) {
						entries[i] = new DataCubeEntry(month, lemma, category, 
								cube.get(month).get(lemma).get(category));
						i++;
					}
				}
			}
		}
		return entries;
	}
	
	
	/**
	 * Computes the number of samples taken
	 * @param N		number of tokens in the data
	 * @param S		size of samples
	 * @param xMode	sampling mode (sampling factor or fixed value)
	 * @param X		Sampling factor or Fixed value
	 * @return
	 */
	protected int numberOfSamples(int N, int S, int xMode, double X) {
		int B = 2;
		if( xMode == 0 )
			B = (int)Math.ceil(N*X/S);
		else
			B = (int)Math.ceil(X);
		
		// at least two samples have to be taken
		if( B < 2 )
			B = 2;
		return B;
	}
	
	
	
	//==========================================================================
	//== Resampling the datacube into a list of new datacubes
	//==========================================================================
	/**
	 * <p>
	 * Alternative, slower sampling procedure.
	 * </p>
	 * <p>
	 * Make random resamplings from the original datacube. This method 
	 * constructs a set of subcorpora.
	 * </p>
	 * <p>
	 * Let S be the expected total number of tokens per subcorpus. B = N/T 
	 * subcorpora are constructed. A token is selected for a subcorpus with 
	 * P = T/N.
	 * </p>
	 * @param S		number of tokens per subcorpus
	 * @param xMode	sampling mode: X ? 0: B = N * X / S, 1: B = X
	 * @param X		X-factor (sampling factor or fixed value)
	 */
	public DataCube[] resampleAlternate( int S, int xMode, double X ) {
		// determine B
		int N = numberOfTokens();					// Number of Tokens
		int B = numberOfSamples(N, S, xMode, X);	// Number of Samples
		
//		System.out.println("N = "+ N);
//		System.out.println("B = "+ B);
		
		// the subcorpora
		DataCube[] subCorpora = new DataCube[B];
		for( int i = 0; i < B; i++ )
			subCorpora[i] = null;
		
		
		/*
		 * If we need to take more samples from the cube, than there are
		 * elements in the cube, we need to return empty subCorpora. 
		 */
		if( numElements() < S )
			return subCorpora;
		
		
		/* making the list of tokens that can be chosen
		 * a random number is chosen:
		 * 	- run through the list from front to back always subtracting the 
		 * 	number of occurrences of the word-form
		 * 	- if the random number (subtractions taken into account) becomes 
		 * 	negative, we choose the
		 * 	current element under the "head" 
		 */
		
		// running through the cube: constructing an entry-list
		Vector<DataCubeEntry> entryList = createEntryVector();
		
		// for each subcorpora
		for( int i = 0; i < B; i++ ) {
			//- keep track of the axes
			TreeSet<String> usedTime = new TreeSet<String>();
			TreeSet<String> usedLemmas = new TreeSet<String>();
			TreeSet<String> usedCategories = new TreeSet<String>();
			HashMap<String, HashMap<String, HashMap<String, Integer>>> cubicle 
				= new HashMap<String, HashMap<String,HashMap<String,Integer>>>();
			
			//- 1. take a copy of the infrastructure (keyList)
			int M = N;
			Vector<DataCubeEntry> entries = 
				new Vector<DataCubeEntry>(entryList.size());
			for( int k = 0; k < entryList.size(); k++ ) {
				DataCubeEntry key = entryList.get(k);
				entries.add(new DataCubeEntry(key.getMonth(), 
											key.getLemma(), 
											key.getCategory(), 
											key.getFrequency()));
			}
			
			//- 2. for each space in the subcorpus
			for( int j = 0; j < S; j++ ) {
				//- choose
				// random number and run through the key list
				double random = Math.random()*M;
				DataCubeEntry chosen = null;
				int head = 0;
				while( random > 0 ) {
					chosen = entries.get(head++);
					random -= chosen.getFrequency();
				}
				M--;
				chosen.setFrequency(chosen.getFrequency()-1);
				
				//- add
				// axes
				usedTime.add(chosen.getMonth());
				usedLemmas.add(chosen.getLemma());
				usedCategories.add(chosen.getCategory());
				
				// cube
				String month = chosen.getMonth();
				String lemma = chosen.getLemma();
				String category = chosen.getCategory();
				
				if( cubicle.get(month) == null )
					cubicle.put(month, new HashMap<String, HashMap<String,Integer>>());
				if( cubicle.get(month).get(lemma) == null )
					cubicle.get(month).put(lemma, new HashMap<String,Integer>());
				if( cubicle.get(month).get(lemma).get(category) == null )
					cubicle.get(month).get(lemma).put(category, 1);
				else {
					Integer y = cubicle.get(month).get(lemma).get(category);
					Integer x = y + 1;
					cubicle.get(month).get(lemma).remove(category);
					cubicle.get(month).get(lemma).put(category, x);
				}
			}
			
			// Converting sets to vectors
			Vector<String> ts = new Vector<String>(usedTime.size());
			Vector<String> ls = new Vector<String>(usedLemmas);
			Vector<String> cs = new Vector<String>(usedCategories);
			
			// keep the time vector in order: add missing times
			for( int j = 0; j < time.size(); j++ )
				if( usedTime.contains(time.get(j)) )
					ts.add(time.get(j));
			
			subCorpora[i] = new DataCube();
			subCorpora[i].setCube(cubicle);
			subCorpora[i].setCategories(cs);
			subCorpora[i].setLemmas(ls);
			subCorpora[i].setTime(ts);
		}
		
		return subCorpora;
	}




	/**
	 * Creates a vector of DataCubeEntries. Each entry is entered once in
	 * the cube, unlike the DataCube array!
	 * @return	vector of DataCubeEntries
	 */
	protected Vector<DataCubeEntry> createEntryVector() {
		Vector<DataCubeEntry> entryList = new Vector<DataCubeEntry>();
		Iterator<String> i1 = cube.keySet().iterator();
		while( i1.hasNext() ) {
			String month = i1.next();
			Iterator<String> i2 = cube.get(month).keySet().iterator();
			while( i2.hasNext() ) {
				String lemma = i2.next();
				Iterator<String> i3 = cube.get(month).get(lemma).keySet().iterator();
				while( i3.hasNext() ) {
					String category = i3.next();
					entryList.add(new DataCubeEntry(month, lemma, category, 
							cube.get(month).get(lemma).get(category)));
				}
			}
		}
		return entryList;
	}




	/**
	 * Make several samples of the given slice.
	 * @param slice	the slice
	 * @param S		number of tokens in the sample
	 * @param xMode	0: B = N * X / S, 1: B = X
	 * @param X		X-factor (resampling factor or fixed value)
	 * @return		samples
	 */
	public HashMap<String, HashMap<String, Integer>>[] resampleAlternate( 
			HashMap<String,	HashMap<String, Integer>> slice, 
			int S, 
			int xMode, 
			double X) {
		//- 1. Setting the variables
		int N = numberOfTokens( slice );
		int B = numberOfSamples(N, S, xMode, X);
		
		// Initialize array of samples with null
		HashMap<String, HashMap<String, Integer>>[] samples 
			= (HashMap<String, HashMap<String, Integer>>[])new HashMap[B];
		for( int i = 0; i < B; i++ )
			samples[i] = null;
		
		// System.out.println("B = "+ B);
		
		/*
		 * If the number of samples is larger than the number of available elements
		 * in the slice, we need to return an array of empty slices. 
		 */
		if( elementsInSlice( slice ) < S )
			return samples;
		
		
		//- 2. running through the slice constructing a keylist
		Vector<SliceEntry> keyList = createSliceEntryVector(slice);
		
		
		/*
		 * Choosing a random number, browsing through the keylist, always 
		 * decreasing the random number until it hits the zero.
		 */
		for( int i = 0; i < B; i++ ) {
			// for the new sample
			HashMap<String, HashMap<String, Integer>> newSlice = new HashMap<String, HashMap<String, Integer>>();
			
			// take a copy of the infrastructure
			int M = N;
			Vector<SliceEntry> keys = new Vector<SliceEntry>(keyList.size());
			for( int k = 0; k < keyList.size(); k++ ) {
				SliceEntry key = keyList.get(k);
				keys.add(new SliceEntry(key.getLemma(), key.getCategory(), key.getFrequency()));
			}
			
			// choose a sample
			for( int j = 0; j < S; j++ ) {
				SliceEntry chosen = null;
				double random = Math.random()*M;
				int head = 0;
				while( random >= 0 ) {
					chosen = keys.get(head++);
					random -= chosen.getFrequency();
				}
				M--;
				chosen.setFrequency(chosen.getFrequency()-1);
				
				// add
				String lemma = chosen.getLemma();
				String category = chosen.getCategory();
				
				if( !newSlice.containsKey(lemma) )
					newSlice.put(lemma, new HashMap<String, Integer>());
				if( !newSlice.get(lemma).containsKey(category) )
					newSlice.get(lemma).put(category, 1);
				else {
					Integer x = newSlice.get(lemma).get(category);
					Integer y = x + 1;
					newSlice.get(lemma).remove(category);
					newSlice.get(lemma).put(category, y);
				}
			}
			
			samples[i] = newSlice;
		}
		
		
		return samples;
	}




	/**
	 * <p>
	 * Creates a vector of tokens from the given slice, tokens are not repeated!
	 * </p>
	 * @param slice	slice to create the array from
	 * @return		vector of entries
	 */
	protected Vector<SliceEntry> createSliceEntryVector(
			HashMap<String, HashMap<String, Integer>> slice) {
		Vector<SliceEntry> keyList = new Vector<SliceEntry>();
		Iterator<String> i1 = slice.keySet().iterator();
		while( i1.hasNext() ) {
			String lemma = i1.next();
			Iterator<String> i2 = slice.get(lemma).keySet().iterator();
			while( i2.hasNext() ) {
				String category = i2.next();
				keyList.add( new SliceEntry(lemma, category, slice.get(lemma).get(category) ) );
			}
		}
		return keyList;
	}




	/**
	 * <p>
	 * Takes the required number of samples from the slice and returns the
	 * samples (i.e. slices). This function uses the array loop 
	 * </p>
	 * @param slice		slice to sample
	 * @param S			sample size
	 * @param xMode		Is X Sampling Factor or Fixed value?
	 * @param X			Sampling Factor or Fixed value
	 * @return			an array of samples
	 */
	public HashMap<String, HashMap<String, Integer>>[] resample( 
			HashMap<String,	HashMap<String, Integer>> slice, 
			int S, 
			int xMode, 
			double X) {
		//- 1. Setting the variables
		int N = numberOfTokens( slice );
		int B = numberOfSamples(N, S, xMode, X);
		
		// Initialize array of samples with null
		HashMap<String, HashMap<String, Integer>>[] samples 
			= (HashMap<String, HashMap<String, Integer>>[])new HashMap[B];
		for( int i = 0; i < B; i++ )
			samples[i] = null;
		
		// System.out.println("B = "+ B);
		
		/*
		 * If the number of samples is larger than the number of available elements
		 * in the slice, we need to return an array of empty slices. 
		 */
		if( elementsInSlice( slice ) < S )
			return samples;
		
		
		//- 2. running through the slice constructing a keylist
		SliceEntry[] entries = createSliceEntryArray(slice, N);
		
		
		//- 3. Creating the samples
		for (int i = 0; i < B; i++ )
			samples[i] = sampleSlice(N, S, entries);
		
		return samples;
	}




	/**
	 * <p>
	 * Creates an array of slice entries from the given slice, containing the
	 * given number of tokens. A token is in the array as many times as its
	 * frequency. For example, if a token occurs 5 times, there will be five
	 * (consecutive) bins of the array filled with this token.
	 * </p>
	 * @param slice	slice to create array from
	 * @param N		number of tokens in the slice
	 * @return		array of (repeated) slice entries
	 */
	private SliceEntry[] createSliceEntryArray(
			HashMap<String, HashMap<String, Integer>> slice, int N) {
		// Creating room for the entries
		SliceEntry[] entries = new SliceEntry[N];
		
		// Running through the slice
		Iterator<String> i1 = slice.keySet().iterator();
		int i = 0;
		while( i1.hasNext() ) {
			String lemma = i1.next();
			
			Iterator<String> i2 = slice.get(lemma).keySet().iterator();
			while( i2.hasNext() ) {
				String category = i2.next();
				
				// Adding the token as many times as its frequency
				int freq = slice.get(lemma).get(category);
				for( int j = 0; j < freq; j++ ) {
					entries[i] = new SliceEntry(lemma, category, freq);
					i++;
				}
			}
		}
		
		return entries;
	}
	
	
	
	/**
	 * <p>
	 * Creates a single sample of given size.
	 * </p>
	 * @param N			number of tokens in slice
	 * @param S			sample size
	 * @param entries	slice entries of the slice
	 * @return	a sample of size S
	 */
	protected HashMap<String, HashMap<String, Integer>> sampleSlice(
			int N, int S, SliceEntry[] entries) {
		// Creating room for the sample
		HashMap<String, HashMap<String, Integer>> sample = 
			new HashMap<String, HashMap<String,Integer>>();
		
		int m = 0; // Already chosen tokens
		for( int i = 0; i < N; i++ ) {
			double chance = ((double)(S-m))/((double)(N-i));
			
			if( chance >= Math.random()) {
				// Select token at position i!
				String lemma = entries[i].getLemma();
				String category = entries[i].getCategory();
				
				// Put token in sample
				if( sample.get(lemma) == null )
					sample.put(lemma, new HashMap<String, Integer>());
				if( sample.get(lemma).get(category) == null )
					sample.get(lemma).put(category, 1);
				else {
					int x = sample.get(lemma).get(category) + 1;
					sample.get(lemma).remove(category);
					sample.get(lemma).put(category, x);
				}
				
				m++;
			}
		}
		
		return sample;
	}
	


	//== Cumulate the datacube
	/**
	 * Cumulate the cube. Add everything from the previous months to all the next months. 
	 * @return	the accumulated datacube
	 */
	public DataCube cumulate() {
		/* create the new cube, because we are in the DataCube class we can directly
			manipulate the new cube! */
		DataCube dc = new DataCube();

		// copying the axes
		dc.setCategories(new Vector<String>(categories));
		dc.setLemmas(new Vector<String>(lemmas));
		dc.setTime(new Vector<String>(time));

		/* run over all the months, lemmas and categories:
		 * add the count x of the current (lemma, category) to all the months
		 * that are coming behind the current month.
		 * 	If there already exists a count for the (lemma, category) pair in
		 * 	a month, the x is added
		 * 	Else a new entry is made and x is inserted */
		
		HashMap<String, HashMap<String, HashMap<String, Integer>>> newCube = new HashMap<String, HashMap<String, HashMap<String, Integer>>>();
		for( int i = 0; i < time.size(); i++ ) {
			HashMap<String, HashMap<String, Integer>> curLemmas = cube.get(time.get(i));
			if( curLemmas != null )
				for( int j = 0; j < lemmas.size(); j++ ) {
					HashMap<String, Integer> curCategories = curLemmas.get(lemmas.get(j));
					if( curCategories != null ) {
						for( int k = 0; k < categories.size(); k++ ) {
							Integer x = curCategories.get(categories.get(k));
							if( x != null )
								for( int l = i; l < time.size(); l++ ) {
									if( newCube.get(time.get(l)) != null &&
											newCube.get(time.get(l)).get(lemmas.get(j)) != null &&
											newCube.get(time.get(l)).get(lemmas.get(j)).get(categories.get(k)) != null ) {
										// add up
										Integer y = newCube.get(time.get(l)).get(lemmas.get(j)).get(categories.get(k));
										Integer z = x + y;
										newCube.get(time.get(l)).get(lemmas.get(j)).remove(categories.get(k));
										newCube.get(time.get(l)).get(lemmas.get(j)).put(categories.get(k), z);
									} else {
										// insert new one
										if( newCube.get(time.get(l)) == null )
											newCube.put(time.get(l), new HashMap<String, HashMap<String, Integer>>());
										if( newCube.get(time.get(l)).get(lemmas.get(j)) == null )
											newCube.get(time.get(l)).put(lemmas.get(j), new HashMap<String, Integer>());
										newCube.get(time.get(l)).get(lemmas.get(j)).put(categories.get(k), x);
									}
								}
						}
					}
				}
		}
		
		// Storing the new data in the new cube
		dc.setCube(newCube);
		
		return dc;
	}
	
	
	
	
	
	
	//===========================================================================
	// MEAN SIZE OF PARADIGM: User Interface
	//===========================================================================
	/**
	 * Calculate the MSP of each month
	 * @param weighting		weight the values?
	 * @param entropy		use entropy or variety?
	 * @return				MSP values
	 * @throws ImpossibleCalculationException
	 */
	public MSPResult MSP( boolean weighting, boolean entropy ) 
		throws ImpossibleCalculationException {
		// basic case: just calculate the MSP of each month
		MSPSpan[] result = new MSPSpan[time.size()];
		
		for( int i = 0; i < time.size(); i++ ) {
			try {
				if( !weighting && !entropy )
					result[i] = 
						new MSPSpan(mspVarietyUnweighted(time.get(i)), time.get(i));
				else if( weighting && !entropy )
					result[i] = 
						new MSPSpan(mspVarietyWeighted(time.get(i)), time.get(i));
				else if( !weighting && entropy )
					result[i] = 
						new MSPSpan(mspEntropyUnweighted(time.get(i)), time.get(i));
				else if( weighting && entropy )
					result[i] = 
						new MSPSpan(mspEntropyWeighted(time.get(i)), time.get(i));
			} catch( NoDataException e ) {
				logger.error("No data? This should not be happening!");
				result[i] = new MSPSpan(0.0, time.get(i));
			}
			
			// notify the interested object about our progress
			notifyProgressListeners((((double)i+1)/time.size())*100);
		}
		
		return new MSPResult(result, null);
	}
	
	
	
	/**
	 * Calculate the MSP values of each month using resampling. Resampling can either be MSP(S)
	 * or "All Span" resampling.
	 * @param weighting				use the weighted variant (false = unweighted, true = weighted)
	 * @param entropy				which base to use? (false = variety, true = entropy)
	 * @param subSampleMode			how to subsample?
	 * @param subSampleSize			size of the subsamples
	 * @param numberOfSamplesMode	how to adjust the number of subsamples?
	 * @param numberOfSamples		how many subsamples?
	 * @throws ImpossibleCalculationException	if something goes wrong in the calculation
	 */
	public MSPResult resampleMSP(boolean weighting, 
									boolean entropy, 
									int subSampleMode, 
									int subSampleSize,
									int numberOfSamplesMode, 
									double numberOfSamples ) 
			throws ImpossibleCalculationException {
		//- Making space for the result and the samples
		MSPSpan[] result = new MSPSpan[time.size()];
		List<List<Double>> sampleMSPs = new ArrayList<List<Double>>(time.size());
		
		
		// depending on the subsampling method: "one span" or "all span"
		if( subSampleMode == ONE_SPAN ) {
			//- Calculate resampled MSP: MSP(S)
			resampleMSPOneSpan(weighting, entropy, subSampleSize,
					numberOfSamplesMode, numberOfSamples, result, sampleMSPs);
		}
		else if( subSampleMode == ALL_SPAN ) {
			//- Calculate the "All Span" resampling
			sampleMSPs = resampleMSPAllSpan(weighting, entropy, subSampleSize,
					numberOfSamplesMode, numberOfSamples, result);
		}
		
		return new MSPResult( result, sampleMSPs );
	}
	
	/**
	 * Computes the sampled MSP values for ONE SPAN sampling
	 * @param weighting				weighting or not?
	 * @param entropy				entropy or not?
	 * @param subSampleSize			size of samples
	 * @param numberOfSamplesMode	Sampling Factor or Fixed Value?
	 * @param numberOfSamples		The Sampling Factor or Fixed Value
	 * @param result				Results array
	 * @param sampleMSPs			List of MSP values for all samples 
	 * @throws ImpossibleCalculationException
	 */
	protected void resampleMSPOneSpan(boolean weighting, boolean entropy,
			int subSampleSize, int numberOfSamplesMode, double numberOfSamples,
			MSPSpan[] result, List<List<Double>> sampleMSPs)
			throws ImpossibleCalculationException {
		for( int i = 0; i < time.size(); i++ ) {
			String span = time.get(i);
			
			// Compute the number of samples
			int N = numberOfTokens( cube.get(span) );
			int B = numberOfSamples(
						N, 
						subSampleSize, 
						numberOfSamplesMode, 
						numberOfSamples);
			
			// Make room in sampleMSPs
			sampleMSPs.add( new ArrayList<Double>(B) );
			
			if( N < subSampleSize ) {
				// Oversampling: we need to take more tokens from the 
				// sample then there are tokens to take!!
				// 1. Fill sampleMSPs with empty lists
				for( int j = 0; j < B; j++ )
					sampleMSPs.get(i).add(new Double(0.0));
				
				// 2. Fill results with neutral MSPSpan
				result[i] = new MSPSpan(1, 0, span);
			} else {
				// Entries
				SliceEntry[] entries = 
					createSliceEntryArray(cube.get(span), N);
				
				// calculate the values
				double[] msps = new double[B];
				
				if( !weighting && !entropy ) {
					// unweighted variety
					for( int j = 0; j < B; j++ )
						try {
							msps[j] = mspVarietyUnweighted(sampleSlice(
											N, subSampleSize, entries));
							sampleMSPs.get(i).add( msps[j] );
						} catch( NoDataException e ) {
							logger.error("No data? This should not be happening!");
							msps[j] = 0.0;
						}
				}
				else if( weighting && !entropy ) {
					// weighted variety
					for( int j = 0; j < B; j++ )
						try {
							HashMap<String, HashMap<String, Integer>> sample =
								sampleSlice(N, subSampleSize, entries);
							msps[j] = mspVarietyWeighted(numberOfTokens(sample), 
															sample);
							sampleMSPs.get(i).add( msps[j] );
						} catch( NoDataException e ) {
							logger.error("No data? This should not be happening!");
							msps[j] = 0.0;
						}
				}
				else if( !weighting && entropy ) {
					// unweighted entropy
					for( int j = 0; j < B; j++ )
						try {
							msps[j] = mspEntropyUnweighted(sampleSlice(
											N, subSampleSize, entries));
							sampleMSPs.get(i).add( msps[j] );
						} catch( NoDataException e ) {
							logger.error("No data? This should not be happening!");
							msps[j] = 0.0;
						}
				}
				else if( weighting && entropy ) {
					// weighted entropy
					for( int j = 0; j < B; j++ )
						try {
							HashMap<String, HashMap<String, Integer>> sample =
								sampleSlice(N, subSampleSize, entries);
							msps[j] = mspEntropyWeighted(numberOfTokens(sample), 
															sample);
							sampleMSPs.get(i).add( msps[j] );
						} catch( NoDataException e ) {
							logger.error("No data? This should not be happening!");
							msps[j] = 0.0;
						}
				}
				
				// calculate the average
				double average = 0.0;
				for( int j = 0; j < B; j++ )
					average += msps[j];
				average /= B;
				
				// calculate the standard deviation
				double variance = 0.0;
				for( int j = 0; j < B; j++ )
					variance = Math.pow(msps[j] - average, 2);
				variance /= B;
				double stddev = Math.sqrt(variance);
				
				result[i] = new MSPSpan(average, stddev, span);
				
				// making progress
				notifyProgressListeners( (((double)i+1)/time.size())*100 );
			}
		}
	}
	
	/**
	 * Calculates the sampled MSP for ALL SPAN sampling.
	 * @param weighting				weighting or not
	 * @param entropy				entropy or not
	 * @param subSampleSize			size of samples
	 * @param numberOfSamplesMode	Sampling Factor or Fixed Value?
	 * @param numberOfSamples		The Sampling Factor or Fixed Value.
	 * @param result				Result array
	 * @return						List of MSP values for all samples
	 * @throws ImpossibleCalculationException
	 */
	protected List<List<Double>> resampleMSPAllSpan(boolean weighting,
			boolean entropy, int subSampleSize, int numberOfSamplesMode,
			double numberOfSamples, MSPSpan[] result)
			throws ImpossibleCalculationException {
		List<List<Double>> sampleMSPs;
		int N = numberOfTokens();
		int B = numberOfSamples(N, 
					subSampleSize, 
					numberOfSamplesMode, 
					numberOfSamples);
		DataCubeEntry[] entries = createEntryArray(N);
		
		notifyProgressListeners(10);
		
		// Run through the samples, calculating the MSPs
		MSPSpan[][] msps = new MSPSpan[B][];
		boolean overSampled = false;
		for( int j = 0; j < B; j++ ) {
			if( N < subSampleSize ) {
				// Need more tokens in the sample then there are tokens
				// in the cube!!
				msps[j] = null;
				overSampled = true;
			} else {
				// Take a sample
				DataCube sample = sampleCube(subSampleSize, N, entries);
				msps[j] = sample.MSP(weighting, entropy).getResults();
			}
		}
		notifyProgressListeners(40);
		
		// Calculate the averages and standard deviations of each span
		sampleMSPs = new ArrayList<List<Double>>(time.size());
		for( int i = 0; i < time.size(); i++ ) {
			// averaging
			String span = time.get(i);
			double avg = 0.0, stddev = 0.0;
			int count = 0;
			
			if( !overSampled ) {
				// Sample bookkeeping
				sampleMSPs.add( new ArrayList<Double>(B) );
				for( int j = 0; j < msps.length; j++ ) {
					boolean found = false;
					for( int k = 0; !found && k < msps[j].length; k++ )
						if( msps[j][k].getSpan().equals(span) ) {
							avg += msps[j][k].getMSP();
							sampleMSPs.get(i).add(msps[j][k].getMSP());
							count++;
							found = true;
						}
				}
				if( count > 0 ) 
					avg /= count;
				
				// standard deviation
				double variance = 0.0;
				for( int j = 0; j < B; j++ ) {
					boolean found = false;
					for( int k = 0; !found && k < msps[j].length; k++ )
						if( msps[j][k].getSpan().equals(span) ) {
							variance += Math.pow(msps[j][k].getMSP() - avg, 2);
							found = true;
						}
				}
				if( count > 0 )
					variance /= count;
				stddev = Math.sqrt(variance);
			} else {
				// Adding an empty list of samples
				sampleMSPs.add( new ArrayList<Double>() );
			}
			
			// adding to the results
			result[i] = new MSPSpan(avg, stddev, span);
			
			// making progress
			notifyProgressListeners( 40 + (((double)i+1)/time.size())*60 );
		}
		return sampleMSPs;
	}




	//== Resampled MSP
	/**
	 * Resampling & Accumulation
	 * @param weighted				use the weighted variant (false = unweighted, true = weighted)
	 * @param entropy				which base to use? (false = variety, true = entropy)
	 * @param S						size of the samples
	 * @param numberOfSamplesMode	factor or fixed?
	 * @param numberOfSamples		X
	 */
	public MSPResult resampleCumulateMSP( boolean weighted, boolean entropy, 
			int S, int numberOfSamplesMode, double numberOfSamples ) 
			throws ImpossibleCalculationException {
		// Output data structures
		MSPSpan[] result = new MSPSpan[time.size()];
		List<List<Double>> sampleMSPs = new ArrayList<List<Double>>(time.size());
		
		// Checking for oversampling
		int N = numberOfTokens();
		boolean overSampled = N < S;
		
		if( !overSampled ) {
			// How many samples do we need to take
			int B = numberOfSamples(N, S, numberOfSamplesMode, numberOfSamples);
			
			// Creating entries of datacube
			DataCubeEntry[] entries = createEntryArray(N);
			
			// For each span: MSP values from samples
//			HashMap<String, List<Double>> msps = 
//				new HashMap<String, List<Double>>();
			
			// Initialize sampleMSPs
			for( int i = 0; i < time.size(); i++ )
				sampleMSPs.add(new ArrayList<Double>(B));
			
			for ( int k = 0; k < B; k++ ) {
				// Taking a sample & Cumulate
				DataCube cube = sampleCube(S, N, entries);
				cube.cumulate();
				
				// calculate the MSPs
				for( int i = 0; i < time.size(); i++ ) {
					String month = time.get(i);
					
					double msp = -1;
					if( !weighted && !entropy )
						// unweighted variety
						try {
							msp = cube.mspVarietyUnweighted(month);
						} catch( NoDataException e ) {
							// no data available for a given month, indicate this with a -1
							// this way we can take the absence of data into account, rather than using NaN's
							logger.debug("Empty month: "+ month);
						}
					else if( weighted && !entropy )
						// weighted variety
						try {
							msp = cube.mspVarietyWeighted(month);
						} catch( NoDataException e ) {
							// no data available for a given month, indicate this with a -1
							// this way we can take the absence of data into account, rather than using NaN's
							logger.debug("Empty month: "+ month);
						}
					else if( !weighted && entropy )
						// unweighted entropy
						try {
							msp = cube.mspEntropyUnweighted(month);
						} catch( NoDataException e ) {
							// no data available for a given month, indicate this with a -1
							// this way we can take the absence of data into account, rather than using NaN's
							logger.debug("Empty month: "+ month);
						}
					else if( weighted && entropy )
						// weighted entropy
						try {
							msp = cube.mspEntropyWeighted(month);
						} catch( NoDataException e ) {
							// no data available for a given month, indicate this with a -1
							// this way we can take the absence of data into account, rather than using NaN's
							logger.debug("Empty month: "+ month);
						}
					sampleMSPs.get(i).add(msp);
					
					// making progress
					notifyProgressListeners( 20.0 + (((double)i+1)/time.size())*70 );
				}
			}
			
			
			
			// calculate the averages and standard deviations
			for( int i = 0; i < sampleMSPs.size(); i++ ) {
				int count = 0;
				double average = 0.0;
				for( int j = 0; j < sampleMSPs.get(i).size(); j++ )
					if( sampleMSPs.get(i).get(j) > 0.0 ) {
						average += sampleMSPs.get(i).get(j);
						count++;
					}
				average /= count;
				
				double variance = 0.0;
				for( int j = 0; j < sampleMSPs.get(i).size(); j++ )
					if( sampleMSPs.get(i).get(j) > 0.0 )
						variance += Math.pow(sampleMSPs.get(i).get(j) - average, 2);
				variance /= count;
				double stddev = Math.sqrt(variance);
				
				result[i] = new MSPSpan( average, stddev, time.get(i) );
			}
		} else {
			// Oversampled: thus making empty result datastructures
			// a. Result
			for( int i = 0; i < result.length; i++ )
				result[i] = new MSPSpan(0, 0, time.get(i));
			
			// b. sampleMSPs
			for( int i = 0; i < time.size(); i++ )
				sampleMSPs.add(new ArrayList<Double>() );
		}
		
		// making progress
		notifyProgressListeners(100);
		
		return new MSPResult( result, sampleMSPs );
	}




	//===========================================================================
	// MEAN SIZE OF PARADIGM
	//===========================================================================
	//== Standard MSP
	/**
	 * Calculate the MSP value using variety and no weighting
	 * @param month		MSP of this month
	 */
	public double mspVarietyUnweighted( String month ) 
	throws ImpossibleCalculationException, NoDataException {
		//		logger.debug("Calculating mspVarietyUnweighted");
		if( cube.get(month) == null )
			throw new NoDataException();
		return mspVarietyUnweighted( cube.get(month) );
	}
	
	/**
	 * Calculate the MSP value using variety and no weighting
	 * @param slice	Slice in which the MSP must be calculated
	 */
	public double mspVarietyUnweighted( HashMap<String, HashMap<String, Integer>> slice ) 
	throws ImpossibleCalculationException, NoDataException {
		//		logger.debug("Calculating mspVarietyUnweighted");
		double x = (double)o( ".", ".", slice );
		double y = (double)o( "*", ".", slice );
		return x / y;
	}
	
	/**
	 * Calculate the MSP value using variety and weighting
	 * @param month		MSP of this month
	 * @return			Weighted Variety MSP for a given month
	 * @throws ImpossibleCalculationException
	 */
	public double mspVarietyWeighted( String month ) 
	throws ImpossibleCalculationException, NoDataException {
		//		logger.debug("Calculating mspVarietyWeighted");
		return mspVarietyWeighted( sliceTokenCounts.get(month), cube.get(month) );
	}
	
	/**
	 * Calculate the MSP value using variety and weighting
	 * @param tokenCount	number of tokens in the slice
	 * @param slice			slice of cube
	 * @return		Weighted Variety MSP values for a given slice
	 * @throws ImpossibleCalculationException
	 */
	public double mspVarietyWeighted( int tokenCount, 
			HashMap<String, HashMap<String, Integer>> slice ) 
	throws ImpossibleCalculationException, NoDataException {
		//		logger.debug("Calculating mspVarietyWeighted");
		if( slice == null )
			throw new NoDataException();
		
		double r = 0.0;
		for( int i = 0; i < lemmas.size(); i++ )
			r += fSpeed("*", lemmas.get(i), slice, tokenCount) 
					* o(".", lemmas.get(i), slice);
		return r;
	}
	
	/**
	 * Calculate the MSP value using entropy and no weighting
	 * @param month		MSP of this month
	 * @return			Unweighted Entropy MSP values of the given month
	 * @throws ImpossibleCalculationException
	 */
	public double mspEntropyUnweighted( String month ) throws ImpossibleCalculationException, NoDataException {
		//		logger.debug("Calculating mspEntropyUnweighted");
		return mspEntropyUnweighted(cube.get(month));
	}
	
	/**
	 * Calculate the MSP value using entropy and no weighting
	 * @param slice	the values
	 * @return		Weighted Entropy MSP values
	 * @throws ImpossibleCalculationException
	 */
	public double mspEntropyUnweighted( HashMap<String, HashMap<String, Integer>> slice ) throws ImpossibleCalculationException, NoDataException {
		//		logger.debug("Calculating mspEntropyUnweighted");
		if( slice == null )
			throw new NoDataException();
		
		double r = 0.0;
		for( int i = 0; i < lemmas.size(); i++ ) {
			if( slice.containsKey(lemmas.get(i)) ) {
				double hc = hc(".", lemmas.get(i), slice);
				r += perplexity(hc);
			}
		}
		double o = o("*", ".", slice);
		r /= o;
		
		return r;
	}
	
	
	/**
	 * Calculate the MSP value using entropy and weighting
	 * @param month		MSP of this month
	 * @return			Weighted Entropy of a given month
	 * @throws ImpossibleCalculationException
	 */
	public double mspEntropyWeighted( String month ) throws ImpossibleCalculationException, NoDataException {
		//		logger.debug("Calculating mspEntropyWeighted");
		return mspEntropyWeighted( sliceTokenCounts.get(month), cube.get(month) );
	}
	
	/**
	 * Calculate the MSP value using entropy and weighting
	 * @param slice	the values
	 * @return		Weighted Entropy of a given slice
	 * @throws ImpossibleCalculationException
	 */
	public double mspEntropyWeighted( int tokenCount,
			HashMap<String, HashMap<String, Integer>> slice ) 
	throws ImpossibleCalculationException, NoDataException {
		//		logger.debug("Calculating mspEntropyWeighted");
		if( slice == null )
			throw new NoDataException();
		
		double r = 0.0;
		for( int i = 0; i < lemmas.size(); i++ )
			r += fSpeed("*", lemmas.get(i), slice, tokenCount) 
					* perplexity(hc(".", lemmas.get(i), slice));
		return r;
	}
	
	
	
	
	
	
	
	//== n: absolute frequency
	/**
	 * Absolute frequency of category j, lemma l at month t.
	 * @param category	j or .
	 * @param lemma		l or .
	 * @param month		t or .
	 */
	public int n( String category, String lemma, String month ) throws ImpossibleCalculationException {
		// TODO speed optimization: keep the time vector and a time hashmap to quickly verify a month
		if( !time.contains(month) )
			throw new ImpossibleCalculationException( "("+ category +", "+ lemma +") at month "+ month );
		
		return n( category, lemma, cube.get(month) );
	}
	
	
	/**
	 * Absolute frequency of category j, lemma l at month t.
	 * @param category	j or .
	 * @param lemma		l or .
	 * @param slice		the slice in which we are working
	 */
	protected int n( String category, String lemma, 
			HashMap<String, HashMap<String, Integer>> slice ) 
	throws ImpossibleCalculationException {
		int r = 0;
		
		if( category.equals("*") || lemma.equals("*") )
			throw new ImpossibleCalculationException( "("+ category +", "+ lemma +") in slice "+ slice );
		
		// Vectors contain all possible values
		Vector<String> l;
		Vector<String> c;

		// Initialize the vectors
		if( lemma.equals(".") )
			l = new Vector<String>(slice.keySet());
		else {
			l = new Vector<String>();
			l.add(lemma);
		}

		if( category.equals(".") )
			c = categories;
		else {
			c = new Vector<String>();
			c.add(category);
		}

		// run through the slice
		for( int j = 0; j < l.size(); j++ )
			if( slice.get(l.get(j)) != null )
				for( int k = 0; k < c.size(); k++ )
					if( slice.get(l.get(j)).get(c.get(k)) != null )
						r += slice.get(l.get(j)).get(c.get(k));

		return r;
	}
	
	
	
	
	//== f: relative frequency
	/**
	 * Relative frequency of category j, lemma l and month t
	 * @param category	j or *
	 * @param lemma		l or *
	 * @param month		t
	 * @return			frequency of the given category and lemma in the given month
	 * @throws ImpossibleCalculationException
	 */
	public double f( String category, String lemma, String month ) throws ImpossibleCalculationException {
		if( !time.contains(month) )
			throw new ImpossibleCalculationException( "("+ category +", "+ lemma +") at month "+ month );
		
		return fSpeed( category, lemma, cube.get(month), sliceTokenCounts.get(month) );
	}
	
	
	/**
	 * Relative frequency of category j, lemma l in the given slice.
	 * @param category	j or *
	 * @param lemma		l or *
	 * @param slice		the values
	 * @return			frequency of the given category and lemma in the given slice
	 * @throws ImpossibleCalculationException
	 */
	protected double f( String category, String lemma, 
			HashMap<String, HashMap<String, Integer>> slice ) 
	throws ImpossibleCalculationException {
		if( category.equals(".") || lemma.equals(".") )
			throw new ImpossibleCalculationException( "("+ category +", "+ lemma +") at slice "+ slice);

		// arguments to pass to n
		String nc = category;
		String nl = lemma;
		
		if( category.equals("*") ) nc = ".";
		if( lemma.equals("*") ) nl = ".";
		
		return ((double)n(nc, nl, slice)) / n(".",".", slice);
	}
	
	/**
	 * Faster version of f, uses the sliceTokenCounts. This requires passing
	 * both the age and the slice of the age. 
	 * @param category	j or *
	 * @param lemma		l or *
	 * @param age		t
	 * @param slice		the values
	 * @return			frequency of the given category and lemma in the given slice
	 * @throws ImpossibleCalculationException
	 */
	protected double fSpeed( String category, String lemma,  
			HashMap<String, HashMap<String, Integer>> slice, int tokenCount ) 
	throws ImpossibleCalculationException {
		// arguments to pass to n
		String nc = category;
		String nl = lemma;
		
		if( category.equals("*") ) nc = ".";
		if( lemma.equals("*") ) nl = ".";
		
		return ((double)n(nc, nl, slice)) / tokenCount;
	}
	
	

	/**
	 * Relative conditional frequency of category j, lemma l and month t. 
	 * Conditional on the lemma.
	 * @param category	j
	 * @param lemma		l
	 * @param month		t
	 * @return			relative conditional frequency
	 * @throws ImpossibleCalculationException
	 */
	public double fc( String category, String lemma, String month ) 
	throws ImpossibleCalculationException {
		if( !time.contains(month) )
			throw new ImpossibleCalculationException( "("+ category +", "+ lemma +") at month "+ month );

		return fc(category, lemma, cube.get(month));
	}
	
	
	/**
	 * Relative conditional frequency of category j, lemma l and month t. 
	 * Conditional on the lemma.
	 * @param category	j
	 * @param lemma		l
	 * @param slice		the values
	 * @return			relative conditional frequency
	 * @throws ImpossibleCalculationException
	 */
	protected double fc( String category, String lemma, HashMap<String, HashMap<String, Integer>> slice ) throws ImpossibleCalculationException {
		if( category.equals(".") || category.equals("*") ||
				lemma.equals(".") || lemma.equals("*") )
			throw new ImpossibleCalculationException( "("+ category +", "+ lemma +") at slice "+ slice );

		return ((double)n(category, lemma, slice)) / n(".", lemma, slice);
	}
	
	
	//== o: Variety
	/**
	 * Variety or occurrence of category j, lemma l at month t. Can handle any 
	 * combination of .'s *'s or values.
	 * @param category	j or . or *
	 * @param lemma		l or . or *
	 * @param month		t or . or *
	 * @throws ImpossibleCalculationException
	 */
	public double o( String category, String lemma, String month ) throws ImpossibleCalculationException {
		if( !time.contains(month) )
			throw new ImpossibleCalculationException( "("+ category +", "+ lemma +") at month "+ month );
		
		return o( category, lemma, cube.get(month) );
	}
	
	/**
	 * Variety or occurrence of category j, lemma l at month t. Can handle any 
	 * combination of .'s *'s or values.
	 * @param category	j or . or *
	 * @param lemma		l or . or *
	 * @param slice		the values
	 * @throws ImpossibleCalculationException
	 */
	public double o( String category, String lemma, HashMap<String, HashMap<String, Integer>> slice ) throws ImpossibleCalculationException {
		/*
		 * If one of the arguments is a dot (.), call the sum-function, otherwise pass it 
		 * directly to the basic function.
		 */
		if( category.equals(".") || lemma.equals(".") )
			return oSum(category, lemma, slice);
		else
			return oBasic(category, lemma, slice);
	}
	
	
	/**
	 * Occurrence of category j, lemma l at month t. Only accepts concrete 
	 * values or *.
	 * @param category	j or *
	 * @param lemma		l or *
	 * @param slice		the values
	 * @return
	 * @throws ImpossibleCalculationException
	 */
	private double oBasic( String category, String lemma, 
			HashMap<String, HashMap<String, Integer>> slice ) 
			throws ImpossibleCalculationException {
		if( category.equals(".") || lemma.equals(".") )
			throw new ImpossibleCalculationException( "("+ category +", "+ lemma +") at slice "+ slice );
		
		/*
		 * If a * occurs, scan the whole axis.
		 */
		boolean found = false;
		Vector<String> c;
		Vector<String> l;
		
		// fixing the axes
		if( category.equals("*") )
			c = categories;
		else {
			c = new Vector<String>();
			c.add(category);
		}
		
		if( lemma.equals("*") )
			l = lemmas;
		else {
			l = new Vector<String>();
			l.add(lemma);
		}
		
		// running through the axes
		for( int j = 0; !found && j < l.size(); j++ )
			if( slice.get(l.get(j)) != null )
				for( int k = 0; !found && k < c.size(); k++ )
					if( slice.get(l.get(j)).get(c.get(k)) != null ) {
						found = slice.get(l.get(j)).get(c.get(k)) > 0;
					}
		
		// occurred or not? 
		if( found )
			return 1;
		else
			return 0;
	}

	/**
	 * Variety of category j, lemma l at month t. Handles .'s
	 * @param category	j or .
	 * @param lemma		l or .
	 * @param slice		the values
	 * @return
	 * @throws ImpossibleCalculationException
	 */
	private double oSum( String category, String lemma, 
			HashMap<String, HashMap<String, Integer>> slice ) 
	throws ImpossibleCalculationException {
		double r = 0.0;
		
		/*
		 * If a . occurs, scan the whole axis.
		 */
		Vector<String> c;
		Vector<String> l;
		
		// fixing the axes
		if( category.equals(".") )
			c = categories;
		else {
			c = new Vector<String>();
			c.add(category);
		}
		
		if( lemma.equals(".") )
			l = lemmas;
		else {
			l = new Vector<String>();
			l.add(lemma);
		}
		
		
		// running through the axes
		// no if-checks, because the variables can contain *
		for( int j = 0; j < l.size(); j++ )
			for( int k = 0; k < c.size(); k++ )
				r += oBasic(c.get(k), l.get(j), slice);
		
		return r;
	}


	//== h: Entropy
	/**
	 * Calculate entropy of category j, lemma l at month t.
	 * @param category	j or .
	 * @param lemma		l or .
	 * @param month		t or *
	 */
	public double h( String category, String lemma, String month ) 
		throws ImpossibleCalculationException {
		if( !time.contains(month) )
			throw new ImpossibleCalculationException( 
					"("+ category +", "+ lemma +") at month "+ month );
		
		return h( category, lemma, cube.get(month) );
	}
	
	/**
	 * Calculate entropy of category j, lemma l at month t.
	 * @param category	j or .
	 * @param lemma		l or .
	 * @param slice		the values
	 */
	public double h( String category, String lemma, HashMap<String, 
			HashMap<String, Integer>> slice ) 
			throws ImpossibleCalculationException {
		if( category.equals("*") || lemma.equals("*") )
			throw new ImpossibleCalculationException( 
					"("+ category +", "+ lemma +") at slice "+ slice );
		
		// Vectors implementing the axes
		Vector<String> c;
		Vector<String> l;
		
		if( category.equals(".") )
			c = categories;
		else {
			c = new Vector<String>();
			c.add(category);
		}
		
		if( lemma.equals(".") )
			l = lemmas;
		else {
			l = new Vector<String>();
			l.add(lemma);
		}
		
		// running along the axes
		double r = 0.0;
		int sliceTokenCount = numberOfTokens(slice);
		for( int i = 0; i < c.size(); i++ )
			for( int j = 0; j < l.size(); j++ ) {
				double f = fSpeed( categories.get(i), lemmas.get(j), 
									slice, sliceTokenCount );
				if( f < -0.0000000000001 || 0.000000000001 < f )
					r += f * Math.log10(f) / log2;
			}
		r = -r;
		
		return r;
	}
	
	/**
	 * Calculate the conditional entropy. Conditional to the lemma.
	 * @param category	j or .
	 * @param lemma		l
	 * @param month		t or *
	 * @return			conditional entropy
	 */
	public double hc( String category, String lemma, String month ) 
	throws ImpossibleCalculationException {
		if( !time.contains(month) )
			throw new ImpossibleCalculationException( "("+ category +", "+ lemma +") at month "+ month );

		return hc( category, lemma, cube.get(month) );
	}
	
	/**
	 * Calculate the conditional entropy. Conditional to the lemma.
	 * @param category	j or .
	 * @param lemma		l
	 * @param slice		the values
	 * @return			conditional entropy
	 */
	public double hc( String category, String lemma, 
			HashMap<String, HashMap<String, Integer>> slice ) 
	throws ImpossibleCalculationException {
		if( category.equals("*") || lemma.equals(".") || lemma.equals("*") )
			throw new ImpossibleCalculationException( "("+ category +", "+ lemma +") at slice "+ slice );

		// If a . occurs, specify the axis-values
		Vector<String> c;

		if( category.equals(".") )
			c = categories;
		else {
			c = new Vector<String>();
			c.add(category);
		}

		// run through the x-axis, pass along an occasional *
		double r = 0.0;
		for( int i = 0; i < c.size(); i++ ) {
			double fc = fc( c.get(i), lemma, slice );
			if( fc < -0.000000001 || 0.000000001 < fc ) {
				double log = Math.log10(fc) / log2;
				r += fc * log;
			}
		}
		r = -r;
		
		return r;
	}
	
	
	/**
	 * Calculate perplexity, given entropy
	 * @param entropy	entropy
	 */
	public double perplexity( double entropy ) {
		return Math.pow(2, entropy);
	}
	
	
	
	
	
	//==========================================================================
	//== Number of tokens in a cube or slice.
	//==========================================================================
	/**
	 * The number of tokens available in the corpus. Size of the corpus.
	 * @return	size of the corpus
	 * @see test.msp.DataCubeTest#maxSampleSizeResampleCumulate()
	 */
	public int numberOfTokens() {
		int r = 0;
		
		if( cube != null ) {
			Iterator<String> i1 = cube.keySet().iterator();
			while( i1.hasNext() ) {
				String month = i1.next();
				Iterator<String> i2 = cube.get(month).keySet().iterator();
				while( i2.hasNext() ) {
					String lemma = i2.next();
					Iterator<String> i3 = cube.get(month).get(lemma).keySet().iterator();
					while( i3.hasNext() ) {
						String category = i3.next();
						r += cube.get(month).get(lemma).get(category);
					}
				}
			}
		}
		
		return r;
	}




	/**
	 * The number of tokens available in the given slice.
	 * @param slice the slice
	 * @return	size of the slice
	 */
	public int numberOfTokens( HashMap<String, HashMap<String, Integer>> slice ) {
		int r = 0;
		
		if( slice != null ) {
			Iterator<String> i1 = slice.keySet().iterator();
			while( i1.hasNext() ) {
				String lemma = i1.next();
				Iterator<String> i2 = slice.get(lemma).keySet().iterator();
				while( i2.hasNext() ) {
					String category = i2.next();
					r += slice.get(lemma).get(category);
				}
			}
		}
			
		return r;
	}




	/**
	 * Returns the number of tokens in the asked for slice.
	 * @param sliceName	name of the slice we want to interact with
	 * @return			number of tokens in that slice (if it exists)
	 */
	public int numberOfTokens( String sliceName ) {
		if( cube.containsKey(sliceName) )
			return numberOfTokens(cube.get(sliceName));
		return 0;
	}




	/**
	 * Number of lemmas in the cube.
	 * @return	number of lemmas
	 */
	public int numberOfLemmas() {
		return lemmas.size();
	}




	/**
	 * Number of lemmas in the asked for slice.
	 * @param sliceName	name of the asked for slice
	 * @return			number of lemmas in that slice
	 */
	public int numberOfLemmas( String sliceName ) {
		if( cube.containsKey(sliceName) )
			return cube.get(sliceName).size();
		return 0;
	}




	/**
	 * B = N / S
	 * @return	B
	 */
	public Integer defaultNumberOfTokens( int S ) {
		double tokens = numberOfTokens();
		double Brough = tokens / S;
		return (int)Math.ceil(Brough);
	}




	//==========================================================================
	//== Calculating the maximum size of a sample
	//==========================================================================
	/**
	 * Calculates the biggest value one can choose as sample size, when doing cumulate & resample
	 * @return	maximum sample size
	 * @see test.msp.DataCubeTest#maxSampleSizeCumulateResample()
	 */
	public int maxSampleSizeOneSpan() {
		if( time.size() > 0 ) {
			int min = numberOfTokens(cube.get(time.get(0)));
			for( int i = 1; i < time.size(); i++ ) {
				int x = numberOfTokens(cube.get(time.get(i)));
				if( x < min )
					min = x;
			}
			
			min--;
			return min;
		}
		return 1;
	}




	/**
	 * Calculates the biggest value one can choose as sample size, when doing resample & cumulate
	 * @return	maximum sample size
	 * @see test.msp.DataCubeTest#maxSampleSizeResampleCumulate()
	 */
	public int maxSampleSizeAllSpan() {
		if( time.size() > 0 )
			return numberOfTokens()-1;
		return 1;
	}




	//===========================================================================
	// Progressor
	//===========================================================================
	/*
	 * (non-Javadoc)
	 * @see msp.Progressor#addProgressListener(msp.ProgressListener)
	 */
	public void addProgressListener( ProgressListener l ) {
		progressListeners.add(l);
	}
	
	/*
	 * (non-Javadoc)
	 * @see msp.Progressor#removeProgressListener(msp.ProgressListener)
	 */
	public void removeProgressListener( ProgressListener l ) {
		progressListeners.remove(l);
	}
	
	/**
	 * Notify the progress listeners of the progress we made.
	 * @param progress	progress made so far
	 */
	private void notifyProgressListeners( double progress ) {
		for( int i = 0; i < progressListeners.size(); i++ )
			progressListeners.get(i).progressed(progress);
	}
	
	/*
	 * (non-Javadoc)
	 * @see msp.ProgressListener#progressed(double)
	 */
	public void progressed(double progress) {
		// when a datacube is cumulated, progress has to be rerouted
		notifyProgressListeners(progress);
	}
	
	
	//===========================================================================
	// Getters
	//===========================================================================
	/**
	 * Returns the list of datasets in the cube.
	 * @return	list of datasets
	 */
	public Vector<String> getDataSets() {
		return time;
	}
	
	/**
	 * Returns the list of lemmas in the cube.
	 * @return 	list of lemmas
	 */
	public Vector<String> getLemmas() {
		return lemmas;
	}
	
	/**
	 * Returns the list of all categories occurring with the given lemma.
	 * @param lemma	the lemma
	 * @return		list of all categories
	 */
	public Collection<String> getCategories(String lemma) {
		HashSet<String> cats = new HashSet<String>();
		
		for( int i = 0; i < time.size(); i++ ) {
			if( cube.containsKey(time.get(i)) && cube.get(time.get(i)).containsKey(lemma) ) {
				// lemma available in the cube at time time.get(i)
				// running through the available categories, adding them to the mix!
				Iterator<String> it = cube.get(time.get(i)).get(lemma).keySet().iterator();
				while( it.hasNext() )
					cats.add(it.next());
			}
		}
		
		return cats;
	}
	
	/**
	 * Returns the number of elements in this cube. 
	 * @return	number of elements in this cube
	 */
	private int numElements() {
		int count = 0;
		
		// running through the cube
		for( String month : cube.keySet() )
			for( String lemma : cube.get(month).keySet() )
				for( String category : cube.get(month).get(lemma).keySet() )
					count += cube.get(month).get(lemma).get(category);
		
		return count;
	}
	
	/**
	 * Returns the number of elements in a slice. 
	 * @param slice	the slice
	 * @return		the number of elements in the slice
	 */
	private int elementsInSlice( HashMap<String, HashMap<String, Integer>> slice ) {
		int count = 0;
		
		// Running through the slice
		for( String lemma : slice.keySet() )
			for( String category : slice.get(lemma).keySet() )
				count += slice.get(lemma).get(category);
		
		return count;
	}
	
	/**
	 * Checks if the slice contains no elements. 
	 * @param slice	the slice
	 * @return		empty or not
	 */
	private boolean emptySlice( HashMap<String, HashMap<String, Integer>> slice ) {
		return elementsInSlice( slice ) == 0;
	}
	
	
	//===========================================================================
	// Object-related functionality
	//===========================================================================
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		// create a table for each time (= file) dimension of the cube
		String s = "";

		Set<String> tKeys = cube.keySet();
		Iterator<String> tIt = tKeys.iterator();
		while( tIt.hasNext() ) {
			// key
			String tKey = tIt.next();
			s += tKey + newLine;

			// make the table
			HashMap<String, HashMap<String, Integer>> cube2D = cube.get(tKey);
			if( cube2D != null ) {
				Iterator<String> lIt = cube2D.keySet().iterator();
				while( lIt.hasNext() ) {
					String lKey = lIt.next();
					s += lKey+"\t\t";
					HashMap<String, Integer> cube1D = cube2D.get(lKey);
					if( cube1D != null ) {
						Iterator<String> cIt = cube1D.keySet().iterator();
						while( cIt.hasNext() ) {
							String cKey = cIt.next();
							Integer freq = cube1D.get(cKey);
							s += cKey +" ("+ freq +")\t\t";
						}
					}
					s += newLine;
				}
			}

			s += newLine + newLine;
		}

		return s;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals( Object o ) {
		boolean equal = false;
		
		if( o instanceof DataCube ) {
			// we have a datacube!
			DataCube dc = (DataCube)o;
			
			// compare the axes and the cube
			equal = cube.equals(dc.cube) && equalVectors(categories, dc.categories)
						&& equalVectors(lemmas, dc.lemmas) && equalVectors(time, dc.time);
		}
		
		return equal;
	}
	
	/**
	 * Checks if there is a one-to-one correspondence between the two vectors.
	 * Two elements match if they are equal.
	 * @param v1	vector 1
	 * @param v2	vector 2
	 * @return		is there a one-to-one correspondence
	 */
	private boolean equalVectors( Vector<String> v1, Vector<String> v2 ) {
		boolean equal = v1.size() == v2.size();
		
		// v1 to v2
		for( int i = 0; equal && i < v1.size(); i++ ) {
			boolean found = false;
			for( int j = 0; !found && j < v2.size(); j++ )
				found = v1.get(i).equals(v2.get(j));
			equal = found;
		}
		
		// v2 to v1
		for( int j = 0; j < v2.size(); j++ ) {
			boolean found = false;
			for( int i = 0; !found && i < v1.size(); i++ )
				found = v2.get(j).equals(v1.get(i));
			equal = found;
		}
		
		return equal;
	}
	
	
	/**
	 * Takes a copy of this DataCube.
	 * @return	copy of this DataCube
	 * @throws CloneNotSupportedException
	 */
	public DataCube copy() throws CloneNotSupportedException {
		return (DataCube)this.clone();
	}
	
	
	
	
	//== TESTING ONLY
	public void setCube( HashMap<String, HashMap<String, HashMap<String, Integer>>> cube ) {
		// Storing data
		this.cube = cube;
		
		// Computing sliceTokenCounts
		Iterator<String> i1 = cube.keySet().iterator();
		while( i1.hasNext() ) {
			String age = i1.next();
			HashMap<String, HashMap<String, Integer>> slice = cube.get(age);
			int tokenCount = 0;
			
			Iterator<String> i2 = slice.keySet().iterator();
			while( i2.hasNext() ) {
				HashMap<String, Integer> categories = slice.get(i2.next());
				
				Iterator<String> i3 = categories.keySet().iterator();
				while( i3.hasNext() )
					tokenCount += categories.get(i3.next());
			}
			
			// Storing tokenCount
			sliceTokenCounts.put(age, tokenCount);
		}
	}

	public void setTime( Vector<String> time ) {
		this.time = time;
	}

	public void setLemmas( Vector<String> lemmas ) {
		this.lemmas = lemmas;
	}

	public void setCategories( Vector<String> categories ) {
		this.categories = categories;
	}
}
