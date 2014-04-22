package com.workout.log.listAdapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.workout.log.bo.Workoutplan;
import com.workout.log.customLayout.ListViewWorkoutplan;

public class WorkoutplanListAdapter extends ArrayAdapter<Workoutplan> {
	public WorkoutplanListAdapter(Context context, int textViewResourceId, List<Workoutplan> objects) {
		super(context, textViewResourceId, objects);
	}
	@Override
	 public View getView(int position, View convertView, ViewGroup parent) {
		Workoutplan workoutplan = getItem(position); 
		ListViewWorkoutplan listViewWorkoutplan = null;
		 if(convertView != null){
			 listViewWorkoutplan = (ListViewWorkoutplan) convertView;
		 }
		 else{
			 listViewWorkoutplan = new ListViewWorkoutplan(getContext());
		 }
		 listViewWorkoutplan.setWorkoutplan(workoutplan);  
	     
		 return listViewWorkoutplan;
	 }
}
