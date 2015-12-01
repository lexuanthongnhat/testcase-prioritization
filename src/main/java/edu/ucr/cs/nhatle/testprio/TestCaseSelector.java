package edu.ucr.cs.nhatle.testprio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ucr.cs.nhatle.testprio.TestCasePrioritization.Criteria;
import edu.ucr.cs.nhatle.testprio.TestCasePrioritization.Method;

public class TestCaseSelector {
	List<TestCase> testCases;
	
	public TestCaseSelector(List<TestCase> testCases) {
		this.testCases = testCases;
	}

	public List<TestCase> select(Criteria criteria, Method method) {
		List<TestCase> selectedTestCases = new ArrayList<TestCase>();
		switch (method) {
			case RANDOM: 
				selectedTestCases = random(criteria);
				break;
			case TOTAL:
				selectedTestCases = total(criteria);
				break;
			case ADDITIONAL:
				selectedTestCases = additional(criteria);
				break;
		}
		return selectedTestCases;
	}

	private List<TestCase> random(Criteria criteria) {
		List<TestCase> selectedTestCases = new ArrayList<TestCase>();
		
		List<TestCase> copyTestCases = new ArrayList<TestCase>(testCases);
		Collections.shuffle(copyTestCases);
		
		selectedTestCases = covering(criteria, copyTestCases);
		
		return selectedTestCases;
	}
	
	private List<TestCase> total(Criteria criteria) {
		List<TestCase> selectedTestCases = new ArrayList<TestCase>();
				
		List<TestCase> orderedTestCases = new ArrayList<TestCase>(testCases);
		
		Collections.sort(orderedTestCases, new Comparator<TestCase>(){
			@Override
			public int compare(TestCase o1, TestCase o2) {
				return o2.getCoverages(criteria).size() - o1.getCoverages(criteria).size();
			}				
		});
		
		selectedTestCases = covering(criteria, orderedTestCases);
		
		return selectedTestCases;
	}
	
	private List<TestCase> covering(Criteria criteria, List<TestCase> testCaseCandidates) {
	
		List<TestCase> selectedTestCases = new ArrayList<TestCase>();
		Set<Integer> covereds = new HashSet<Integer>();
		
		Set<Integer> allPossibleCoverages =  new HashSet<Integer>();
		for (TestCase testCase : testCaseCandidates) 
			allPossibleCoverages.addAll(testCase.getCoverages(criteria));
		int numToCover = allPossibleCoverages.size();
		allPossibleCoverages = null;
	
		int numCovered = covereds.size(); 
		for (int i = 0; numCovered < numToCover; ++i) {
			TestCase selected = testCaseCandidates.get(i);			
			covereds.addAll(selected.getCoverages(criteria));
			
			if (covereds.size() > numCovered) {			
				selectedTestCases.add(selected);
				numCovered = covereds.size(); 
			}			
		}
		
		return selectedTestCases;
	}

	private List<TestCase> additional(Criteria criteria) {
		List<TestCase> selectedTestCases = new ArrayList<TestCase>();
		
		Set<Integer> covereds = new HashSet<Integer>();
		int numToCover = 0;
		
		Set<Integer> allPossibleCoverages =  new HashSet<Integer>();
		for (TestCase testCase : testCases) 
			allPossibleCoverages.addAll(testCase.getCoverages(criteria));
		numToCover = allPossibleCoverages.size();
		allPossibleCoverages = null;
			
		// Get the first test case
		List<TestCase> orderedTestCases = new ArrayList<TestCase>(testCases);
		Collections.sort(orderedTestCases, new Comparator<TestCase>(){
			@Override
			public int compare(TestCase o1, TestCase o2) {
				return o2.getCoverages(criteria).size() - o1.getCoverages(criteria).size();
			}				
		});
		
		TestCase nextTestCase = orderedTestCases.get(0);
		selectedTestCases.add(nextTestCase);
		covereds.addAll(nextTestCase.getCoverages(criteria));
		orderedTestCases.remove(0);
		
		// Start to get the additional test cases		
		while (covereds.size() < numToCover) {
			Collections.sort(orderedTestCases, new Comparator<TestCase>(){
				@Override
				public int compare(TestCase o1, TestCase o2) {
					int numUncoveredO1 = 0;
					int numUncoveredO2 = 0;
					for (Integer coverage : o1.getCoverages(criteria))
						if (!covereds.contains(coverage))
							numUncoveredO1++;
					for (Integer statement : o2.getCoverages(criteria))
						if (!covereds.contains(statement))
							numUncoveredO2++;
					
					return numUncoveredO2 - numUncoveredO1;
				}				
			});	
			
			nextTestCase = orderedTestCases.get(0);
			selectedTestCases.add(nextTestCase);
			covereds.addAll(nextTestCase.getCoverages(criteria));
			orderedTestCases.remove(0);
		}			
				
		return selectedTestCases;
	}
}
