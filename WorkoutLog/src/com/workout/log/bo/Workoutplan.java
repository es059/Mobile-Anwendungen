package com.workout.log.bo;

import java.util.Date;

public class Workoutplan {

	private int mId;
	private String mName;
	private Date timeStamp;
	
	
	public int getId() {
		return mId;
	}
	public void setId(int Id) {
		this.mId = Id;
	}
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date workoutplanTimeStamp) {
		this.timeStamp = workoutplanTimeStamp;
	}
	
	@Override
	public boolean equals(Object o){
		if (o.getClass() == Workoutplan.class){
			Workoutplan w = (Workoutplan) o;
			if(w.getId() == this.getId()){
				return true;
			}
		}
		return false;
		
	}
	
	
}
