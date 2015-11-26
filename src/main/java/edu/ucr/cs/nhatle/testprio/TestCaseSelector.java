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
		
		if (criteria == Criteria.STATEMENT) {
			Set<Integer> coveredStatements = new HashSet<Integer>();			
			
			// Get the first test case
			List<TestCase> statementOrderTestCases = new ArrayList<TestCase>(testCases);
			Collections.sort(statementOrderTestCases, new Comparator<TestCase>(){
				@Override
				public int compare(TestCase o1, TestCase o2) {
					return o2.getStatements().size() - o1.getStatements().size();
				}				
			});
			
			TestCase nextTestCase = statementOrderTestCases.get(0);
			selectedTestCases.add(nextTestCase);
			coveredStatements.addAll(nextTestCase.getStatements());
			statementOrderTestCases.remove(0);
			
			// Start to get the additional test cases			
			Set<Integer> allPossibleStatements =  new HashSet<Integer>(coveredStatements);
			for (TestCase testCase : statementOrderTestCases) 
				allPossibleStatements.addAll(testCase.getStatements());
			int numStatements = allPossibleStatements.size();
			allPossibleStatements = null;
			
			while (coveredStatements.size() < numStatements) {
				Collections.sort(statementOrderTestCases, new Comparator<TestCase>(){
					@Override
					public int compare(TestCase o1, TestCase o2) {
						int numUncoveredO1 = 0;
						int numUncoveredO2 = 0;
						for (Integer statement : o1.getStatements())
							if (!coveredStatements.contains(statement))
								numUncoveredO1++;
						for (Integer statement : o2.getStatements())
							if (!coveredStatements.contains(statement))
								numUncoveredO2++;
						
						return numUncoveredO2 - numUncoveredO1;
					}				
				});	
				
				nextTestCase = statementOrderTestCases.get(0);
				selectedTestCases.add(nextTestCase);
				coveredStatements.addAll(nextTestCase.getStatements());
				statementOrderTestCases.remove(0);
			}			
		} else if (criteria == Criteria.BRANCH) {
			Set<Integer> coveredBranches = new HashSet<Integer>();			
			
			// Get the first test case
			List<TestCase> branchOrderTestCases = new ArrayList<TestCase>(testCases);
			Collections.sort(branchOrderTestCases, new Comparator<TestCase>(){
				@Override
				public int compare(TestCase o1, TestCase o2) {
					return o2.getBranches().size() - o1.getBranches().size();
				}				
			});
			TestCase nextTestCase = branchOrderTestCases.get(0);
			selectedTestCases.add(nextTestCase);
			coveredBranches.addAll(nextTestCase.getBranches());
			branchOrderTestCases.remove(0);
			
			// Start to get the additional test cases			
			Set<Integer> allPossibleBranches =  new HashSet<Integer>(coveredBranches);
			for (TestCase testCase : branchOrderTestCases) 
				allPossibleBranches.addAll(testCase.getBranches());
			int numBranches = allPossibleBranches.size();
			allPossibleBranches = null;
			
			while (coveredBranches.size() < numBranches) {
				Collections.sort(branchOrderTestCases, new Comparator<TestCase>(){
					@Override
					public int compare(TestCase o1, TestCase o2) {
						int numUncoveredO1 = 0;
						int numUncoveredO2 = 0;
						for (Integer branch : o1.getBranches())
							if (!coveredBranches.contains(branch))
								numUncoveredO1++;
						for (Integer branch : o2.getBranches())
							if (!coveredBranches.contains(branch))
								numUncoveredO2++;
						
						return numUncoveredO2 - numUncoveredO1;
					}				
				});	
				
				nextTestCase = branchOrderTestCases.get(0);
				selectedTestCases.add(nextTestCase);
				coveredBranches.addAll(nextTestCase.getBranches());
				branchOrderTestCases.remove(0);
			}			
		}
		
		return selectedTestCases;
	}
}
