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


package test.msp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import msp.data.DataCubeHash;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test the DataCube class
 * @author Joris Gillis
 */
@RunWith(Parameterized.class)
public class DataCubeHashTest {
	
	private DataCubeHash dataCube;
	private Vector<Double> results1;
	private Vector<Double> results2;
	private Vector<Double> results3;
	private Vector<Double> results4;
	private Vector<String> time;
	private int numTokensRC;
	private int numTokensCR;
	
	/**
	 * Constructing
	 * @param cube			the cube
	 * @param time			time
	 * @param lemmas		lemmas
	 * @param categories	categories
	 * @param results1		results for mspVarietyUnweighted
	 * @param results2		results for mspVarietyWeighted
	 * @param results3		results for mspEntropyUnweighted
	 * @param results4		results for mspEntropyWeighted
	 */
	public DataCubeHashTest( 
			HashMap<String, HashMap<String, HashMap<String, Integer>>> cube,
			Vector<String> time, 
			Vector<String> lemmas, 
			Vector<String> categories, 
			Vector<Double> results1, 
			Vector<Double> results2, 
			Vector<Double> results3, 
			Vector<Double> results4,
			int numTokensRC, 
			int numTokensCR ) {
		this.time = time;
		this.results1 = results1;
		this.results2 = results2;
		this.results3 = results3;
		this.results4 = results4;
		
		this.numTokensRC = numTokensRC;
		this.numTokensCR = numTokensCR;
		
		dataCube = new DataCubeHash();
		dataCube.setCategories(categories);
		dataCube.setCube(cube);
		dataCube.setLemmas(lemmas);
		dataCube.setTime(time);
	}
	
	/**
	 * Test mspVarietyUnweighted. 
	 */
	@Test
	public void mspVarietyUnweightedTest() {
		try {
			for( int i = 0; i < results1.size(); i++ )
				assertTrue(results1.get(i).doubleValue() == dataCube.mspVarietyUnweighted(time.get(i)) );
		} catch( Exception e ) {
			System.err.println(e.getMessage());
			assertTrue("mspVarietyUnweighted", false);
		}
	}
	
	/**
	 * Test mspVarietyWeighted
	 */
	@Test
	public void mspVarietyWeightedTest() {
		try {
			for( int i = 0; i < results2.size(); i++ )
				assertTrue(results2.get(i).doubleValue() == dataCube.mspVarietyWeighted(time.get(i)));
		} catch( Exception e ) {
			System.err.println(e.getMessage());
			assertTrue("mspVarietyWeighted", false);
		}
	}
	
	/**
	 * Test mspEntropyUnweighted
	 */
	@Test
	public void mspEntropyUnweightedTest() {
		try {
			for( int i = 0; i < results3.size(); i++ ) {
				double expected = results3.get(i).doubleValue();
				double observed = dataCube.mspEntropyUnweighted(time.get(i));
//				System.out.println("Unweighted: ");
//				System.out.println((expected-0.0000001) +" <= "+ observed +" <= "+ (expected + 0.0000001));
//				System.out.println( expected - 0.0000001 <= observed && observed <= expected + 0.0000001 );
//				System.out.println();
				assertTrue( expected - 0.0000001 <= observed && observed <= expected + 0.0000001 );
			}
		} catch( Exception e ) {
			System.err.println(e.getMessage());
			assertTrue("mspEntropyUnweighted", false);
		}
	}
	
