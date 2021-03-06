package com.workout.log.customLayout;

import com.example.workoutlog.R;
import com.workout.log.bo.PerformanceActual;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListViewSet extends RelativeLayout{
	private TextView set;
	private EditText repetition;
	private EditText weight;
	
	public ListViewSet(Context context) {
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
		if (performanceActual.getRepetition() != 0){
			this.repetition.setText(String.valueOf(performanceActual.getRepetition()));
		}
		if (performanceActual.getWeight() != 0.0){
			this.weight.setText(String.valueOf(performanceActual.getWeight()));
		}
	}
	
}
