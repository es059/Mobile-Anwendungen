package com.workout.log.bo;


/*
 * Class to manage muscle groups 
 * 
 * @author Eric Schmidt
 */
public class MuscleGroup {
	private String mName = "";
	private int mID;
	
	public MuscleGroup(){
		
	}
	
	public String getName() {
		return mName;
	}
	public void setName(String mName) {
		this.mName = mName;
	}
	public int getID() {
		return mID;
	}
	public void setID(int mID) {
		this.mID = mID;
	}
	
}
