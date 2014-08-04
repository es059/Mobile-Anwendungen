package com.workout.log.data;

import java.util.ArrayList;

import com.workout.log.bo.PerformanceActual;

public class StatisticListElement {
	
	
private String timestamp; 
private ArrayList<PerformanceActual> performanceActualList;
public String getTimestamp() {
	return timestamp;
}
public void setTimestamp(String timestamp) {
	this.timestamp = timestamp;
}
public ArrayList<PerformanceActual> getPerformanceActualList() {
	return performanceActualList;
}
public void setPerformanceActualList(
		ArrayList<PerformanceActual> performanceActualList) {
	this.performanceActualList = performanceActualList;
}


}
