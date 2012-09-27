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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import msp.data.DataCubeList;
import msp.data.LemmaIndex;
import msp.data.SpanIndex;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Testing the lemma equivalence capabilities of DataCubeList
 * @author joris
 */
@RunWith(Parameterized.class)
public class DCListLemmaEquivalencesListTest {
	
	protected DataCubeList cube, result;
	protected HashMap<String, String> lemmaEquivalences;
	
	
	/**
	 * Sets up the testing.
	 * @param cube				start
	 * @param result			finished result
	 * @param lemmaEquivalences	equivalences to apply
	 */
	public DCListLemmaEquivalencesListTest(DataCubeList cube,
									 DataCubeList result,
									 HashMap<String, String> lemmaEquivalences) {
		this.cube				= cube;
		this.result				= result;
		this.lemmaEquivalences	= lemmaEquivalences;
	}
	
	/**
	 * Test whether the lemmaEquivalences on dc have the desired result.
	 */
	@Test
	public void test1() {
		Assert.assertEquals("Equivalence", 
							result, 
							cube.lemmaEquivalences(lemmaEquivalences));
	}
	
	/** 
	 * Test whether the original DataCube stays invariant.
	 */
	@Test
	public void invariance1() {
		try {
			DataCubeList clone = cube.copyCube();
			cube.lemmaEquivalences(lemmaEquivalences);
			Assert.assertTrue("Invariance", clone.equals(cube));
		} catch( Exception e ) {
			System.out.println(e.getMessage());
			Assert.assertTrue(false);
		}
	}
	
