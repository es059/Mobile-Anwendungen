package com.workout.log.customLayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.db.PerformanceActualMapper;

public class SpinnerExercise extends RelativeLayout {
	private TextView titel;
	private TextView trainingDays;
	
	public SpinnerExercise(Context context) {
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.spinner_exercise, null);
		
		titel = (TextView) v.findViewById(R.id.exercise);
		trainingDays = (TextView) v.findViewById(R.id.trainingDays);
		
		addView(v);
	}
	
	/**
	 * Set the TextView to the current ExerciseName and the Number of TrainingDays
	 * 
	 * @param workoutplan
	 */
	public void setExercise(Exercise exercise){
		PerformanceActualMapper paMapper = new PerformanceActualMapper(getContext());
		this.titel.setText(exercise.getName());
		//Fill the TextView with the trainingDays of the exercise
		this.trainingDays.setHint(getResources().getString(R.string.TrainingDays) + ": " + String.valueOf(paMapper.getTrainingDaysbyExercise(exercise.getId())));
	}
}
