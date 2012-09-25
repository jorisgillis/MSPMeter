package msp.data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A span and the index of lemmas in the span.
 *
 * @author joris
 */
public class SpanIndex {

	/**
	 * The span.
	 */
	protected String span;
	/**
	 * The lemmas in this span.
	 */
	protected List<LemmaIndex> lemmas;

	/**
	 * Constructs a new SpanIndex for the given span, lemma indices will be
	 * added later.
	 *
	 * @param span	Span
	 */
	public SpanIndex(String span) {
		this.span = span;
		lemmas = new LinkedList<LemmaIndex>();
	}

	/**
	 * Constructs a new SpanIndex for the given span, with the given lemma
	 * indices (deep copy)
	 *
	 * @param span	  span
	 * @param lemmas	lemma indices
	 */
	public SpanIndex(String span, List<LemmaIndex> lemmas) {
		// Making room
		this.span = span;
		this.lemmas = new LinkedList<LemmaIndex>();

		// Copying the lemma indices
		for (LemmaIndex li : lemmas)
			this.lemmas.add(new LemmaIndex(li.getLemma(), li.getCategories()));
	}

	/**
	 * Adds a lemma index to the span index.
	 *
	 * @param lemma	new lemma index
	 */
	public void addLemmaIndex(LemmaIndex lemma) {
		this.lemmas.add(lemma);
	}

	/**
	 * Removes the lemmaindex at the given position.
	 * @param pos	position to remove
	 */
	public void removeLemmaIndex(int pos) {
		this.lemmas.remove(pos);
	}

	/**
	 * Renames the from lemma to the to lemma.
	 * @param from	renaming from
	 * @param to	  to this
	 */
	public void rename(String from, String to) throws DataFaultException {
		if (!containsLemma(to))
			if (containsLemma(from))
				try {
					int fromPos = getLemmaPosition(from);
					LemmaIndex li = lemmas.get(fromPos);
					lemmas.remove(fromPos);
					lemmas.add(fromPos, new LemmaIndex(to, li.getCategories()));
				} catch (NoSuchLemmaException e) {
					// Always present.
				}
			else
				throw new DataFaultException(
					"Renaming from " + from + ", but not present in span index!");
		else
			throw new DataFaultException(
				"Renaming " + from + " to " + to + ", but " + to + " already exists in the span index!");
	}

	/**
	 * Get the span.
	 * @return span
	 */
	public String getSpan() {
		return span;
	}

	/**
	 * Get the list of lemma indices in this span.
	 *
	 * @return list of lemma indices
	 */
	public List<LemmaIndex> getLemmas() {
		return lemmas;
	}

	/**
	 * Returns the lemma at the given position.
	 *
	 * @param lemmaPos	position
	 * @return	lemma at this position
	 */
	public LemmaIndex getLemma(int lemmaPos) {
		return lemmas.get(lemmaPos);
	}

	/**
	 * Checks whether the given lemma occurs in this span.
	 *
	 * @param lemma	lemma to search for
	 * @return	found?
	 */
	public boolean containsLemma(String lemma) {
		for (LemmaIndex li : lemmas)
			if (li.getLemma().equals(lemma))
				return true;
		return false;
	}

	/**
	 * Returns the position at which the given lemma is located within this
	 * span.
	 *
	 * @param lemma	lemma to search for
	 * @return	position of the lemma: -1 if not found
	 */
	public int getLemmaPosition(String lemma) throws NoSuchLemmaException {
		// Searching for the position of lemma
		int pos = -1;
		Iterator<LemmaIndex> it = lemmas.iterator();
		int count = 0;
		while (it.hasNext()) {
			if (it.next().getLemma().equals(lemma)) {
				pos = count;
				break;
			}
			count++;
		}

		// If lemma not found: throw exception
		if (pos == -1)
			throw new NoSuchLemmaException();

		return pos;
	}
}
