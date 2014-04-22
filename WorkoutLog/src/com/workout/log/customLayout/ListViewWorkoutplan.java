package com.workout.log.customLayout;

import com.example.workoutlog.R;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListViewWorkoutplan extends RelativeLayout{
	private TextView titel;
	
	public ListViewWorkoutplan(Context context) {
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.listview_workoutplan, null);
		
		titel = (TextView) v.findViewById(R.id.workoutplan_listview_titel);
		
		addView(v);
	}
	public void setWorkoutplan(Workoutplan workoutplan){
		this.titel.setText(workoutplan.getName());
	}
}