	/**
	 * Setting up the test cases.
	 * @return	a list of testcases.
	 */
	@Parameters
	public static Collection<Object[]> setup() {
		Collection<Object[]> cases = new LinkedList<Object[]>();
		
		//- 0.
		List<List<List<Integer>>> cubicleIn = new ArrayList<List<List<Integer>>>(2);
		cubicleIn.add(new ArrayList<List<Integer>>());	// 0;01
		cubicleIn.add(new ArrayList<List<Integer>>());	// 0;02
		
		cubicleIn.get(0).add(new ArrayList<Integer>());	// doen
		cubicleIn.get(0).add(new ArrayList<Integer>());	// aan#doen
		cubicleIn.get(0).add(new ArrayList<Integer>());	// hebben
		
		cubicleIn.get(1).add(new ArrayList<Integer>());	// doen
		cubicleIn.get(1).add(new ArrayList<Integer>());	// bij#hebben
		
		cubicleIn.get(0).get(0).add(2);	// 1ps
		cubicleIn.get(0).get(0).add(6);	// inf
		cubicleIn.get(0).get(1).add(1);	// 1ps
		cubicleIn.get(0).get(1).add(5);	// 1pp
		cubicleIn.get(0).get(2).add(9);	// inf
		
		cubicleIn.get(1).get(0).add(2);	// 1ps
		cubicleIn.get(1).get(0).add(6);	// inf
		cubicleIn.get(1).get(1).add(1);	// 1pp
		cubicleIn.get(1).get(1).add(1);	// inf
		
		List<SpanIndex> spanIndexIn = new LinkedList<SpanIndex>();
		spanIndexIn.add(new SpanIndex("0;01"));
		spanIndexIn.add(new SpanIndex("0;02"));
		
		spanIndexIn.get(0).addLemmaIndex(new LemmaIndex("doen"));
		spanIndexIn.get(0).addLemmaIndex(new LemmaIndex("aan#doen"));
		spanIndexIn.get(0).addLemmaIndex(new LemmaIndex("hebben"));
		spanIndexIn.get(1).addLemmaIndex(new LemmaIndex("doen"));
		spanIndexIn.get(1).addLemmaIndex(new LemmaIndex("bij#hebben"));
		
		spanIndexIn.get(0).getLemma(0).addCategory("1ps");
		spanIndexIn.get(0).getLemma(0).addCategory("inf");
		spanIndexIn.get(0).getLemma(1).addCategory("1ps");
		spanIndexIn.get(0).getLemma(1).addCategory("1pp");
		spanIndexIn.get(0).getLemma(2).addCategory("inf");
		
		spanIndexIn.get(1).getLemma(0).addCategory("1ps");
		spanIndexIn.get(1).getLemma(0).addCategory("inf");
		spanIndexIn.get(1).getLemma(1).addCategory("1pp");
		spanIndexIn.get(1).getLemma(1).addCategory("inf");
		
		DataCubeList cube = new DataCubeList();
		cube.setCube(cubicleIn);
		cube.setSpanIndex(spanIndexIn);
		
		HashMap<String, String> equivalences = new HashMap<String, String>();
		equivalences.put("aan#doen", "doen");
		equivalences.put("bij#hebben", "hebben");
		
		List<List<List<Integer>>> cubicleOut = new ArrayList<List<List<Integer>>>();
		cubicleOut.add(new ArrayList<List<Integer>>());	// 0;01
		cubicleOut.add(new ArrayList<List<Integer>>());	// 0;02
		
		cubicleOut.get(0).add(new ArrayList<Integer>());	// doen
		cubicleOut.get(0).add(new ArrayList<Integer>());	// hebben
		cubicleOut.get(1).add(new ArrayList<Integer>());	// doen
		cubicleOut.get(1).add(new ArrayList<Integer>());	// hebben
		
		cubicleOut.get(0).get(0).add(3);	// 1ps
		cubicleOut.get(0).get(0).add(6);	// inf
		cubicleOut.get(0).get(0).add(5);	// 1pp
		cubicleOut.get(0).get(1).add(9);	// inf
		
		cubicleOut.get(1).get(0).add(2);	// 1ps
		cubicleOut.get(1).get(0).add(6);	// inf
		cubicleOut.get(1).get(1).add(1);	// 1pp
		cubicleOut.get(1).get(1).add(1);	// inf

		List<SpanIndex> spanIndexOut = new ArrayList<SpanIndex>();
		spanIndexOut.add(new SpanIndex("0;01"));
		spanIndexOut.add(new SpanIndex("0;02"));
		
		spanIndexOut.get(0).addLemmaIndex(new LemmaIndex("doen"));
		spanIndexOut.get(0).addLemmaIndex(new LemmaIndex("hebben"));
		spanIndexOut.get(1).addLemmaIndex(new LemmaIndex("doen"));
		spanIndexOut.get(1).addLemmaIndex(new LemmaIndex("hebben"));
		
		spanIndexOut.get(0).getLemma(0).addCategory("1ps");
		spanIndexOut.get(0).getLemma(0).addCategory("inf");
		spanIndexOut.get(0).getLemma(0).addCategory("1pp");
		spanIndexOut.get(0).getLemma(1).addCategory("inf");
		
		spanIndexOut.get(1).getLemma(0).addCategory("1ps");
		spanIndexOut.get(1).getLemma(0).addCategory("inf");
		spanIndexOut.get(1).getLemma(1).addCategory("1pp");
		spanIndexOut.get(1).getLemma(1).addCategory("inf");
		
		DataCubeList result = new DataCubeList();
		result.setCube(cubicleOut);
		result.setSpanIndex(spanIndexOut);
		
		cases.add(new Object[]{cube, result, equivalences});
		
		
		//- 1.
		cubicleIn	= new ArrayList<List<List<Integer>>>();
		spanIndexIn	= new ArrayList<SpanIndex>();
		
		cubicleIn.add(new ArrayList<List<Integer>>());
		spanIndexIn.add(new SpanIndex("0;01"));
		
		cubicleIn.get(0).add(new ArrayList<Integer>());
		cubicleIn.get(0).add(new ArrayList<Integer>());
		spanIndexIn.get(0).addLemmaIndex(new LemmaIndex("aan#doen"));
		spanIndexIn.get(0).addLemmaIndex(new LemmaIndex("mee#doen"));
		
		cubicleIn.get(0).get(0).add(1);
		cubicleIn.get(0).get(0).add(2);
		cubicleIn.get(0).get(1).add(3);
		cubicleIn.get(0).get(1).add(4);
		spanIndexIn.get(0).getLemma(0).addCategory("1ps");
		spanIndexIn.get(0).getLemma(0).addCategory("1pp");
		spanIndexIn.get(0).getLemma(1).addCategory("inf");
		spanIndexIn.get(0).getLemma(1).addCategory("1ps");
		
		cube = new DataCubeList();
		cube.setCube(cubicleIn);
		cube.setSpanIndex(spanIndexIn);
		
		equivalences = new HashMap<String, String>();
		equivalences.put("aan#doen", "doen");
		equivalences.put("mee#doen", "doen");
		
		cubicleOut		= new ArrayList<List<List<Integer>>>();
		spanIndexOut	= new ArrayList<SpanIndex>();
		
		cubicleOut.add(new ArrayList<List<Integer>>());
		spanIndexOut.add(new SpanIndex("0;01"));
		
		cubicleOut.get(0).add(new ArrayList<Integer>());
		spanIndexOut.get(0).addLemmaIndex(new LemmaIndex("doen"));
		
		cubicleOut.get(0).get(0).add(3);
		cubicleOut.get(0).get(0).add(5);
		cubicleOut.get(0).get(0).add(2);
		spanIndexOut.get(0).getLemma(0).addCategory("inf");
		spanIndexOut.get(0).getLemma(0).addCategory("1ps");
		spanIndexOut.get(0).getLemma(0).addCategory("1pp");
		
		result = new DataCubeList();
		result.setCube(cubicleOut);
		result.setSpanIndex(spanIndexOut);
		
		cases.add(new Object[]{cube, result, equivalences});
		
		return cases;
	}
}
