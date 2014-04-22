package com.workout.log.listAdapter;

import java.util.List;

import com.workout.log.bo.Exercise;
import com.workout.log.customLayout.ListViewExercise;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ExerciseListAdapter extends ArrayAdapter<Exercise> {
	public ExerciseListAdapter(Context context, int textViewResourceId, List<Exercise> objects) {
			super(context, textViewResourceId, objects);
		}
		@Override
		 public View getView(int position, View convertView, ViewGroup parent) {
			 Exercise exercise = getItem(position);
			 ListViewExercise listViewExercise = null;
			 if(convertView != null){
				 listViewExercise = (ListViewExercise) convertView;
			 }
			 else{
				 listViewExercise = new ListViewExercise(getContext());
			 }
			 
			 listViewExercise.setExercise(exercise);
			 return listViewExercise;
		 }

	}
