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
		//evaluate2();
	}
	
	private static void evaluate() {
		/*String[] programs = new String[] {"tcas", "totinfo", 
				"schedule", "schedule2", "printtokens", "printtokens2"};*/
		String[] programs = new String[] {"tcas"};
		for (String program : programs) {
			for (Criteria criteria : Criteria.values()) {
				for (Method method : Method.values()) {
					String programFolder = BENCHMARKS_FOLDER + program + "/";
					List<String> origin = readFilesAsString(programFolder 
							+ "origin/" + criteria + "_" + method + "/");
					
					List<List<String>> versions = new ArrayList<List<String>>();
					int i = 1;
					String vFolder = programFolder + "v" + i + "/";
					while (Files.isDirectory(Paths.get(vFolder))) {
						versions.add(readFilesAsString(vFolder + criteria + "_" + method + "/"));
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
	
	private static void evaluate2() {
		String[] programs = new String[] {"tcas", "totinfo", 
				"schedule", "schedule2", "printtokens", "printtokens2"};
		for (String program : programs) {
			for (Criteria criteria : Criteria.values()) {
				for (Method method : Method.values()) {
					String programFolder = BENCHMARKS_FOLDER + program + "/";
					String origin = readFileAsString(programFolder + "origin/" + criteria + "-" + method + "-output.txt");
					
					List<String> versions = new ArrayList<String>();
					int i = 1;
					String vFolder = programFolder + "v" + i + "/";
					while (Files.exists(Paths.get(vFolder))) {
						versions.add(readFileAsString(vFolder + criteria + "-" + method + "-output.txt"));
						i++;
						vFolder = programFolder + "v" + i + "/";
					}
					
					int countDetected = 0;
					for (String version : versions) {						
						if (!origin.equalsIgnoreCase(version))
							countDetected++;
					}
					
					System.out.println("Program: " + program + "\t" + criteria + " - " + method + ":\t detected " + countDetected);
				}
			}
		}
	}

	private static String readFileAsString(String filePath) {
		String content = "";
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				content = content + line + "\n";
			}				

		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
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

	private static List<String> readFilesAsString(String folder) {
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
				"schedule", "schedule2", "printtokens", "printtokens2", "replace"};
		//String[] programs = new String[] {"totinfo"};
		
		for (String program : programs) {
			ProfileParser profileParser = new ProfileParser(BENCHMARKS_FOLDER, program);
			profileParser.parsing();
			
			TestCaseSelector selector = new TestCaseSelector(profileParser.getTestCases());

			
			//Method method = Method.ADDITIONAL;
			for (Criteria criteria : Criteria.values()) {
				for (Method method : Method.values()) {
					List<TestCase> selectedTestCases = new ArrayList<TestCase>();
					selectedTestCases = selector.select(criteria, method);
					
					try {
						outputTestCases(program, criteria, method, selectedTestCases);
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println("Program: " + program + "\t" + criteria + " - " + method + ":\t" + selectedTestCases.size());
				}
			}
		}
	}
	
	private static void outputTestCases(String program, Criteria criteria, Method method, 
			List<TestCase> selectedTestCases) throws IOException {
		Set<Integer> selectedLines = new HashSet<Integer>();
		for (TestCase selectedTestCase : selectedTestCases)
			selectedLines.add(selectedTestCase.getId());
		
		String programFolder = BENCHMARKS_FOLDER + program + "/";
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
