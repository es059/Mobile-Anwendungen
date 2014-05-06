package com.workout.log.bo;

public class TrainingDay {

	private String mName = "";
	private  int mId = 0;
	private int trainingDayHasWorkoutplanId = 0;
	
	public int getTrainingDayHasWorkoutplanId() {
		return trainingDayHasWorkoutplanId;
	}

	public void setTrainingDayHasWorkoutplanId(int trainingDayHasWorkoutplanId) {
		this.trainingDayHasWorkoutplanId = trainingDayHasWorkoutplanId;
	}

	public TrainingDay(){
		
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

	
		
	}

