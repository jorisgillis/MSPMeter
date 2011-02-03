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
