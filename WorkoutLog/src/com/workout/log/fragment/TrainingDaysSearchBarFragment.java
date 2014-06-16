package com.workout.log.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.workoutlog.R;
import com.workout.log.ManageTrainingDays;
import com.workout.log.TrainingDayAddToWorkoutplan;
import com.workout.log.bo.TrainingDay;
import com.workout.log.db.TrainingDayMapper;

@SuppressLint("ValidFragment")
public class TrainingDaysSearchBarFragment extends Fragment {
	
	private EditText searchBar;
	private Fragment fragment;
	private TrainingDayMapper tdMapper;

	public TrainingDaysSearchBarFragment(Fragment fragment){
		this.fragment = fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.actionbar_searchbar_fragment, container,false);
		
		tdMapper = new TrainingDayMapper(getActivity());
		searchBar = (EditText) view.findViewById(R.id.searchbar_text);
		searchBar.addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}

	        public void onTextChanged(CharSequence s, int start, int before, int count){}

	        public void afterTextChanged(Editable s){
	        	
	        	ArrayList<TrainingDay> trainingDayList = new ArrayList<TrainingDay>();
	        	trainingDayList =   tdMapper.searchKeyString(String.valueOf(s));
	        	
	        	if (fragment instanceof ManageTrainingDays)((ManageTrainingDays) fragment).updateAdapter(trainingDayList);
	        	if (fragment instanceof TrainingDayAddToWorkoutplan)((TrainingDayAddToWorkoutplan) fragment).updateAdapter(trainingDayList);
	        }
	  });
		return view;
		
	}
}

