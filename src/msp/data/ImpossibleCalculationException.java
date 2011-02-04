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
 * Exception which indicates the impossibility of calculating some value.
 * For example: o_{.l}(t)
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class ImpossibleCalculationException extends Exception {
	
	/**
	 * Create a new exception telling the system that the requested calculation with the
	 * specific arguments is impossible.
	 * @param arguments	arguments causing for the impossibility
	 */
	public ImpossibleCalculationException( String arguments ) {
		super("Impossible calculation! Arguments: "+ arguments);
	}
	
}
