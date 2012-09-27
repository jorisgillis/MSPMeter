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

import junit.framework.Assert;
import msp.data.DataCubeList;
import msp.data.LemmaIndex;
import msp.data.MSPResult;
import msp.data.MSPTriple;
import msp.data.SpanIndex;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 *
 * @author joris
 */
@RunWith(Parameterized.class)
public class DataCubeListTest {

	protected DataCubeList cube;
	
	protected List<Double> unweightedVariety;
	protected List<Double> weightedVariety;
	protected List<Double> unweightedEntropy;
	protected List<Double> weightedEntropy;
	
	protected int numberOfTokens;
	protected HashMap<String, Integer> numberOfLemmas;
	protected DataCubeList cumulatedDC;
	protected int subSampleSize;
	protected int allSampleSize;
	

	/**
	 * Constructor
	 * @param cubicle			the data
	 * @param spanIndex			the span index
	 * @param unweightedVariety	results
	 * @param weightedVariety	results
	 * @param unweightedEntropy	results
	 * @param weightedEntropy	results
	 * @param numberOfTokens	number of tokens in the data
	 * @param numberOfLemmas	number of lemmas in the data
	 * @param subSampleSize		size of the sample
	 * @param allSampleSize		size of the sample in all datasets sampling
	 */
	public DataCubeListTest(List<List<List<Integer>>> cubicle,
							List<SpanIndex> spanIndex,
							List<Double> unweightedVariety,
							List<Double> weightedVariety,
							List<Double> unweightedEntropy,
							List<Double> weightedEntropy,
							int numberOfTokens,
							HashMap<String, Integer> numberOfLemmas,
							DataCubeList cumulatedDC,
							int subSampleSize,
							int allSampleSize) {
		// Storing the cube
		cube = new DataCubeList();
		cube.setCube(cubicle);
		cube.setSpanIndex(spanIndex);
		
		// Storing the expected values
		this.unweightedVariety	= unweightedVariety;
		this.unweightedEntropy	= unweightedEntropy;
		this.weightedVariety	= weightedVariety;
		this.weightedEntropy	= weightedEntropy;
		
		this.numberOfTokens		= numberOfTokens;
		this.numberOfLemmas		= numberOfLemmas;
		this.cumulatedDC		= cumulatedDC;
		this.subSampleSize		= subSampleSize;
		this.allSampleSize		= allSampleSize;
	}

	/**
	 * Testing the unweighted variety.
	 */
	@Test
	public void mspUnweightedVarietyTest() {
		// Compute and get the values
		MSPResult result = cube.MSP(false, false);
		MSPTriple[] values = result.getResults();
		
		// Compare
		for (int i = 0; i < values.length; i++)
			Assert.assertEquals("Unweighted Variety",  
					unweightedVariety.get(i), 
					values[i].getMSP(),
					0.000001);
	}

	/**
	 * Testing the unweighted variety.
	 */
	@Test
	public void mspWeightedVarietyTest() {
		// Compute and get the values
		MSPResult result = cube.MSP(true, false);
		MSPTriple[] values = result.getResults();
		
		// Compare
		for (int i = 0; i < values.length; i++)
			Assert.assertEquals("Weighted Variety", 
					weightedVariety.get(i), 
					values[i].getMSP(), 
					0.000001);
	}

	/**
	 * Testing the unweighted variety.
	 */
	@Test
	public void mspUnweightedEntropyTest() {
		// Compute and get the values
		MSPResult result = cube.MSP(false, true);
		MSPTriple[] values = result.getResults();
		
		// Compare
		for (int i = 0; i < values.length; i++)
			Assert.assertEquals("Unweighted Entropy", 
					unweightedEntropy.get(i),
					values[i].getMSP(),
					0.0000001);
	}

	/**
	 * Testing the unweighted variety.
	 */
	@Test
	public void mspWeightedEntropyTest() {
		// Compute and get the values
		MSPResult result = cube.MSP(true, true);
		MSPTriple[] values = result.getResults();
		
		// Compare
		for (int i = 0; i < values.length; i++)
			Assert.assertEquals("Weighted Entropy", 
					weightedEntropy.get(i),
					values[i].getMSP(),
					0.0000001);
	}
	
	/**
	 * Testing the function numberOfTokens
	 */
	@Test
	public void numberOfTokensTest() {
		Assert.assertEquals("Number Of Tokens", 
				numberOfTokens, 
				cube.numberOfTokens());
	}
	
