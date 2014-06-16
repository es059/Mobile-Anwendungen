package com.workout.log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceActual;
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.PerformanceActualMapper;
import com.workout.log.db.PerformanceTargetMapper;
import com.workout.log.fragment.ActionBarDatePickerFragment;
import com.workout.log.listAdapter.PerformanceActualListAdapter;
import com.workout.log.navigation.OnBackPressedListener;
import com.workout.log.navigation.OnHomePressedListener;

public class ExerciseSpecific extends Fragment {
  
	private ListView exerciseView;
	private Exercise exercise;
	private int exerciseId;
	private int trainingDayId;
	private EditText repetition;
	private EditText weight;
	private Boolean saveMode = false;
	
	private PerformanceActualListAdapter adapter;
	private ExerciseOverview exerciseOverview = new ExerciseOverview();
	private PerformanceActualMapper paMapper;
	private ArrayList<PerformanceActual> performanceActualList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.exercise_specific, container, false);
		
		/**
		 * Set the visibility of the NavigationDrawer to Invisible
		 */
		((HelperActivity) getActivity()).setNavigationDrawerVisibility(false);
		
		/**
		 * Load the top navigation fragment into the current fragment
		 */
	    FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.specific_dateTimePicker, new ActionBarDatePickerFragment(), "DateTimePicker");
        transaction.commit();
        
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		setHasOptionsMenu(true);
		
		/**
		 * Handles the behavior if the back button is pressed
		 */
		((HelperActivity)getActivity()).setOnBackPressedListener(new OnBackPressedListener(){
			@Override
			public void doBack() {
				savePerformanceActual();
				openExerciseOverview();	
			}		
		});
		/**
		 * Handles the behavior if the Home button in the actionbar is pressed
		 */
		((HelperActivity)getActivity()).setOnHomePressedListener(new OnHomePressedListener(){
			@Override
			public Intent doHome() {
				savePerformanceActual();
				openExerciseOverview();	
				return null;
			}		
		});
		
		exerciseView = (ListView) view.findViewById(R.id.exerciseSpecificList);
		
		/**
		 * Receive the arguments set by ExerciseOverview
		 */
		final Bundle transferExtras = getArguments();
		if (transferExtras != null){	
			try{
				trainingDayId = transferExtras.getInt("TrainingDayId");
				exerciseId = transferExtras.getInt("ExerciseID");
				getActivity().getActionBar().setTitle(transferExtras.getString("ExerciseName"));
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		return view;
		
	}
	/**
	 * Inzilation of the mapper classes
	 * 
	 */
	@Override
	public void onResume(){
		super.onResume();
		//Exercise Mapper + Object		
		ExerciseMapper eMapper = new ExerciseMapper(getActivity());
		exercise = eMapper.getExerciseById(exerciseId);
		//PerformanceActual Mapper + Object List
		paMapper = new PerformanceActualMapper(getActivity());
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
		PerformanceTargetMapper ptMapper = new PerformanceTargetMapper(getActivity());
		PerformanceTarget performanceTarget = ptMapper.getPerformanceTargetByExerciseId(exercise, trainingDayId);
		
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
		adapter = new PerformanceActualListAdapter(getActivity(), 0, pa);
		exerciseView.setAdapter(adapter);
	}
		
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.exercise_specific_menu, menu);
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
		Bundle data = new Bundle();
		
	    data.putInt("TrainingDayId",trainingDayId);
	    data.putBoolean("SaveMode",saveMode);
		
	    exerciseOverview = new ExerciseOverview();
	    exerciseOverview.setArguments(data);
	    
	    FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.fragment_container, exerciseOverview , "ExerciseOverview");
        transaction.addToBackStack(null);
        transaction.commit();
        
		/**
		 * Set the visibility of the NavigationDrawer to Visible
		 */
		((HelperActivity) getActivity()).setNavigationDrawerVisibility(true);
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
		Toast.makeText(getActivity(), "Neuen Satz hinzugefügt!", Toast.LENGTH_SHORT).show();
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
			paMapper = new PerformanceActualMapper(getActivity());
			paMapper.deletePerformanceActualById(performanceActual.getId());
			//Set the ArrayList on the current value
			performanceActualList = adapter.getPerformanceActualList();
			Toast.makeText(getActivity(), "Letzten Satz entfernt!", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(getActivity(), "Keine weiteren Sätze verfügbar!", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Save/Update all PerfromanceActual Objects in Database
	 * 
	 */
	public void savePerformanceActual(){ 
		PerformanceActualMapper pMapper = new PerformanceActualMapper(getActivity());
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


