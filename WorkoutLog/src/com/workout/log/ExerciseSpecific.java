package com.workout.log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.example.workoutlog.R;
import com.example.workoutlog.R.id;
import com.example.workoutlog.R.layout;
import com.example.workoutlog.R.menu;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceActual;
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.data.MenueListe;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.PerformanceActualMapper;
import com.workout.log.db.PerformanceTargetMapper;
import com.workout.log.listAdapter.CustomDrawerAdapter;
import com.workout.log.listAdapter.PerformanceActualListAdapter;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.os.Build;

public class ExerciseSpecific extends Activity {
  
	private ListView exerciseView;
	private Exercise exercise;
	private int exerciseId;
	private EditText repetition;
	private EditText weight;
	
	private PerformanceActualListAdapter adapter;
	PerformanceActualMapper paMapper;
	private ArrayList<PerformanceActual> performanceActualList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exercise_specific);
		
		getActionBar().setHomeButtonEnabled(true);
		exerciseView = (ListView) findViewById(R.id.exerciseSpecificList);
		
		//Übergabe der Exercise ID und Name
		final Bundle intentExtras = getIntent().getExtras();
		if (intentExtras != null){	
			try{
				exerciseId = intentExtras.getInt("ExerciseID");
				getActionBar().setTitle(intentExtras.getString("ExerciseName"));
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * Inzilation of the mapper classes
	 * 
	 */
	@Override
	protected void onResume(){
		super.onResume();
		//Exercise Mapper + Object		
		ExerciseMapper eMapper = new ExerciseMapper(this);
		exercise = eMapper.getExerciseById(exerciseId);
		//PerformanceActual Mapper + Object Liste
		paMapper = new PerformanceActualMapper(this);
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		performanceActualList = paMapper.getPerformanceActualByExerciseId(exercise, sp.format(new Date()));
		
		if (performanceActualList.isEmpty()){
			PerformanceTargetMapper ptMapper = new PerformanceTargetMapper(this);
			PerformanceTarget performanceTarget = ptMapper.getPerformanceTargetByExerciseId(exercise);
			
			performanceActualList = new ArrayList<PerformanceActual>();
			
			for (int i = 1;i <= performanceTarget.getSet(); i++){
				PerformanceActual pa = new PerformanceActual();
				pa.setExercise(exercise);
				pa.setSet(i);
				performanceActualList.add(pa);
			}
			//ListAdapter
			adapter = new PerformanceActualListAdapter(this, 0, performanceActualList);
			exerciseView.setAdapter(adapter);
		}else{
			adapter = new PerformanceActualListAdapter(this, 0, performanceActualList);
			exerciseView.setAdapter(adapter);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.exercise_specific_menu, menu);
		return true;
	}
	/**
	 * If the user press the Back Button, the content will be saved
	 * 
	 */
	@Override 
	public void onBackPressed(){
		super.onBackPressed();
		savePerformanceActual();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id){
			case R.id.menu_add:
				addPerformanceActualItem();
				break;
			case R.id.menu_delete:
				removePerformanceActualItem();
				break;
			case android.R.id.home:
				savePerformanceActual();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Add a new PerfromanceActual Object to the ListView
	 * 
	 */
	public void addPerformanceActualItem(){
		//New PerformanceActual Object
		PerformanceActual pa = new PerformanceActual();
		pa.setExercise(exercise);
		pa.setSet(performanceActualList.size() + 1);
		performanceActualList.add(pa);
		//Update Adapter + ListView
		adapter.add(pa);
		adapter.notifyDataSetChanged();
		exerciseView.invalidateViews();
	}
	
	/**
	 * Save/Update all PerfromanceActual Objects in Database
	 * 
	 */
	public void savePerformanceActual(){ 
		PerformanceActualMapper pMapper = new PerformanceActualMapper(this);

		for(PerformanceActual item : performanceActualList){
			if (item.getRepetition() != 0 || item.getWeight() != 0.0){
				View v = exerciseView.getChildAt(item.getSet() -1);
				repetition = (EditText) v.findViewById(R.id.specific_edit_repetition);
				weight = (EditText) v.findViewById(R.id.specific_edit_weight);
				
				if (!repetition.getText().toString().isEmpty()){
					item.setRepetition(Integer.parseInt(repetition.getText().toString()));
				}
				if (!weight.getText().toString().isEmpty()){
					item.setRepetition(Integer.parseInt(weight.getText().toString()));
				}			
				PerformanceActual pa = pMapper.savePerformanceActual(item);	
			}
		}
	}
	
	/**
	 * Remove the last PerfromanceActual Object from the ListView
	 * 
	 */
	public void removePerformanceActualItem(){
		PerformanceActual performanceActual = performanceActualList.get(performanceActualList.size()-1);
		//Update Adapter + ListView
		adapter.remove(performanceActual);
		adapter.notifyDataSetChanged();
		exerciseView.invalidateViews();
		//Remove Entry from Database
		paMapper = new PerformanceActualMapper(this);
		paMapper.deletePerformanceActualById(performanceActual.getId());
	}
}


