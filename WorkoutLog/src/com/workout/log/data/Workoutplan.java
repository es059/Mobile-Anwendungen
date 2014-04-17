package com.workout.log.data;

import java.util.*;

public class Workoutplan {

	private int mID;
	private String name;
	private Date timeStamp;
	
	
	public int getID() {
		return mID;
	}
	public void setID(int id) {
		this.mID = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date workoutplanTimeStamp) {
		this.timeStamp = workoutplanTimeStamp;
	}
	
	
	
}
