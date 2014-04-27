package com.workout.log.customLayout;

import com.example.workoutlog.R;
import com.workout.log.bo.PerformanceActual;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListViewSet extends RelativeLayout{
	private TextView set;
	
	public ListViewSet(Context context) {
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.listview_performance_actual, null);
		
		set = (TextView) v.findViewById(R.id.specific_set);
		
		addView(v);
	}
	public void setPerfromanceActual(PerformanceActual performanceActual){
		this.set.setText(String.valueOf(performanceActual.getSet()));
	}
	
}
