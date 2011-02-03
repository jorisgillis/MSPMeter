package io.project;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Functions for writing out lemma equivalences.
 * @author Joris Gillis
 */
public class LemmaWriter {
	
	/**
	 * Writes a lemma file, comprised of the given data structures.
	 * @param fileName	where we store
	 * @param subLemmas	mapping from lemma to the list of sublemmas
	 * @param allLemmas	set of all lemmas
	 */
	public void writeLemmas( String fileName, HashMap<String, Vector<String>> subLemmas, HashSet<String> allLemmas )
		throws FileNotFoundException, IOException {
		//- Sort out all the lemmas
		TreeSet<String> lemmas = new TreeSet<String>(allLemmas);
		
		//- Open up the file
		BufferedWriter w = new BufferedWriter(new FileWriter(fileName));
		
		Iterator<String> it = lemmas.iterator();
		while( it.hasNext() ) {
			String lemma = it.next();
			if( subLemmas.containsKey(lemma) ) {
				// generic lemma
				w.write(lemma+"\t"+lemma);
				
				// write out all the sublemmas of this generic lemma
				Vector<String> subs = subLemmas.get(lemma);
				for( int i = 0; i < subs.size(); i++ )
					w.write("_"+ subs.get(i));
				
				// start the next one on a new line
				w.newLine();
			}
		}
		
		w.close();
	}
	
}
