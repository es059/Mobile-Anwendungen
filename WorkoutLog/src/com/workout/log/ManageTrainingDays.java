package com.workout.log;

import java.util.ArrayList;

import com.example.workoutlog.R;

import com.workout.log.bo.TrainingDay;

import com.workout.log.db.TrainingDayMapper;
import com.workout.log.dialog.TrainingDayAddDialogFragment;
import com.workout.log.fragment.ManageTrainingDaysSearchBar;
import com.workout.log.listAdapter.TrainingDayListAdapter;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
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

public class ManageTrainingDays extends Fragment implements OnItemClickListener, OnItemLongClickListener{

	private ArrayList<TrainingDay> trainingDayList;
	private TrainingDayListAdapter trainingDayListAdapter;
	private ListView trainingDayListView; 
	private TrainingDayMapper tdMapper;
	private int actualTrainingDayId;
	private String actualTrainingDayName;
	private View view;
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.training_day_add, container,false);
		
		return view;
	}
	/**
	 * Implements the LiveSearch capability of the EditView and loads 
	 * the Training Days into the ListView
	 * 
	 * @author Eric Schmidt
	 */
	@Override
	public void onResume(){
		super.onResume();
		
		tdMapper = new TrainingDayMapper(getActivity());
		trainingDayListView = (ListView) view.findViewById(R.id.trainingDay_add_list);
		trainingDayList = new ArrayList<TrainingDay>();
		trainingDayList = tdMapper.getAllTrainingDay();
		trainingDayListAdapter = new TrainingDayListAdapter(getActivity(), R.layout.listview_training_day, trainingDayList);
		trainingDayListView.setAdapter(trainingDayListAdapter);
		trainingDayListView.setOnItemClickListener(this);
		trainingDayListView.setOnItemLongClickListener(this);

		   
        /**
		 * Add the searchBar fragment to the current fragment
		 */
	    FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.add_searchBar, new ManageTrainingDaysSearchBar(), "ManageTrainingDaysSearchBar");
        transaction.addToBackStack(null);
        transaction.commit();

		setHasOptionsMenu(true);
	}
	
	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.workoutplan_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	   
		switch (item.getItemId()){
		case R.id.menu_add:

			DialogFragment dialogFragment = TrainingDayAddDialogFragment.newInstance(getActivity());
			dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		TrainingDay td = (TrainingDay) arg0.getItemAtPosition(arg2);
		actualTrainingDayId = td.getId();
		actualTrainingDayName = td.getName();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.fragment_container, new TrainingDayExerciseOverview(), "TrainingDayExerciseOverview");
        transaction.addToBackStack(null);
        transaction.commit();

	}


	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public int getActualTrainingDayId() {
		return actualTrainingDayId;
	}
	public String getActualTrainingDayName() {
		return actualTrainingDayName;
	}
	public void updateAdapter(ArrayList<TrainingDay> trainingDayList2) {
		 trainingDayListAdapter.clear();
		 trainingDayListAdapter = new TrainingDayListAdapter(getActivity(),0,trainingDayList);
		 trainingDayListView.setAdapter(trainingDayListAdapter);
		
	}
	
	
}