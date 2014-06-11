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

public class ActionBarWorkoutPlanPickerFragment extends Fragment implements OnClickListener  {
	private ImageButton pre;
	private ImageButton next;
	private TextView trainingDay;
	
	private ArrayList<Workoutplan> workoutplanList;
	private ArrayList<TrainingDay> trainingDayList;
	
	private WorkoutplanMapper wpMapper;
	private TrainingDayMapper tdMapper;
	private ManageWorkoutplan mW; 
	
	private static int tdId = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		
		//Reference ExerciseOverview Layout and set ListView 
		View view = inflater.inflate(R.layout.actionbar_training_day_picker_fragment, container,false);
		mW  = (ManageWorkoutplan) getActivity().getFragmentManager().findFragmentByTag("ManageWorkoutplan");
		pre = (ImageButton) view.findViewById(R.id.Previous);
		next = (ImageButton) view.findViewById(R.id.Next);
		trainingDay = (TextView) view.findViewById(R.id.trainingDayPicker);
		
		workoutplanList = new ArrayList<Workoutplan>();
		trainingDayList = new ArrayList<TrainingDay>();
		
		wpMapper = new WorkoutplanMapper(getActivity());
		tdMapper = new TrainingDayMapper(getActivity());
		workoutplanList = wpMapper.getAll();
		trainingDayList = tdMapper.getAll(workoutplanList.get(0).getId());
		
		trainingDay.setText(workoutplanList.get(tdId).getName());
		mW.setWorkoutplanId(workoutplanList.get(tdId).getId());
		
		pre.setOnClickListener(this);
		next.setOnClickListener(this);
		
	return view;
	}

	@Override
	public void onResume(){
		super.onResume();
		if(tdId == 0) {
		pre.setVisibility(View.INVISIBLE); }
	}
		


	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.Next:
			if(workoutplanList.size() <= tdId +1) {
				
				
				DialogFragment dialogFragment = WorkoutplanAddDialogFragment.newInstance(getActivity(), workoutplanList, tdId);
				dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
				pre.setVisibility(View.VISIBLE);
				
			}
			else {
				
			if(tdId == 0) {
				pre.setVisibility(View.VISIBLE);
			}
			trainingDay.setText(workoutplanList.get(tdId +1).getName());
			mW.addtoList(tdMapper.getAll(workoutplanList.get(tdId +1).getId()));
			mW.setWorkoutplanId(workoutplanList.get(tdId +1).getId());
			wpMapper.setCurrent(workoutplanList.get(tdId +1).getId());
			tdId += 1;
			
			if(workoutplanList.size() <= tdId +1) {
				next.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_add));
				
			}
			
			
			}
			break;
		case R.id.Previous:
			if(workoutplanList.size() <= tdId +1) {
				next.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_navigation_next_item));}
			trainingDay.setText(workoutplanList.get(tdId -1).getName());
			mW.addtoList(tdMapper.getAll(workoutplanList.get(tdId -1).getId()));
			mW.setWorkoutplanId(workoutplanList.get(tdId - 1).getId());
			wpMapper.setCurrent(workoutplanList.get(tdId -1).getId());
			tdId -= 1;
			if(tdId == 0) {
				pre.setVisibility(View.INVISIBLE);
			}
			break;
		
	}

	
	
	
	
	}
	public void settdIdByWorkoutplanId(int workoutplanId) {
	  for(int i = 0; i <= workoutplanList.size(); i++) {
		if(workoutplanList.get(i).getId() == workoutplanId) {
			tdId = i;
		}
	  }
	}

	
}
