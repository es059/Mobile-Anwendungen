package com.workout.log;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class HelperActivity extends Activity  {
	private static final String PREF_FIRST_LAUNCH = "first";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
	    super.onCreate(savedInstanceState);
		if (!firstTimeCheck()){
			startActivity(new Intent(this, ExerciseOverview.class));
		}else{
			startActivity(new Intent(this, WorkoutplanSelect.class));
		    finish();
		}
	}
	/**
	 * Check Shared Preferences if the user had opened the Application before
	 * 
	 * @return false if not launched for the first time
	 * @author Eric Schmidt
	 */
	private boolean firstTimeCheck(){
		return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREF_FIRST_LAUNCH, true);
	}
}
