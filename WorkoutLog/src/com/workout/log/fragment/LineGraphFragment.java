package com.workout.log.fragment;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.data.LineGraph;

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
		LineGraph line = new LineGraph();
		View linearGraph = line.getView(getActivity(), new Exercise());
		layout.addView(linearGraph);
		
		return view;
	}
}