	/**
	 * Testing the function numberOfLemmas
	 */
	@Test
	public void numberOfLemmasTest() {
		for (String span : numberOfLemmas.keySet())
			Assert.assertEquals("Number Of Lemmas",
					numberOfLemmas.get(span).intValue(),
					cube.numberOfLemmas(span));
	}
	
	/**
	 * Tests the cumulation of a cube, and whether the original cube remains 
	 * unaltered. 
	 */
	@Test
	public void cumulate() {
		DataCubeList copy		= cube.copyCube();
		DataCubeList cumulDC	= cube.cumulate();
		
		Assert.assertEquals("Original unchanged after cumulate", copy, cube);
		Assert.assertEquals("Cumulate", cumulatedDC, cumulDC);
	}
	
	/**
	 * Test one dataset sampling with unweighted variety. 
	 */
	@Test
	public void resampleOneSpanTestUnweightedVariety() {
		// Unweighted Variety
		MSPTriple[] results = 
				cube.resampleMSP(false, false, 0, subSampleSize, 0, 100).getResults();
		for (int i = 0; i < results.length; i++)
			if (results[i].getMSP() >= 1) {
				double deviation = Math.max(0.0001, 
						2.56*results[i].getStdDev());
				Assert.assertEquals("Unweighted Variety "+ i, 
						unweightedVariety.get(i), 
						results[i].getMSP(), 
						deviation);
			}
	}
	
	/**
	 * Test one dataset sampling with weighted variety.
	 */
	@Test
	public void resampleOneSpanTestWeightedVariety() {
		// Weighted Variety
		MSPTriple[] results = 
				cube.resampleMSP(true, false, 0, subSampleSize, 0, 100).getResults();
		for (int i = 0; i < results.length; i++)
			if (results[i].getMSP() >= 1) {
				double deviation = Math.max(0.0001, 
						2.56*results[i].getStdDev());
				Assert.assertEquals("Weighted Variety "+ i, 
						weightedVariety.get(i), 
						results[i].getMSP(), 
						deviation);
			}
	}
	
	/**
	 * Test one dataset sampling with unweighted entropy.
	 */
	@Test
	public void resampleOneSpanTestUnweightedEntropy() {	
		// Unweighted Variety
		MSPTriple[] results = 
				cube.resampleMSP(false, true, 0, subSampleSize, 0, 100).getResults();
		for (int i = 0; i < results.length; i++)
			if (results[i].getMSP() >= 1) {
				double deviation = Math.max(0.0001, 
						2.56*results[i].getStdDev());
				Assert.assertEquals("Unweighted Entropy "+ i, 
						unweightedEntropy.get(i), 
						results[i].getMSP(), 
						deviation);
			}
	}
	
	/**
	 * Test one dataset sampling with weighted entropy.
	 */
	@Test
	public void resampleOneSpanTestWeightedEntropy() {	
		// Unweighted Variety
		MSPTriple[] results = 
				cube.resampleMSP(true, true, 0, subSampleSize, 0, 100).getResults();
		for (int i = 0; i < results.length; i++)
			if (results[i].getMSP() >= 1) {
				double deviation = Math.max(0.0001, 
						2.56*results[i].getStdDev());
				Assert.assertEquals("Weighted Entropy "+ i, 
						weightedEntropy.get(i), 
						results[i].getMSP(), 
						deviation);
			}
	}
	
	/**
	 * Test all dataset sampling with unweighted entropy.
	 */
	@Test
	public void resampleAllSpanTestUnweightedVariety() {
		MSPTriple[] results = 
				cube.resampleMSP(false, false, 1, allSampleSize, 1, 100).getResults();
		for (int i = 0; i < results.length; i++) {
			if (results[i].getMSP() >= 1) {
				double deviation = Math.max(0.0001, 
						2.56*results[i].getStdDev());
				Assert.assertEquals("All Datasets Unweighted Variety "+ i, 
						unweightedVariety.get(i),
						results[i].getMSP(),
						deviation);
			}
		}
	}
	
	/**
	 * Test all dataset sampling with weighted variety.
	 */
	@Test
	public void resampleAllSpanTestWeightedVariety() {
		MSPTriple[] results = 
				cube.resampleMSP(true, false, 1, allSampleSize, 1, 100).getResults();
		for (int i = 0; i < results.length; i++) {
			if (results[i].getMSP() >= 1) {
				double deviation = Math.max(0.0001, 
						2.56*results[i].getStdDev());
				Assert.assertEquals("All Datasets Weighted Variety "+ i, 
						weightedVariety.get(i),
						results[i].getMSP(),
						deviation);
			}
		}
	}
	
