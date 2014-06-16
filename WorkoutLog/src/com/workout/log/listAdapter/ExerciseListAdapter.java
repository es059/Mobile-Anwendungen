package com.workout.log.listAdapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.workout.log.bo.Exercise;
import com.workout.log.customLayout.ListViewExercise;

public class ExerciseListAdapter extends ArrayAdapter<Exercise> {
	
	private int trainingDayId;
	
	public ExerciseListAdapter(Context context, int textViewResourceId, List<Exercise> objects, int trainingDayId) {
			super(context, textViewResourceId, objects);
			this.trainingDayId = trainingDayId;
		}
		@Override
		 public View getView(int position, View convertView, ViewGroup parent) {
			 Exercise exercise = (Exercise) getItem(position);
			 ListViewExercise listViewExercise = null;
			 if(convertView != null){
				 listViewExercise = (ListViewExercise) convertView;
			 }
			 else{
				 listViewExercise = new ListViewExercise(getContext());
			 }
			 
			 listViewExercise.setExercise(exercise, trainingDayId);
			 return listViewExercise;
		 }

	}
