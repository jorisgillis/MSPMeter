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
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * A DataFile is given a path to a file, treats that file as a frequency file
 * and allows other classes to iterate over the content. If no category is
 * present, the default category 'nil' is used.
 * @author Joris Gillis
 */
public class FrequencyFile implements Iterable<FrequencyLineParse> {
	
	/** Stores the line parses */
	protected List<FrequencyLineParse> lines;
	
	// Logger
	private static Logger logger = Logger.getLogger(FrequencyFile.class);
	
	
	/**
	 * Constructs a new FrequencyFile, filling it up with the given file.
	 * @param file				frequency file
	 * @param firstSeparator	separates ante from lemma
	 * @param secondSeparator	separates lemma from category
	 */
	public FrequencyFile( File file, String firstSeparator, 
			String secondSeparator ) throws DataFaultException {
		processFile(file, firstSeparator, secondSeparator);
	}
	
	/**
	 * Processes a file into a list of FrequencyLineParse's. 
	 * @param file				file to parse
	 * @param firstSeparator	separates ante from lemma
	 * @param secondSeparator	separates lemma from category
	 */
	private void processFile( File file, String firstSeparator, 
			String secondSeparator ) throws DataFaultException {
		
		if( file == null )
			throw new DataFaultException("No file specified.");
		if( firstSeparator == null || firstSeparator.length() == 0 )
			throw new DataFaultException("No characters defined for the starting "+
					"point of lemmas.");
		if( secondSeparator == null || secondSeparator.length() == 0 )
			throw new DataFaultException("No characters defined for the division "+
					"of lemmas and categories.");
		
		/*
		 * For each line in each file:
		 * 1. Remove leading and trailing whitespaces.
		 * 2. Get the integer at the front: Frequency
		 * 3. Get the largest sequence of non-first-separator tokens
		 * 4. Get the largest sequence of non-second-separators tokens: Lemma
		 * 5. The remainder of the line (if larger than 0): Category
		 */
		// Make room for the parses
		lines = new LinkedList<FrequencyLineParse>();
		
		// Patterns
		Pattern pCorrectLine = 
			Pattern.compile(".*[0-9]+.*["+ firstSeparator +"].+" +
				"(["+ secondSeparator +"].+)?");
		Pattern pFreq = Pattern.compile("[0-9]+");
		Pattern pFirst = Pattern.compile("[^"+ firstSeparator +"]*");
		Pattern pSecond = Pattern.compile("[^"+ secondSeparator +"]+");
		
		try {
			// opening the reader
			BufferedReader r = new BufferedReader(new FileReader(file));
			
			//- loop through the lines
			String line = "";
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
						String frequency = mFreq.group();
						pos = mFreq.end();
						
						// 3. Get the piece between the frequency and the lemma out
						Matcher mFirst = pFirst.matcher(line);
						if( !mFirst.find(pos) )
							throw new DataFaultException("Frequency -> Lemma");
						String ante = line.substring(pos, mFirst.end());
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
						
						lines.add(new FrequencyLineParse(frequency, 
															ante, 
															lemma, 
															category));
					}
				} catch( DataFaultException e ) {
					// A faulty line, just ignore
				}
			}
			
			// closing the reader
			r.close();
		} catch( Exception e ) {
			logger.error("Fault while parsing file "+ file +": "+ e.getMessage());
			throw new DataFaultException(e.getMessage());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<FrequencyLineParse> iterator() {
		return lines.iterator();
	}
}