	/**
	 * Test all dataset sampling with unweighted entropy.
	 */
	@Test
	public void resampleAllSpanTestUnweightedEntropy() {
		MSPTriple[] results = 
				cube.resampleMSP(false, true, 1, allSampleSize, 1, 100).getResults();
		for (int i = 0; i < results.length; i++) {
			if (results[i].getMSP() >= 1) {
				double deviation = Math.max(0.0001, 
						2.56*results[i].getStdDev());
				Assert.assertEquals("All Datasets Unweighted Entropy "+ i, 
						unweightedEntropy.get(i),
						results[i].getMSP(),
						deviation);
			}
		}
	}
	
	/**
	 * Test all dataset sampling with weighted entropy.
	 */
	@Test
	public void resampleAllSpanTestWeightedEntropy() {
		MSPTriple[] results = 
				cube.resampleMSP(true, true, 1, allSampleSize, 1, 100).getResults();
		for (int i = 0; i < results.length; i++) {
			if (results[i].getMSP() >= 1) {
				double deviation = Math.max(0.0001, 
						2.56*results[i].getStdDev());
				Assert.assertEquals("All Datasets Weighted Entropy"+ i, 
						weightedEntropy.get(i),
						results[i].getMSP(),
						deviation);
			}
		}
	}
	
