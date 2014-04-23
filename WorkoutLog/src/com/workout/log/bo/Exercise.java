package com.workout.log.bo;

import com.workout.log.data.ExerciseItem;

public class Exercise implements ExerciseItem {

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

	@Override
	public boolean isSection() {
		// TODO Auto-generated method stub
		return false;
	}
}
