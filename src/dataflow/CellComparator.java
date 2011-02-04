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

package dataflow;

import java.util.Comparator;

/**
 * Compares two cells and determines in which order they have to be processed.
 * @author Joris Gillis
 */
public class CellComparator implements Comparator<String> {
	// The grid
	private Grid grid = Grid.instance();
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(String c1, String c2) {
		if( grid.ancestor(c1, c2) )
			return -1;
		else if( grid.ancestor(c2, c1) )
			return 1;
		else
			return c1.compareTo(c2);
	}
	
	// TODO check ordering in queue!!

}
