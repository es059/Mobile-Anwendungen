package com.workout.log.listAdapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.workout.log.bo.Exercise;
import com.workout.log.customLayout.ListViewExercise;
import com.workout.log.customLayout.ListViewExerciseWithoutSetsReps;

public class ExerciseListWithoutSetsRepsAdapter extends ArrayAdapter<Exercise> {
	public ExerciseListWithoutSetsRepsAdapter(Context context, int textViewResourceId, List<Exercise> objects) {
		super(context, textViewResourceId, objects);
	}
	
	@Override
	 public View getView(int position, View convertView, ViewGroup parent) {
		 Exercise exercise = getItem(position);
		 ListViewExerciseWithoutSetsReps listViewExercise = null;
		 if(convertView != null){
			 listViewExercise = (ListViewExerciseWithoutSetsReps) convertView;
		 }
		 else{
			 listViewExercise = new ListViewExerciseWithoutSetsReps(getContext());
		 }
		 
		 listViewExercise.setExercise(exercise);
		 return listViewExercise;
	 }
}
