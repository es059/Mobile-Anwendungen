package com.workout.log.listAdapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.workout.log.bo.Exercise;
import com.workout.log.customLayout.SpinnerExercise;

public class ExerciseSpinnerListAdapter extends ArrayAdapter<Exercise>{
	public ExerciseSpinnerListAdapter(Context context, int textViewResourceId, ArrayList<Exercise> objects) {
		super(context, textViewResourceId, objects);
	}
	@Override public View getDropDownView(int position, View convertView, ViewGroup parent) { 
		
		 Exercise exercise = getItem(position);
		 SpinnerExercise spinnerExercise = null;
		 if(convertView != null){
			 spinnerExercise = (SpinnerExercise) convertView;
		 }
		 else{
			 spinnerExercise = new SpinnerExercise(getContext());
		 }
		 
		 spinnerExercise.setExercise(exercise);
		 return spinnerExercise;
		 }

	@Override
	 public View getView(int position, View convertView, ViewGroup parent) {
		 Exercise exercise = getItem(position);
		 SpinnerExercise spinnerExercise = null;
		 if(convertView != null){
			 spinnerExercise = (SpinnerExercise) convertView;
		 }
		 else{
			 spinnerExercise = new SpinnerExercise(getContext());
		 }
		 
		 spinnerExercise.setExercise(exercise);
		 return spinnerExercise;
	 }
}
