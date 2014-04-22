package com.workout.log.bo;

public class TrainingDay {

	private String mName = "";
	private  int mId = 0;
	
	public TrainingDay(){
		
	}
	
	public  TrainingDay(String name){
		setName(name);
		setID(getID() + 1);
	}

	public String getName() {
		return mName;
	}

	public void setName(String Name) {
		this.mName = Name;
	}

	public  int getID() {
		return mId;
	}

	public void setID(int Id) {
		mId = Id;
	}
}
