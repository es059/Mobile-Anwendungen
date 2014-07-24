package com.workout.log.bo;

import java.util.ArrayList;

import com.workout.log.data.ListItem;

public class Exercise implements ListItem {

	private String mName = "";
	private  int mId = 0;
	private MuscleGroup mMuscleGroup = null;
	private ArrayList<Integer> mTrainingDayIdList = null;
	private ArrayList<PerformanceTarget> mPerformanceTargetList = null;
	private ArrayList<PerformanceActual> mPerformanceActualList = null;
	
	public ArrayList<Integer> getTrainingDayIdList() {
		return mTrainingDayIdList;
	}

	public void setTrainingDayIdList(ArrayList<Integer> trainingDayIdList) {
		this.mTrainingDayIdList = trainingDayIdList;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public  int getId() {
		return mId;
	}

	public void setID(int Id) {
		mId = Id;
	}

	@Override
	public boolean isSection() {
		// TODO Auto-generated method stub
		return false;
	}

	public MuscleGroup getMuscleGroup() {
		return mMuscleGroup;
	}

	public void setMuscleGroup(MuscleGroup mMuscleGroup) {
		this.mMuscleGroup = mMuscleGroup;
	}

	public ArrayList<PerformanceTarget> getPerformanceTargetList() {
		return mPerformanceTargetList;
	}

	public void setPerformanceTargetList(ArrayList<PerformanceTarget> mPerformanceTargetList) {
		this.mPerformanceTargetList = mPerformanceTargetList;
	}

	public ArrayList<PerformanceActual> getPerformanceActualList() {
		return mPerformanceActualList;
	}

	public void setPerformanceActualList(ArrayList<PerformanceActual> mPerformanceActualList) {
		this.mPerformanceActualList = mPerformanceActualList;
	}
}
