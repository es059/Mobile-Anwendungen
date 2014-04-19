package com.workout.log.fragment;


import java.util.ArrayList;

import com.example.workoutlog.R;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class ActionBarTrainingDayPickerFragment extends Fragment implements OnClickListener{
	private ImageButton next;
	private ImageButton previous;
	private TextView trainingDayPicker;
	private ArrayList<TrainingDay> trainingDayList;
	private int index = 0;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.actionbar_training_day_picker_fragment, container,false);
		
		//Erstelle einen Calender. Erstelle passendes Format und weiﬂe TextView date aktuelles Datum zu
		trainingDayPicker = (TextView) view.findViewById(R.id.trainingDayPicker);
		getTrainingDay();
		trainingDayPicker.setText(trainingDayList.get(index).getName());
		
		//ImageButton referenzieren
		next = (ImageButton) view.findViewById(R.id.Next);
		previous = (ImageButton) view.findViewById(R.id.Previous);
		
		next.setOnClickListener(this);
		previous.setOnClickListener(this);
		return view;
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.Next:
			if (index < (trainingDayList.size() -1)){
				index++;
				trainingDayPicker.setText(trainingDayList.get(index).getName());
			}else{
				index = 0;
			}
			break;
		case R.id.Previous:
			if (index > 0){
				index--;
				trainingDayPicker.setText(trainingDayList.get(index).getName());
			}else{
				index = (trainingDayList.size() -1);
			}
			break;
		default:
			break;
		}
	}
	
	private void getTrainingDay(){
		//Select Current Workoutplan
		WorkoutplanMapper wMapper = new WorkoutplanMapper(super.getActivity());
		Workoutplan w = wMapper.getCurrent();
		//Select all TrainingDays from currrent Workoutplan
		TrainingDayMapper tMapper = new TrainingDayMapper(super.getActivity());
		trainingDayList = tMapper.getAll(w.getID());
	}
}
