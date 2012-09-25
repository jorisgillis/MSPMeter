package test.msp.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;
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
 * Testing the category equivalence function of the DataCubeList.
 * @author joris
 */
@RunWith(Parameterized.class)
public class CategoryEquivalencesTest {

	private DataCubeList dc, result;
	private boolean useInMSP;
	private HashMap<String, HashMap<String, String>> equivalences;
	private HashMap<String, String> lemmaEquivalences;

	/**
	 * Constructing a new test.
	 * @param dc	input cube
	 * @param result	output cube
	 * @param useInMSP	using in msp or not?
	 * @param equivalences	the equivalences
	 * @param lemmaEquivalences lemma equivalences
	 */
	public CategoryEquivalencesTest(DataCubeList dc, DataCubeList result,
									boolean useInMSP,
									HashMap<String, HashMap<String, String>> equivalences,
									HashMap<String, String> lemmaEquivalences) {
		this.dc = dc;
		this.result = result;
		this.useInMSP = useInMSP;
		this.equivalences = equivalences;
		this.lemmaEquivalences = lemmaEquivalences;
	}

	/**
	 * Testing the category equivalence function
	 */
	@Test
	public void testEquivalences() {
		Assert.assertEquals("Category equivalences",
				result,
				dc.categoryEquivalences(equivalences, useInMSP, lemmaEquivalences));
	}

