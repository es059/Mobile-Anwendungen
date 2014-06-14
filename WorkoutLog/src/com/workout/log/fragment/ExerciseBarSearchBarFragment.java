package com.workout.log.fragment;

import java.util.ArrayList;

import com.example.workoutlog.R;




import com.workout.log.ExerciseAdd;
import com.workout.log.bo.Exercise;
import com.workout.log.db.ExerciseMapper;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ExerciseBarSearchBarFragment extends Fragment{
	
	private EditText searchBar;
	private ExerciseAdd exerciseAdd;
	private ExerciseMapper eMapper;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.actionbar_searchbar_fragment, container,false);
		
		eMapper = new ExerciseMapper(getActivity());
		exerciseAdd = (ExerciseAdd) getActivity().getFragmentManager().findFragmentByTag("ExerciseAdd");
		searchBar = (EditText) view.findViewById(R.id.searchbar_text);
		searchBar.addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}

	        public void onTextChanged(CharSequence s, int start, int before, int count){}

	        public void afterTextChanged(Editable s){
	        	ArrayList<Exercise> List = new ArrayList<Exercise>();
	        	List = eMapper.searchKeyString(String.valueOf(s));
	        	exerciseAdd.updateAdapter(List);
	        }
	  });
		return view;
		
	}
}
