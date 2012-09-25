package msp.data;

/**
 * Class which represent a lemma and a category (a word-form in a certain slice), 
 * together with its frequency count. 
 * @author Joris Gillis
 */
public class SpanKey {
	
	/** Lemma */
	private String lemma;
	/** Category */
	private String category;
	/** frequency of the word-form at the given time */
	private int frequency;
	
	
	/**
	 * Construct a new slicekey.
	 * @param lemma		lemma
	 * @param category	category
	 * @param frequency	frequency
	 */
	public SpanKey( String lemma, String category, int frequency ) {
		this.lemma = lemma;
		this.category = category;
		this.frequency = frequency;
	}
	
	
	/**
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}
	
	
	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
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
