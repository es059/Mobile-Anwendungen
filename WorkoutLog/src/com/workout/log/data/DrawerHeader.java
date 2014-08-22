package com.workout.log.data;

public class DrawerHeader implements ListItem{
private final String mTitle;
	
	public DrawerHeader(String title){
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
