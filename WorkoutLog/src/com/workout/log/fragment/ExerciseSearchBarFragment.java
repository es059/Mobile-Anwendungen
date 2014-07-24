package com.workout.log.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.workoutlog.R;
import com.workout.log.ExerciseAdd;
import com.workout.log.ExerciseAddToTrainingDay;
import com.workout.log.bo.Exercise;
import com.workout.log.db.ExerciseMapper;

@SuppressLint("ValidFragment")
public class ExerciseSearchBarFragment extends Fragment{
	
	private EditText searchBar;
	private Fragment fragment;
	private ExerciseMapper eMapper;
	
	public ExerciseSearchBarFragment(Fragment fragment){
		this.fragment = fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.actionbar_searchbar_fragment, container,false);
		
		eMapper = new ExerciseMapper(getActivity());
		searchBar = (EditText) view.findViewById(R.id.searchbar_text);
		searchBar.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}

	        @Override
			public void onTextChanged(CharSequence s, int start, int before, int count){}

	        @Override
			public void afterTextChanged(Editable s){
	        	ArrayList<Exercise> List = new ArrayList<Exercise>();
	        	List = eMapper.searchKeyString(String.valueOf(s));
	        	if (fragment instanceof ExerciseAdd){
	        		((ExerciseAdd) fragment).updateListView(List, true);
	        	}else{
	        		((ExerciseAddToTrainingDay) fragment).updateListView(List);	
	        	}
	        }
	  });
		return view;
	}
}
