package com.workout.log;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.example.workoutlog.R.id;
import com.example.workoutlog.R.layout;
import com.example.workoutlog.R.menu;
import com.workout.log.bo.TrainingDay;
import com.workout.log.data.*;
import com.workout.log.dialog.ExerciseLongClickDialogFragment;
import com.workout.log.dialog.ExerciseLongClickDialogFragment.ExerciseSelectionDialogListener;
import com.workout.log.listAdapter.CustomDrawerAdapter;
import com.workout.log.listAdapter.DefaultAddListAdapter;
import com.workout.log.listAdapter.TrainingDayListAdapter;

import android.app.Activity;
import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.os.Build;

public class TrainingDaySelect extends Fragment implements OnItemClickListener, OnItemLongClickListener, ExerciseSelectionDialogListener {

	private ListView trainingsDayList;
	private ArrayList<TrainingDay> list;
	private WorkoutplanSelect workoutplanSelect;
	
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.training_day_select, container,false);	
		
		workoutplanSelect = (WorkoutplanSelect) getActivity().getFragmentManager().findFragmentByTag("ExerciseAdd");
		
		trainingsDayList.setOnItemClickListener(this);
		trainingsDayList.setOnItemLongClickListener(this);

		getActivity().getActionBar().setTitle(workoutplanSelect.getCurrentWorkoutplanName());
		
		return view;
	}

	
	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.training_day_select, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}




	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		TrainingDay td;
		td = (TrainingDay) trainingsDayList.getItemAtPosition(position);
		openExerciseOverview(td);
		
	}

	private void openExerciseOverview(TrainingDay td) {
	/*	Intent intent = new Intent();
		intent.setClass(this, ExerciseOverview.class);
		intent.putExtra("mID", td.getId());
		intent.putExtra("mName", td.getName());
		startActivity(intent); */
		
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		this.showDialogLongClickFragment();
		return true;
	}
	public void showDialogLongClickFragment(){
	//	DialogFragment dialogFragment = ExerciseLongClickDialogFragment.newInstance();
	//	dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
	}
	@Override
	public void onExerciseSelectionItemLongClick(
			ExerciseLongClickDialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}

	
}
