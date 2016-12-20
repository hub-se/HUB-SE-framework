package se.de.hu_berlin.informatik.utils.statistics;

import java.util.List;

public interface StatisticsContainer {

	public int getNumberOfStatistics();
	
	public List<Statistics> getStatistics();
	
	default public float getMean(int index) {
		
	}
	
	default public float getCount(int index) {
		
	}
	
	public String printStatistics();
	
}
