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

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import msp.data.DataCube;
import msp.data.ImpossibleCalculationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

/**
 * Test the n (absolute frequency) function.
 * @author Joris Gillis
 */
@RunWith(Parameterized.class)
public class DataCubeTestFrequency {
	
	private DataCube dc;
	private double n1, n2, n3, n4;
	private double f1, f2, f3, f4;
	private double fc1;
	private double o1, o2, o3, o4, o5, o6, o7;
	private double h1, h2, h3, h4;
	private double hc1;
	
	/**
	 * Construct
	 * @param dc	DataCube
	 * @param n1	Solution to the first test of n
	 * @param n2	Solution to the second test of n
	 * @param n3	Solution to the third test of n
	 * @param n4	Solution to the fourth test of n
	 */
	public DataCubeTestFrequency( DataCube dc, 
			double n1, double n2, double n3, double n4,
			double f1, double f2, double f3, double f4, double fc1,
			double o1, double o2, double o3, double o4, double o5, double o6, double o7,
			double h1, double h2, double h3, double h4, double hc1 ) {
		this.dc = dc;
		this.n1 = n1; this.n2 = n2; this.n3 = n3; this.n4 = n4;
		this.f1 = f1; this.f2 = f2; this.f3 = f3; this.f4 = f4;
		this.fc1 = fc1;
		this.o1 = o1; this.o2 = o2; this.o3 = o3; this.o4 = o4; this.o5 = o5;
		this.o6 = o6; this.o7 = o7; 
		this.h1 = h1; this.h2 = h2; this.h3 = h3; this.h4 = h4;
		this.hc1 = hc1;
	}
	