	/**
	 * Setting up the test cases
	 * @return	list of test cases
	 */
	@Parameters
	public static Collection<Object[]> setUp() {
		Collection<Object[]> cases = new LinkedList<Object[]>();

		//- 0.
		List<List<List<Integer>>> cubicle = new ArrayList<List<List<Integer>>>();
		List<List<List<Integer>>> cumCube = new ArrayList<List<List<Integer>>>();
		cubicle.add(new ArrayList<List<Integer>>());
		cumCube.add(new ArrayList<List<Integer>>());
		
		List<SpanIndex> spanIndex = new LinkedList<SpanIndex>();
		SpanIndex si = new SpanIndex("0;01");
		spanIndex.add(si);
		
		List<SpanIndex> cumIndex  = new LinkedList<SpanIndex>();
		SpanIndex cSI = new SpanIndex("0;01"); 
		cumIndex.add(cSI);
		
		// Lemmas
		cubicle.get(0).add(new ArrayList<Integer>());
		cubicle.get(0).add(new ArrayList<Integer>());
		cumCube.get(0).add(new ArrayList<Integer>());
		cumCube.get(0).add(new ArrayList<Integer>());
		
		si.addLemmaIndex(new LemmaIndex("have"));
		si.addLemmaIndex(new LemmaIndex("be"));
		cSI.addLemmaIndex(new LemmaIndex("have"));
		cSI.addLemmaIndex(new LemmaIndex("be"));
		
		// Categories
		cubicle.get(0).get(0).add(2);
		
		cumCube.get(0).get(0).add(2);
		
		si.getLemma(0).addCategory("inf");
		cSI.getLemma(0).addCategory("inf");
		
		cubicle.get(0).get(1).add(2);
		cubicle.get(0).get(1).add(1);
		
		cumCube.get(0).get(1).add(2);
		cumCube.get(0).get(1).add(1);
		
		si.getLemma(1).addCategory("1pp");
		si.getLemma(1).addCategory("1ps");
		cSI.getLemma(1).addCategory("1pp");
		cSI.getLemma(1).addCategory("1ps");
		
		// Results
		List<Double> unweightedVariety	= new ArrayList<Double>(1);
		List<Double> weightedVariety	= new ArrayList<Double>(1);
		List<Double> unweightedEntropy	= new ArrayList<Double>(1);
		List<Double> weightedEntropy	= new ArrayList<Double>(1);
		
		unweightedVariety.add(new Double(1.5));
		weightedVariety.add(new Double(1.6));
		unweightedEntropy.add(new Double(1.444940787));
		weightedEntropy.add(new Double(1.533928945));
		
		int numberOfTokens = 5;
		HashMap<String, Integer> numberOfLemmas = new HashMap<String, Integer>();
		numberOfLemmas.put("0;01", 2);
		int subSampleSize = 5;
		int allSampleSize = 5;
		
		DataCubeList cumulatedDC = new DataCubeList();
		cumulatedDC.setCube(cumCube);
		cumulatedDC.setSpanIndex(cumIndex);
		
		cases.add(new Object[]{cubicle, spanIndex, 
								unweightedVariety, weightedVariety, 
								unweightedEntropy, weightedEntropy,
								numberOfTokens, numberOfLemmas,
								cumulatedDC, subSampleSize, allSampleSize});
		
		
		//- 1.
		cubicle = new ArrayList<List<List<Integer>>>(3);
		cubicle.add(new ArrayList<List<Integer>>());
		cubicle.add(new ArrayList<List<Integer>>());
		cubicle.add(new ArrayList<List<Integer>>());
		cumCube = new ArrayList<List<List<Integer>>>(3);
		cumCube.add(new ArrayList<List<Integer>>());
		cumCube.add(new ArrayList<List<Integer>>());
		cumCube.add(new ArrayList<List<Integer>>());
		
		spanIndex = new ArrayList<SpanIndex>(3);
		spanIndex.add(new SpanIndex("0;01"));
		spanIndex.add(new SpanIndex("0;02"));
		spanIndex.add(new SpanIndex("0;03"));
		
		cumIndex = new ArrayList<SpanIndex>(3);
		cumIndex.add(new SpanIndex("0;01"));
		cumIndex.add(new SpanIndex("0;02"));
		cumIndex.add(new SpanIndex("0;03"));
		
		// Lemmas
		cubicle.get(0).add(new ArrayList<Integer>());
		cubicle.get(0).add(new ArrayList<Integer>());
		
		cubicle.get(1).add(new ArrayList<Integer>());
		cubicle.get(1).add(new ArrayList<Integer>());
		
		cubicle.get(2).add(new ArrayList<Integer>());
		cubicle.get(2).add(new ArrayList<Integer>());
		cubicle.get(2).add(new ArrayList<Integer>());
		
		cumCube.get(0).add(new ArrayList<Integer>());
		cumCube.get(0).add(new ArrayList<Integer>());
		
		cumCube.get(1).add(new ArrayList<Integer>());
		cumCube.get(1).add(new ArrayList<Integer>());
		
		cumCube.get(2).add(new ArrayList<Integer>());
		cumCube.get(2).add(new ArrayList<Integer>());
		cumCube.get(2).add(new ArrayList<Integer>());
		
		spanIndex.get(0).addLemmaIndex(new LemmaIndex("have"));
		spanIndex.get(0).addLemmaIndex(new LemmaIndex("be"));
		
		spanIndex.get(1).addLemmaIndex(new LemmaIndex("have"));
		spanIndex.get(1).addLemmaIndex(new LemmaIndex("be"));
		
		spanIndex.get(2).addLemmaIndex(new LemmaIndex("have"));
		spanIndex.get(2).addLemmaIndex(new LemmaIndex("be"));
		spanIndex.get(2).addLemmaIndex(new LemmaIndex("want"));
		
		cumIndex.get(0).addLemmaIndex(new LemmaIndex("have"));
		cumIndex.get(0).addLemmaIndex(new LemmaIndex("be"));
		
		cumIndex.get(1).addLemmaIndex(new LemmaIndex("have"));
		cumIndex.get(1).addLemmaIndex(new LemmaIndex("be"));
		
		cumIndex.get(2).addLemmaIndex(new LemmaIndex("have"));
		cumIndex.get(2).addLemmaIndex(new LemmaIndex("be"));
		cumIndex.get(2).addLemmaIndex(new LemmaIndex("want"));
		
		// Categories
		cubicle.get(0).get(0).add(3);
		cubicle.get(0).get(1).add(2);
		
		cubicle.get(1).get(0).add(1);
		cubicle.get(1).get(0).add(3);
		cubicle.get(1).get(1).add(4);
		cubicle.get(1).get(1).add(2);
		
		cubicle.get(2).get(0).add(3);
		cubicle.get(2).get(0).add(2);
		cubicle.get(2).get(0).add(4);
		
		cubicle.get(2).get(1).add(1);
		cubicle.get(2).get(1).add(2);
		cubicle.get(2).get(1).add(2);
		
		cubicle.get(2).get(2).add(1);
		cubicle.get(2).get(2).add(2);
		
		cumCube.get(0).get(0).add(3);
		cumCube.get(0).get(1).add(2);
		
		cumCube.get(1).get(0).add(6);
		cumCube.get(1).get(0).add(1);
		cumCube.get(1).get(1).add(4);
		cumCube.get(1).get(1).add(4);
		
		cumCube.get(2).get(0).add(10);
		cumCube.get(2).get(0).add(1);
		cumCube.get(2).get(0).add(3);
		cumCube.get(2).get(0).add(2);
		cumCube.get(2).get(1).add(6);
		cumCube.get(2).get(1).add(4);
		cumCube.get(2).get(1).add(1);
		cumCube.get(2).get(1).add(2);
		cumCube.get(2).get(2).add(1);
		cumCube.get(2).get(2).add(2);
		
		
		spanIndex.get(0).getLemma(0).addCategory("inf");
		spanIndex.get(0).getLemma(1).addCategory("inf");
		
		spanIndex.get(1).getLemma(0).addCategory("1pp");
		spanIndex.get(1).getLemma(0).addCategory("inf");
		spanIndex.get(1).getLemma(1).addCategory("1pp");
		spanIndex.get(1).getLemma(1).addCategory("inf");
		
		spanIndex.get(2).getLemma(0).addCategory("1ps");
		spanIndex.get(2).getLemma(0).addCategory("2ps");
		spanIndex.get(2).getLemma(0).addCategory("inf");
		spanIndex.get(2).getLemma(1).addCategory("1ps");
		spanIndex.get(2).getLemma(1).addCategory("2ps");
		spanIndex.get(2).getLemma(1).addCategory("inf");
		spanIndex.get(2).getLemma(2).addCategory("1ps");
		spanIndex.get(2).getLemma(2).addCategory("inf");
		
		cumIndex.get(0).getLemma(0).addCategory("inf");
		cumIndex.get(0).getLemma(1).addCategory("inf");
		
		cumIndex.get(1).getLemma(0).addCategory("inf");
		cumIndex.get(1).getLemma(0).addCategory("1pp");
		cumIndex.get(1).getLemma(1).addCategory("inf");
		cumIndex.get(1).getLemma(1).addCategory("1pp");
		
		cumIndex.get(2).getLemma(0).addCategory("inf");
		cumIndex.get(2).getLemma(0).addCategory("1pp");
		cumIndex.get(2).getLemma(0).addCategory("1ps");
		cumIndex.get(2).getLemma(0).addCategory("2ps");
		cumIndex.get(2).getLemma(1).addCategory("inf");
		cumIndex.get(2).getLemma(1).addCategory("1pp");
		cumIndex.get(2).getLemma(1).addCategory("1ps");
		cumIndex.get(2).getLemma(1).addCategory("2ps");
		cumIndex.get(2).getLemma(2).addCategory("1ps");
		cumIndex.get(2).getLemma(2).addCategory("inf");
		
		// Results
		unweightedVariety	= new ArrayList<Double>();
		weightedVariety		= new ArrayList<Double>();
		unweightedEntropy	= new ArrayList<Double>();
		weightedEntropy		= new ArrayList<Double>();
		
		unweightedVariety.add(new Double(1));
		unweightedVariety.add(new Double(2));
		unweightedVariety.add(new Double(8.0/3.0));
		
		weightedVariety.add(new Double(1));
		weightedVariety.add(new Double(2));
		weightedVariety.add(new Double((9.0/17.0)*3 + (5.0/17.0)*3 + (3.0/17.0)*2));
		
		unweightedEntropy.add(new Double(1));
		unweightedEntropy.add(new Double(1.82232346272282));
		unweightedEntropy.add(new Double(2.55015765971245));
		
		weightedEntropy.add(new Double(1));
		weightedEntropy.add(new Double(1.83583508514672));
		weightedEntropy.add(new Double(2.7075284596007));
		
		numberOfTokens	= 32;
		numberOfLemmas	= new HashMap<String, Integer>();
		numberOfLemmas.put("0;01", 2);
		numberOfLemmas.put("0;02", 2);
		numberOfLemmas.put("0;03", 3);
		subSampleSize	= 20;
		allSampleSize 	= 29;
		
		cumulatedDC = new DataCubeList();
		cumulatedDC.setCube(cumCube);
		cumulatedDC.setSpanIndex(cumIndex);
		
		cases.add(new Object[]{cubicle, spanIndex,
								unweightedVariety, weightedVariety,
								unweightedEntropy, weightedEntropy,
								numberOfTokens, numberOfLemmas,
								cumulatedDC, subSampleSize, allSampleSize});
		
		return cases;
	}
}
