package com.workout.log.customLayout;

import com.example.workoutlog.R;
import com.workout.log.data.Exercise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListViewExercise extends RelativeLayout{
	TextView exercise;
	TextView set;
	TextView repetitions;
	
	public ListViewExercise(Context context) {
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View listViewExercise = inflater.inflate(R.layout.listview_exercise, null);
		exercise = (TextView) listViewExercise.findViewById(R.id.exercise);
		set = (TextView) listViewExercise.findViewById(R.id.set);
		repetitions = (TextView) listViewExercise.findViewById(R.id.repetitions);
		addView(listViewExercise);
	}
	public void setExercise(Exercise exercise){
		this.exercise.setText(exercise.getName());
	}
	
}
