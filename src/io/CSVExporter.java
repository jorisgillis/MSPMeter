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


package io;

import javax.swing.JFrame;


/**
 * Class which provides the MSPMeter with CSV export capabilities. This class accepts several different
 * data structures and converts them to a CSV stream (String).
 * @author Joris Gillis
 */
public class CSVExporter extends DelimitedExporter {
	
	/**
	 * Constructing a new CSV exporter
	 */
	public CSVExporter( JFrame parent ) {
		super(parent, ",");
	}
}
