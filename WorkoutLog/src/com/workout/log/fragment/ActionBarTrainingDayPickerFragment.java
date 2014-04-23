package com.workout.log.fragment;


import java.util.ArrayList;

import com.example.workoutlog.R;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.data.UpdateListView;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class ActionBarTrainingDayPickerFragment extends Fragment implements OnClickListener{
	private ImageButton next;
	private ImageButton previous;
	private TextView trainingDayPicker;
	private int index = 0;
	private ArrayList<TrainingDay> trainingDayList;
	private UpdateListView exerciseListViewUpdate;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		//Reference ExerciseOverview Layout and set ListView 
		View view = inflater.inflate(R.layout.actionbar_training_day_picker_fragment, container,false);
	
		getAllTrainingDay();
		trainingDayPicker = (TextView) view.findViewById(R.id.trainingDayPicker);
		if (!trainingDayList.isEmpty()){
			trainingDayPicker.setText(trainingDayList.get(index).getName());
		}
		next = (ImageButton) view.findViewById(R.id.Next);
		previous = (ImageButton) view.findViewById(R.id.Previous);
		
		next.setOnClickListener(this);
		previous.setOnClickListener(this);
		
		return view;
		
	}
	/**
	 * Added ClickListener to change to different TrainingDays within the
	 * current Workoutplan. Reference the UpdateListView Object if null
	 * 
	 * @param View v
	 * @author Eric Schmidt
	 */
	@Override
	public void onClick(View v) {
		if (exerciseListViewUpdate == null){
			exerciseListViewUpdate = UpdateListView.updateListView();
		}
		switch (v.getId()){
		case R.id.Next:
			if (index < (trainingDayList.size() -1)){
				index++;
			}else{
				index = 0;
			}
			if (!trainingDayList.isEmpty()){
				trainingDayPicker.setText(trainingDayList.get(index).getName());
				exerciseListViewUpdate.ExerciseListViewUpdate(super.getActivity(),trainingDayList.get(index).getID());
			}
			break;
		case R.id.Previous:
			if (index > 0){
				index--;
				trainingDayPicker.setText(trainingDayList.get(index).getName());
			}else{
				index = (trainingDayList.size() -1);
			}
			if (!trainingDayList.isEmpty()){
				trainingDayPicker.setText(trainingDayList.get(index).getName());
				exerciseListViewUpdate.ExerciseListViewUpdate(super.getActivity(),trainingDayList.get(index).getID());
			}
			break;
		default:
			break;
		}
	}
	/**
	 * Fills the ArrayList trainingDayList
	 * 
	 * @author Eric Schmidt
	 */
	private void getAllTrainingDay(){
		//Select Current Workoutplan
		WorkoutplanMapper wMapper = new WorkoutplanMapper(super.getActivity());
		Workoutplan w = wMapper.getCurrent();
		//Select all TrainingDays from currrent Workoutplan
		TrainingDayMapper tMapper = new TrainingDayMapper(super.getActivity());
		trainingDayList = tMapper.getAll(w.getId());
	}
}
