package com.workout.log.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.graph.LineGraph;

public class LineGraphFragment extends Fragment{
	private LinearLayout mLayout = null;
	private static View view = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		view = (View) inflater.inflate(R.layout.line_graph_fragment, null);
		return view;
	}
	
	/**
	 * Update the Line-Graph with the given exercise
	 * 
	 * @param Exercise 
	 * @author Eric Schmidt
	 */
	public void updateGraph(Exercise exercise){
		mLayout = (LinearLayout) view.findViewById(R.id.graph);
		//Create the Graph
		LineGraph line = new LineGraph();
		View linearGraph = line.getView(getActivity(),exercise);
		if (linearGraph != null){
			mLayout.removeAllViews();
			mLayout.addView(linearGraph);
		}else{
			Toast.makeText(getActivity(), "Es ist leider noch kein Trainingstag verfügbar", Toast.LENGTH_SHORT).show();
		}
	}
}