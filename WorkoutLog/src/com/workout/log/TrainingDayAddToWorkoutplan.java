package com.workout.log;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.example.workoutlog.R.id;
import com.example.workoutlog.R.layout;
import com.example.workoutlog.R.menu;
import com.workout.log.bo.TrainingDay;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.dialog.ExerciseClickDialogFragment;
import com.workout.log.dialog.TrainingDayAddToWorkoutplanDialogFragment;
import com.workout.log.listAdapter.TrainingDayListAdapter;

import android.app.Activity;
import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.os.Build;

public class TrainingDayAddToWorkoutplan extends Activity implements OnItemClickListener, OnItemLongClickListener {


	ArrayList<TrainingDay> trainingDayList;
	TrainingDayListAdapter trainingDayListAdapter;
	TrainingDayMapper tdMapper;
	private int workoutplanId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.training_day_add);
		Bundle intent = getIntent().getExtras();
		workoutplanId = intent.getInt("WorkoutplanId");
		tdMapper = new TrainingDayMapper(this);
		ListView trainingDayListView = (ListView) findViewById(R.id.trainingDay_add_list);
		trainingDayList = new ArrayList<TrainingDay>();
		trainingDayList = tdMapper.getAllTrainingDay();
		trainingDayListAdapter = new TrainingDayListAdapter(this, R.layout.listview_training_day, trainingDayList);
		trainingDayListView.setAdapter(trainingDayListAdapter);
		trainingDayListView.setOnItemClickListener(this);
		trainingDayListView.setOnItemLongClickListener(this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.workoutplan_menu, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		TrainingDay t = (TrainingDay) arg0.getItemAtPosition(arg2);
		int trainingDayID = t.getId();
		DialogFragment dialogFragment = TrainingDayAddToWorkoutplanDialogFragment.newInstance(this, trainingDayID, workoutplanId);
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
		
	}
	
	

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}
}
