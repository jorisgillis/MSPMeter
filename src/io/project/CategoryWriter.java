package io.project;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Class for writing out the data structures associated with the category
 * equivalences. 
 * @author Joris Gillis
 */
public class CategoryWriter {
	
	/**
	 * Write out the given data structures to the given location
	 */
	public void writeCategories( String fileName, HashMap<String, HashMap<String, Vector<String>>> subCategories,
			HashMap<String, HashSet<String>> allCategories ) throws FileNotFoundException, IOException {
		//- Sorting the lemmas
		TreeMap<String, HashSet<String>> lemmas = new TreeMap<String, HashSet<String>>(allCategories);
		
		//- Opening the file
		BufferedWriter w = new BufferedWriter(new FileWriter(fileName));
		
		//- Running through the lemmas
		Iterator<String> lit = lemmas.keySet().iterator();
		while( lit.hasNext() ) {
			String lemma = lit.next();
			w.write(lemma);
			TreeSet<String> cats = new TreeSet<String>(lemmas.get(lemma));
			
			//- Running through the categories
			Iterator<String> cit = cats.iterator();
			while( cit.hasNext() ) {
				String cat = cit.next();
				
				if( subCategories.get(lemma).containsKey(cat) ) {
					// generic category
					w.write("\t"+cat);
					
					Vector<String> subCats = subCategories.get(lemma).get(cat);
					for( int i = 0; i < subCats.size(); i++ )
						w.write("_"+subCats.get(i));
				}
			}
			
			// next lemma on the next line
			w.newLine();
		}
		
		w.close();
	}
}
