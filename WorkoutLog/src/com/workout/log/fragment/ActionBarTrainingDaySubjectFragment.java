package com.workout.log.fragment;

import com.example.workoutlog.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class ActionBarTrainingDaySubjectFragment  extends Fragment{
	private EditText subject;
	
	@Override	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.actionbar_training_day_subject_fragment, container,false);
		
		subject = (EditText) view.findViewById(R.id.trainingDay_subject);
		return view;
		
	}
}