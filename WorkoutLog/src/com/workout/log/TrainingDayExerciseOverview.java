package com.workout.log;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.MuscleGroup;
import com.workout.log.data.ExerciseItem;
import com.workout.log.data.MuscleGroupSectionItem;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.MuscleGroupMapper;
import com.workout.log.db.PerformanceTargetMapper;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.fragment.ActionBarTrainingDaySelectFragment;
import com.workout.log.listAdapter.OverviewAdapter;
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
import android.widget.ListView;
import android.widget.Toast;


public class TrainingDayExerciseOverview extends Fragment {
	private ListView exerciseListView;
	private ArrayList<Exercise> exerciseList;
	private ExerciseMapper eMapper;
	private int trainingDayId;
	private static TrainingDayMapper tdMapper;
	private PerformanceTargetMapper ptMapper;
	
    /**
     * Variables for the UpdateListView method
     */
	private ArrayList<MuscleGroup> mList;
	private ArrayList<ExerciseItem> listComplete;
	private ArrayList<Exercise> eListMuscleGroup;
	private OverviewAdapter adapter = null;
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.training_day_exercise_overview, container, false);
        /**
		 * Add the ActionBar fragment to the current fragment
		 */
	    FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.training_day_select_fragment, new ActionBarTrainingDaySelectFragment(), "ActionBarTrainingDaySelectFragment");
        transaction.commit();        
		
        return view;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		tdMapper = new TrainingDayMapper(getActivity());
		ptMapper = new PerformanceTargetMapper(getActivity());
		
		/**
		 * Set the visibility of the NavigationDrawer to Invisible
		 */
		((HelperActivity) getActivity()).setNavigationDrawerVisibility(false);
		
		/**
		 * Receive the arguments set by either ManageWorkoutplan or ManageTrainingDays
		 */
		eMapper = new ExerciseMapper(getActivity());
		exerciseListView = (ListView) getView().findViewById(R.id.ExerciseListView);
		exerciseList = new ArrayList<Exercise>();
		
		/**
		 * Get the current trainingDayId to load the corresponding exercise list
		 */
		final Bundle transferExtras = getArguments();
		if (transferExtras != null){	
			try{
				trainingDayId = transferExtras.getInt("TrainingDayId");
				updateListView();
				exerciseList = eMapper.getExerciseByTrainingDay(trainingDayId);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
				
		loadSwipeToDismiss();
        
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.workoutplan_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		else if( id == R.id.menu_add){
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
	        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	        transaction.replace(R.id.fragment_container, new ExerciseAddToTrainingDay(), "ExerciseAddToTrainingDay");
	        transaction.addToBackStack(null);
	        transaction.commit();
		}
		return super.onOptionsItemSelected(item);
	}

	public int getTrainingDayId() {
		return trainingDayId;
	}
	
	/**
	 * Update the ListView and provides for header for each
	 * MuscleGroup.
	 *
	 * @author Eric Schmidt
	 */
	public void updateListView(){
		/**
		 * Select all MuscleGroups
		 */
		MuscleGroupMapper mMapper = new MuscleGroupMapper(getActivity());
		mList = mMapper.getAll();
		/**
		 * Select Exercises from selected Trainingday and MuscleGroup 
		 */
		ExerciseMapper eMapper = new ExerciseMapper(getActivity());
		exerciseList = eMapper.getExerciseByTrainingDay(trainingDayId);
		listComplete = new ArrayList<ExerciseItem>();
		for (MuscleGroup m : mList){
			eListMuscleGroup = eMapper.getExerciseByMuscleGroup(exerciseList, m.getId());
			if (!eListMuscleGroup.isEmpty()){
				listComplete.add(new MuscleGroupSectionItem(m.getName()));
				listComplete.addAll(eListMuscleGroup);
			}
		}
		
		adapter = new OverviewAdapter(getActivity(), listComplete,trainingDayId);
		exerciseListView.setAdapter(adapter);
	}
	
	/**
	 * Implementation of Swipe to dissmiss function
	 */
	private void loadSwipeToDismiss(){
		SwipeDismissListViewTouchListener touchListener =
            new SwipeDismissListViewTouchListener(
        		exerciseListView,
                new SwipeDismissListViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                        	Exercise e = (Exercise) exerciseListView.getItemAtPosition(position);
                    		ptMapper.deletePerformanceTarget(trainingDayId, e.getId());
                        	tdMapper.exerciseDeleteFromTrainingDay(trainingDayId, e);
                        	adapter.remove(adapter.getItem(position));
                        	Toast.makeText(getActivity(), "Übung wurde gelöscht!", Toast.LENGTH_SHORT ).show();
                        }
                        adapter.notifyDataSetChanged();
                    }
        		});
        exerciseListView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        exerciseListView.setOnScrollListener(touchListener.makeScrollListener());
	}
}


