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

public class TestCasePrioritization {

	public static final String BENCHMARKS_FOLDER = "src/main/resources/benchmarks/"; 
	
	public enum Criteria {STATEMENT, BRANCH, COMBINATION};
	public enum Method {RANDOM, TOTAL, ADDITIONAL};
	
	public static void main(String[] args) {
		
		parsingProgramProfile();
		evaluate();
	}
	
	private static void evaluate() {
		/*String[] programs = new String[] {"tcas", "totinfo", 
				"schedule", "schedule2", "printtokens", "printtokens2"};*/
		String[] programs = new String[] {"tcas"};
		for (String program : programs) {
			for (Criteria criteria : Criteria.values()) {
				for (Method method : Method.values()) {
					String programFolder = BENCHMARKS_FOLDER + program + "/";
					List<String> origin = readFileAsString(programFolder 
							+ "origin/" + criteria + "_" + method + "/");
					
					List<List<String>> versions = new ArrayList<List<String>>();
					int i = 1;
					String vFolder = programFolder + "v" + i + "/";
					while (Files.isDirectory(Paths.get(vFolder))) {
						versions.add(readFileAsString(vFolder + criteria + "_" + method + "/"));
						i++;
						vFolder = programFolder + "v" + i + "/";
					}
					
					int countDetected = 0;
					for (List<String> version : versions) {						
						if (isDifferent(origin, version))
							countDetected++;
					}
					
					System.out.println("Program: " + program + "\t" + criteria + " - " + method + ":\t detected " + countDetected);
				}
			}
		}
	}	

	private static boolean isDifferent(List<String> origin, List<String> version) {
		boolean fault = false;
		for (int i = 0; i < origin.size(); ++i) {
			if (!origin.get(i).equalsIgnoreCase(version.get(i))) {
				fault = true;
				break;
			}
		}
		
		return fault;
	}

	private static List<String> readFileAsString(String folder) {
		List<String> result = new ArrayList<String>();
		int i = 0;
		String filePath = folder + i + ".txt";
		while (Files.exists(Paths.get(filePath))) {
			try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
				String content = "";
				String line = null;
				while ((line = reader.readLine()) != null) {
					content = content + line + "\n";
				}				
				result.add(content);
			} catch (IOException e) {
				e.printStackTrace();
			}
			i++;
			filePath = folder + i + ".txt";
		}
		return result;
	}

	private static void parsingProgramProfile() {
		String[] programs = new String[] {"tcas", "totinfo", 
				"schedule", "schedule2", "printtokens", "printtokens2"};
		
		for (String program : programs) {
			ProfileParser profileParser = new ProfileParser(BENCHMARKS_FOLDER, program);
			profileParser.parsing();
			
			TestCaseSelector selector = new TestCaseSelector(profileParser.getTestCases());

			
			//Method method = Method.ADDITIONAL;
			for (Criteria criteria : Criteria.values()) {
				for (Method method : Method.values()) {
					List<TestCase> selectedTestCases = new ArrayList<TestCase>();
					selectedTestCases = selector.select(criteria, method);					
					outputTestCases(program, criteria, method, selectedTestCases);
					System.out.println("Program: " + program + "\t" + criteria + " - " + method + ":\t" + selectedTestCases.size());
				}
			}

		}
	}
	
	private static void outputTestCases(String program, Criteria criteria, Method method, List<TestCase> selectedTestCases) {
		Set<Integer> selectedLines = new HashSet<Integer>();
		for (TestCase selectedTestCase : selectedTestCases)
			selectedLines.add(selectedTestCase.getId());
		
		String programFolder = BENCHMARKS_FOLDER + program + "/";
		List<String> testSuite = new ArrayList<String>();		
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(programFolder + "universe.txt"))) {
			int lineNum = 0;
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (selectedLines.contains(lineNum))
					testSuite.add(line);
				
				lineNum++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(programFolder + criteria + "_" + method + ".txt"), 
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			for (String testCase : testSuite) {
				writer.write(testCase);
				writer.newLine();
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
