package com.workout.log;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.workout.log.bo.TrainingDay;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.dialog.TrainingDayAddToWorkoutplanDialogFragment;
import com.workout.log.listAdapter.TrainingDayListAdapter;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class TrainingDayAddToWorkoutplan extends Fragment implements OnItemClickListener, OnItemLongClickListener {


	ArrayList<TrainingDay> trainingDayList;
	TrainingDayListAdapter trainingDayListAdapter;
	TrainingDayMapper tdMapper;
	private int workoutplanId;
	private ManageWorkoutplan manageWorkoutplan;
	private View view;
	
	 @Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			super.onCreateView(inflater, container, savedInstanceState);
			
		view = inflater.inflate(R.layout.training_day_add, container,false);			
		manageWorkoutplan = (ManageWorkoutplan) getActivity().getFragmentManager().findFragmentByTag("ManageWorkoutplan");
		workoutplanId = manageWorkoutplan.getWorkoutplanId();
		tdMapper = new TrainingDayMapper(getActivity());
		ListView trainingDayListView = (ListView) view.findViewById(R.id.trainingDay_add_list);
		trainingDayList = new ArrayList<TrainingDay>();
		trainingDayList = tdMapper.getAllTrainingDay();
		trainingDayListAdapter = new TrainingDayListAdapter(getActivity(), R.layout.listview_training_day, trainingDayList);
		trainingDayListView.setAdapter(trainingDayListAdapter);
		trainingDayListView.setOnItemClickListener(this);
		trainingDayListView.setOnItemLongClickListener(this);
		
		setHasOptionsMenu(true);
		return view;
		
	}

	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.workoutplan_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		TrainingDay t = (TrainingDay) arg0.getItemAtPosition(arg2);
		int trainingDayID = t.getId();
		DialogFragment dialogFragment = TrainingDayAddToWorkoutplanDialogFragment.newInstance(getActivity(), trainingDayID, workoutplanId);
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
		
	}
	
	

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}
}
