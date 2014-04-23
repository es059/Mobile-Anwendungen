package com.workout.log.data;

public class MuscleGroupSectionItem implements ExerciseItem{
	private final String mTitle;
	
	public MuscleGroupSectionItem(String title){
		mTitle = title;
	}
	
	public String getTitle(){
		return mTitle;
	}
	
	@Override
	public boolean isSection() {
		// TODO Auto-generated method stub
		return true;
	}
}
