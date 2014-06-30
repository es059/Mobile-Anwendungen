package com.workout.log.customLayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.workoutlog.R;
import com.workout.log.bo.PerformanceActual;

public class ListViewPerformanceActual extends RelativeLayout{
	private TextView set;
	private EditText repetition;
	private EditText weight;
	
	public ListViewPerformanceActual(Context context) {
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.listview_performance_actual, null);
		
		set = (TextView) v.findViewById(R.id.specific_set);
		repetition = (EditText) v.findViewById(R.id.specific_edit_repetition);
		weight = (EditText) v.findViewById(R.id.specific_edit_weight);
		
		
		addView(v);
	}
	public void setPerfromanceActual(PerformanceActual performanceActual){
		this.set.setText(String.valueOf(performanceActual.getSet()));
		if (performanceActual.getRepetition() != -1){
			this.repetition.setText(String.valueOf(performanceActual.getRepetition()));
		}
		if (performanceActual.getWeight() != -1){
			this.weight.setText(String.valueOf(performanceActual.getWeight()));
		}
	}
	
}
