package com.workout.log.customLayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.db.PerformanceTargetMapper;

public class ListViewExercise extends RelativeLayout{
	TextView exerciseView;
	TextView setView;
	TextView repetitionView;
	
	public ListViewExercise(Context context) {
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View listViewExercise = inflater.inflate(R.layout.listview_exercise, null);
		exerciseView = (TextView) listViewExercise.findViewById(R.id.exercise);
		setView = (TextView) listViewExercise.findViewById(R.id.set);
		repetitionView = (TextView) listViewExercise.findViewById(R.id.repetitions);
		addView(listViewExercise);
	}
	/**
	 * Set the Text of the TextViews within the Custom Layout using the parameter 
	 * Exercise
	 * 
	 * @param Exercise exercise
	 * @author Eric Schmidt
	 */
	public void setExercise(Exercise exercise, int trainingDayId){
		this.exerciseView.setText(exercise.getName());
		//Get target performance information (Set & Repetition)
		PerformanceTargetMapper pMapper = new PerformanceTargetMapper(getContext());
		PerformanceTarget performanceTarget = pMapper.getPerformanceTargetByExerciseId(exercise, trainingDayId);
		this.setView.setHint(getResources().getString(R.string.Set) + ": " + String.valueOf(performanceTarget.getSet()));
		this.repetitionView.setHint(getResources().getString(R.string.Rep) + "Wdh: " + String.valueOf(performanceTarget.getRepetition()));	
	}
	
}
