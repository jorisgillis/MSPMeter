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

/**
 * Class which represent a lemma and a category (a word-form in a certain slice), 
 * together with its frequency count. 
 * @author Joris Gillis
 */
public class SliceKey {
	
	/** Lemma */
	private String lemma;
	/** Category */
	private String category;
	/** frequency of the word-form at the given time */
	private int frequency;
	
	
	/**
	 * Construct a new slicekey.
	 * @param lemma		lemma
	 * @param category	category
	 * @param frequency	frequency
	 */
	public SliceKey( String lemma, String category, int frequency ) {
		this.lemma = lemma;
		this.category = category;
		this.frequency = frequency;
	}
	
	
	/**
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}
	
	
	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	
	/**
	 * @return the lemma
	 */
	public String getLemma() {
		return lemma;
	}
	
	
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	
}
