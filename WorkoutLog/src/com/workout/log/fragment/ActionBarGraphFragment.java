package com.workout.log.fragment;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.listAdapter.ExerciseSpinnerListAdapter;

/**
 * Fragment class to handel the Spinner view in the GraphActivity
 * 
 * @author Eric Schmidt
 *
 */
public class ActionBarGraphFragment  extends Fragment implements OnItemSelectedListener{
	
	private Spinner exerciseSpinner;
	private LineGraphFragment lineGraphFragment;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.actionbar_graph_fragment, container,false);
		
		exerciseSpinner = (Spinner) view.findViewById(R.id.graph_exercise);
		lineGraphFragment = (LineGraphFragment) getActivity().getFragmentManager().findFragmentById(R.id.lineGraph);
		
		ExerciseMapper eMapper = new ExerciseMapper(getActivity());
		ArrayList<Exercise> exerciseList = eMapper.getAllExercise();
		
		ExerciseSpinnerListAdapter adapter = new ExerciseSpinnerListAdapter(getActivity(),0, exerciseList);
		exerciseSpinner.setAdapter(adapter);
		exerciseSpinner.setOnItemSelectedListener(this);
		
		return view;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		if (lineGraphFragment == null){
			lineGraphFragment = (LineGraphFragment) getActivity().getFragmentManager().findFragmentById(R.id.lineGraph);
		}
		lineGraphFragment.updateGraph((Exercise)parent.getItemAtPosition(pos));
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}