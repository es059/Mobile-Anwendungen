package com.workout.log;

import com.example.workoutlog.R;
import com.workout.log.fragment.ActionBarGraphFragment;
import com.workout.log.fragment.LineGraphFragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GraphActivity extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_graph, container, false);
		
		FragmentTransaction transaction;
		
		transaction = getFragmentManager().beginTransaction();
	    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	    transaction.replace(R.id.overview_dateTimePicker, new ActionBarGraphFragment(), "ActionBarGraphFragment");
	    transaction.addToBackStack(null);
	    transaction.commit();
	    
	    transaction = getFragmentManager().beginTransaction();
	    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	    transaction.replace(R.id.lineGraph, new LineGraphFragment(), "LineGraphFragment");
	    transaction.addToBackStack(null);
	    transaction.commit();
	    
	    return view;
	}
	
}
