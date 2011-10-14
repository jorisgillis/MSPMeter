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
 * The result of parsing a line in a frequency file, consisting of:
 * <ul>
 * 	<li>Frequency</li>
 * 	<li>Ante</li>
 * 	<li>Lemma</li>
 * 	<li>Category</li>
 * </ul>
 * @author joris
 */
public class FrequencyLineParse {
	// The Data
	protected String frequency, ante, lemma, category;
	
	
	/**
	 * Constructs a new FrequencyLineParse.
	 * @param frequency	frequency
	 * @param ante		ante
	 * @param lemma		lemma
	 * @param category	category
	 */
	public FrequencyLineParse(String frequency, 
								String ante, 
								String lemma, 
								String category) {
		this.frequency	= frequency;
		this.ante		= ante;
		this.lemma		= lemma;
		this.category	= category;
	}


	/**
	 * @return the frequency
	 */
	public String getFrequency() {
		return frequency;
	}


	/**
	 * @return the ante
	 */
	public String getAnte() {
		return ante;
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
