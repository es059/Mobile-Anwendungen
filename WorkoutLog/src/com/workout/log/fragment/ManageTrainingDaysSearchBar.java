package com.workout.log.fragment;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.workoutlog.R;
import com.workout.log.bo.TrainingDay;
import com.workout.log.ManageTrainingDays;
import com.workout.log.db.TrainingDayMapper;

public class ManageTrainingDaysSearchBar extends Fragment {
	
	private EditText searchBar;
	private ManageTrainingDays manageTrainingDays;
	private  TrainingDayMapper tdMapper = new TrainingDayMapper(getActivity());
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.actionbar_searchbar_fragment, container,false);
		manageTrainingDays = (ManageTrainingDays) getActivity().getFragmentManager().findFragmentByTag("ManageTrainingDays");
		searchBar = (EditText) view.findViewById(R.id.searchbar_text);
		searchBar.addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}

	        public void onTextChanged(CharSequence s, int start, int before, int count){}

	        public void afterTextChanged(Editable s){
	        	
	        	ArrayList<TrainingDay> trainingDayList = new ArrayList<TrainingDay>();
	        	trainingDayList =   tdMapper.searchKeyString(String.valueOf(s));
	        	
	        	manageTrainingDays.updateAdapter(trainingDayList);
	        }
	  });
		return view;
		
	}
}


