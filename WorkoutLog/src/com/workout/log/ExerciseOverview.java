package com.workout.log;

import java.util.ArrayList;

import com.workout.log.bo.Exercise;
import com.workout.log.data.*;
import com.example.workoutlog.R;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.dialog.ExerciseLongClickDialogFragment;
import com.workout.log.dialog.ExerciseLongClickDialogFragment.ExerciseSelectionDialogListener;
import com.workout.log.fragment.ActionBarTrainingDayPickerFragment;
import com.workout.log.listAdapter.CustomDrawerAdapter;

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
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ExerciseOverview extends ActionBarActivity implements OnItemLongClickListener, OnItemClickListener, ExerciseSelectionDialogListener  {

	private static ListView exerciseView; 
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private CustomDrawerAdapter adapter;
    private UpdateListView updateOverview; 
    private MenueListe l = new MenueListe();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exercise_overview);
		
		getActionBar().setDisplayShowTitleEnabled(false);
		
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
        mDrawerLayout.setDrawerListener(mDrawerToggle);      
	}
    /**
     * Calls the <code>UpdateListView</code> Singleton Constructor for the first time 
     * and sets the ListView reference. If the Activity was called through a intent, 
     * the ListView is filled by a method call in the fragment
     * 
     * @author Eric Schmidt
     * @date 18.04.2014
     */  
	@Override
	protected void onResume(){
		super.onResume();
		final Bundle intentExtras = getIntent().getExtras();
		exerciseView = (ListView) findViewById(R.id.exerciseOverviewList);
		updateOverview = UpdateListView.updateListView(exerciseView);
		if (intentExtras != null){	
			try{
			int trainingDayId = intentExtras.getInt("TrainingDayId");
			ActionBarTrainingDayPickerFragment actionBarTrainingDayPickerFragment = (ActionBarTrainingDayPickerFragment) getFragmentManager().findFragmentById(R.id.overview_trainingDayPicker);
			actionBarTrainingDayPickerFragment.setCurrentTrainingDay(trainingDayId);
			} catch (Exception e){
				e.printStackTrace();
			}
		}else{	
			updateOverview.ExerciseListViewUpdate(this);
		}
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
	/**
	 * Method to open the ExerciseSpecific Activity with the selected Exercise and the current TrainingDay
	 * 
	 * @param exercise
	 * @author Eric Schmidt
	 */
	private void openExerciseSpecific (Exercise exercise){
		Intent intent = new Intent();
		ActionBarTrainingDayPickerFragment actionBarTrainingDayPickerFragment = 
				(ActionBarTrainingDayPickerFragment) getFragmentManager().findFragmentById(R.id.overview_trainingDayPicker);
		intent.setClass(this, ExerciseSpecific.class);
		intent.putExtra("ExerciseID", exercise.getId());
		intent.putExtra("ExerciseName", exercise.getName());
		intent.putExtra("TrainingDayId", actionBarTrainingDayPickerFragment.getCurrentTrainingDay().getId());
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
				intent.setClass(this, ExerciseOverview.class);
				startActivity(intent);
				break;
			case 1: 
				intent = new Intent();
				intent.setClass(this, WorkoutplanSelect.class);
				startActivity(intent);
				break;
			case 2: 
				break;
			case 3: 
				intent = new Intent();
				intent.setClass(this, ExerciseAdd.class);
				startActivity(intent);
				break;
			case 4: 
				intent = new Intent();
				intent.setClass(this, GraphActivity.class);
				startActivity(intent);
				break;
		}
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
