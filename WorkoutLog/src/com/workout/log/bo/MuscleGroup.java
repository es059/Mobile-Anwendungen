package com.workout.log.bo;


/*
 * Class to manage muscle groups 
 * 
 * @author Eric Schmidt
 */
public class MuscleGroup {
	private String mName = "";
	private int mId;
	
	public MuscleGroup(){
		
	}
	
	public String getName() {
		return mName;
	}
	public void setName(String mName) {
		this.mName = mName;
	}
	public int getId() {
		return mId;
	}
	public void setId(int Id) {
		this.mId = Id;
	}
	
}
