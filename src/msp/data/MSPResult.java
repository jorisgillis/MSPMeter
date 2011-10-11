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

import java.util.List;

/**
 * <p>
 * This object consists of a MSPSpan array representing the results of an
 * MSP calculation, and a sampleMSPs list of lists of doubles containing
 * the raw MSP values for the samples. If the MSP calculation uses no sampling
 * the sampleMSPs field will be null.
 * </p> 
 * @author joris
 */
public class MSPResult {
	
	private MSPSpan[] result;
	private List<List<Double>> sampleMSPs;
	
	/**
	 * Constructs a new pair of result and raw values
	 * @param result		result of the computation
	 * @param sampleMSPs	sample values used to derived result
	 */
	public MSPResult( MSPSpan[] result, List<List<Double>> sampleMSPs ) {
		this.result = result;
		this.sampleMSPs = sampleMSPs;
	}
	
	/**
	 * Returns the result set of this pair. 
	 * @return	result set
	 */
	public MSPSpan[] getResults() {
		return result;
	}
	
	/**
	 * Returns the set of MSP values of the samples. 
	 * @return	MSP values of the samples
	 */
	public List<List<Double>> getSampleValues() {
		return sampleMSPs;
	}
}
