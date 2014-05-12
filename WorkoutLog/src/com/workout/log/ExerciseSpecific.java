package com.workout.log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceActual;
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.PerformanceActualMapper;
import com.workout.log.db.PerformanceTargetMapper;
import com.workout.log.fragment.ActionBarDatePickerFragment;
import com.workout.log.listAdapter.PerformanceActualListAdapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
public class ExerciseSpecific extends Activity {
  
	private ListView exerciseView;
	private Exercise exercise;
	private int exerciseId;
	private int trainingDayId;
	private EditText repetition;
	private EditText weight;
	private Boolean saveMode = false;
	
	private PerformanceActualListAdapter adapter;
	private PerformanceActualMapper paMapper;
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
				trainingDayId = intentExtras.getInt("TrainingDayId");
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
		performanceActualList = paMapper.getCurrentPerformanceActual(exercise, sp.format(new Date()));
		
		if (performanceActualList.isEmpty()){
			performanceActualList = prepareStandardListView();
			updateListView(performanceActualList);
		}else{
			updateListView(performanceActualList);
		}
	}
	
	/**
	 * Prepares the ListView for the case that there was no current PerformanceActual Object
	 * Mainly for the external call from <@see ActionBarDatePickerFragment>
	 * 
	 */
	public ArrayList<PerformanceActual> prepareStandardListView(){
		PerformanceTargetMapper ptMapper = new PerformanceTargetMapper(this);
		PerformanceTarget performanceTarget = ptMapper.getPerformanceTargetByExerciseId(exercise);
		
		performanceActualList = new ArrayList<PerformanceActual>();
		
		for (int i = 1;i <= performanceTarget.getSet(); i++){
			PerformanceActual pa = new PerformanceActual();
			pa.setExercise(exercise);
			pa.setSet(i);
			performanceActualList.add(pa);
		}
		return performanceActualList;
	}
	
	
	/**
	 * Update the ListView with a given ArrayList
	 * 
	 * @param ArrayList<PerformanceActual> 
	 * @author Eric Schmidt
	 */
	public void updateListView(ArrayList<PerformanceActual> pa){
		adapter = new PerformanceActualListAdapter(this, 0, pa);
		exerciseView.setAdapter(adapter);
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
		savePerformanceActual();
		openExerciseOverview();
	}
	
	/**
	 * Overrides the functionality of the Menu Home Button
	 * 
	 */
	@Override
	public Intent getParentActivityIntent() {
		savePerformanceActual();
		Bundle bundleanimation = ActivityOptions.makeCustomAnimation(this, 
				R.anim.slide_in_left,R.anim.slide_over_left).toBundle();
		Intent intent = new Intent();
		intent.setClass(this, ExerciseOverview.class);
		intent.putExtra("TrainingDayId", trainingDayId);
		intent.putExtra("SaveMode", saveMode);
		startActivity(intent,bundleanimation);
		
		/**
		 * tempIntent to "trick" the method
		 */
		Intent tempIntent = new Intent();
		tempIntent = null;
		return tempIntent;
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
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Opens the ExerciseOverview Activity and tells it which TrainingDay to open
	 * 
	 * @author Eric Schmidt
	 */
	public void openExerciseOverview(){
		Intent intent = new Intent();
		Bundle bundleanimation = ActivityOptions.makeCustomAnimation(this, 
				R.anim.slide_in_left,R.anim.slide_over_left).toBundle();
		intent.setClass(this, ExerciseOverview.class);
		intent.putExtra("TrainingDayId", trainingDayId);
		intent.putExtra("SaveMode", saveMode);
		startActivity(intent,bundleanimation);
	}
	
	/**
	 * Add a new PerformanceActual Object to the ListView
	 * 
	 */
	public void addPerformanceActualItem(){
		//New PerformanceActual Object
		PerformanceActual pa = new PerformanceActual();
		pa.setExercise(exercise);
		pa.setSet(performanceActualList.size() + 1);
		//Update Adapter + ListView
		adapter.add(pa);
		//Set the ArrayList on the current value
		performanceActualList = adapter.getPerformanceActualList();
		//Show the User a hint message
		Toast.makeText(this, "Neuen Satz hinzugefügt!", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Remove the last PerformanceActual Object from the ListView and the Database
	 * 
	 */
	public void removePerformanceActualItem(){
		if (!performanceActualList.isEmpty()){
			PerformanceActual performanceActual = performanceActualList.get(performanceActualList.size()-1);
			//Update Adapter + ListView
			adapter.remove(performanceActual);
			adapter.notifyDataSetChanged();
			exerciseView.invalidateViews();
			//Remove Entry from Database
			paMapper = new PerformanceActualMapper(this);
			paMapper.deletePerformanceActualById(performanceActual.getId());
			//Set the ArrayList on the current value
			performanceActualList = adapter.getPerformanceActualList();
			Toast.makeText(this, "Letzten Satz entfernt!", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, "Keine weiteren Sätze verfügbar!", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Save/Update all PerfromanceActual Objects in Database
	 * 
	 */
	public void savePerformanceActual(){ 
		PerformanceActualMapper pMapper = new PerformanceActualMapper(this);
		ActionBarDatePickerFragment dateFragment = (ActionBarDatePickerFragment) getFragmentManager().findFragmentById(R.id.specific_dateTimePicker);
		for(PerformanceActual item : performanceActualList){
			View v = exerciseView.getChildAt(item.getSet() -1);
			repetition = (EditText) v.findViewById(R.id.specific_edit_repetition);
			weight = (EditText) v.findViewById(R.id.specific_edit_weight);
			
			if (!repetition.getText().toString().isEmpty()){
				item.setRepetition(Integer.parseInt(repetition.getText().toString()));
			}
			if (!weight.getText().toString().isEmpty()){
				item.setWeight(Double.parseDouble(weight.getText().toString()));
			}
			/**
			 * This block checks if the current Day is Today. 
			 * Afterwards the Recordset it saved if either the repetition and weight 
			 * TextView is filled. After this every Entry in the ListView is saved.
			 * This ensures that if a user goes back to an old Entry and returns to the current
			 * all of the sets are saved.
			 */
			if (dateFragment.isToday()){
				if (saveMode == true){
					PerformanceActual pa = pMapper.savePerformanceActual(item);
				}else if (item.getRepetition() != 0 || item.getWeight() != 0.0){
					PerformanceActual pa = pMapper.savePerformanceActual(item);
					saveMode = true;
				}else{
					saveMode = false;
				}
			}
		}
	}
	
	/**
	 * Returns the current Exercise
	 * 
	 * @param Exercise 
	 * @author Eric Schmidt
	 */
	public Exercise getExercise(){
		return exercise;
	}
}


