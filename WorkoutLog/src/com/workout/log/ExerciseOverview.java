package com.workout.log;

import java.util.ArrayList;

import com.workout.log.bo.Exercise;
import com.workout.log.bo.MuscleGroup;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.data.*;
import com.example.workoutlog.R;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.MuscleGroupMapper;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.dialog.ExerciseLongClickDialogFragment;
import com.workout.log.dialog.ExerciseLongClickDialogFragment.ExerciseSelectionDialogListener;
import com.workout.log.fragment.ActionBarTrainingDayPickerFragment;
import com.workout.log.listAdapter.CustomDrawerAdapter;
import com.workout.log.listAdapter.OverviewAdapter;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.app.DialogFragment;
import android.content.Context;
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
import android.widget.Toast;

public class ExerciseOverview extends ActionBarActivity implements OnItemClickListener, ExerciseSelectionDialogListener  {

	private static ListView exerciseView; 
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private CustomDrawerAdapter adapter;
    private MenueListe l = new MenueListe();
    
    /**
     * Variabels for the UpdateListView methode
     */
	private ArrayList<TrainingDay> tList;
	private ArrayList<Exercise> eList;
	private ArrayList<MuscleGroup> mList;
	private ArrayList<ExerciseItem> listComplete;
	private ArrayList<Exercise> eListMuscleGroup;
	private MuscleGroupMapper mMapper;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exercise_overview);
		
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
     * If the Activity was called through a intent, 
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
		if (intentExtras != null){	
			try{
				if (intentExtras.getBoolean("SaveMode")){
					Toast.makeText(this, "Daten wurden gespeichert", Toast.LENGTH_SHORT).show();
				}
				int trainingDayId = intentExtras.getInt("TrainingDayId");
				ActionBarTrainingDayPickerFragment actionBarTrainingDayPickerFragment = (ActionBarTrainingDayPickerFragment) getFragmentManager().findFragmentById(R.id.overview_trainingDayPicker);
				actionBarTrainingDayPickerFragment.setCurrentTrainingDay(trainingDayId);
			} catch (Exception e){
				e.printStackTrace();
			}
		}else{	
			ExerciseListViewUpdate();
		}
		exerciseView.setOnItemClickListener(this);
	}
	
	  
	/**
	 * Updates Exercise ListViews using the ExerciseListAdapter. 
	 * Ensures that there are no unnecessary Database queries
	 * if the ArrayList<TrainingDay> is already referenced 
	 * 
	 * @param context 
	 * @param trainingDayId 
	 * @author Eric Schmidt
	 */
	public void ExerciseListViewUpdate(){
		exerciseView = (ListView) findViewById(R.id.exerciseOverviewList);
		if (tList == null){
			//Select Current Workoutplan
			WorkoutplanMapper wMapper = new WorkoutplanMapper(this);
			Workoutplan w = wMapper.getCurrent();
			//Select all Trainingdays from the current Workoutplan
			TrainingDayMapper tMapper = new TrainingDayMapper(this);
			tList = tMapper.getAll(w.getId());
		}
		if (!tList.isEmpty()){
			if (mList == null){	
				//Select all MuscleGroups
				MuscleGroupMapper mMapper = new MuscleGroupMapper(this);
				mList = mMapper.getAll();
			}
			//Select Exercises from Selected Trainingday and MuscleGroup 
			ExerciseMapper eMapper = new ExerciseMapper(this);
			eList = eMapper.getExerciseByTrainingDay(tList.get(0).getId());
			listComplete = new ArrayList<ExerciseItem>();
			for (MuscleGroup m : mList){
				eListMuscleGroup = eMapper.getExerciseByMuscleGroup(eList, m.getId());
				if (!eListMuscleGroup.isEmpty()){
					listComplete.add(new MuscleGroupSectionItem(m.getName()));
					listComplete.addAll(eListMuscleGroup);
				}
			}
			
			OverviewAdapter adapter = new OverviewAdapter(this, listComplete);
			exerciseView.setAdapter(adapter);
		}
	}
	
	/**
	 * If TrainingDayId is known use this method
	 * 
	 * @param context
	 * @param trainingDayId
	 * @author Eric Schmidt
	 */
	public void ExerciseListViewUpdate(int trainingDayId){
		exerciseView = (ListView) findViewById(R.id.exerciseOverviewList);
		if (mList == null){	
			//Select all MuscleGroups
			MuscleGroupMapper mMapper = new MuscleGroupMapper(this);
			mList = mMapper.getAll();
		}
		//Select Exercises from Selected Trainingday and MuscleGroup 
		ExerciseMapper eMapper = new ExerciseMapper(this);
		eList = eMapper.getExerciseByTrainingDay(trainingDayId);
		listComplete = new ArrayList<ExerciseItem>();
		for (MuscleGroup m : mList){
			eListMuscleGroup = eMapper.getExerciseByMuscleGroup(eList, m.getId());
			if (!eListMuscleGroup.isEmpty()){
				listComplete.add(new MuscleGroupSectionItem(m.getName()));
				listComplete.addAll(eListMuscleGroup);
			}
		}
		
		OverviewAdapter adapter = new OverviewAdapter(this, listComplete);
		exerciseView.setAdapter(adapter);
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.exercise_overview_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	     if (mDrawerToggle.onOptionsItemSelected(item)) {
	            return true;
	      }
		Intent intent= null;
		switch (item.getItemId()){
			case R.id.menu_add:
				intent = new Intent();
				intent.setClass(this, ExerciseAdd.class);
				startActivity(intent);
				WorkoutplanMapper m = new WorkoutplanMapper(this);
				break;
		}
		return super.onOptionsItemSelected(item);
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
	
/*	public void showDialogLongClickFragment(){
		DialogFragment dialogFragment = ExerciseLongClickDialogFragment.newInstance();
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
	}
*/
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
				intent.setClass(this, ManageWorkoutplan.class);
				startActivity(intent);
				break;
			case 2: 
				intent = new Intent();
				intent.setClass(this, ManageTrainingDays.class);
				startActivity(intent);
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
