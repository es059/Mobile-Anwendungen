package com.workout.log.data;

public class Default implements ManageWorkoutplanListItem {

	private String mTitel = "";
	private String mHint = "";
	
	public  Default(){
	}

	public String getTitel() {
		return mTitel;
	}

	public void setTitel(String mTitel) {
		this.mTitel = mTitel;
	}

	public String getHint() {
		return mHint;
	}

	public void setHint(String mHint) {
		this.mHint = mHint;
	}
}
