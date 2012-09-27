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

import java.util.List;
import java.util.ArrayList;
import msp.data.DataCubeList;
import msp.data.LemmaIndex;
import msp.data.SpanIndex;

/**
 * Testing the sampling function from the DataCubeList class.
 * @author joris
 */
public class DCListSampleTest {
	
	/*
	 * Main
	 */
	public static void main(String[] args) {
		// Constructing a cube
		List<List<List<Integer>>> cube = new ArrayList<List<List<Integer>>>();
		List<SpanIndex> index = new ArrayList<SpanIndex>();
		
		cube.add(new ArrayList<List<Integer>>());
		cube.add(new ArrayList<List<Integer>>());
		cube.add(new ArrayList<List<Integer>>());
		cube.add(new ArrayList<List<Integer>>());
		index.add(new SpanIndex("0;01"));
		index.add(new SpanIndex("0;02"));
		index.add(new SpanIndex("0;03"));
		index.add(new SpanIndex("0;04"));
		
		// 0;01
		cube.get(0).add(new ArrayList<Integer>());
		cube.get(0).add(new ArrayList<Integer>());
		index.get(0).addLemmaIndex(new LemmaIndex("hebben"));
		index.get(0).addLemmaIndex(new LemmaIndex("doen"));
		
		cube.get(0).get(0).add(2);
		cube.get(0).get(0).add(3);
		cube.get(0).get(0).add(4);
		cube.get(0).get(1).add(1);
		cube.get(0).get(1).add(2);
		cube.get(0).get(1).add(1);
		index.get(0).getLemma(0).addCategory("inf");
		index.get(0).getLemma(0).addCategory("1ps");
		index.get(0).getLemma(0).addCategory("stem");
		index.get(0).getLemma(1).addCategory("inf");
		index.get(0).getLemma(1).addCategory("1ps");
		index.get(0).getLemma(1).addCategory("2pp");
		
		// 0;02
		cube.get(1).add(new ArrayList<Integer>());
		cube.get(1).add(new ArrayList<Integer>());
		cube.get(1).add(new ArrayList<Integer>());
		index.get(1).addLemmaIndex(new LemmaIndex("lopen"));
		index.get(1).addLemmaIndex(new LemmaIndex("hebben"));
		index.get(1).addLemmaIndex(new LemmaIndex("zijn"));
		
		cube.get(1).get(0).add(4);
		cube.get(1).get(0).add(3);
		cube.get(1).get(1).add(3);
		cube.get(1).get(1).add(2);
		cube.get(1).get(1).add(7);
		cube.get(1).get(1).add(6);
		cube.get(1).get(2).add(2);
		cube.get(1).get(2).add(5);
		index.get(1).getLemma(0).addCategory("inf");
		index.get(1).getLemma(0).addCategory("1pp");
		index.get(1).getLemma(1).addCategory("1pp");
		index.get(1).getLemma(1).addCategory("1ps");
		index.get(1).getLemma(1).addCategory("2pp");
		index.get(1).getLemma(1).addCategory("inf");
		index.get(1).getLemma(2).addCategory("inf");
		index.get(1).getLemma(2).addCategory("stem");
		
		// 0;03
		cube.get(2).add(new ArrayList<Integer>());
		cube.get(2).add(new ArrayList<Integer>());
		cube.get(2).add(new ArrayList<Integer>());
		cube.get(2).add(new ArrayList<Integer>());
		index.get(2).addLemmaIndex(new LemmaIndex("zijn"));
		index.get(2).addLemmaIndex(new LemmaIndex("doen"));
		index.get(2).addLemmaIndex(new LemmaIndex("lopen"));
		index.get(2).addLemmaIndex(new LemmaIndex("hebben"));
		
		cube.get(2).get(0).add(3);
		cube.get(2).get(0).add(5);
		cube.get(2).get(0).add(6);
		cube.get(2).get(1).add(1);
		cube.get(2).get(1).add(6);
		cube.get(2).get(1).add(8);
		cube.get(2).get(2).add(1);
		cube.get(2).get(2).add(3);
		cube.get(2).get(3).add(3);
		cube.get(2).get(3).add(4);
		cube.get(2).get(3).add(5);
		cube.get(2).get(3).add(2);
		index.get(2).getLemma(0).addCategory("1ps");
		index.get(2).getLemma(0).addCategory("2ps");
		index.get(2).getLemma(0).addCategory("1pp");
		index.get(2).getLemma(1).addCategory("1pp");
		index.get(2).getLemma(1).addCategory("1ps");
		index.get(2).getLemma(1).addCategory("inf");
		index.get(2).getLemma(2).addCategory("1ps");
		index.get(2).getLemma(2).addCategory("1pp");
		index.get(2).getLemma(3).addCategory("1ps");
		index.get(2).getLemma(3).addCategory("2ps");
		index.get(2).getLemma(3).addCategory("1pp");
		index.get(2).getLemma(3).addCategory("inf");
		
		// 0;03
		cube.get(3).add(new ArrayList<Integer>());
		cube.get(3).add(new ArrayList<Integer>());
		cube.get(3).add(new ArrayList<Integer>());
		cube.get(3).add(new ArrayList<Integer>());
		index.get(3).addLemmaIndex(new LemmaIndex("zijn"));
		index.get(3).addLemmaIndex(new LemmaIndex("doen"));
		index.get(3).addLemmaIndex(new LemmaIndex("lopen"));
		index.get(3).addLemmaIndex(new LemmaIndex("hebben"));
		
		cube.get(3).get(0).add(3);
		cube.get(3).get(0).add(5);
		cube.get(3).get(0).add(6);
		cube.get(3).get(1).add(1);
		cube.get(3).get(1).add(6);
		cube.get(3).get(1).add(8);
		cube.get(3).get(2).add(1);
		cube.get(3).get(2).add(3);
		cube.get(3).get(2).add(3);
		cube.get(3).get(3).add(4);
		cube.get(3).get(3).add(5);
		cube.get(3).get(3).add(2);
		index.get(3).getLemma(0).addCategory("1ps");
		index.get(3).getLemma(0).addCategory("2ps");
		index.get(3).getLemma(0).addCategory("1pp");
		index.get(3).getLemma(1).addCategory("1pp");
		index.get(3).getLemma(1).addCategory("1ps");
		index.get(3).getLemma(1).addCategory("inf");
		index.get(3).getLemma(2).addCategory("1ps");
		index.get(3).getLemma(2).addCategory("1pp");
		index.get(3).getLemma(2).addCategory("stem");
		index.get(3).getLemma(3).addCategory("2ps");
		index.get(3).getLemma(3).addCategory("1pp");
		index.get(3).getLemma(3).addCategory("inf");
		
		
		// Constructing the cube
		DataCubeList dc = new DataCubeList();
		dc.setCube(cube);
		dc.setSpanIndex(index);
		
		// Resample
		DataCubeList[] samples = dc.resample(20, 1, 10);
		
		for (DataCubeList sample : samples) {
			System.out.println("========================================");
			System.out.println(sample);
			System.out.println("========================================");
		}
	}
}
