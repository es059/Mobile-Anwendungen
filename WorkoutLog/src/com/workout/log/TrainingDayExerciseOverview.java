package com.workout.log;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.PerformanceTargetMapper;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.fragment.ActionBarSearchBarFragment;
import com.workout.log.fragment.ActionBarTrainingDaySelectFragment;
import com.workout.log.listAdapter.ExerciseListAdapter;
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
	private ExerciseListAdapter exerciseListAdapter;
	private ExerciseMapper eMapper;
	private int trainingDayId;
	private static TrainingDayMapper tdMapper;
	private ManageTrainingDays manageTrainingDays;
	private PerformanceTargetMapper ptMapper;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.training_day_exercise_overview, container, false);
		
		manageTrainingDays = (ManageTrainingDays) getActivity().getFragmentManager().findFragmentByTag("ManageTrainingDays");
		tdMapper = new TrainingDayMapper(getActivity());
		ptMapper = new PerformanceTargetMapper(getActivity());
		// Übergabe der Trainingtags-ID über das Intent 
		trainingDayId = manageTrainingDays.getActualTrainingDayId();
		
		
		eMapper = new ExerciseMapper(getActivity());
		exerciseListView = (ListView) view.findViewById(R.id.ExerciseListView);
		exerciseList = new ArrayList<Exercise>();
		exerciseList = eMapper.getExerciseByTrainingDay(trainingDayId);
		exerciseListAdapter = new ExerciseListAdapter(getActivity(), R.layout.listview_exercise, exerciseList);
		exerciseListView.setAdapter(exerciseListAdapter);
		
		/**
		 * Implementierung Swipe to dissmiss Funktion
		 */
		final Toast toast = Toast.makeText(getActivity(), "Übung wurde gelöscht!", Toast.LENGTH_SHORT );
		
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
                            		int i = e.getTrainingDayHasExerciseId();
                            		ptMapper.deletePerformanceTarget(trainingDayId, e.getId());
                                	tdMapper.exerciseDeleteFromTrainingDay(i);;
                                	exerciseListAdapter.remove(exerciseListAdapter.getItem(position));
                                	toast.show();
                                    
                                    
                                }
                                exerciseListAdapter.notifyDataSetChanged();
                            }
                        });
        exerciseListView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        exerciseListView.setOnScrollListener(touchListener.makeScrollListener());
        
        /**
		 * Add the ActionBar fragment to the current fragment
		 */
	    FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.training_day_select_fragment, new ActionBarTrainingDaySelectFragment(), "ActionBarTrainingDaySelectFragment");
        transaction.addToBackStack(null);
        transaction.commit();

		setHasOptionsMenu(true);
        
        return view;
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

	
	}


