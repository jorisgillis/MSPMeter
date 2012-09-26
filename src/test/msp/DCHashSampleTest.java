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

import java.io.File;
import java.util.Vector;

import msp.data.DataCubeHash;
import msp.data.DataFaultException;
import ui.panels.file.FileRow;

/**
 * This class tests the speed of various sampling algorithms.
 * @author joris
 */
public class DCHashSampleTest {
	
	/**
	 * Start the test!
	 */
	public static void main(String[] args) {
		new DCHashSampleTest();
	}
	
	private DataCubeHash cube;
	
	/**
	 * The constructor constructs some datacubes and runs the tests.
	 */
	public DCHashSampleTest() {
		// Construct the cube
		constructCube();
		
		// Set up the test values
		int[] sampleSizes	= new int[]{2000, 5000, 10000, 15000, 20000};
		double[] factors	= new double[]{0.5, 1, 1.5, 2, 5};
		double[] values		= new double[]{5, 10, 50, 100, 200};
		
		double total1 = 0.0;
		double total2 = 0.0;
		int runs = 0;
		for( int i = 0; i < sampleSizes.length; i++ ) {
			// xMode = 0
			for( int j = 0; j < factors.length; j++ ) {
				double time1 = sample1(sampleSizes[i], 0, factors[j]);
				double time2 = sample2(sampleSizes[i], 0, factors[j]);
				
				total1 += time1;
				total2 += time2;
				
//				System.out.format("Sampler 1\t%5d\t%01.1f\t%f\n", 
//						sampleSizes[i], factors[j], time1);
//				System.out.format("Sampler 2\t%5d\t%01.1f\t%f\n", 
//						sampleSizes[i], factors[j], time2);
				runs++;
			}
			
			// xMode = 1
			for( int j = 0; j < values.length; j++ ) {
				double time1 = sample1(sampleSizes[i], 1, values[j]);
				double time2 = sample2(sampleSizes[i], 1, values[j]);
				
				total1 += time1;
				total2 += time2;
				
//				System.out.format("Sampler 1\t%5d\t%3.0f\t%f\n", 
//						sampleSizes[i], values[j], time1);
//				System.out.format("Sampler 2\t%5d\t%3.0f\t%f\n", 
//						sampleSizes[i], values[j], time2);
				runs++;
			}
		}
		
		System.out.format("Time for sampling procedure 1: %6.3f ns\n", 
				(total1/runs));
		System.out.format("Time for sampling procedure 2: %6.3f ns\n",
				(total2/runs));
	}
	
	/**
	 * Tests the first sampling procedure.
	 * @param S		sample size
	 * @param xMode	Is X Sampling factor or Fixed value?
	 * @param X		Sampling factor or Fixed value
	 * @return	time needed to sample
	 */
	private double sample1(int S, int xMode, double X) {
		// Start clock
		double start = System.nanoTime();
		
		// Sample!
		cube.resample(S, xMode, X);
		
		// Stop clock
		double end = System.nanoTime();
		
		return (end-start);
	}
	
	/**
	 * Tests the second sampling procedure.
	 * @param S		sample size
	 * @param xMode	Is X Sampling factor or Fixed value?
	 * @param X		Sampling factor or Fixed value
	 * @return	time needed to sample
	 */
	private double sample2(int S, int xMode, double X) {
		// Start clock
		double start = System.nanoTime();
		
		// Sample!
		cube.resample(S, xMode, X);
		
		// Stop the clock
		double end = System.nanoTime();
		
		return (end-start);
	}
	
	/**
	 * Constructs the cube, and fill it up with Brown's data.
	 */
	private void constructCube() {
		cube = new DataCubeHash();
		
		Vector<FileRow> files = new Vector<FileRow>();
		files.add(new FileRow(1, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "1"));
		files.add(new FileRow(2, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "2"));
		files.add(new FileRow(3, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "3"));
		files.add(new FileRow(4, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "4"));
		files.add(new FileRow(5, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "5"));
		files.add(new FileRow(6, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "6"));
		files.add(new FileRow(7, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "7"));
		files.add(new FileRow(8, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "8"));
		files.add(new FileRow(9, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "9"));
		files.add(new FileRow(10, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "10"));
		files.add(new FileRow(11, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "11"));
		files.add(new FileRow(12, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "12"));
		files.add(new FileRow(13, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "13"));
		files.add(new FileRow(14, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "14"));
		files.add(new FileRow(15, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "15"));
		files.add(new FileRow(16, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "16"));
		files.add(new FileRow(17, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "17"));
		files.add(new FileRow(18, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "18"));
		files.add(new FileRow(19, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "19"));
		files.add(new FileRow(20, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "20"));
		files.add(new FileRow(21, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "21"));
		files.add(new FileRow(22, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "22"));
		files.add(new FileRow(23, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "23"));
		files.add(new FileRow(24, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "24"));
		files.add(new FileRow(25, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "25"));
		files.add(new FileRow(26, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "26"));
		files.add(new FileRow(27, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "27"));
		files.add(new FileRow(28, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "28"));
		files.add(new FileRow(29, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "29"));
		files.add(new FileRow(30, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "30"));
		files.add(new FileRow(31, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "31"));
		files.add(new FileRow(32, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "32"));
		files.add(new FileRow(33, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "33"));
		files.add(new FileRow(34, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "34"));
		files.add(new FileRow(35, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "35"));
		files.add(new FileRow(36, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "36"));
		files.add(new FileRow(37, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "37"));
		files.add(new FileRow(38, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "38"));
		files.add(new FileRow(39, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "39"));
		files.add(new FileRow(40, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "40"));
		files.add(new FileRow(41, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "41"));
		files.add(new FileRow(42, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "42"));
		files.add(new FileRow(43, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "43"));
		files.add(new FileRow(44, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "44"));
		files.add(new FileRow(45, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "45"));
		files.add(new FileRow(46, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "46"));
		files.add(new FileRow(47, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "47"));
		files.add(new FileRow(48, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "48"));
		files.add(new FileRow(49, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "49"));
		files.add(new FileRow(50, new File("../../Data/Brown/verbs/adam01.kwa.frq.cex"), "50"));
		
		try {
			cube.fillCube(files, "|", "&-~ ");
		} catch( DataFaultException e ) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
