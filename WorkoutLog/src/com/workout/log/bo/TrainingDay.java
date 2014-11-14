package com.workout.log.bo;

import java.util.ArrayList;

import com.workout.log.data.ManageWorkoutplanListItem;

public class TrainingDay implements ManageWorkoutplanListItem {

	private String mName = "";
	private int mId = 0;
	
	/**
	 * Needed to Undo removal 
	 */
	private ArrayList<Workoutplan> mWorkoutplanList = null;
	private ArrayList<PerformanceTarget> mPerformanceTargetList = null;
	private ArrayList<Exercise> mExerciseList = null;
	
	public  ArrayList<Workoutplan> getWorkoutplanList() {
		return mWorkoutplanList;
	}

	public void setWorkoutplanList( ArrayList<Workoutplan> workoutplanList) {
		this.mWorkoutplanList = workoutplanList;
	}

	public String getName() {
		return mName;
	}

	public void setName(String Name) {
		this.mName = Name;
	}

	public  int getId() {
		return mId;
	}

	public void setId(int Id) {
		mId = Id;
	}

	public ArrayList<PerformanceTarget> getPerformanceTargetList() {
		return mPerformanceTargetList;
	}

	public void setPerformanceTargetList(ArrayList<PerformanceTarget> performanceTargetList) {
		this.mPerformanceTargetList = performanceTargetList;
	}

	public ArrayList<Exercise> getExerciseList() {
		return mExerciseList;
	}

	public void setExerciseList(ArrayList<Exercise> mExerciseList) {
		this.mExerciseList = mExerciseList;
	}
	
}

