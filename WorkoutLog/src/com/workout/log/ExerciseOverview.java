package com.workout.log;

import java.util.ArrayList;
import java.util.List;

import com.workout.log.bo.Exercise;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.data.*;
import com.example.workoutlog.R;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.dialog.ExerciseLongClickDialogFragment;
import com.workout.log.dialog.ExerciseLongClickDialogFragment.ExerciseSelectionDialogListener;
import com.workout.log.listAdapter.CustomDrawerAdapter;
import com.workout.log.listAdapter.ExerciseListAdapter;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Build;

public class ExerciseOverview extends ActionBarActivity implements OnItemLongClickListener, OnItemClickListener, ExerciseSelectionDialogListener  {
// Version 1.1 
	private ListView exerciseView; 
	private ArrayList<Exercise> exerciseList;
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private CustomDrawerAdapter adapter;

    private MenueListe l = new MenueListe();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exercise_overview);
		
		// Initializing
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                    GravityCompat.START);
        
        // Add Drawer Item to dataList
        adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item, l.getDataList());
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                    R.drawable.ic_drawer, R.string.drawer_open,
                    R.string.drawer_close) {
              public void onDrawerClosed(View view) {
                    getActionBar().setTitle(mTitle);
                    invalidateOptionsMenu(); // creates call to
                                                              // onPrepareOptionsMenu()
              }
              public void onDrawerOpened(View drawerView) {
                    getActionBar().setTitle(mDrawerTitle);
                    invalidateOptionsMenu(); // creates call to
                    // onPrepareOptionsMenu()
              }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);      
        
        /*
         * Anbindung an Datenbank
         * @author Eric Schmidt
         * @date 18.04.2014
         */
        
		exerciseView = (ListView) findViewById(R.id.exerciseOverviewList);
		
		//Select Current Workoutplan
		WorkoutplanMapper wMapper = new WorkoutplanMapper(this);
		Workoutplan w = wMapper.getCurrent();
		
		//Select all Trainingdays from the current Workoutplan
		TrainingDayMapper tMapper = new TrainingDayMapper(this);
		ArrayList<TrainingDay> tList = tMapper.getAll(w.getID());
		
		//Select Exercises from Trainingday
		ExerciseMapper eMapper = new ExerciseMapper(this);
		ArrayList<Exercise> eList = eMapper.getAllExercise(tList.get(0).getID());
		
		ExerciseListAdapter adapter = new ExerciseListAdapter(this, 0, eList);
		
		exerciseView.setAdapter(adapter);

		exerciseView.setOnItemLongClickListener(this);
		exerciseView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.exercise_overview_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		// The action bar home/up action should open or close the drawer.
	      // ActionBarDrawerToggle will take care of this.
	      if (mDrawerToggle.onOptionsItemSelected(item)) {
	            return true;
	      }
	 
	      
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Intent intent= null;
		switch (item.getItemId()){
			case R.id.menu_add:
				intent = new Intent();
				intent.setClass(this, ExerciseAdd.class);
				startActivity(intent);
				WorkoutplanMapper m = new WorkoutplanMapper(this);
				break;
			case R.id.menu_workoutplan:
				intent = new Intent();
				intent.setClass(this, WorkoutplanAdd.class);
				startActivity(intent);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		this.showDialogLongClickFragment();
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Exercise exercise;
		exercise = (Exercise) exerciseView.getItemAtPosition(position);
		openExerciseSpecific(exercise);
		
	}
	
	private void openExerciseSpecific (Exercise exercise){
		Intent intent = new Intent();
		intent.setClass(this, ExerciseSpecific.class);
		intent.putExtra("ExerciseID", exercise.getID());
		intent.putExtra("ExerciseName", exercise.getName());
		startActivity(intent);
	}
	
	public void showDialogLongClickFragment(){
		DialogFragment dialogFragment = ExerciseLongClickDialogFragment.newInstance();
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
	}

	@Override
	public void onExerciseSelectionItemLongClick(
			ExerciseLongClickDialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * A placeholder fragment containing a simple view.
	 * 
	 *
	 */
	public void SelectItem(int possition) { 
		Intent intent= null;
		switch(possition) {
		case 0:
			intent = new Intent();
			intent.setClass(this, WorkoutplanSelect.class);
			startActivity(intent);
			break;
		}
		
		/**
		 * TODOO
		 * 
		 */
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	      super.onConfigurationChanged(newConfig);
	      // Pass any configuration change to the drawer toggles
	      mDrawerToggle.onConfigurationChanged(newConfig);
	}
	private class DrawerItemClickListener implements
    ListView.OnItemClickListener {
		
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
	          long id) {
	    SelectItem(position);
	
	}
}
}
