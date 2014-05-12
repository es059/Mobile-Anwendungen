package com.workout.log.customLayout;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.db.PerformanceTargetMapper;
import com.workout.log.db.TrainingDayMapper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListViewWorkoutplan extends RelativeLayout{
	private TextView titel;
	private TextView traningday;
	
	public ListViewWorkoutplan(Context context) {
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.listview_workoutplan, null);
		
		titel = (TextView) v.findViewById(R.id.workoutplan_listview_titel);
		traningday = (TextView) v.findViewById(R.id.trainingday);
		
		addView(v);
	}
	public void setWorkoutplan(Workoutplan workoutplan){
		this.titel.setText(workoutplan.getName());
		//Get the amount if trainingdays in the current workoutplan
		TrainingDayMapper tMapper = new TrainingDayMapper(getContext());
		ArrayList<TrainingDay> trainingDayList = tMapper.getAll(workoutplan.getId());
		this.traningday.setHint("(Trainingstage: " + String.valueOf(trainingDayList.size()) + ")");	
	}
}
