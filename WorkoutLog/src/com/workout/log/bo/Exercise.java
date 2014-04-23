package com.workout.log.bo;

public class Exercise {

	private String mName = "";
	private  int mId = 0;
	
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
