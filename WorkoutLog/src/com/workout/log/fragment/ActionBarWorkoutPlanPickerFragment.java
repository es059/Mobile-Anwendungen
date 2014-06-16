package com.workout.log.fragment;

import java.util.ArrayList;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.workoutlog.R;
import com.workout.log.ManageWorkoutplan;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.dialog.WorkoutplanAddDialogFragment;
import com.workout.log.dialog.WorkoutplanUpdateDialogFragment;

public class ActionBarWorkoutPlanPickerFragment extends Fragment implements OnClickListener  {
	private ImageButton previousButton;
	private ImageButton nextButton;
	private TextView workoutplanTextView;
	
	private ArrayList<Workoutplan> workoutplanList;
	private ArrayList<TrainingDay> trainingDayList;
	
	private WorkoutplanMapper wpMapper;
	private TrainingDayMapper tdMapper;
	private ManageWorkoutplan manageWorkoutplan; 
	
	private static int currentListId = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		//Reference ExerciseOverview Layout and set ListView 
		View view = inflater.inflate(R.layout.actionbar_workout_plan_picker_fragment, container,false);
		return view;
	}

	@Override
	public void onResume(){
		super.onResume();
		
		manageWorkoutplan  = (ManageWorkoutplan) getActivity().getFragmentManager().findFragmentByTag("ManageWorkoutplan");
		previousButton = (ImageButton) getView().findViewById(R.id.Previous);
		nextButton = (ImageButton) getView().findViewById(R.id.Next);
		workoutplanTextView = (TextView) getView().findViewById(R.id.trainingDayPicker);
		
		
		workoutplanList = new ArrayList<Workoutplan>();
		trainingDayList = new ArrayList<TrainingDay>();
		
		wpMapper = new WorkoutplanMapper(getActivity());
		tdMapper = new TrainingDayMapper(getActivity());
		
		workoutplanList = wpMapper.getAll();
		if (workoutplanList.size() != 0){
			setCurrentIdByWorkoutplanId(wpMapper.getCurrent().getId());
			trainingDayList = tdMapper.getAll(workoutplanList.get(currentListId).getId());
			workoutplanTextView.setText(workoutplanList.get(currentListId).getName());
			manageWorkoutplan.setWorkoutplanId(workoutplanList.get(currentListId).getId());
			/**
			 * ClickListener handles the Update/delete function of the workoutplan
			 */
			workoutplanTextView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					DialogFragment dialogFragment = WorkoutplanUpdateDialogFragment.newInstance(getActivity(), workoutplanList.get(currentListId).getId());
					dialogFragment.show(getActivity().getFragmentManager(), "Open Exercise Settings on Long Click");			
				}	
			});
			
		}else{
			nextButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_add));
			workoutplanTextView.setHint("Trainingspläne");
		}
		
		previousButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		
		/**
		 * Ensure that the previousButton is invisible, the add Button is showing if 
		 * the currentId is the last index in the workoutplanList and that the 
		 * current trainingDayList is added to the ListView
		 */
		if(currentListId == 0)previousButton.setVisibility(View.INVISIBLE); 
		if(workoutplanList.size() <= currentListId + 1)nextButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_add));
		manageWorkoutplan.updateListView(trainingDayList);
	}
		
	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.Next:
			if(workoutplanList.size() <= currentListId +1) {
				DialogFragment dialogFragment = WorkoutplanAddDialogFragment.newInstance(getActivity(), workoutplanList);
				dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
				if(workoutplanList.size()>1) previousButton.setVisibility(View.VISIBLE);	
			}
			else {	
				if(currentListId == 0) {
					previousButton.setVisibility(View.VISIBLE);
				}
				workoutplanTextView.setText(workoutplanList.get(currentListId +1).getName());
				manageWorkoutplan.updateListView(tdMapper.getAll(workoutplanList.get(currentListId +1).getId()));
				manageWorkoutplan.setWorkoutplanId(workoutplanList.get(currentListId +1).getId());
				wpMapper.setCurrent(workoutplanList.get(currentListId +1).getId());
				Toast.makeText(getActivity(), workoutplanList.get(currentListId +1).getName() + " ist nun aktiv!", Toast.LENGTH_SHORT ).show();
				currentListId += 1;
				if(workoutplanList.size() <= currentListId +1) {
					nextButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_add));
				}
			}
			break;
		case R.id.Previous:
			if(workoutplanList.size() <= currentListId +1) {
				nextButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_navigation_next_item));}
				workoutplanTextView.setText(workoutplanList.get(currentListId -1).getName());
				manageWorkoutplan.updateListView(tdMapper.getAll(workoutplanList.get(currentListId -1).getId()));
				manageWorkoutplan.setWorkoutplanId(workoutplanList.get(currentListId - 1).getId());
				wpMapper.setCurrent(workoutplanList.get(currentListId -1).getId());
				Toast.makeText(getActivity(), workoutplanList.get(currentListId -1).getName() + " ist nun aktiv!", Toast.LENGTH_SHORT ).show();
				currentListId -= 1;
			if(currentListId == 0) {
				previousButton.setVisibility(View.INVISIBLE);
			}
			break;
		
		}	
	}
	
	public void setCurrentIdByWorkoutplanId(int workoutplanId) {
	  for(int i = 0; i <= workoutplanList.size() - 1; i++) {
		if(workoutplanList.get(i).getId() == workoutplanId) {
			currentListId = i;
		}
	  }
	}

	public static void setCurrentListId(int currentListId) {
		ActionBarWorkoutPlanPickerFragment.currentListId = currentListId;
	}	
	
	public static int getCurrentListId() {
		return currentListId;
	}
	
	/**
	 * Returns the previous workoutplan to set the current workoutplan
	 * if the one used now is deleted
	 * 
	 * @return
	 */
	public Workoutplan getPreviousWorkoutplan(){
		if (workoutplanList.size() > 1){
			if (currentListId != 0) return workoutplanList.get(currentListId -1);
			else return workoutplanList.get(currentListId +1);
		}
		return null;
	}
}
