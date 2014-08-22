package com.workout.log;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.remic.workoutlog.R;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.workout.log.data.StatisticListElement;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.PerformanceActualMapper;
import com.workout.log.fragment.ActionBarTrainingDayPickerFragment;
import com.workout.log.listAdapter.DailyStatisticListAdapter;
import com.workout.log.navigation.OnBackPressedListener;
import com.workout.log.navigation.OnHomePressedListener;


public class DailyStatistic extends Fragment{
	private ListView expandableListView = null;
	private DailyStatisticListAdapter adapter = null;
	private PerformanceActualMapper paMapper = null;
	private ExerciseMapper eMapper = null;
	
	private int exercise_Id;
	private String exerciseName;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.daily_statistic, container, false);
		
		/**
		 * Handles the behavior if the back button is pressed
		 */
		((HelperActivity) getActivity())
				.setOnBackPressedListener(new OnBackPressedListener() {
					@Override
					public void doBack() {
						openExerciseSpecific();
						((HelperActivity) getActivity())
								.setOnBackPressedListener(null);
					}
				});
		/**
		 * Handles the behavior if the Home button in the actionbar is pressed
		 */
		((HelperActivity) getActivity())
				.setOnHomePressedListener(new OnHomePressedListener() {
					@Override
					public Intent doHome() {
						openExerciseSpecific();
						((HelperActivity) getActivity())
								.setOnBackPressedListener(null);
						return null;
					}
				});
		
		return view;
	}
	

	@Override
	public void onResume(){
		super.onResume();
		
		Bundle bundle = this.getArguments();
		exercise_Id = bundle.getInt("exercise_Id");
		exerciseName = bundle.getString("exerciseName");
		
		expandableListView = (ListView) getView().findViewById(R.id.expandable_listView);
		
		paMapper = new PerformanceActualMapper(getActivity());
		eMapper = new ExerciseMapper(getActivity());
		
		List<StatisticListElement> statisticList = new ArrayList<StatisticListElement>();
		statisticList = paMapper.getAllStatisticElements(exercise_Id, paMapper.getAllDates(eMapper.getExerciseById(exercise_Id)));
		
		Collections.reverse(statisticList);
		
		adapter = new DailyStatisticListAdapter(getActivity(), statisticList);
        AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(adapter);
        alphaInAnimationAdapter.setAbsListView(expandableListView);
        alphaInAnimationAdapter.setInitialDelayMillis(500);
        expandableListView.setAdapter(alphaInAnimationAdapter);
        adapter.expand(0);
		
	}
	
	private void openExerciseSpecific() {
		Bundle data = new Bundle();
		ExerciseSpecific exerciseSpecific = new ExerciseSpecific();
		
		ActionBarTrainingDayPickerFragment actionBarTrainingDayPickerFragment = 
				(ActionBarTrainingDayPickerFragment) getFragmentManager().findFragmentByTag("TrainingDayPicker");
		
        data.putInt("ExerciseID", exercise_Id);
        data.putString("ExerciseName",exerciseName);
        data.putInt("TrainingDayId",actionBarTrainingDayPickerFragment.getCurrentTrainingDay().getId());
        
        exerciseSpecific.setArguments(data);
        
	    FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.fragment_container, exerciseSpecific , "ExerciseSpecific");
        transaction.commit();
	}

}
