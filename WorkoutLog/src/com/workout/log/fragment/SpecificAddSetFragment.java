package com.workout.log.fragment;

import java.util.Date;

import com.remic.workoutlog.R;
import com.workout.log.ExerciseSpecific;
import com.workout.log.bo.PerformanceActual;
import com.workout.log.data.CountDownBroadcastService;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class SpecificAddSetFragment extends Fragment {
	
	
	private EditText etRepetition;
	private EditText etWeight;
	private View rootView;
	private ExerciseSpecific exerciseSpecific;
	private ImageView ivAdd;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		rootView = inflater.inflate(R.layout.fragment_central_add_set, container,false);
		
		
		
		return rootView;
	}

	@Override
	public void onResume(){
		super.onResume();
		
		exerciseSpecific = (ExerciseSpecific) getFragmentManager().findFragmentByTag("ExerciseSpecific");
		etRepetition = (EditText) rootView.findViewById(R.id.etEditRepetition);
		etWeight = (EditText) rootView.findViewById(R.id.etEditWeight);
		rootView.findViewById(R.id.ivAddSet).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int rep;
				double weight;
				if(etRepetition.getText().toString().isEmpty()){
					rep = -1;
				}else {
					rep = Integer.parseInt(etRepetition.getText().toString());
				}
				if(etWeight.getText().toString().isEmpty()) {
					weight = -1;
				} else {
					weight =Double.parseDouble(etWeight.getText().toString());
				}
				exerciseSpecific.addPerformanceActualItem( weight, rep);
			}
			
		});
	}
	
	
}
