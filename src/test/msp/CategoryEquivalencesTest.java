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

import msp.data.DataCubeHash;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class CategoryEquivalencesTest {
	
	private DataCubeHash dc;
	private DataCubeHash result;
	private HashMap<String, HashMap<String, String>> categoryEquivalences;
	
	/**
	 * Construct a new testcase
	 * @param dc					start
	 * @param result				end
	 * @param categoryEquivalences	equivalences
	 */
	public CategoryEquivalencesTest( DataCubeHash dc, DataCubeHash result, HashMap<String, HashMap<String, String>> categoryEquivalences ) {
		this.dc = dc;
		this.result = result;
		this.categoryEquivalences = categoryEquivalences;
	}
	
	
	/**
	 * Test whether the subcategories are well removed
	 */
	@Test
	public void test1() {
		assertEquals(result, dc.categoryEquivalences(categoryEquivalences, false, null));
	}
	
	
	/**
	 * Set up the testcases.
	 * @return	testcase data
	 */
	@Parameters
	public static Collection<Object[]> setUp() {
		Vector<Object[]> c = new Vector<Object[]>();
		
		//- 1.
		Vector<String> time = new Vector<String>();
		Vector<String> lemmas = new Vector<String>();
		Vector<String> categories = new Vector<String>();
		
		time.add("0;01");
		time.add("0;02");
		
		lemmas.add("doen");
		lemmas.add("hebben");
		lemmas.add("zijn");
		
		categories.add("1pp");
		categories.add("2pp");
		categories.add("3pp");
		categories.add("inf");
		categories.add("stem");
		
		Vector<String> resultCategories = new Vector<String>();
		resultCategories.add("1pp");
		resultCategories.add("inf");
		resultCategories.add("stem");
		
		
		// the cube
		HashMap<String, HashMap<String, HashMap<String, Integer>>> cube = new HashMap<String, HashMap<String,HashMap<String,Integer>>>();
		
		cube.put("0;01", new HashMap<String, HashMap<String,Integer>>());
		cube.put("0;02", new HashMap<String, HashMap<String,Integer>>());
		
		cube.get("0;01").put("doen", new HashMap<String, Integer>());
		cube.get("0;01").put("hebben", new HashMap<String, Integer>());
		cube.get("0;02").put("hebben", new HashMap<String, Integer>());
		cube.get("0;02").put("zijn", new HashMap<String, Integer>());
		
		cube.get("0;01").get("doen").put("1pp", 2);
		cube.get("0;01").get("doen").put("inf", 3);
		cube.get("0;01").get("doen").put("stem", 2);
		cube.get("0;01").get("hebben").put("2pp", 4);
		cube.get("0;01").get("hebben").put("1pp", 2);
		
		cube.get("0;02").get("hebben").put("inf", 3);
		cube.get("0;02").get("hebben").put("stem", 4);
		cube.get("0;02").get("hebben").put("1pp", 2);
		cube.get("0;02").get("hebben").put("2pp", 2);
		cube.get("0;02").get("zijn").put("1pp", 2);
		cube.get("0;02").get("zijn").put("stem", 3);
		
		
		// the result cube
		HashMap<String, HashMap<String, HashMap<String, Integer>>> resultCube = new HashMap<String, HashMap<String,HashMap<String,Integer>>>();
		resultCube.put("0;01", new HashMap<String, HashMap<String,Integer>>());
		resultCube.put("0;02", new HashMap<String, HashMap<String,Integer>>());
		
		resultCube.get("0;01").put("doen", new HashMap<String, Integer>());
		resultCube.get("0;01").put("hebben", new HashMap<String, Integer>());
		resultCube.get("0;02").put("hebben", new HashMap<String, Integer>());
		resultCube.get("0;02").put("zijn", new HashMap<String, Integer>());
		
		resultCube.get("0;01").get("doen").put("inf", 5);
		resultCube.get("0;01").get("doen").put("stem", 2);
		resultCube.get("0;01").get("hebben").put("inf", 6);
		
		resultCube.get("0;02").get("hebben").put("inf", 7);
		resultCube.get("0;02").get("hebben").put("stem", 4);
		resultCube.get("0;02").get("zijn").put("1pp", 2);
		resultCube.get("0;02").get("zijn").put("stem", 3);
		
		
		// equivalences
		HashMap<String, HashMap<String, String>> categoryEquivalences = new HashMap<String, HashMap<String,String>>();
		categoryEquivalences.put("doen", new HashMap<String, String>());
		categoryEquivalences.put("hebben", new HashMap<String, String>());
		categoryEquivalences.put("zijn", new HashMap<String, String>());
		
		categoryEquivalences.get("doen").put("1pp", "inf");
		categoryEquivalences.get("hebben").put("1pp", "inf");
		categoryEquivalences.get("hebben").put("2pp", "inf");
		
		
		// assembly
		DataCubeHash dc = new DataCubeHash();
		dc.setCategories(categories);
		dc.setCube(cube);
		dc.setLemmas(lemmas);
		dc.setTime(time);
		
		DataCubeHash result = new DataCubeHash();
		result.setCategories(resultCategories);
		result.setCube(resultCube);
		result.setLemmas(lemmas);
		result.setTime(time);
		
		// add the testcase
		c.add(new Object[]{dc, result, categoryEquivalences});
		
		
		return c;
	}
	
}
