package msp.data;

/**
 * Class which represent a month, a lemma and a category (a word-form in time), together with its
 * frequency count. 
 * @author Joris Gillis
 */
public class DataCubeKey {
	
	/** Month */
	private String month;
	/** Lemma */
	private String lemma;
	/** Category */
	private String category;
	/** frequency of the word-form at the given time */
	private int frequency;
	
	
	/**
	 * Create a new key
	 * @param month
	 * @param lemma
	 * @param category
	 * @param frequency
	 */
	public DataCubeKey( String month, String lemma, String category, int frequency ) {
		this.month = month;
		this.lemma = lemma;
		this.category = category;
		this.frequency = frequency;
	}
	
	
	/**
	 * @return the month
	 */
	public String getMonth() {
		return month;
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


	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return month +" ("+ lemma +", "+ category +") = "+ frequency;
	}
}
