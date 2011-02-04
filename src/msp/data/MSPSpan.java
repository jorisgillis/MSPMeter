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
 * Couple of MSP and a month in which the MSP has been calculated.
 * @author Joris Gillis
 */
public class MSPSpan {
	
	private double msp = 0.0;
	private double stddev = -1.0;
	private String span;
	
	/**
	 * Construct a new (MSP, month) tuple.
	 * @param msp	msp value
	 * @param span	span
	 */
	public MSPSpan( double msp, String span ) {
		this.msp = msp;
		this.span = span;
	}
	
	/**
	 * Construct a new (MSP, month, stddev) tuple.
	 * @param msp		msp value
	 * @param stddev	standard deviation
	 * @param span		span
	 */
	public MSPSpan( double msp, double stddev, String span ) {
		this.msp = msp;
		this.stddev = stddev;
		this.span = span;
	}
	
	/**
	 * Returns MSP
	 * @return	msp
	 */
	public double getMSP() {
		return msp;
	}
	
	/**
	 * Returns the standard deviation
	 * @return	standard deviation
	 */
	public double getStdDev() {
		return stddev;
	}
	
	/**
	 * Returns the month
	 * @return	month
	 */
	public String getSpan() {
		return span;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "("+ span +"; "+ msp +"; "+ stddev +")";
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals( Object o ) {
		if( o instanceof MSPSpan ) {
			MSPSpan m = (MSPSpan)o;
			return span.equals(m.span) && Math.abs(msp - m.msp) < 0.0001 && Math.abs(stddev - m.stddev) < 0.0001;
		}
		return false;
	}
}
