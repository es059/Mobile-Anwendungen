package com.workout.log.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.remic.workoutlog.R;
import com.workout.log.TrainingDayExerciseOverview;
import com.workout.log.bo.TrainingDay;
import com.workout.log.db.TrainingDayMapper;

public class ActionBarTrainingDaySelectFragment extends Fragment {
private TextView subject;
private TrainingDayExerciseOverview trainingDayExerciseOverview;
	
	@Override	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.actionbar_training_day_select, container,false);
		
		trainingDayExerciseOverview = (TrainingDayExerciseOverview) getActivity().getSupportFragmentManager().findFragmentByTag("TrainingDayExerciseOverview");
		subject = (TextView) view.findViewById(R.id.aktuellerTrainingsplan);
		
		/**
		 * Get the current TrainingDay 
		 */
		TrainingDayMapper tMapper = new TrainingDayMapper(getActivity());
		TrainingDay trainingDay = new TrainingDay();
		trainingDay = tMapper.getTrainingDayById(trainingDayExerciseOverview.getTrainingDayId());
		
		
		/**
		 * Insert the current TrainingDay in the TextView
		 */
		subject.setText(trainingDay.getName());
		return view;
		
	}

}
