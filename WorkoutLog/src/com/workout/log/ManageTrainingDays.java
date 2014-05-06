package com.workout.log;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.example.workoutlog.R.id;
import com.example.workoutlog.R.layout;
import com.example.workoutlog.R.menu;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.TrainingDay;
import com.workout.log.data.Default;
import com.workout.log.data.MenueListe;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.dialog.TrainingDayAddDialogFragment;
import com.workout.log.dialog.WorkoutplanAddDialogFragment;
import com.workout.log.listAdapter.CustomDrawerAdapter;
import com.workout.log.listAdapter.DefaultAddListAdapter;
import com.workout.log.listAdapter.TrainingDayListAdapter;

import android.app.Activity;
import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.os.Build;

public class ManageTrainingDays extends Activity implements OnItemClickListener, OnItemLongClickListener{
	
	private ArrayList<TrainingDay> trainingDayList;
	private TrainingDayListAdapter trainingDayListAdapter;
	private ListView trainingDayListView; 
	private TrainingDayMapper tdMapper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.training_day_add);
		
		tdMapper = new TrainingDayMapper(this);
		trainingDayListView = (ListView) findViewById(R.id.trainingDay_add_list);
		trainingDayList = new ArrayList<TrainingDay>();
		trainingDayList = tdMapper.getAllTrainingDay();
		EditText search = (EditText) findViewById(R.id.searchbar_text);
		trainingDayListAdapter = new TrainingDayListAdapter(this, R.layout.listview_training_day, trainingDayList);
		trainingDayListView.setAdapter(trainingDayListAdapter);
		trainingDayListView.setOnItemClickListener(this);
		trainingDayListView.setOnItemLongClickListener(this);
		
		search.addTextChangedListener(new TextWatcher(){    
	          public void afterTextChanged(Editable s)
	          {
	              
	        	  trainingDayListAdapter.clear();
	        	  trainingDayList =   tdMapper.searchKeyString(String.valueOf(s));
	        	  
	        	  
	        	  trainingDayListAdapter = new TrainingDayListAdapter(getApplicationContext(),0,trainingDayList);
	        	  trainingDayListView.setAdapter(trainingDayListAdapter);
	        	  
	          }

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
	  });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.workoutplan_menu, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		switch (item.getItemId()){
		case R.id.menu_add:
			
			DialogFragment dialogFragment = TrainingDayAddDialogFragment.newInstance(this);
			dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		TrainingDay td = (TrainingDay) arg0.getItemAtPosition(arg2);
		int trainingDayId = td.getId();
		String trainingDayName = td.getName();
		openExerciseOverview(trainingDayId, trainingDayName);
		
	}
	
	public void openExerciseOverview(int trainingDayId, String trainingDayName){
		Intent intent = new Intent();
		intent.putExtra("trainingDayId", trainingDayId);
		intent.putExtra("trainingDayName", trainingDayName);
		intent.setClass(this, TrainingDayExerciseOverview.class);
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}

}
