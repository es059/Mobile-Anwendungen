package com.workout.log.bo;

import com.workout.log.data.ExerciseItem;

public class Exercise implements ExerciseItem {

	private String mName = "";
	private  int mId = 0;
	private int trainingDayHasExerciseId = 0;
	
	public int getTrainingDayHasExerciseId() {
		return trainingDayHasExerciseId;
	}

	public void setTrainingDayHasExerciseId(int trainingDayHasExerciseId) {
		this.trainingDayHasExerciseId = trainingDayHasExerciseId;
	}

	public Exercise(){
		
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
}
