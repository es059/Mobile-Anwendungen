package com.workout.log;

import java.util.ArrayList;
import com.example.workoutlog.R;
import com.workout.log.bo.Workoutplan;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.dialog.ExerciseLongClickDialogFragment;
import com.workout.log.dialog.ExerciseLongClickDialogFragment.ExerciseSelectionDialogListener;
import com.workout.log.listAdapter.WorkoutplanListAdapter;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class WorkoutplanSelect extends Fragment  implements OnItemLongClickListener, OnItemClickListener, ExerciseSelectionDialogListener{
	
	private ListView workoutplanListView;
	private ArrayList<Workoutplan> wList;
	private WorkoutplanMapper wMapper;
	private static final String PREF_FIRST_LAUNCH = "first";
	

	private int currentWorkoutplanId = 0;
	private String currentWorkoutplanName = "";
	
	private View view;
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		
		view = inflater.inflate(R.layout.workoutplan_select, container,false);	
		
		setHasOptionsMenu(true);
		return view;
	}
	/**
	 * Initialize the ListView content
	 * 
	 * @author Eric Schmidt
	 */
	@Override
	public void onResume(){
		super.onResume();
		workoutplanListView = (ListView) view.findViewById(R.id.workoutplanList);
		//Select the current Workoutplan
		wMapper = new WorkoutplanMapper(getActivity());
		wList = wMapper.getAll();
		
		WorkoutplanListAdapter adapter = new WorkoutplanListAdapter(getActivity(),0,wList);
		workoutplanListView.setAdapter(adapter);
		
		workoutplanListView.setOnItemClickListener(this);
		workoutplanListView.setOnItemLongClickListener(this);

		storeSharedPref(false);
	}
	/**
	 * Select an Item in a specific ListView
	 * 
	 * @param position
	 * @param listView
	 */
	public void selectItem(int position, ListView listView){
		//listView.performItemClick(listView.getAdapter().getView(position, null, null), position, listView.getAdapter().getItemId(position));
		this.onItemClick(listView, listView.getAdapter().getView(position, null, null), position, position);
	}
	
	
	/**
	 * Storing in Shared Preferences if the Application is starts it for the first time
	 * 
	 * @param false if not first time
	 */
	protected void storeSharedPref(Boolean firstTime){
		SharedPreferences isLogged = PreferenceManager.getDefaultSharedPreferences(getActivity());
		SharedPreferences.Editor editor = isLogged.edit();
		editor.putBoolean(PREF_FIRST_LAUNCH, firstTime);
		editor.commit();
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
	          long id) {
		
		//Alternative Lösung
	    Workoutplan w = (Workoutplan) workoutplanListView.getItemAtPosition(position);
	    wMapper.setCurrent(w.getId());
	    currentWorkoutplanId = w.getId();
	    currentWorkoutplanName = w.getName();
	    FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.fragment_container, new TrainingDaySelect(), "TrainingDaySelect");
      
	    
		/*
	       if(lastView==null){
	    	    view.setBackgroundResource(R.drawable.actionbar_orange);
	    	    Workoutplan w = (Workoutplan) workoutplanListView.getItemAtPosition(position);
	    	    wMapper.setCurrent(w.getId());
	            lastView=view;
	            }
	            else{
	            lastView.setBackgroundColor(Color.WHITE); 
	            view.setBackgroundResource(R.drawable.actionbar_orange);
	            wMapper.setCurrent(wList.get(position).getId());
	            lastView=view;
	            }
	         */
	}
	
	public void showDialogLongClickFragment(){
	//	DialogFragment dialogFragment = ExerciseLongClickDialogFragment.newInstance();
	//	dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		this.showDialogLongClickFragment();
		return true;
	}

	@Override
	public void onExerciseSelectionItemLongClick(
			ExerciseLongClickDialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}
	
	

	public int getCurrentWorkoutplanId() {
		return currentWorkoutplanId;
	}
	public void setCurrentWorkoutplanId(int currentWorkoutplanId) {
		this.currentWorkoutplanId = currentWorkoutplanId;
	}
	public String getCurrentWorkoutplanName() {
		return currentWorkoutplanName;
	}
	public void setCurrentWorkoutplanName(String currentWorkoutplanName) {
		this.currentWorkoutplanName = currentWorkoutplanName;
	}
	
	
	
}
