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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * An index to a cube: a list of span indices.
 * </p>
 * @author joris
 */
public class CubeIndex implements Iterable<SpanIndex> {
	
	/**
	 * The index.
	 */
	protected List<SpanIndex> index;
	
	/**
	 * Constructing a new index.
	 */
	public CubeIndex() {
		index = new ArrayList<SpanIndex>();
	}
	
	/**
	 * Copy constructor.
	 * @param ci	cube index
	 */
	public CubeIndex(CubeIndex ci) {
		index = new ArrayList<SpanIndex>(ci.index);
	}
	
	/**
	 * Add a span index to the cube index.
	 * @param si	span index
	 */
	public void add(SpanIndex si) {
		index.add(si);
	}
	
	/**
	 * Returns the span index at the given position.
	 * @param i	position
	 * @return	span index at the given position
	 */
	public SpanIndex get(int i) {
		return index.get(i);
	}
	
	/**
	 * Returns the size of the index.
	 * @return	size of the index
	 */
	public int size() {
		return index.size();
	}
	
	/**
	 * Returns an iterator on the index.
	 * @return	iterator on the index
	 */
	public Iterator<SpanIndex> iterator() {
		return index.iterator();
	}
	
	/**
	 * Checks whether this cube has a certain span.
	 * 
	 * @param span span to search for
	 * @return found?
	 */
	public boolean containsSpan(String span) {
		for (SpanIndex si : index)
			if (si.getSpan().equals(span))
				return true;
		return false;
	}
	
	/**
	 * Returns the position of the given span
	 * 
	 * @param span span
	 * @return position of the span
	 * @throws NoSuchSpanException if the span is not in the cube
	 */
	public int getSpanPosition(String span) throws NoSuchSpanException {
		int pos = -1;
		
		// Iterate through list of span indices
		Iterator<SpanIndex> it = index.iterator();
		int count = 0;
		while (it.hasNext()) {
			if (it.next().getSpan().equals(span)) {
				pos = count;
				break;
			}
			count++;
		}
		
		// If the span is not found: throw exception
		if (pos == -1)
			throw new NoSuchSpanException();
		
		return pos;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof CubeIndex) {
			// Convert
			CubeIndex ci = (CubeIndex)o;
			
			// Compare
			boolean equal = index.size() == ci.index.size();
			for (int i = 0; equal && i < index.size(); i++)
				equal = index.get(i).equals(ci.index.get(i));
			return equal;
		} else
			return false;
	}
}
