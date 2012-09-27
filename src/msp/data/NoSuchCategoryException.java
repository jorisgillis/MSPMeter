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
 * Exception thrown if asked for a non-existing category.
 * @author joris
 */
public class NoSuchCategoryException extends Exception {
	/**
	 * Default constructor. 
	 */
	public NoSuchCategoryException() {
		super();
	}
	
	/**
	 * Constructor with a message.
	 * @param message message
	 */
	public NoSuchCategoryException(String message) {
		super(message);
	}
}
