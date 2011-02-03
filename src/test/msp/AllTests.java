package test.msp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({DataCubeTest.class,DataCubeTestFrequency.class,CumulationAndResamplingTest.class,
	LemmaEquivalencesTest.class,CategoryEquivalencesTest.class})
public class AllTests {}
