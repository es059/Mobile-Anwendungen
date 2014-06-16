package com.workout.log.customLayout;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.TrainingDay;
import com.workout.log.db.ExerciseMapper;

public class ListViewTrainingDay extends RelativeLayout{
	private TextView titel;
	private TextView exerciseCount;
	
	public ListViewTrainingDay(Context context) {
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.listview_training_day, null);
		
		titel = (TextView) v.findViewById(R.id.trainingDay_listview_titel);
		exerciseCount = (TextView) v.findViewById(R.id.exerciseCount);
		
		addView(v);
	}
	public void setTrainingDay(TrainingDay trainingDay){
		this.titel.setText(trainingDay.getName());
		//Get the amount if trainingdays in the current workoutplan
		ExerciseMapper eMapper = new ExerciseMapper(getContext());
		ArrayList<Exercise> exerciseList = eMapper.getExerciseByTrainingDay(trainingDay.getId());
		this.exerciseCount.setHint("(Übungen: " + String.valueOf(exerciseList.size()) + ")");	
	}
}
