package edu.ucr.cs.nhatle.testprio;

import java.util.HashSet;
import java.util.Set;

public class TestCase {
	private int id;
	private int numStatements;
	private int numBranches;
	private Set<Integer> statements = new HashSet<Integer>();
	private Set<Integer> branches = new HashSet<Integer>();
		
	public TestCase(int id) {
		super();
		this.id = id;		
	}
	public TestCase(int id, Set<Integer> statements, Set<Integer> branches) {
		super();
		this.id = id;
		this.statements = statements;
		this.branches = branches;
	}
	
	public void addStatement(int num) {
		statements.add(num);
	}
	
	public void addBranch(int num) {
		branches.add(num);
	}
	
	public int getId() {
		return id;
	}
	public Set<Integer> getStatements() {
		return statements;
	}
	public Set<Integer> getBranches() {
		return branches;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestCase other = (TestCase) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		return "TestCase [id=" + id + ", numStatements=" + numStatements + ", numBranches=" + numBranches + "]";
	}
	public int getNumStatements() {
		return numStatements;
	}
	public void setNumStatements(int numStatements) {
		this.numStatements = numStatements;
	}
	public int getNumBranches() {
		return numBranches;
	}
	public void setNumBranches(int numBranches) {
		this.numBranches = numBranches;
	}
	
	
}
