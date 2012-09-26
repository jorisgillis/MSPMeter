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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import msp.data.DataCubeHash;
import msp.data.ImpossibleCalculationException;
import msp.data.MSPTriple;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test whether lemmaEquivalences are handled well.
 * @author Joris Gillis
 */
@RunWith(Parameterized.class)
public class LemmaEquivalencesTest {
	
	private DataCubeHash dc;
	private DataCubeHash result;
	private HashMap<String, String> lemmaEquivalences;
	
	
	/**
	 * Construct a new testcase.
	 * @param dc		start
	 * @param result	end
	 * @param lemmaEquivalences	the equivalences
	 */
	public LemmaEquivalencesTest( DataCubeHash dc, DataCubeHash result, HashMap<String, String> lemmaEquivalences ) {
		this.dc = dc;
		this.result = result;
		this.lemmaEquivalences = lemmaEquivalences;
	}
	
	/**
	 * Test whether the lemmaEquivalences on dc have the desired result.
	 */
	@Test
	public void test1() {
		assertEquals(result, dc.lemmaEquivalences(lemmaEquivalences));
	}
	
	/** 
	 * Test whether the original DataCube stays invariant.
	 */
	@Test
	public void invariance1() {
		try {
			DataCubeHash clone = dc.copy();
			dc.lemmaEquivalences(lemmaEquivalences);
			assertTrue(clone.equals(dc));
		} catch( Exception e ) {
			System.out.println(e.getMessage());
			assertTrue(false);
		}
	}
	
	/**
	 * Test again whether the original DataCube stays invariant.
	 */
	@Test
	public void invariance2() {
		try {
			MSPTriple[] cumulatedOriginal = dc.MSP(true, true).getResults();
			dc.lemmaEquivalences(lemmaEquivalences);
			MSPTriple[] cumulatedAfter = dc.MSP(true, true).getResults();
			
			for( int i = 0; i < cumulatedOriginal.length; i++ )
				assertEquals(cumulatedOriginal[i], cumulatedAfter[i]);
		} catch( ImpossibleCalculationException e ) {
			System.out.println(e.getMessage());
			assertTrue(false);
		}
	}
	
	
	/**
	 * Setting up the scenes
	 * @return	collection of testcases
	 */
	@Parameters
	public static Collection<Object[]> setUp() {
		ArrayList<Object[]> c = new ArrayList<Object[]>();
		
		//- 1. possibly missing
		// axes
		Vector<String> time = new Vector<String>();
		Vector<String> lemmas = new Vector<String>();
		Vector<String> resultLemmas = new Vector<String>();
		Vector<String> categories = new Vector<String>();
		
		time.add("0;01");
		time.add("0;02");
		
		lemmas.add("doen");
		lemmas.add("aan#doen");
		lemmas.add("hebben");
		lemmas.add("bij#hebben");
		
		resultLemmas.add("doen");
		resultLemmas.add("hebben");
		
		categories.add("1ps");
		categories.add("2ps");
		categories.add("1pp");
		categories.add("inf");
		
		// cube
		HashMap<String, HashMap<String, HashMap<String, Integer>>> cube = new HashMap<String, HashMap<String,HashMap<String,Integer>>>();
		cube.put("0;01", new HashMap<String, HashMap<String,Integer>>());
		cube.put("0;02", new HashMap<String, HashMap<String,Integer>>());
		
		cube.get("0;01").put("doen", new HashMap<String, Integer>());
		cube.get("0;01").put("aan#doen", new HashMap<String, Integer>());
		cube.get("0;01").put("hebben", new HashMap<String, Integer>());
		cube.get("0;02").put("doen", new HashMap<String, Integer>());
		cube.get("0;02").put("bij#hebben", new HashMap<String, Integer>());
		
		cube.get("0;01").get("doen").put("1ps", new Integer(2));
		cube.get("0;01").get("doen").put("inf", new Integer(6));
		cube.get("0;01").get("aan#doen").put("1ps", new Integer(1));
		cube.get("0;01").get("aan#doen").put("1pp", new Integer(5));
		cube.get("0;01").get("hebben").put("inf", new Integer(9));
		
		cube.get("0;02").get("doen").put("1ps", new Integer(2));
		cube.get("0;02").get("doen").put("inf", new Integer(6));
		cube.get("0;02").get("bij#hebben").put("1pp", new Integer(1));
		cube.get("0;02").get("bij#hebben").put("inf", new Integer(1));
		
		// resultCube
		HashMap<String, HashMap<String, HashMap<String, Integer>>> resultCube = new HashMap<String, HashMap<String,HashMap<String,Integer>>>();
		resultCube.put("0;01", new HashMap<String, HashMap<String,Integer>>());
		resultCube.put("0;02", new HashMap<String, HashMap<String,Integer>>());
		
		resultCube.get("0;01").put("doen", new HashMap<String, Integer>());
		resultCube.get("0;01").put("hebben", new HashMap<String, Integer>());
		resultCube.get("0;02").put("doen", new HashMap<String, Integer>());
		resultCube.get("0;02").put("hebben", new HashMap<String, Integer>());
		
		resultCube.get("0;01").get("doen").put("1ps", new Integer(3));
		resultCube.get("0;01").get("doen").put("inf", new Integer(6));
		resultCube.get("0;01").get("doen").put("1pp", new Integer(5));
		resultCube.get("0;01").get("hebben").put("inf", new Integer(9));
		
		resultCube.get("0;02").get("doen").put("1ps", new Integer(2));
		resultCube.get("0;02").get("doen").put("inf", new Integer(6));
		resultCube.get("0;02").get("hebben").put("1pp", new Integer(1));
		resultCube.get("0;02").get("hebben").put("inf", new Integer(1));
		
		// assembling the cubes
		DataCubeHash dc = new DataCubeHash();
		dc.setCube(cube);
		dc.setTime(time);
		dc.setLemmas(lemmas);
		dc.setCategories(categories);
		
		DataCubeHash result = new DataCubeHash();
		result.setCube(resultCube);
		result.setTime(time);
		result.setLemmas(resultLemmas);
		result.setCategories(categories);
		
		
		HashMap<String, String> equivs = new HashMap<String, String>();
		equivs.put("aan#doen", "doen");
		equivs.put("bij#hebben", "hebben");
		
		// adding it to the list
		c.add(new Object[]{dc, result, equivs});
		
		
		return c;
	}
}
