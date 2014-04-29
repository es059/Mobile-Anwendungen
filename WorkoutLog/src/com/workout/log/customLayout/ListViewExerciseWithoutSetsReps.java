package com.workout.log.customLayout;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListViewExerciseWithoutSetsReps extends RelativeLayout {
 private TextView exerciseView;
 
 public ListViewExerciseWithoutSetsReps(Context context) {
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View listViewExercise = inflater.inflate(R.layout.listview_exercise_without_repssets, null);
		exerciseView = (TextView) listViewExercise.findViewById(R.id.exercise);
		addView(listViewExercise);
	}
 
 public void setExercise(Exercise exercise){
		this.exerciseView.setText(exercise.getName());
 }
}
