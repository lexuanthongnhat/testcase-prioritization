package edu.ucr.cs.nhatle.testprio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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

	public List<TestCase> random(Criteria criteria) {
		List<TestCase> selectedTestCases = new ArrayList<TestCase>();
		
		List<TestCase> copyTestCases = new ArrayList<TestCase>(testCases);
		Collections.shuffle(copyTestCases);
		
		if (criteria == Criteria.STATEMENT)
			selectedTestCases = coveringStatement(copyTestCases);
		else if (criteria == Criteria.BRANCH)
			selectedTestCases = coveringBranch(copyTestCases);
		
		return selectedTestCases;
	}
	
	private List<TestCase> coveringStatement(List<TestCase> testCaseCandidates) {

		List<TestCase> selectedTestCases = new ArrayList<TestCase>();
		Set<Integer> coveredStatements = new HashSet<Integer>();
		
		Set<Integer> allPossibleStatements =  new HashSet<Integer>();
		for (TestCase testCase : testCaseCandidates) 
			allPossibleStatements.addAll(testCase.getStatements());
		int numStatements = allPossibleStatements.size();
		allPossibleStatements = null;

		for (int i = 0; coveredStatements.size() < numStatements; ++i) {
			TestCase selected = testCaseCandidates.get(i);
			selectedTestCases.add(selected);
			coveredStatements.addAll(selected.getStatements());
		}
		
		return selectedTestCases;
	}
	
	private List<TestCase> coveringBranch(List<TestCase> testCaseCandidates) {
		List<TestCase> selectedTestCases = new ArrayList<TestCase>();
		Set<Integer> coveredBranches = new HashSet<Integer>();
		
		Set<Integer> allPossibleBranches = new HashSet<Integer>();
		for (TestCase testCase : testCaseCandidates) 
			allPossibleBranches.addAll(testCase.getBranches());
		int numBranches = allPossibleBranches.size();
		allPossibleBranches = null;
		
		for (int i = 0; coveredBranches.size() < numBranches; ++i) {
			TestCase selected = testCaseCandidates.get(i);
			selectedTestCases.add(selected);
			coveredBranches.addAll(selected.getBranches());
		}
		
		return selectedTestCases;
	}
	
	private List<TestCase> total(Criteria criteria) {
		List<TestCase> selectedTestCases = new ArrayList<TestCase>();
				
		if (criteria == Criteria.STATEMENT) {
			List<TestCase> statementOrderTestCases = new ArrayList<TestCase>(testCases);
			Collections.sort(statementOrderTestCases, new Comparator<TestCase>(){
				@Override
				public int compare(TestCase o1, TestCase o2) {
					return o2.getStatements().size() - o1.getStatements().size();
				}				
			});
			
			selectedTestCases = coveringStatement(statementOrderTestCases);
		}
		else if (criteria == Criteria.BRANCH) {
			List<TestCase> branchOrderTestCases = new ArrayList<TestCase>(testCases);
			Collections.sort(branchOrderTestCases, new Comparator<TestCase>(){
				@Override
				public int compare(TestCase o1, TestCase o2) {
					return o2.getBranches().size() - o1.getBranches().size();
				}				
			});
			
			selectedTestCases = coveringBranch(branchOrderTestCases);
		}
		
		return selectedTestCases;
	}
	
	private List<TestCase> additional(Criteria criteria) {
		List<TestCase> selectedTestCases = new ArrayList<TestCase>();
		
		return selectedTestCases;
	}
}
