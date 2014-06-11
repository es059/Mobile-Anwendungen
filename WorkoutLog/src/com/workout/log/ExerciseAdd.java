package com.workout.log;

import java.util.ArrayList;
import com.example.workoutlog.R;
import com.workout.log.listAdapter.*;
import com.workout.log.bo.Exercise;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.dialog.ExerciseAddDialogFragment;
import com.workout.log.dialog.ExerciseLongClickDialogFragment;
import com.workout.log.dialog.ExerciseLongClickDialogFragment.ExerciseSelectionDialogListener;
import com.workout.log.fragment.ActionBarSearchBarFragment;
import com.workout.log.listAdapter.ExerciseListWithoutSetsRepsAdapter;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 *  With this fragment the user can manage his exercises. You can delete, add, and update exercises. 
 * @author remi
 *
 */
public class ExerciseAdd extends Fragment implements ExerciseSelectionDialogListener, OnItemLongClickListener {

	private ArrayList<Exercise> List;
	private ExerciseListWithoutSetsRepsAdapter listAdapter;
	private ListView exerciseListView;
	private DialogFragment dialogFragment;
	
	
	private  ExerciseMapper em;
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.exercise_add, container, false);
		// Iinitialize the exerciseMapper
		em = new ExerciseMapper(getActivity());
		// Initialize the ArrayList for the ListView Data
		List = new ArrayList<Exercise>();
		// Get All Exercises from the db and put them into the list
		List = em.getAll();
		
		// initialize the ListView
		exerciseListView = (ListView) view.findViewById(R.id.add_exerciseList);
		// initialize the ListView Adapter
		listAdapter = new ExerciseListWithoutSetsRepsAdapter(getActivity() , R.layout.listview_exercise_without_repssets, List);
		// connect the ListView with the ListAdapter
		exerciseListView.setAdapter(listAdapter);
		
		exerciseListView.setOnItemLongClickListener(this);
		
		
		// Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
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
                            		int i = e.getId();
                                	em.delete(i);
                                	listAdapter.remove(listAdapter.getItem(position));
                                	final Toast toast = Toast.makeText(getActivity(), "Übung wurde gelöscht!", Toast.LENGTH_SHORT );
                                	toast.show();
                                    
                                    
                                }
                                listAdapter.notifyDataSetChanged();
                            }
                        });
        exerciseListView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        exerciseListView.setOnScrollListener(touchListener.makeScrollListener());
        
        
        /**
		 * Add the searchBar fragment to the current fragment
		 */
	    FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.add_searchBar, new ActionBarSearchBarFragment(), "ActionBarSearchBarFragment");
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
		
		
		switch (item.getItemId()){
			case R.id.menu_add:
				this.showDialogAddFragment();
				break; 
				}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Method which opens a DialogFragment to create a new exercise
	 */
	public void showDialogAddFragment(){
		DialogFragment dialogFragment = ExerciseAddDialogFragment.newInstance(getActivity(), listAdapter);
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
	}

	@Override
	public void onExerciseSelectionItemLongClick(
			ExerciseLongClickDialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		
		Exercise e = (Exercise) arg0.getItemAtPosition(arg2);
		int i = e.getId();
		showDialogLongClickFragment(i, listAdapter);
		
		
		
		return false;
	}
	
	
public void listviewactual() {
	listAdapter.clear();
	listAdapter.addAll(em.getAll());
	listAdapter.notifyDataSetChanged();
	 exerciseListView.invalidateViews();
}

/**
 * Method which opens a DialogFragment to update the selected item
 * @param i is the ID of the Exercise 
 * @param a is the Adapter
 */
	private void showDialogLongClickFragment(int i, ExerciseListWithoutSetsRepsAdapter a) {
		
		dialogFragment = ExerciseLongClickDialogFragment.newInstance(i, a);
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
		
	}

	public void updateAdapter(ArrayList<Exercise> List){
		listAdapter.clear();
		listAdapter.addAll(List);
		listAdapter.notifyDataSetChanged();
    	exerciseListView.invalidateViews(); 
		
	}
}


