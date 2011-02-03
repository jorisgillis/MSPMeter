package msp.defaults;

import java.util.HashMap;

/**
 * The set of default values, used to initialize the system.
 * @author Joris Gillis
 */
public class Defaults {
	
	/** Singleton */
	private static Defaults instance = null;
	
	/** Default values are stored in an HashMap */
	HashMap<String, Object> values;
	
	/**
	 * Private constructor: singleton pattern.
	 */
	private Defaults() {
		//- the hashmap
		values = new HashMap<String, Object>();
		
		//- the values
		// version
		values.put("subSampleSize", new Integer(1));
		values.put("subSampleMode", new Integer(0));
		values.put("mode", new Integer(0));
		values.put("weighting", new Boolean(false));
		values.put("entropy", new Boolean(false));
		values.put("numberOfSamplesMode", new Integer(0));
		values.put("numberOfSamples", new Double(1));
		
		// line parsing
		values.put("firstSeparator", "|");
		values.put("secondSeparator", "&~-");
		
		// output
		values.put("lemmasmissing", "missing_lemmas.txt");
		
		// TODO delete this one on release
		values.put("working directory", "/Users/joris/unief/Onderzoek/Projecten/Linguistic/MSP/Data");
	}
	
	/**
	 * Get the singleton out.
	 * @return	singleton
	 */
	public static Defaults instance() {
		if( instance == null )
			instance = new Defaults();
		return instance;
	}
	
	
	/**
	 * Get the value for the variable
	 * @param name	name of the variable
	 * @return		boolean-value for the variable
	 */
	public boolean getBoolean( String name ) throws NoValueException {
		if( values.get(name) instanceof Boolean )
			return ((Boolean)values.get(name)).booleanValue();
		else
			throw new NoValueException();
	}
	
	/**
	 * Get the value for the variable
	 * @param name	name of the variable
	 * @return		integer-value for the variable
	 * @throws NoValueException
	 */
	public int getInteger( String name ) throws NoValueException {
		if( values.get(name) instanceof Integer )
			return ((Integer)values.get(name)).intValue();
		else
			throw new NoValueException();
	}
	
	/**
	 * Get the value for the variable
	 * @param name	name of the variable
	 * @return		double-value for the variable
	 * @throws NoValueException
	 */
	public double getDouble( String name ) throws NoValueException {
		if( values.get(name) instanceof Double )
			return ((Double)values.get(name)).doubleValue();
		else
			throw new NoValueException();
	}
	
	/**
	 * Get the value for the variable
	 * @param name	name of the variable
	 * @return		string-value for the variable
	 * @throws NoValueException
	 */
	public String getString( String name ) throws NoValueException {
		if( values.get(name) instanceof String )
			return (String)values.get(name);
		else
			throw new NoValueException();
	}
	
	
	
}
