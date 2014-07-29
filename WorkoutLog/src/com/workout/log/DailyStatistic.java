package com.workout.log;


import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.workoutlog.R;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.workout.log.listAdapter.DailyStatisticListAdapter;


public class DailyStatistic extends Fragment{
	private ListView expandableListView = null;
	private DailyStatisticListAdapter adapter = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.daily_statistic, container, false);
	
		return view;
	}
	

	@Override
	public void onResume(){
		super.onResume();
		expandableListView = (ListView) getView().findViewById(R.id.expandable_listView);
		List<String>s = new ArrayList<String>();
		s.add("asd");
		s.add("asd");
		
		adapter = new DailyStatisticListAdapter(getActivity(), s);
        AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(adapter);
        alphaInAnimationAdapter.setAbsListView(expandableListView);
        alphaInAnimationAdapter.setInitialDelayMillis(500);
        expandableListView.setAdapter(alphaInAnimationAdapter);
		
	}

}
