package com.workout.log.bo;

public class TrainingDay {

	private String mName = "";
	private  int mId = 0;
	
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
