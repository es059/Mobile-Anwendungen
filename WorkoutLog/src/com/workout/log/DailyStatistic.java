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
import com.workout.log.bo.PerformanceActual;
import com.workout.log.data.StatisticListElement;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.PerformanceActualMapper;
import com.workout.log.listAdapter.DailyStatisticListAdapter;


public class DailyStatistic extends Fragment{
	private ListView expandableListView = null;
	private DailyStatisticListAdapter adapter = null;
	private PerformanceActualMapper paMapper = null;
	private ExerciseMapper eMapper = null;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.daily_statistic, container, false);
	
		return view;
	}
	

	@Override
	public void onResume(){
		super.onResume();
		Bundle bundle = this.getArguments();
		int exercise_Id = bundle.getInt("exercise_Id");
		expandableListView = (ListView) getView().findViewById(R.id.expandable_listView);
		paMapper = new PerformanceActualMapper(getActivity());
		eMapper = new ExerciseMapper(getActivity());
		List<StatisticListElement> statisticList = new ArrayList<StatisticListElement>();
		statisticList = paMapper.getAllStatisticElements(exercise_Id, paMapper.getAllDates(eMapper.getExerciseById(exercise_Id)));
		List<String>s = new ArrayList<String>();
		s.add("asd");
		s.add("asd");
		
		adapter = new DailyStatisticListAdapter(getActivity(), statisticList);
        AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(adapter);
        alphaInAnimationAdapter.setAbsListView(expandableListView);
        alphaInAnimationAdapter.setInitialDelayMillis(500);
        expandableListView.setAdapter(alphaInAnimationAdapter);
		
	}

}
