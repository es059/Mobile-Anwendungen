package com.workout.log;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.dialog.ExerciseAddDialogFragment;
import com.workout.log.dialog.ExerciseClickDialogFragment;
import com.workout.log.dialog.ExerciseLongClickDialogFragment;
import com.workout.log.dialog.ExerciseLongClickDialogFragment.ExerciseSelectionDialogListener;
import com.workout.log.fragment.ActionBarSearchBarFragment;
import com.workout.log.listAdapter.ExerciseListWithoutSetsRepsAdapter;
import com.workout.log.listAdapter.SwipeDismissListViewTouchListener;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
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
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class ExerciseAddToTrainingDay extends Fragment implements ExerciseSelectionDialogListener, OnItemLongClickListener, OnItemClickListener {
	
    private ArrayList<Exercise> List;
    private ExerciseListWithoutSetsRepsAdapter a;
    private ListView exerciseListView;
    private DialogFragment dialogFragment;
    private  ExerciseMapper em;
	private int trainingDayId;
	private int exerciseId;
	
	private TrainingDayExerciseOverview trainingDayExerciseOverview;
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.exercise_add, container, false);
		
		em = new ExerciseMapper(getActivity());
		final Toast toast = Toast.makeText(getActivity(), "Übung wurde gelöscht!", Toast.LENGTH_SHORT );
		// Übergabe der Trainingtags-ID über das Intent 
		trainingDayExerciseOverview = (TrainingDayExerciseOverview) getActivity().getFragmentManager().findFragmentByTag("TrainingDayExerciseOverview");
		trainingDayId = trainingDayExerciseOverview.getTrainingDayId();
		
		exerciseListView = (ListView) view.findViewById(R.id.add_exerciseList);
		List = new ArrayList<Exercise>();
		List = em.getAll();
		a = new ExerciseListWithoutSetsRepsAdapter(getActivity() , R.layout.listview_exercise_without_repssets, List);
		exerciseListView.setAdapter(a);
		exerciseListView.setOnItemLongClickListener(this);
		exerciseListView.setOnItemClickListener(this);
		
		/**
		 * Implementierung der Swipe to dissmiss-funktion 
		 */
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
                                	a.remove(a.getItem(position));
                                	toast.show();
                                    
                                    
                                }
                                a.notifyDataSetChanged();
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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_add) {
			this.showDialogAddFragment();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showDialogAddFragment(){
		DialogFragment dialogFragment = ExerciseAddDialogFragment.newInstance(getActivity(), a);
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
		showDialogLongClickFragment(i, a);
		
		
		
		return false;
	}
	public void listviewactual() {
		 a.clear();
		 a.addAll(em.getAll());
		 a.notifyDataSetChanged();
		 exerciseListView.invalidateViews();
}
	private void showDialogLongClickFragment(int i, ExerciseListWithoutSetsRepsAdapter a) {
		
		dialogFragment = ExerciseLongClickDialogFragment.newInstance(i, a);
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Exercise e = (Exercise) arg0.getItemAtPosition(arg2);
		exerciseId = e.getId();
		showDialogClickFragment(getActivity(), trainingDayId, exerciseId);
		
	}
	private void showDialogClickFragment(Context a, int  trainingDayId, int exerciseId) {
		
		dialogFragment = ExerciseClickDialogFragment.newInstance(a, trainingDayId, exerciseId);
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
		
	}
	
	public void updateAdapter(ArrayList<Exercise> List){
		a.clear();
    	a.addAll(List);
    	a.notifyDataSetChanged();
    	exerciseListView.invalidateViews(); 
		
	}


}
