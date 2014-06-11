package com.workout.log;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.fragment.ActionBarWorkoutPlanPickerFragment;
import com.workout.log.listAdapter.SwipeDismissListViewTouchListener;
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
import android.widget.ListView;

public class ManageWorkoutplan extends Fragment implements OnItemClickListener {
	
	
	private DynamicListView listView;
	private ArrayList<Workoutplan> workoutplanList;
	private ArrayList<TrainingDay> trainingDayList;
	private StableArrayAdapter adapter12;
	private WorkoutplanMapper wpMapper;
	private TrainingDayMapper tdMapper;
	private int workoutplanId =1;
	int tdId = 0;
	private View view;
	
	
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.activity_manage_workoutplan, container,false);
		
		return view;
	}
	
	/**
	 * Implements the SwipeToDelete capability of the ListView and loads 
	 * the Workoutplan into the ListView
	 * 
	 * @author Eric Schmidt
	 */
	@Override
	public void onResume(){
		super.onResume();
		workoutplanList = new ArrayList<Workoutplan>();
		trainingDayList = new ArrayList<TrainingDay>();

		wpMapper = new WorkoutplanMapper(getActivity());
		tdMapper = new TrainingDayMapper(getActivity());
		workoutplanList = wpMapper.getAll();
		for(int i = 0; i < workoutplanList.size(); i++) {
			if(workoutplanList.get(i).getId() == workoutplanId) {
				tdId = i;
			}}
		trainingDayList = tdMapper.getAll(workoutplanList.get(tdId).getId());
		
		adapter12 = new StableArrayAdapter(getActivity(), R.layout.listview_training_day, trainingDayList);
		listView = (DynamicListView) view.findViewById(R.id.TrainingDayList);
		listView.setCheeseList(trainingDayList);
		listView.setAdapter(adapter12);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); 
	
		/**
		 * Create a ListView-specific touch listener. ListViews are given special treatment because
		 * by default they handle touches for their list items... i.e. they're in charge of drawing
		 * the pressed state (the list selector), handling list item clicks, etc.
		 * 
		 * @author Remi Tessier
		 */
		SwipeDismissListViewTouchListener touchListener =
               new SwipeDismissListViewTouchListener(
               		listView,
                       new SwipeDismissListViewTouchListener.DismissCallbacks() {
                           @Override
                           public boolean canDismiss(int position) {
                               return true;
                           }

                           @Override
                           public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                               for (int position : reverseSortedPositions) {
                               	TrainingDay td = (TrainingDay) listView.getItemAtPosition(position);
                           		int i = td.getId();
                           		int primarykey = td.getTrainingDayHasWorkoutplanId();
                           		tdMapper.deleteTrainingDayFromWorkoutplan(i, workoutplanId, primarykey);
                             
                           	
                           		trainingDayList.remove(position);
                           		adapter12 = new StableArrayAdapter(getActivity(), R.layout.listview_training_day, trainingDayList);
                           		((DynamicListView) listView).setCheeseList(trainingDayList);
                           		listView.setAdapter(adapter12);
                           		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); 
                               	
                                   
                                   
                               }
                           //    adapter12.notifyDataSetChanged();
                           }
                       });
		listView.setOnTouchListener(touchListener);
		// Setting this scroll listener is required to ensure that during ListView scrolling,
		// we don't look for swipes.
		listView.setOnScrollListener(touchListener.makeScrollListener());
		
		/**
		 * Add the searchBar fragment to the current fragment
		 */
	    FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.specific_dateTimePicker, new ActionBarWorkoutPlanPickerFragment(), "ActionBarWorkoutPlanPickerFragment");
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
			 FragmentTransaction transaction = getFragmentManager().beginTransaction();
		     transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		     transaction.replace(R.id.fragment_container, new TrainingDayAddToWorkoutplan(), "TrainingDayAddToWorkoutplan");
		     transaction.addToBackStack(null);
		     transaction.commit();
			break; 
			}
		return super.onOptionsItemSelected(item);
	}

	//

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		
		
	}
	public void addtoList(ArrayList<TrainingDay> trainingdayList1) {
		adapter12 = new StableArrayAdapter(getActivity(), R.layout.listview_training_day, trainingdayList1);
		listView.setCheeseList(trainingdayList1);
	    listView.setAdapter(adapter12);
	    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); 
	}

	public void setWorkoutplanId(int id) {
	 workoutplanId = id;
		}

	public int getWorkoutplanId() {
		return workoutplanId;
	}
	
	
	
	
	
	
}