	/**
	 * Loading the parameters.
	 * @return	list of test cases
	 */
	@Parameters
	public static Collection<Object[]> setup() {
		Collection<Object[]> cases = new LinkedList<Object[]>();

		//- 0.
		List<List<List<Integer>>> cube = new ArrayList<List<List<Integer>>>(2);
		List<SpanIndex> index = new ArrayList<SpanIndex>();

		cube.add(new ArrayList<List<Integer>>(2));
		cube.add(new ArrayList<List<Integer>>(2));
		index.add(new SpanIndex("0;01"));
		index.add(new SpanIndex("0;02"));

		cube.get(0).add(new ArrayList<Integer>());
		cube.get(0).add(new ArrayList<Integer>());
		index.get(0).addLemmaIndex(new LemmaIndex("doen"));
		index.get(0).addLemmaIndex(new LemmaIndex("hebben"));

		cube.get(1).add(new ArrayList<Integer>());
		cube.get(1).add(new ArrayList<Integer>());
		index.get(1).addLemmaIndex(new LemmaIndex("hebben"));
		index.get(1).addLemmaIndex(new LemmaIndex("zijn"));

		cube.get(0).get(0).add(2);
		cube.get(0).get(0).add(3);
		cube.get(0).get(0).add(2);
		index.get(0).getLemma(0).addCategory("1pp");
		index.get(0).getLemma(0).addCategory("inf");
		index.get(0).getLemma(0).addCategory("stem");

		cube.get(0).get(1).add(4);
		cube.get(0).get(1).add(2);
		index.get(0).getLemma(1).addCategory("2pp");
		index.get(0).getLemma(1).addCategory("1pp");

		cube.get(1).get(0).add(3);
		cube.get(1).get(0).add(4);
		cube.get(1).get(0).add(2);
		cube.get(1).get(0).add(2);
		index.get(1).getLemma(0).addCategory("inf");
		index.get(1).getLemma(0).addCategory("stem");
		index.get(1).getLemma(0).addCategory("1pp");
		index.get(1).getLemma(0).addCategory("2pp");

		cube.get(1).get(1).add(2);
		cube.get(1).get(1).add(3);
		index.get(1).getLemma(1).addCategory("1pp");
		index.get(1).getLemma(1).addCategory("stem");

		DataCubeList dc = new DataCubeList();
		dc.setCube(cube);
		dc.setSpanIndex(index);

		cube = new ArrayList<List<List<Integer>>>();
		index = new ArrayList<SpanIndex>();
		
		cube.add(new ArrayList<List<Integer>>());
		cube.add(new ArrayList<List<Integer>>());
		index.add(new SpanIndex("0;01"));
		index.add(new SpanIndex("0;02"));
		
		cube.get(0).add(new ArrayList<Integer>());
		cube.get(0).add(new ArrayList<Integer>());
		index.get(0).addLemmaIndex(new LemmaIndex("doen"));
		index.get(0).addLemmaIndex(new LemmaIndex("hebben"));
		
		cube.get(1).add(new ArrayList<Integer>());
		cube.get(1).add(new ArrayList<Integer>());
		index.get(1).addLemmaIndex(new LemmaIndex("hebben"));
		index.get(1).addLemmaIndex(new LemmaIndex("zijn"));
		
		cube.get(0).get(0).add(5);
		cube.get(0).get(0).add(2);
		index.get(0).getLemma(0).addCategory("inf");
		index.get(0).getLemma(0).addCategory("stem");
		
		cube.get(0).get(1).add(6);
		index.get(0).getLemma(1).addCategory("inf");
		
		cube.get(1).get(0).add(7);
		cube.get(1).get(0).add(4);
		index.get(1).getLemma(0).addCategory("inf");
		index.get(1).getLemma(0).addCategory("stem");
		
		cube.get(1).get(1).add(2);
		cube.get(1).get(1).add(3);
		index.get(1).getLemma(1).addCategory("1pp");
		index.get(1).getLemma(1).addCategory("stem");
		
		DataCubeList result = new DataCubeList();
		result.setCube(cube);
		result.setSpanIndex(index);
		
		HashMap<String, HashMap<String, String>> equivs = 
				new HashMap<String, HashMap<String, String>>();
		equivs.put("doen", new HashMap<String, String>());
		equivs.put("hebben", new HashMap<String, String>());
		equivs.put("zijn", new HashMap<String, String>());
		
		equivs.get("doen").put("1pp", "inf");
		equivs.get("hebben").put("1pp", "inf");
		equivs.get("hebben").put("2pp", "inf");
		
		HashMap<String, String> lemmaEquivs = new HashMap<String, String>();
		
		cases.add(new Object[]{dc, result, false, equivs, lemmaEquivs});
		
		
		//- 1.
		cube = new ArrayList<List<List<Integer>>>();
		index = new ArrayList<SpanIndex>();
		
		cube.add(new ArrayList<List<Integer>>());
		cube.add(new ArrayList<List<Integer>>());
		index.add(new SpanIndex("0;01"));
		index.add(new SpanIndex("0;02"));
		
		cube.get(0).add(new ArrayList<Integer>());
		cube.get(0).add(new ArrayList<Integer>());
		index.get(0).addLemmaIndex(new LemmaIndex("doen"));
		index.get(0).addLemmaIndex(new LemmaIndex("hebben"));
		
		cube.get(1).add(new ArrayList<Integer>());
		cube.get(1).add(new ArrayList<Integer>());
		index.get(1).addLemmaIndex(new LemmaIndex("hebben"));
		index.get(1).addLemmaIndex(new LemmaIndex("zijn"));
		
		cube.get(0).get(0).add(7);
		index.get(0).getLemma(0).addCategory("inf");
		
		cube.get(0).get(1).add(6);
		index.get(0).getLemma(1).addCategory("inf");
		
		cube.get(1).get(0).add(11);
		index.get(1).getLemma(0).addCategory("inf");
		
		cube.get(1).get(1).add(2);
		cube.get(1).get(1).add(3);
		index.get(1).getLemma(1).addCategory("1pp");
		index.get(1).getLemma(1).addCategory("stem");
		
		result = new DataCubeList();
		result.setCube(cube);
		result.setSpanIndex(index);
		
		equivs = new HashMap<String, HashMap<String, String>>();
		equivs.put("hebben", new HashMap<String, String>());
		equivs.get("hebben").put("1pp", "inf");
		equivs.get("hebben").put("2pp", "inf");
		equivs.get("hebben").put("stem", "inf");
		
		lemmaEquivs = new HashMap<String, String>();
		lemmaEquivs.put("doen", "hebben");
		
		cases.add(new Object[]{dc, result, false, equivs, lemmaEquivs});
		
		
		return cases;
	}
}
