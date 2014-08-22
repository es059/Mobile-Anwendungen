package com.workout.log;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.remic.workoutlog.R;
import com.workout.log.fragment.ActionBarGraphFragment;
import com.workout.log.fragment.LineGraphFragment;

public class Graph extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_graph, container, false);
		
		FragmentTransaction transaction;
		
		transaction = getActivity().getSupportFragmentManager().beginTransaction();
	    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	    transaction.replace(R.id.overview_dateTimePicker, new ActionBarGraphFragment(), "ActionBarGraphFragment");
	    transaction.commit();
	    
	    transaction = getActivity().getSupportFragmentManager().beginTransaction();
	    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	    transaction.replace(R.id.lineGraph, new LineGraphFragment(), "LineGraphFragment");
	    transaction.commit();
	    
	    return view;
	}
	
}
