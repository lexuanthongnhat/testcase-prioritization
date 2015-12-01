package edu.ucr.cs.nhatle.testprio;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ucr.cs.nhatle.testprio.TestCasePrioritization.Criteria;

public class ProfileParser {
	private final String STATEMENT_EXP = "^\\s+(#####|[0-9]+):\\s+[0-9]+:.*";
	private final String STATEMENT_EXP_EXECUTED = "^\\s+[0-9]+:\\s+[0-9]+:.*";
	private final String BRANCH_EXP = "^branch\\s+[0-9]+.*";
	private final String BRANCH_EXP_0 = "^branch\\s+[0-9]+\\s+taken\\s+0+.*";
	private final String BRANCH_EXP_TAKEN = "^branch\\s+[0-9]+\\s+taken\\s+[0-9]+.*";
	
	private String benchmarksFolder;
	private String program;
	private List<TestCase> testCases = new ArrayList<TestCase>();
	
	public ProfileParser(String benchmarksFolder, String program) {
		this.benchmarksFolder = benchmarksFolder;
		this.program = program;
	}

	public void parsing() {
		
		List<String> profileFiles = new ArrayList<String>();		
		int count = 0;
		String profileFile = benchmarksFolder + program + "/profile/" + count + "/" + program + ".c.gcov";
		do {
			profileFiles.add(profileFile);			
			++count;
			profileFile = benchmarksFolder + program + "/profile/" + count + "/" + program + ".c.gcov";
		} while (Files.exists(Paths.get(profileFile)));
		
		for (int i = 0; i < profileFiles.size(); ++i) {
			testCases.add(parsing(i, profileFiles.get(i)));
		}
		
		System.out.println("Program: \"" + program + "\" - " + testCases.size() + " test cases");
	}
	
	private TestCase parsing(int testCaseId, String profileFile) {
		TestCase testCase = new TestCase(testCaseId);
		int lineNum = 1;
		int numStatements = 0;
		int numBranches = 0;
		Set<Integer> statements = new HashSet<Integer>();
		Set<Integer> branches = new HashSet<Integer>();
			
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(profileFile))) {

			String line = null;
			while ((line = reader.readLine()) != null) {

				
				if (line.matches(STATEMENT_EXP)) {
					++numStatements;
				}
				
				if (line.matches(STATEMENT_EXP_EXECUTED)) {
					int codeLine = Integer.parseInt(line.split("\\s*:\\s*")[1]);					
					statements.add(codeLine);
				}					
				
				if (line.matches(BRANCH_EXP)) {
					numBranches++;
				}
					
				if (line.matches(BRANCH_EXP_TAKEN) && !line.matches(BRANCH_EXP_0)) {
					branches.add(lineNum);
				}
				
				++lineNum;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		testCase.addNumMaxCoverage(Criteria.STATEMENT, numStatements);
		testCase.addNumMaxCoverage(Criteria.BRANCH, numBranches);
		testCase.addCoverages(Criteria.STATEMENT, statements);
		testCase.addCoverages(Criteria.BRANCH, branches);
		testCase.updateCombinedCoverages();

		
		return testCase;
	}

	public List<TestCase> getTestCases() {
		return testCases;
	}	
}
