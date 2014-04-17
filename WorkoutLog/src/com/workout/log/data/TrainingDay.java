package com.workout.log.data;

public class TrainingDay {

	private String mName = "";
	private  int mID = 0;
	
	public TrainingDay(){
		
	}
	
	public  TrainingDay(String name){
		setName(name);
		setID(getID() + 1);
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public  int getID() {
		return mID;
	}

	public void setID(int mID) {
		mID = mID;
	}
}
