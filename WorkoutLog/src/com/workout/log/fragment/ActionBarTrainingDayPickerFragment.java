package com.workout.log.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.remic.workoutlog.R;
import com.workout.log.ExerciseOverview;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;

/**
 * Handles the TrainingDay changes in the Fragment ExerciseOverview
 * 
 * @author Eric Schmidt
 */
public class ActionBarTrainingDayPickerFragment extends Fragment implements OnClickListener{
	private ImageButton next;
	private ImageButton previous;
	private TextView trainingDayPicker;
	private static int index = 0;
	private ArrayList<TrainingDay> trainingDayList;
	private TrainingDayMapper tMapper;
	private ExerciseOverview exerciseOverview;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		//Reference ExerciseOverview Layout and set ListView 
		View view = inflater.inflate(R.layout.actionbar_trainingday_picker_fragment, container,false);
	
		getAllTrainingDay();
		trainingDayPicker = (TextView) view.findViewById(R.id.trainingDayPicker);
		
		if (!trainingDayList.isEmpty()){
			if(index >= trainingDayList.size()) index = 0;
			trainingDayPicker.setText(trainingDayList.get(index).getName());
		}else{
			trainingDayPicker.setHint("Trainingstage");
		}
		
		exerciseOverview = (ExerciseOverview) getActivity().getSupportFragmentManager().findFragmentByTag("ExerciseOverview");
		
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
	@SuppressWarnings("static-access")
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.Next) {
			onNext();
		} else if (id == R.id.Previous) {
			onPrevious();
		} else {
		}
	}
	
	/**
	 * Functions for next trainingDay
	 */
	public void onNext(){
		if (index < (trainingDayList.size() -1)){
			index++;
		}else{
			index = 0;
		}
		if (!trainingDayList.isEmpty()){
			trainingDayPicker.setText(trainingDayList.get(index).getName());
			exerciseOverview.setTrainingDay(trainingDayList.get(index).getId());
			exerciseOverview.updateListView(trainingDayList.get(index).getId());
		}
	}
	
	/**
	 * Functions for previous trainingDay
	 */
	public void onPrevious(){
		if (index > 0){
			index--;
			trainingDayPicker.setText(trainingDayList.get(index).getName());
		}else{
			index = (trainingDayList.size() -1);
		}
		if (!trainingDayList.isEmpty()){
			trainingDayPicker.setText(trainingDayList.get(index).getName());
			exerciseOverview.setTrainingDay(trainingDayList.get(index).getId());
			exerciseOverview.updateListView(trainingDayList.get(index).getId());
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
		trainingDayList = tMapper.getAllTrainingDaysFromWorkoutplan(w.getId());
	}
	
	public TrainingDay getCurrentTrainingDay(){
		return trainingDayList.get(index);
	}
	
	/**
	 * Set the current TrainingDay by TrainingDayId
	 * 
	 * @param trainingDayId
	 */
	public void setCurrentTrainingDay(int trainingDayId){
		tMapper = new TrainingDayMapper(getActivity());
		int index;
		index = indexOfArrayList(tMapper.getTrainingDayById(trainingDayId));
		if (index != -1){
			trainingDayPicker.setText(trainingDayList.get(index).getName());
			exerciseOverview.updateListView(trainingDayList.get(index).getId());
		}
	}
	
	/**
	 * Searches the TrainingDayList for a sepcific trainingDay and returns the 
	 * index
	 * 
	 * @param trainingDay
	 * @return index of ArrayList where the object was found
	 */
	private int indexOfArrayList(TrainingDay trainingDay){
		int index = -1;
		int count = 0;
		for (TrainingDay item : trainingDayList){
			if (item.getId() == trainingDay.getId()){
				index = count;
				return index;
			}
			count++;
		}
		return index;
	}
}
