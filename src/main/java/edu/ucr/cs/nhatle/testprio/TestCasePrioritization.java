package edu.ucr.cs.nhatle.testprio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Nhat Le
 *
 */
public class TestCasePrioritization {

	private static final String BENCHMARKS_FOLDER = "src/main/resources/benchmarks/"; 
	private static String benchmarksFolder = BENCHMARKS_FOLDER;
	
	public enum Criteria {STATEMENT, BRANCH, COMBINATION};
	public enum Method {RANDOM, TOTAL, ADDITIONAL};
	
	public static void main(String[] args) {	
		if (args.length < 1) {
			System.out.println("Please provide the directory of benchmarks");
			System.exit(0);
		} else {
			benchmarksFolder = args[0];
		}			
		
		prioritizeTestCases();
	}
	
	private static void prioritizeTestCases() {
		String[] programs = new String[] {"tcas", "totinfo", 
				"schedule", "schedule2", "printtokens", "printtokens2", "replace"};
//		String[] programs = new String[] {"schedule2"};
		
		for (String program : programs) {
			ProfileParser profileParser = new ProfileParser(benchmarksFolder, program);
			profileParser.parsing();
			
			TestCaseSelector selector = new TestCaseSelector(profileParser.getTestCases());

			for (Criteria criteria : Criteria.values()) {
				for (Method method : Method.values()) {
		/*	for (Criteria criteria : new Criteria[]{Criteria.STATEMENT}) {
				for (Method method : new Method[]{Method.TOTAL}) {*/
					List<TestCase> selectedTestCases = new ArrayList<TestCase>();
					selectedTestCases = selector.select(criteria, method);
										
					try {
						outputTestCases(program, criteria, method, selectedTestCases);
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println("Program: " + program + " \t" + criteria + " \t" + method + ": \t" + selectedTestCases.size());
					System.out.print("   Test cases: ");
					for (TestCase testcase : selectedTestCases)
						System.out.print((testcase.getId() + 1) + ", ");
					System.out.println();
				}
			}
		}
	}
	
	private static void outputTestCases(String program, Criteria criteria, Method method, 
			List<TestCase> selectedTestCases) throws IOException {
		Set<Integer> selectedLines = new HashSet<Integer>();
		for (TestCase selectedTestCase : selectedTestCases)
			selectedLines.add(selectedTestCase.getId());
		
		String programFolder = benchmarksFolder + program + "/";
		List<String> testSuite = new ArrayList<String>();		
		
		BufferedReader reader = Files.newBufferedReader(Paths.get(programFolder + "universe.txt"));
		int lineNum = 0;
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (selectedLines.contains(lineNum))
				testSuite.add(line);
			
			lineNum++;
		}
		
		
		String testsuiteFolder = programFolder + "testsuite/";
		if (!Files.isDirectory(Paths.get(testsuiteFolder))) {
			Files.createDirectory(Paths.get(testsuiteFolder));
		}
		BufferedWriter writer = Files.newBufferedWriter(
				Paths.get(testsuiteFolder + criteria + "_" + method + ".txt"), 
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING); 
		for (String testCase : testSuite) {
			writer.write(testCase);
			writer.newLine();
		}		
		writer.flush();
	}
}
