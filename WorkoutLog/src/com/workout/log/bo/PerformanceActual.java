package com.workout.log.bo;

import java.util.Date;

public class PerformanceActual {
	
	private int mId;
	private int mRepetition;
	private int mSet;
	private double mWeight;
	private Exercise mExercise;
	private Date mTimestamp;
	
	
	public PerformanceActual(){
		
	}


	public int getId() {
		return mId;
	}


	public void setId(int mId) {
		this.mId = mId;
	}


	public int getRepetition() {
		return mRepetition;
	}


	public void setRepetition(int mRepetition) {
		this.mRepetition = mRepetition;
	}


	public int getSet() {
		return mSet;
	}


	public void setSet(int mSet) {
		this.mSet = mSet;
	}


	public double getWeight() {
		return mWeight;
	}


	public void setWeight(double mWeight) {
		this.mWeight = mWeight;
	}


	public Exercise getExercise() {
		return mExercise;
	}


	public void setExercise(Exercise mExercise) {
		this.mExercise = mExercise;
	}


	public Date getTimestamp() {
		return mTimestamp;
	}


	public void setTimestamp(Date mTimestamp) {
		this.mTimestamp = mTimestamp;
	}

	
}
