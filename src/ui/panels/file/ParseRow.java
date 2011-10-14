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

package ui.panels.file;

/**
 * <p>
 * Represents a row in a LineParsing table. It consists of four parts:
 * <ol>
 * 	<li>The dataset: from which the line originates</li>
 * 	<li>Frequency</li>
 * 	<li>An ante: the stuff that goes in front of the lemma.</li>
 * 	<li>The lemma</li>
 * 	<li>The category</li>
 * </ol>
 * </p>
 * @author Joris Gillis
 */
public class ParseRow implements Comparable<ParseRow> {
	
	protected String dataset, frequency, ante, lemma, category;
	
	/**
	 * Constructs an empty ParseRow. 
	 */
	public ParseRow() {
		dataset		= "";
		frequency	= "";
		ante		= "";
		lemma		= "";
		category	= "";
	}
	
	/**
	 * @return the fileName
	 */
	public String getDataSet() {
		return dataset;
	}

	/**
	 * @param dataset the dataset to set
	 */
	public void setDataSet(String dataset) {
		this.dataset = dataset;
	}
	
	/**
	 * @return the frequency
	 */
	public String getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return the ante
	 */
	public String getAnte() {
		return ante;
	}

	/**
	 * @param ante the ante to set
	 */
	public void setAnte(String ante) {
		this.ante = ante;
	}

	/**
	 * @return the lemma
	 */
	public String getLemma() {
		return lemma;
	}

	/**
	 * @param lemma the lemma to set
	 */
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}



	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ParseRow r) {
		/*
		 * Sort on fileName, lemma, category.
		 */
		if( dataset.compareTo(r.dataset) != 0 )
			return dataset.compareTo(r.dataset);
		else if( lemma.compareTo(r.lemma) != 0 )
			return lemma.compareTo(r.lemma);
		else
			return category.compareTo(r.category);
	}
}
