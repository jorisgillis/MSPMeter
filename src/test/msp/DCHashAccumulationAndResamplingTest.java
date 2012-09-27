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
 * Test the accumulation of a DataCube.
 * @author Joris Gillis
 */
@RunWith(Parameterized.class)
public class DCHashAccumulationAndResamplingTest {

	/** The cube */
	private DataCubeHash dataCube;
	/** The accumulation of the cube */
	private DataCubeHash accumulated;
	/** Number of tokens in the DataCube dataCube */
	private int numberOfTokens;

	/**
	 * Create a new testcase
	 * @param dataCube			datacube
	 * @param accumulated		accumulation of the provided datacube
	 * @param numberOfTokens	number of tokens in the datacube
	 */
	public DCHashAccumulationAndResamplingTest( DataCubeHash dataCube, DataCubeHash accumulated, int numberOfTokens ) {
		this.dataCube = dataCube;
		this.accumulated = accumulated;
		this.numberOfTokens = numberOfTokens;
	}
	
	/**
	 * Execute an accumulation and test whether the result is correct
	 */
	@Test
	public void testAccumulation() {
		// this is true! System.out.println(accumulated.toString().equals(dataCube.accumulate().toString()));
		assertEquals( accumulated, dataCube.cumulate() );
	}
	
	/**
	 * Test whether the number of tokens is calculated correctly.
	 */
	@Test
	public void numberOfTokensTest() {
		assertEquals( numberOfTokens, dataCube.numberOfTokens() );
	}
	
	/**
	 * Test whether the resampling is done. This is a random process, this means, no correct answer exists.
	 */
	@Test
	public void resampleTest() {
		DataCubeHash[] resampled = dataCube.resample(6, 0, 1.0);
		assertTrue(resampled != null);
		
//		System.out.println(dataCube);
//		
//		for( int i = 0; i < resampled.length; i++ )
//			System.out.println(resampled[i]);
	}
	
	
	@Parameters
	public static Collection<Object[]> setUp() {
		Vector<Object[]> c = new Vector<Object[]>();

		//- 1.
		// cubes
		HashMap<String, HashMap<String, HashMap<String, Integer>>> cube1 = new HashMap<String, HashMap<String,HashMap<String,Integer>>>();
		HashMap<String, HashMap<String, HashMap<String, Integer>>> accumulated1 = new HashMap<String, HashMap<String,HashMap<String,Integer>>>();

		// axes
		Vector<String> c1 = new Vector<String>();
		c1.add("inf");
		c1.add("1ps");
		c1.add("1pp");

		Vector<String> l1 = new Vector<String>();
		l1.add("have");
		l1.add("be");

		Vector<String> t1 = new Vector<String>();
		t1.add("0;01");
		t1.add("0;02");
		
		// cube
		cube1.put("0;01", new HashMap<String, HashMap<String,Integer>>());
		
		cube1.get("0;01").put("have", new HashMap<String, Integer>());
		cube1.get("0;01").put("be", new HashMap<String, Integer>());
		
		cube1.get("0;01").get("have").put("inf", 1);
		cube1.get("0;01").get("be").put("1pp", 2);
		
		cube1.put("0;02", new HashMap<String, HashMap<String, Integer>>());
		
		cube1.get("0;02").put("have", new HashMap<String, Integer>());
		cube1.get("0;02").put("be", new HashMap<String, Integer>());
		
		cube1.get("0;02").get("have").put("inf", 1);
		cube1.get("0;02").get("be").put("1ps", 3);
		cube1.get("0;02").get("be").put("inf", 2);
		
		// accumulation
		accumulated1.put("0;01", new HashMap<String, HashMap<String,Integer>>());
		
		accumulated1.get("0;01").put("have", new HashMap<String, Integer>());
		accumulated1.get("0;01").put("be", new HashMap<String, Integer>());
		
		accumulated1.get("0;01").get("have").put("inf", 1);
		accumulated1.get("0;01").get("be").put("1pp", 2);
		
		accumulated1.put("0;02", new HashMap<String, HashMap<String, Integer>>());
		
		accumulated1.get("0;02").put("have", new HashMap<String, Integer>());
		accumulated1.get("0;02").put("be", new HashMap<String, Integer>());
		
		accumulated1.get("0;02").get("have").put("inf", 2);
		accumulated1.get("0;02").get("be").put("1ps", 3);
		accumulated1.get("0;02").get("be").put("1pp", 2);
		accumulated1.get("0;02").get("be").put("inf", 2);
		
		// making DataCubes out of it
		DataCubeHash dc1 = new DataCubeHash();
		dc1.setCategories(c1);
		dc1.setLemmas(l1);
		dc1.setTime(t1);
		dc1.setCube(cube1);
		
		DataCubeHash ac1 = new DataCubeHash();
		ac1.setCategories(c1);
		ac1.setLemmas(l1);
		ac1.setTime(t1);
		ac1.setCube(accumulated1);
		
		// adding to the test-queue
		c.add(new Object[]{dc1, ac1, 9});

		return c;
	}

}