	/**
	 * Test mspEntropyWeighted
	 */
	@Test
	public void mspEntropyWeightedTest() {
		try {
			for( int i = 0; i < results4.size(); i++ ) {
				double expected = results4.get(i).doubleValue();
				double observed = dataCube.mspEntropyWeighted(time.get(i));
//				System.out.println("Weighted: ");
//				System.out.println((expected-0.0000001) +" <= "+ observed +" <= "+ (expected + 0.0000001));
//				System.out.println( expected - 0.0000001 <= observed && observed <= expected + 0.0000001 );
//				System.out.println();
				assertTrue(expected - 0.0000001 <= observed && observed <= expected + 0.0000001 );
			}
		} catch( Exception e ) {
			System.err.println(e.getMessage());
			assertTrue("mspEntropyWeighted", false);
		}
	}
	
	
	/**
	 * Test maxSampleSizeResamokeCumulate
	 */
	@Test
	public void maxSampleSizeResampleCumulate() {
		assertEquals( numTokensRC, dataCube.maxSampleSizeAllSpan() );
	}
	
	/**
	 * Test maxSampleSizeCumulateResample
	 */
	@Test
	public void maxSampleSizeCumulateResample() {
		assertEquals( numTokensCR, dataCube.maxSampleSizeOneSpan() );
	}
	
	
	/**
	 * Setting the tests up
	 */
	@Parameters
	public static Collection<Object[]> setUp() {
		Collection<Object[]> c = new Vector<Object[]>();
		
		//- 1.
		// setting up axes
		Vector<String> c1 = new Vector<String>();
		c1.add("inf");
		c1.add("1ps");
		c1.add("1pp");
		
		Vector<String> l1 = new Vector<String>();
		l1.add("have");
		l1.add("be");
		
		Vector<String> t1 = new Vector<String>();
		t1.add("0;01");
		
		// setting up the cube
		HashMap<String, HashMap<String, HashMap<String, Integer>>> cubicle1 = new HashMap<String, HashMap<String,HashMap<String,Integer>>>();
		cubicle1.put("0;01", new HashMap<String, HashMap<String,Integer>>());
		
		cubicle1.get("0;01").put("have", new HashMap<String, Integer>());
		cubicle1.get("0;01").put("be", new HashMap<String, Integer>());
		
		cubicle1.get("0;01").get("have").put("inf", 2);
		cubicle1.get("0;01").get("be").put("1pp", 2);
		cubicle1.get("0;01").get("be").put("1ps", 1);
		
		// the response to the different tests
		Vector<Double> results11 = new Vector<Double>();
		Vector<Double> results12 = new Vector<Double>();
		Vector<Double> results13 = new Vector<Double>();
		Vector<Double> results14 = new Vector<Double>();
		
		results11.add(new Double(1.5));
		results12.add(new Double(1.6));
		results13.add(new Double(1.444940787));
		results14.add(new Double(1.533928945));
		
		int numTokensRC1 = 4;
		int numTokensCR1 = 4;
		
		// adding the constructs to the test
		c.add(new Object[]{cubicle1, t1, l1, c1, results11, results12, results13, results14, numTokensRC1, numTokensCR1});
		
		
		//- 2.
		Vector<String> c2 = new Vector<String>();
		c2.add("inf");
		c2.add("1ps");
		c2.add("2ps");
		
		Vector<String> l2 = new Vector<String>();
		l2.add("have");
		l2.add("be");
		l2.add("want");
		
		Vector<String> t2 = new Vector<String>();
		t2.add("0;01");
		t2.add("0;02");
		t2.add("0;03");
		
		
		// setting up the cube
		HashMap<String, HashMap<String, HashMap<String, Integer>>> cubicle2 = new HashMap<String, HashMap<String,HashMap<String,Integer>>>();
		cubicle2.put("0;01", new HashMap<String, HashMap<String,Integer>>());
		
		cubicle2.get("0;01").put("have", new HashMap<String, Integer>());
		cubicle2.get("0;01").put("be", new HashMap<String, Integer>());
		
		cubicle2.get("0;01").get("have").put("inf", 3);
		cubicle2.get("0;01").get("be").put("inf", 2);
		
		
		cubicle2.put("0;02", new HashMap<String, HashMap<String,Integer>>());
		
		cubicle2.get("0;02").put("have", new HashMap<String, Integer>());
		cubicle2.get("0;02").put("be", new HashMap<String, Integer>());
		
		cubicle2.get("0;02").get("have").put("1ps", 1);
		cubicle2.get("0;02").get("have").put("inf", 3);
		cubicle2.get("0;02").get("be").put("1ps", 4);
		cubicle2.get("0;02").get("be").put("inf", 2);
		
		
		cubicle2.put("0;03", new HashMap<String, HashMap<String,Integer>>());
		
		cubicle2.get("0;03").put("have", new HashMap<String, Integer>());
		cubicle2.get("0;03").put("be", new HashMap<String, Integer>());
		cubicle2.get("0;03").put("want", new HashMap<String, Integer>());
		
		cubicle2.get("0;03").get("have").put("1ps", 3);
		cubicle2.get("0;03").get("have").put("2ps", 2);
		cubicle2.get("0;03").get("have").put("inf", 4);
		cubicle2.get("0;03").get("be").put("1ps", 1);
		cubicle2.get("0;03").get("be").put("2ps", 5);
		cubicle2.get("0;03").get("be").put("inf", 5);
		cubicle2.get("0;03").get("want").put("1ps", 1);
		cubicle2.get("0;03").get("want").put("2ps", 0);
		cubicle2.get("0;03").get("want").put("inf", 3);
		
		// msps
		Vector<Double> results21 = new Vector<Double>();
		Vector<Double> results22 = new Vector<Double>();
		Vector<Double> results23 = new Vector<Double>();
		Vector<Double> results24 = new Vector<Double>();
		
		results21.add(new Double(1));
		results21.add(new Double(2));
		results21.add(new Double(8.0/3.0));
		
		results22.add(new Double(1));
		results22.add(new Double(2));
		results22.add(new Double(3.0*9.0/24.0 + 3.0*11.0/24.0 + 2.0*4.0/24.0));
		
		results23.add(new Double(1));
		results23.add(new Double((Math.pow(2, -(1.0/4) * log2(1.0/4) - (3.0/4) * log2(3.0/4)) +
									Math.pow(2, -(2.0/3) * log2(2.0/3) - (1.0/3) * log2(1.0/3))) / 2));
		results23.add(new Double((Math.pow(2, -((1.0/3)*log2(1.0/3) + (2.0/9)*log2(2.0/9) + (4.0/9)*log2(4.0/9))) +
									Math.pow(2, -((1.0/11)*log2(1.0/11) + 2*(5.0/11)*log2(5.0/11))) +
									Math.pow(2, -((1.0/4)*log2(1.0/4) + (3.0/4)*log2(3.0/4))))/3.0));
		
		results24.add(new Double(1));
		results24.add(new Double((4.0/10)*Math.pow(2, -((1.0/4)*log2(1.0/4) + (3.0/4)*log2(3.0/4)))
									+ (6.0/10)*Math.pow(2, -((2.0/3)*log2(2.0/3) + (1.0/3)*log2(1.0/3))) ));
		results24.add(new Double((9.0/24)*Math.pow(2, -((1.0/3)*log2(1.0/3) + (2.0/9)*log2(2.0/9) + (4.0/9)*log2(4.0/9)))
									+ (11.0/24)*Math.pow(2, -((1.0/11)*log2(1.0/11) + 2*(5.0/11)*log2(5.0/11)))
									+ (4.0/24)*Math.pow(2, -((1.0/4)*log2(1.0/4) + (3.0/4)*log2(3.0/4)))));
		
		// number of tokens
		int numTokensRC2 = 38;
		int numTokensCR2 = 4;
		
		// adding the constructs to the testcase
		c.add(new Object[]{cubicle2, t2, l2, c2, results21, results22, results23, results24, numTokensRC2, numTokensCR2});
		
		return c;
	}
	
	
	private static double log2( double d ) {
		return Math.log(d) / Math.log(2);
	}
}
