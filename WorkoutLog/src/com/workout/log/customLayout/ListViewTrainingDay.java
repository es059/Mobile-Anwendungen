package com.workout.log.customLayout;

import com.example.workoutlog.R;
import com.workout.log.bo.TrainingDay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListViewTrainingDay extends RelativeLayout{
	private TextView titel;
	
	public ListViewTrainingDay(Context context) {
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.listview_training_day, null);
		
		titel = (TextView) v.findViewById(R.id.trainingDay_listview_titel);
		
		addView(v);
	}
	public void setTrainingDay(TrainingDay trainingDay){
		this.titel.setText(trainingDay.getName());
	}
}
