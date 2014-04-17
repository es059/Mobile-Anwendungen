package com.workout.log.data;

public class Default {

	private String mTitel = "";
	private String mHint = "";
	
	public  Default(String titel, String hint){
		setTitel(titel);
		setHint(hint);
		
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
