package edu.ucr.cs.nhatle.testprio;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.ucr.cs.nhatle.testprio.TestCasePrioritization.Criteria;

public class TestCase {
	private int id;
	private Map<Criteria, Integer> criteriaToNumMaxCoverages = new HashMap<Criteria, Integer>();
	private Map<Criteria, Set<Integer>> criteriaToCoverages = new HashMap<Criteria, Set<Integer>>();
		
	public TestCase(int id) {
		super();
		this.id = id;		
	}
			
	public int getId() {
		return id;
	}
		
	public Map<Criteria, Integer> getCriteriaToNumMaxCoverages() {
		return criteriaToNumMaxCoverages;
	}

	public Map<Criteria, Set<Integer>> getCriteriaToCoverages() {
		return criteriaToCoverages;
	}
	
	public void addNumMaxCoverage(Criteria criteria, int num) {
		criteriaToNumMaxCoverages.put(criteria, num);
	}
	
	public void addCoverages(Criteria criteria, Set<Integer> coverages) {
		criteriaToCoverages.put(criteria, coverages);
	}
	
	public Set<Integer> getCoverages(Criteria criteria) {
		return criteriaToCoverages.get(criteria);
	}

	public int getNumMaxCoverages(Criteria criteria) {
		return criteriaToNumMaxCoverages.get(criteria);
	}
	
	public void updateCombinedCoverages() {
		criteriaToNumMaxCoverages.put(Criteria.COMBINATION, 
				criteriaToNumMaxCoverages.get(Criteria.BRANCH) + criteriaToNumMaxCoverages.get(Criteria.STATEMENT));
		
		Set<Integer> combination = new HashSet<Integer>(criteriaToCoverages.get(Criteria.STATEMENT));
		for (Integer branch : criteriaToCoverages.get(Criteria.BRANCH))
			combination.add(-branch);
		
		criteriaToCoverages.put(Criteria.COMBINATION, combination);
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
		
}
