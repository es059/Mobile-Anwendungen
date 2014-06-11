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
import com.workout.log.fragment.ActionBarSearchBarFragment;
import com.workout.log.fragment.ActionBarWorkoutPlanSubjectFragment;
import com.workout.log.listAdapter.CustomDrawerAdapter;
import com.workout.log.listAdapter.DefaultAddListAdapter;
import com.workout.log.listAdapter.TrainingDayListAdapter;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.os.Build;

public class WorkoutplanAdd extends Fragment implements OnItemClickListener {
	
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.workoutplan_add, container,false);	
		
		//ListView defaultListView = (ListView) findViewById(R.id.workoutplan_add_list);
		ListView trainingDayListView = (ListView) view.findViewById(R.id.workoutplan_trainingDay_list);
		
		/*
		//Default ListAdapter mit Hinzufügen-Eintrag
		ArrayList<Default> defaultAddList = new ArrayList<Default>();
		Default defaultTrainingDay = new Default("Hinzufügen", "(füge einen neue Trainingstag hinzu)");
		defaultAddList.add(defaultTrainingDay);
		DefaultAddListAdapter adapter = new DefaultAddListAdapter(this,0,defaultAddList);
		defaultListView.setAdapter(adapter);
		defaultListView.setOnItemClickListener(this);
		*/
		/**
		 * Add the searchBar fragment to the current fragment
		 */
	    FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.workoutplan_fragment, new ActionBarWorkoutPlanSubjectFragment(), "ActionBarWorkoutPlanSubjectFragment");
        transaction.addToBackStack(null);
        transaction.commit();

		setHasOptionsMenu(true);
	
		return view;
	}

	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.exercise_overview_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.menu_add:
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
		        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		        transaction.replace(R.id.fragment_container, new ManageTrainingDays(), "ManageTrainingDays");
		        transaction.addToBackStack(null);
		        transaction.commit();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		openTrainingDayAdd();	
	}
	
	public void openTrainingDayAdd(){
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.fragment_container, new ManageTrainingDays(), "ManageTrainingDays");
        transaction.addToBackStack(null);
        transaction.commit();
	}
}