	/**
	 * Test the function n: scenario 1.
	 */
	@Test
	public void nTest1() {
		try {
			assertTrue(n1 == dc.n("2ps", "want", "0;03"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("nTest1", false);
		}
	}
	
	/**
	 * Test the function n: scenario 2.
	 */
	@Test
	public void nTest2() {
		try {
			assertTrue(n2 == dc.n(".", "be", "0;02"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("nTest2", false);
		}
	}
	
	/**
	 * Test the function n: scenario 3.
	 */
	@Test
	public void nTest3() {
		try {
			assertTrue(n3 == dc.n("inf", ".", "0;01"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("nTest3", false);
		}
	}
	
	/**
	 * Test the function n: scenario 4.
	 */
	@Test
	public void nTest4() {
		try {
			assertTrue(n4 == dc.n(".", ".", "0;03"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("nTest5", false);
		}
	}
	
	
	
	/**
	 * Test the function f: scenario 1.
	 */
	@Test
	public void fTest1() {
		try {
			assertTrue(f1 == dc.f("2ps", "want", "0;03"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("fTest1", false);
		}
	}
	
	/**
	 * Test the function f: scenario 2.
	 */
	@Test
	public void fTest2() {
		try {
			assertTrue(f2 == dc.f("*", "be", "0;02"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("fTest2", false);
		}
	}
	
	/**
	 * Test the function f: scenario 3.
	 */
	@Test
	public void fTest3() {
		try {
			assertTrue(f3 == dc.f("inf", "*", "0;01"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("fTest3", false);
		}
	}
	
//	/**
//	 * Test the function f: scenario 4.
//	 */
//	@Test
//	public void fTest4() {
//		try {
//			assertEquals(f4, dc.f("inf", "be", "*"));
//		} catch( ImpossibleCalculationException e ) {
//			System.err.println(e.getMessage());
//			assertTrue("fTest4", false);
//		}
//	}
	
	/**
	 * Test the function f: scenario 5.
	 */
	@Test
	public void fTest4() {
		try {
			assertTrue(f4 == dc.f("*", "*", "0;03"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("fTest5", false);
		}
	}
	
	
	
	/**
	 * Test the function fc in one scenario.
	 */
	@Test
	public void fcTest() {
		try {
			assertTrue(fc1 == dc.fc("inf", "want", "0;03"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("fcTest", false);
		}
	}
	
	
	
	
	
	/**
	 * Test the function o: scenario 1
	 */
	@Test
	public void oTest1() {
		try {
			assertTrue(o1 == dc.o("2ps", "want", "0;03"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("oTest1", false);
		}
	}
	
	/**
	 * Test the function o: scenario 2
	 */
	@Test
	public void oTest2() {
		try {
			assertTrue(o2 == dc.o("*", "be", "0;02"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("oTest2", false);
		}
	}
	
	/**
	 * Test the function o: scenario 3
	 */
	@Test
	public void oTest3() {
		try {
			assertTrue(o3 == dc.o("inf", "*", "0;01"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("oTest3", false);
		}
	}
	
//	/**
//	 * Test the function o: scenario 4
//	 */
//	@Test
//	public void oTest4() {
//		try {
//			assertEquals(o4, dc.o("inf", "be", "*"));
//		} catch( ImpossibleCalculationException e ) {
//			System.err.println(e.getMessage());
//			assertTrue("oTest4", false);
//		}
//	}
	
	/**
	 * Test the function o: scenario 5
	 */
	@Test
	public void oTest4() {
		try {
			assertTrue(o4 == dc.o("*", "*", "0;03"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("oTest5", false);
		}
	}
	
	/**
	 * Test the function o: scenario 6
	 */
	@Test
	public void oTest5() {
		try {
			assertTrue(o5 == dc.o(".", ".", "0;03"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("oTest6", false);
		}
	}
	
	/**
	 * Test the function o: scenario 7
	 */
	@Test
	public void oTest6() {
		try {
			assertTrue(o6 == dc.o("*", ".", "0;02"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("oTest7", false);
		}
	}
	
	/**
	 * Test the function o: scenario 8
	 */
	@Test
	public void oTest7() {
		try {
			assertTrue(o7 == dc.o(".", "*", "0;02"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("oTest8", false);
		}
	}
	
//	/**
//	 * Test the function o: scenario 9
//	 */
//	@Test
//	public void oTest9() {
//		try {
//			assertEquals(o9, dc.o(".", ".", "*"));
//		} catch( ImpossibleCalculationException e ) {
//			System.err.println(e.getMessage());
//			assertTrue("oTest9", false);
//		}
//	}
	
	
	
	/**
	 * Test the function h: scenario 1
	 */
	@Test
	public void hTest1() {
		try {
//			System.out.println("1 "+dc.h("1ps", "have", "0;03"));
			assertTrue(h1 == dc.h("1ps", "have", "0;03"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("hTest1", false);
		}
	}
	
	/**
	 * Test the function h: scenario 2
	 */
	@Test
	public void hTest2() {
		try {
//			System.out.println("2 "+ dc.hc("1ps", "have", "0;03"));
			assertTrue(h2 == dc.hc("1ps", "have", "0;03"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("hTest2", false);
		}
	}
	
	/**
	 * Test the function h: scenario 3
	 */
	@Test
	public void hTest3() {
		try {
//			System.out.println("3 "+dc.h(".", ".", "0;02"));
			assertTrue(h3 == dc.h(".", ".", "0;02"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("hTest3", false);
		}
	}
	
	/**
	 * Test the function h: scenario 4
	 */
	@Test
	public void hTest4() {
		try {
//			System.out.println("4 "+dc.hc(".","want","0;03"));
			assertTrue(h4 == dc.hc(".", "want", "0;03"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("hTest4", false);
		}
	}
	
//	/**
//	 * Test the function h: scenario 5
//	 */
//	@Test
//	public void hTest5() {
//		try {
////			System.out.println("5 "+dc.h(".",".","*"));
//			assertEquals(h5, dc.h(".", ".", "*"));
//		} catch( ImpossibleCalculationException e ) {
//			System.err.println(e.getMessage());
//			assertTrue("hTest5", false);
//		}
//	}
	
	
	/**
	 * Test the function hc: scenario 1
	 */
	@Test
	public void hcTest1() {
		try {
			assertTrue(hc1 == dc.hc(".", "be", "0;03"));
		} catch( ImpossibleCalculationException e ) {
			System.err.println(e.getMessage());
			assertTrue("hcTest1", false);
		}
	}
	
	
	
	/**
	 * Set up the testing environment
	 * @return	collection of environments
	 */
	@Parameters
	public static Collection<Object[]> setUp() {
		Vector<Object[]> c = new Vector<Object[]>();
		
		// 1. 
		DataCube dC1 = new DataCube();
		
		Vector<String> time = new Vector<String>();
		Vector<String> lemmas = new Vector<String>();
		Vector<String> categories = new Vector<String>();
		
		time.add("0;01");
		time.add("0;02");
		time.add("0;03");
		
		lemmas.add("have");
		lemmas.add("be");
		lemmas.add("want");
		
		categories.add("1ps");
		categories.add("2ps");
		categories.add("inf");
		
		dC1.setTime(time);
		dC1.setLemmas(lemmas);
		dC1.setCategories(categories);
		
		HashMap<String, HashMap<String, HashMap<String, Integer>>> cubicle =
			new HashMap<String, HashMap<String,HashMap<String,Integer>>>();
		
		cubicle.put("0;01", new HashMap<String, HashMap<String,Integer>>());
		cubicle.put("0;02", new HashMap<String, HashMap<String,Integer>>());
		cubicle.put("0;03", new HashMap<String, HashMap<String,Integer>>());
		
		cubicle.get("0;01").put("have", new HashMap<String, Integer>());
		cubicle.get("0;01").put("be", new HashMap<String, Integer>());
		cubicle.get("0;02").put("have", new HashMap<String, Integer>());
		cubicle.get("0;02").put("be", new HashMap<String, Integer>());
		cubicle.get("0;03").put("have", new HashMap<String, Integer>());
		cubicle.get("0;03").put("be", new HashMap<String, Integer>());
		cubicle.get("0;03").put("want", new HashMap<String, Integer>());
		
		cubicle.get("0;01").get("have").put("inf", 3);
		cubicle.get("0;01").get("be").put("inf", 2);
		cubicle.get("0;02").get("have").put("1ps", 1);
		cubicle.get("0;02").get("be").put("1ps", 4);
		cubicle.get("0;02").get("have").put("inf", 3);
		cubicle.get("0;02").get("be").put("inf", 2);
		cubicle.get("0;03").get("have").put("1ps", 3);
		cubicle.get("0;03").get("be").put("1ps", 1);
		cubicle.get("0;03").get("want").put("1ps", 1);
		cubicle.get("0;03").get("have").put("2ps", 2);
		cubicle.get("0;03").get("be").put("2ps", 5);
		cubicle.get("0;03").get("want").put("2ps", 0);
		cubicle.get("0;03").get("have").put("inf", 4);
		cubicle.get("0;03").get("be").put("inf", 5);
		cubicle.get("0;03").get("want").put("inf", 3);
		
		dC1.setCube(cubicle);
		
		double n1 = 0; 	// (2ps, want, 0;03)
		double n2 = 6; 	// (., be, 0;02)
		double n3 = 5; 	// (inf, ., 0;01)
		double n4 = 24; // (., ., 0;03)
		
		double f1 = 0.0; 		// (2ps, want, 0;03)
		double f2 = 0.6; 		// (*, be, 0;02)
		double f3 = 1.0; 		// (inf, *, 0;01)
		double f4 = 1.0; 		// (*, *, 0;03)
		
		double fc1 = 0.75;	// (inf, want, 0;03)
		
		double o1 = 0.0; 	// (2ps, want, 0;03)
		double o2 = 1.0; 	// (*, be, 0;02)
		double o3 = 1.0; 	// (inf, *, 0;01)
		double o4 = 1.0; 	// (*, *, 0;03)
		double o5 = 8.0;	// (., ., 0;03)
		double o6 = 2.0;	// (*, ., 0;02)
		double o7 = 2.0;	// (., *, 0;02)
		
		double log2 = Math.log10(2.0);
		double h1 = -(3.0/24.0)*Math.log10(3.0/24.0)/log2;	// (1ps, have, 0;03)
		double h2 = -(3.0/9.0)*Math.log10(3.0/9.0)/log2;	// (1ps, have(c), 0;03)
		double h3 = -((1.0/10.0)*Math.log10(1.0/10.0)/log2 + (3.0/10.0)*Math.log10(3.0/10.0)/log2 +
				(4.0/10.0)*Math.log10(4.0/10.0)/log2 + (2.0/10.0)*Math.log10(2.0/10.0)/log2);	// (., ., 0;02)
		double h4 = -((1.0/4.0)*Math.log10(1.0/4.0)/log2 + (3.0/4.0)*Math.log10(3.0/4.0)/log2);	// (., want(c), 0;03)
//		double h5 = -((4.0/39.0)*Math.log10(4.0/39.0)/log2 + (2.0/39.0)*Math.log10(2.0/39.0)/log2 + (10.0/39.0)*Math.log10(10.0/39.0)/log2 +
//				(5.0/39)*Math.log10(5.0/39.0)/log2 + (5.0/39.0)*Math.log10(5.0/39.0) + (9.0/39.0)*Math.log10(9.0/39.0)/log2 +
//				(1.0/39.0)*Math.log10(1.0/39.0)/log2 + (3.0/39.0)*Math.log10(3.0/39.0)/log2);	// (., ., *)
		
		double hc1 = -( (1.0/11.0) * Math.log10(1.0/11.0)/log2 + 2 * (5.0/11.0) * Math.log10(5.0/11.0)/log2 );
		
		c.add(new Object[]{dC1, n1, n2, n3, n4, f1, f2, f3, f4, fc1, 
				o1, o2, o3, o4, o5, o6, o7, h1, h2, h3, h4, hc1});
		
		return c;
	}
	
}
