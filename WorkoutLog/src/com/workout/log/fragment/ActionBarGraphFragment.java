package com.workout.log.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.workoutlog.R;
import com.workout.log.data.LineGraph;
import com.workout.log.db.ExerciseMapper;

public class ActionBarGraphFragment  extends Fragment{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.actionbar_graph_fragment, container,false);

		return view;
	}
}