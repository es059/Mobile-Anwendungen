package com.workout.log.bo;

public class Exercise {

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
}
