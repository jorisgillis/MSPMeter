/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.msp.data;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Collection;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import msp.data.DataCubeList;
import msp.data.LemmaIndex;
import msp.data.MSPResult;
import msp.data.MSPSpan;
import msp.data.SpanIndex;
import org.junit.Test;

/**
 *
 * @author joris
 */
@RunWith(Parameterized.class)
public class DataCubeListTest {

	protected DataCubeList cube;
	protected List<Double> unweightedVariety, weightedVariety;
	protected List<Double> unweightedEntropy, weightedEntropy;

	/**
	 * Constructor
	 * @param cubicle			the data
	 * @param spanIndex			the span index
	 * @param unweightedVariety	results
	 * @param weightedVariety	results
	 * @param unweightedEntropy	results
	 * @param weightedEntropy	results
	 */
	public DataCubeListTest(List<List<List<Integer>>> cubicle,
							List<SpanIndex> spanIndex,
							List<Double> unweightedVariety,
							List<Double> weightedVariety,
							List<Double> unweightedEntropy,
							List<Double> weightedEntropy) {
		// Storing the cube
		cube = new DataCubeList();
		cube.setCube(cubicle);
		cube.setSpanIndex(spanIndex);
		
		// Storing the expected values
		this.unweightedVariety	= unweightedVariety;
		this.unweightedEntropy	= unweightedEntropy;
		this.weightedVariety	= weightedVariety;
		this.weightedEntropy	= weightedEntropy;
	}

	/**
	 * Testing the unweighted variety.
	 */
	@Test
	public void mspUnweightedVarietyTest() {
		// Compute and get the values
		MSPResult result = cube.MSP(false, false);
		MSPSpan[] values = result.getResults();
		
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
		MSPSpan[] values = result.getResults();
		
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
		MSPSpan[] values = result.getResults();
		
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
		MSPSpan[] values = result.getResults();
		
		// Compare
		for (int i = 0; i < values.length; i++)
			Assert.assertEquals("Weighted Entropy", 
					weightedEntropy.get(i),
					values[i].getMSP(),
					0.0000001);
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
		cubicle.add(new ArrayList<List<Integer>>());
		
		List<SpanIndex> spanIndex = new LinkedList<SpanIndex>();
		SpanIndex si = new SpanIndex("0;01");
		spanIndex.add(si);
		
		// Lemmas
		cubicle.get(0).add(new ArrayList<Integer>());
		cubicle.get(0).add(new ArrayList<Integer>());
		
		si.addLemmaIndex(new LemmaIndex("have"));
		si.addLemmaIndex(new LemmaIndex("be"));
		
		// Categories
		cubicle.get(0).get(0).add(2);
		
		si.getLemma(0).addCategory("inf");
		
		cubicle.get(0).get(1).add(2);
		cubicle.get(0).get(1).add(1);
		
		si.getLemma(1).addCategory("1pp");
		si.getLemma(1).addCategory("1ps");
		
		// Results
		List<Double> unweightedVariety	= new ArrayList<Double>(1);
		List<Double> weightedVariety	= new ArrayList<Double>(1);
		List<Double> unweightedEntropy	= new ArrayList<Double>(1);
		List<Double> weightedEntropy	= new ArrayList<Double>(1);
		
		unweightedVariety.add(new Double(1.5));
		weightedVariety.add(new Double(1.6));
		unweightedEntropy.add(new Double(1.444940787));
		weightedEntropy.add(new Double(1.533928945));
		
		cases.add(new Object[]{cubicle, spanIndex, 
								unweightedVariety, weightedVariety, 
								unweightedEntropy, weightedEntropy});
		
		
		//- 1.
		cubicle = new ArrayList<List<List<Integer>>>(3);
		cubicle.add(new ArrayList<List<Integer>>());
		cubicle.add(new ArrayList<List<Integer>>());
		cubicle.add(new ArrayList<List<Integer>>());
		
		spanIndex = new ArrayList<SpanIndex>(3);
		spanIndex.add(new SpanIndex("0;01"));
		spanIndex.add(new SpanIndex("0;02"));
		spanIndex.add(new SpanIndex("0;03"));
		
		// Lemmas
		cubicle.get(0).add(new ArrayList<Integer>());
		cubicle.get(0).add(new ArrayList<Integer>());
		
		cubicle.get(1).add(new ArrayList<Integer>());
		cubicle.get(1).add(new ArrayList<Integer>());
		
		cubicle.get(2).add(new ArrayList<Integer>());
		cubicle.get(2).add(new ArrayList<Integer>());
		cubicle.get(2).add(new ArrayList<Integer>());
		
		spanIndex.get(0).addLemmaIndex(new LemmaIndex("have"));
		spanIndex.get(0).addLemmaIndex(new LemmaIndex("be"));
		
		spanIndex.get(1).addLemmaIndex(new LemmaIndex("have"));
		spanIndex.get(1).addLemmaIndex(new LemmaIndex("be"));
		
		spanIndex.get(2).addLemmaIndex(new LemmaIndex("have"));
		spanIndex.get(2).addLemmaIndex(new LemmaIndex("be"));
		spanIndex.get(2).addLemmaIndex(new LemmaIndex("want"));
		
		
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
		cubicle.get(2).get(1).add(5);
		cubicle.get(2).get(1).add(5);
		
		cubicle.get(2).get(2).add(1);
		cubicle.get(2).get(2).add(3);
		
		spanIndex.get(0).getLemma(0).addCategory("inf");
		spanIndex.get(0).getLemma(1).addCategory("inf");
		
		spanIndex.get(1).getLemma(0).addCategory("1ps");
		spanIndex.get(1).getLemma(0).addCategory("inf");
		spanIndex.get(1).getLemma(1).addCategory("1ps");
		spanIndex.get(1).getLemma(1).addCategory("inf");
		
		spanIndex.get(2).getLemma(0).addCategory("1ps");
		spanIndex.get(2).getLemma(0).addCategory("2ps");
		spanIndex.get(2).getLemma(0).addCategory("inf");
		spanIndex.get(2).getLemma(1).addCategory("1ps");
		spanIndex.get(2).getLemma(1).addCategory("2ps");
		spanIndex.get(2).getLemma(1).addCategory("inf");
		spanIndex.get(2).getLemma(2).addCategory("1ps");
		spanIndex.get(2).getLemma(2).addCategory("inf");
		
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
		weightedVariety.add(new Double((9.0/24.0)*3 + (11.0/24.0)*3 + (4.0/24.0)*2));
		
		unweightedEntropy.add(new Double(1));
		unweightedEntropy.add(new Double(1.82232346272282));
		unweightedEntropy.add(new Double(2.3967460911004));
		
		weightedEntropy.add(new Double(1));
		weightedEntropy.add(new Double(1.83583508514672));
		weightedEntropy.add(new Double(2.54298218827021));
		
		cases.add(new Object[]{cubicle, spanIndex,
								unweightedVariety, weightedVariety,
								unweightedEntropy, weightedEntropy});
		
		return cases;
	}
}
