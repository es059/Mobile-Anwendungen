package com.workout.log.bo;

/*
 * Class to manage the target performances
 * 
 * @author Eric Schmidt
 */

public class PerformanceTarget {
	private int mId;
	private int mRepetition;
	private int mSet;
	private Exercise mExercise;
	private int mTrainingDayId;
	
	public PerformanceTarget(){
		
	}
	
	public Exercise getExercise() {
		return mExercise;
	}

	public void setExercise(Exercise exercise) {
		this.mExercise = exercise;
	}

	public int getSet() {
		return mSet;
	}

	public void setSet(int mSet) {
		this.mSet = mSet;
	}

	public int getRepetition() {
		return mRepetition;
	}

	public void setRepetition(int mRepetition) {
		this.mRepetition = mRepetition;
	}

	public int getTrainingDayId() {
		return mTrainingDayId;
	}

	public void setTrainingDayId(int mTrainingDayId) {
		this.mTrainingDayId = mTrainingDayId;
	}

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}
}
