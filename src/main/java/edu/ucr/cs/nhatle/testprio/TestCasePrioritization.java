package edu.ucr.cs.nhatle.testprio;

import java.util.ArrayList;
import java.util.List;

public class TestCasePrioritization {

	public static final String BENCHMARKS_FOLDER = "src/main/resources/benchmarks/"; 
	
	public enum Criteria {STATEMENT, BRANCH};
	public enum Method {RANDOM, TOTAL, ADDITIONAL};
	
	public static void main(String[] args) {
		
		parsingProgramProfile();		
	}
	
	private static void parsingProgramProfile() {
		String[] programs = new String[] {"tcas", "totinfo", 
				"schedule", "schedule2", "printtokens", "printtokens2"};
		
		for (String program : programs) {
			ProfileParser profileParser = new ProfileParser(BENCHMARKS_FOLDER, program);
			profileParser.parsing();
			
			TestCaseSelector selector = new TestCaseSelector(profileParser.getTestCases());

			
			Method method = Method.ADDITIONAL;
			/*for (Criteria criteria : Criteria.values())
				for (Method method : Method.values())*/
			for (Criteria criteria : Criteria.values()) {
					List<TestCase> selectedTestCases = new ArrayList<TestCase>();
					selectedTestCases = selector.select(criteria, method);					
					System.out.println(selectedTestCases.size());
			}

		}
	}
}
