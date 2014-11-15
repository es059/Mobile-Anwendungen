package com.workout.log.data;

public class MuscleGroupSectionItem implements ListItem{
	private String mTitle;
	
	public MuscleGroupSectionItem(String title){
		mTitle = title;
	}
	
	public String getTitle(){
		return mTitle;
	}
	
	public void setTitle(String title){
		this.mTitle = title;
	}
	
	@Override
	public boolean isSection() {
		// TODO Auto-generated method stub
		return true;
	}
}
