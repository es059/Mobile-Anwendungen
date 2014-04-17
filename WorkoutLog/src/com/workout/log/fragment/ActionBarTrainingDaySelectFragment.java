package com.workout.log.fragment;

import com.example.workoutlog.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ActionBarTrainingDaySelectFragment extends Fragment {
private TextView subject;
	
	@Override	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.actionbar_training_day_select, container,false);
		
		subject = (TextView) view.findViewById(R.id.aktuellerTrainingsplan);
		return view;
		
	}

}
