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
 * Exception thrown whenever no data is available in the DataCube. This is possible when
 * a resampling took place.
 * @author Joris Gillis
 */
public class NoDataException extends Exception {
	
	private static final long serialVersionUID = -1552064372119130293L;
	
	/**
	 * Default constructor
	 */
	public NoDataException() {
		super();
	}
	
	/**
	 * Exception with a message
	 * @param msg	a message
	 */
	public NoDataException( String msg ) {
		super(msg);
	}
}
