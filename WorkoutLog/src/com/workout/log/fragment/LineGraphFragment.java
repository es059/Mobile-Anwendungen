package com.workout.log.fragment;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.data.LineGraph;
import com.workout.log.db.ExerciseMapper;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class LineGraphFragment extends Fragment{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.line_graph_fragment, null);
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.graph);
		
		//Create the Graph
		ExerciseMapper e = new ExerciseMapper(getActivity());
		LineGraph line = new LineGraph();
		View linearGraph = line.getView(getActivity(),e.getExerciseById(3));
		layout.addView(linearGraph);
		
		return view;
	}
}
