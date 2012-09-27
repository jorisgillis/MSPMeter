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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Keeps track of the order of the categories within a lemma.
 * @author joris
 */
public class LemmaIndex {

	/**
	 * The lemma.
	 */
	protected String lemma;
	/**
	 * The categories.
	 */
	protected List<String> categories;

	/**
	 * Constructs a new lemma index.
	 * @param lemma	lemma to index
	 */
	public LemmaIndex(String lemma) {
		this.lemma = lemma;
		this.categories = new LinkedList<String>();
	}
	
	/**
	 * Constructing a LemmaIndex from a given lemma and a list of categories 
	 * (= deep copy).
	 * @param lemma			lemma
	 * @param categories	categories
	 */
	public LemmaIndex(String lemma, List<String> categories) {
		// Making room
		this.lemma = lemma;
		this.categories = new LinkedList<String>();
		
		// Copying categories
		for (String cat : categories)
			this.categories.add(cat);
	}
	
	/**
	 * Add a category to the index.
	 * @param category 
	 */
	public void addCategory(String category) {
		categories.add(category);
	}
	
	/**
	 * Returns the lemma.
	 * @return lemma
	 */
	public String getLemma() {
		return lemma;
	}

	/**
	 * Returns the list of categories in this lemma.
	 * @return	list of categories
	 */
	public List<String> getCategories() {
		return categories;
	}
	
	/**
	 * Checks whether this lemma contains the given category.
	 * @param category	category to search for
	 * @return			found?
	 */
	public boolean containsCategory(String category) {
		for (String cat : categories)
			if (cat.equals(category))
				return true;
		return false;
	}
	
	/**
	 * Returns the position of the given category in this index.
	 * @param category	category to search
	 * @return			position
	 * @throws NoSuchCategoryException if category is not found
	 */
	public int getCategoryPosition(String category) throws NoSuchCategoryException {
		int pos = -1;
		
		// Search
		int count = 0;
		Iterator<String> it = categories.iterator();
		while (it.hasNext()) {
			if (it.next().equals(category)) {
				pos = count;
				break;
			}
			count++;
		}
		
		// If category not found: throw exception
		if (pos == -1)
			throw new NoSuchCategoryException();
		return pos;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof LemmaIndex) {
			// Convert
			LemmaIndex li = (LemmaIndex)o;
			
			// Compare
			boolean equal = lemma.equals(li.lemma) &&
					categories.size() == li.categories.size();
			for (int i = 0; equal && i < categories.size(); i++)
				equal = categories.get(i).equals(li.categories.get(i));
			return equal;
		} else
			return false;
	}
}
