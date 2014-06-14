package com.workout.log;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.workout.log.bo.TrainingDay;
import com.workout.log.db.PerformanceTargetMapper;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.dialog.TrainingDayAddDialogFragment;
import com.workout.log.dialog.TrainingDayAddToWorkoutplanDialogFragment;
import com.workout.log.fragment.TrainingDaysSearchBarFragment;
import com.workout.log.listAdapter.SwipeDismissListViewTouchListener;
import com.workout.log.listAdapter.TrainingDayListAdapter;
import com.workout.log.navigation.OnBackPressedListener;
import com.workout.log.navigation.OnHomePressedListener;

import android.app.DialogFragment;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Same Layout as ManageTrainingDays but with a redirect to ManageWorkoutplan
 * 
 * @author Eric Schmidt
 */
public class TrainingDayAddToWorkoutplan extends Fragment implements OnItemClickListener{
	private ArrayList<TrainingDay> trainingDayList;
	private TrainingDayListAdapter trainingDayListAdapter;
	private TrainingDayMapper tdMapper;
	private ListView trainingDayListView; 
	private int workoutplanId;
	private ManageWorkoutplan manageWorkoutplan;
	private View view;
	
	 @Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			super.onCreateView(inflater, container, savedInstanceState);
			view = inflater.inflate(R.layout.training_day_add, container,false);			
			
			/**
			 * Set the visibility of the NavigationDrawer to Invisible
			 */
			((HelperActivity) getActivity()).setNavigationDrawerVisibility(false);
			
			/**
			 * Handles the behavior if the back button is pressed
			 */
			((HelperActivity)getActivity()).setOnBackPressedListener(new OnBackPressedListener(){
				@Override
				public void doBack() {
					openManageWorkoutplan();
				}		
			});
			
			/**
			 * Handles the behavior if the Home button in the actionbar is pressed
			 */
			((HelperActivity)getActivity()).setOnHomePressedListener(new OnHomePressedListener(){
				@Override
				public Intent doHome() {
					openManageWorkoutplan();
					return null;
				}		
			});
			
			setHasOptionsMenu(true);
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
		
		manageWorkoutplan = (ManageWorkoutplan) getActivity().getFragmentManager().findFragmentByTag("ManageWorkoutplan");
		workoutplanId = manageWorkoutplan.getWorkoutplanId();
		tdMapper = new TrainingDayMapper(getActivity());
		trainingDayListView = (ListView) view.findViewById(R.id.trainingDay_add_list);
		trainingDayList = new ArrayList<TrainingDay>();
		trainingDayList = tdMapper.getAllTrainingDay();
		trainingDayListAdapter = new TrainingDayListAdapter(getActivity(), R.layout.listview_training_day, trainingDayList);
		trainingDayListView.setAdapter(trainingDayListAdapter);
		trainingDayListView.setOnItemClickListener(this);
	   
		loadSwipeToDismiss();
		
        /**
		 * Add the searchBar fragment to the current fragment
		 */
	    FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.add_searchBar, new TrainingDaysSearchBarFragment(this), "ManageTrainingDaysSearchBar");
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
			DialogFragment dialogFragment = TrainingDayAddDialogFragment.newInstance(getActivity(), trainingDayListAdapter);
			dialogFragment.show(this.getFragmentManager(), "Open Exercise Add Dialog on Click");
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		TrainingDay t = (TrainingDay) arg0.getItemAtPosition(arg2);
		int trainingDayID = t.getId();
		DialogFragment dialogFragment = TrainingDayAddToWorkoutplanDialogFragment.newInstance(getActivity(), trainingDayID, workoutplanId);
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
		
	}
	
	public void updateAdapter(ArrayList<TrainingDay> trainingDayList) {
		trainingDayListAdapter.clear();
		trainingDayListAdapter = new TrainingDayListAdapter(getActivity(),0,trainingDayList);
		trainingDayListView.setAdapter(trainingDayListAdapter);	
	}
	
	public void openManageWorkoutplan(){
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
    	transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    	transaction.replace(R.id.fragment_container, new ManageWorkoutplan() , "ExerciseOverview");
    	transaction.addToBackStack(null);
    	transaction.commit();
    	
		/**
		 * Set the visibility of the NavigationDrawer to Visible
		 */
		((HelperActivity) getActivity()).setNavigationDrawerVisibility(true);
	}
	
	/**
	 * Implementation of Swipe to dismiss function
	 */
	private void loadSwipeToDismiss(){
		SwipeDismissListViewTouchListener touchListener =
            new SwipeDismissListViewTouchListener(trainingDayListView,
                new SwipeDismissListViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }
                    @Override
                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                        	TrainingDay trainingDay = (TrainingDay) trainingDayListView.getItemAtPosition(position);                  		
                    		/**
                    		 * So ensure referential integrity the data is to be deleted from the tables
                    		 * TrainingDay, WorkoutplanhasTrainingDay, TrainingDayhasExercise, PerformanceTarget
                    		 */
                    		TrainingDayMapper tMapper = new TrainingDayMapper(getActivity());
                    		PerformanceTargetMapper pMapper = new PerformanceTargetMapper(getActivity());
                    		
                    		tMapper.delete(trainingDay);
                    		tMapper.deleteTrainingDayFromAllWorkoutplan(trainingDay.getId());
                    		pMapper.deleteTrainingDayFromAllPerformanceTarget(trainingDay.getId());
                    		
                    		trainingDayListAdapter.remove(trainingDayListAdapter.getItem(position));
                        	Toast.makeText(getActivity(), "Trainingstag wurde gelöscht!", Toast.LENGTH_SHORT ).show();
                        }
                        trainingDayListAdapter.notifyDataSetChanged();
                    }
        		});
		trainingDayListView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
		trainingDayListView.setOnScrollListener(touchListener.makeScrollListener());
	}
	
}
