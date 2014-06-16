package com.workout.log;

import java.util.ArrayList;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
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

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.MuscleGroup;
import com.workout.log.data.ExerciseItem;
import com.workout.log.data.MuscleGroupSectionItem;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.MuscleGroupMapper;
import com.workout.log.db.PerformanceActualMapper;
import com.workout.log.db.PerformanceTargetMapper;
import com.workout.log.dialog.ExerciseAddDialogFragment;
import com.workout.log.dialog.ExerciseUpdateDialogFragment;
import com.workout.log.fragment.ExerciseBarSearchBarFragment;
import com.workout.log.listAdapter.ExerciseListWithoutSetsRepsAdapter;
import com.workout.log.listAdapter.SwipeDismissListViewTouchListener;

/**
 * With this fragment the user can manage his exercises. You can delete, add, and update exercises. 
 * 
 * @author Remi & Eric Schmidt
 *
 */
public class ExerciseAdd extends Fragment implements OnItemLongClickListener{
	private ListView exerciseListView;
	private DialogFragment dialogFragment;
	private ExerciseListWithoutSetsRepsAdapter listAdapter;
	
	private  ExerciseMapper eMapper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.exercise_add, container, false);
	
        /**
		 * Add the searchBar fragment to the current fragment
		 */
	    FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.add_searchBar, new ExerciseBarSearchBarFragment(this), "ActionBarSearchBarFragment");
        transaction.commit();

		setHasOptionsMenu(true);
		return view;
	}
	
	/**
	 * Load the methods to populate the ListView
	 */
	@Override
	public void onResume(){
		super.onResume();
		
		eMapper = new ExerciseMapper(getActivity());
		
		exerciseListView = (ListView) getView().findViewById(R.id.add_exerciseList);
		exerciseListView.setOnItemLongClickListener(this);
		
		updateListView(eMapper.getAll());
		loadSwipeToDismiss();
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
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		Exercise e = (Exercise) arg0.getItemAtPosition(arg2);
		int i = e.getId();
		showDialogLongClickFragment(i, listAdapter);
		
		return false;
	}
	
	/**
	 * Method which opens a DialogFragment to update the selected item
	 * 
	 * @param i is the ID of the Exercise 
	 * @param a is the Adapter
	 */
	private void showDialogLongClickFragment(int id, ExerciseListWithoutSetsRepsAdapter adapter) {	
		dialogFragment = ExerciseUpdateDialogFragment.newInstance(getActivity(), adapter, id);
		dialogFragment.show(getActivity().getFragmentManager(), "Open Exercise Update Dialog on Long Click");
	}

	/**
	 * Update the ListView with a given Exercise ArrayList
	 * 
	 * @param List the updated ArrayList
	 * @author Eric Schmidt
	 */
	@SuppressWarnings("unchecked")
	public void updateListView(ArrayList<Exercise> List){
		new BackGroundTask(exerciseListView).execute(List);
	}
	
	/**
	 * Create a ListView-specific touch listener. ListViews are given special treatment because
	 * by default they handle touches for their list items... i.e. they're in charge of drawing
	 * the pressed state (the list selector), handling list item clicks, etc.
	 * 
	 * @author Remi Tessier
	 */
	private void loadSwipeToDismiss(){ 
		 SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(exerciseListView,
		    new SwipeDismissListViewTouchListener.DismissCallbacks() {
		        @Override
		        public boolean canDismiss(int position) {
		            return true;
		        }
		        @Override
		        public void onDismiss(ListView listView, int[] reverseSortedPositions) {
		            for (int position : reverseSortedPositions) {
		            	Exercise e = (Exercise) exerciseListView.getItemAtPosition(position);
		            	
		            	PerformanceActualMapper paMapper = new PerformanceActualMapper(getActivity());
                    	PerformanceTargetMapper ptMapper = new PerformanceTargetMapper(getActivity());
                    	/**
                    	 * Delete from TrainingDayHasExercise, PerformanceActual
                    	 * and PerformanceTarget
                    	 */
                    	eMapper.deleteExerciseFromAllTrainingDays(e);
                    	eMapper.delete(e);
                    	ptMapper.deleteExerciseFromPerfromanceTarget(e);
                    	paMapper.deleteExerciseFromPerfromanceActual(e);
		        		
		        		//Delete Performance Actual
		            	listAdapter.remove(listAdapter.getItem(position));
		            	Toast.makeText(getActivity(), "Übung wurde gelöscht!", Toast.LENGTH_SHORT).show();;  
		            }
		            listAdapter.notifyDataSetChanged();
		        }
		    });
		 exerciseListView.setOnTouchListener(touchListener);
		 // Setting this scroll listener is required to ensure that during ListView scrolling,
		 // we don't look for swipes.
		 exerciseListView.setOnScrollListener(touchListener.makeScrollListener());
	}
	
	/**
	 * Handels the Database queries in an Async Task
	 * 
	 * @author Eric Schmidt
	 */
	public class BackGroundTask extends AsyncTask<ArrayList<Exercise>, Void, ExerciseListWithoutSetsRepsAdapter> {
	    /**
	     * Variables for the UpdateListView method
	     */
		private ArrayList<Exercise> eListMuscleGroup;
		private ArrayList<MuscleGroup> mList = null;
		private MuscleGroupMapper mMapper = null;
		
		private ListView exerciseListView;

		public BackGroundTask (ListView exerciseListView){	
			this.exerciseListView = exerciseListView;
		}

	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        getActivity().setProgressBarIndeterminateVisibility(true);
	    }

	    @Override
	    protected ExerciseListWithoutSetsRepsAdapter doInBackground(ArrayList<Exercise>... params) {
	    	/**
			 * Build a ArrayList containing the muscleGroup and exercises
			 */
	    	
			mMapper = new MuscleGroupMapper(getActivity());
			//Select all MuscleGroups
			mList = mMapper.getAll();
			ArrayList<ExerciseItem> listComplete = new ArrayList<ExerciseItem>();
			for (MuscleGroup m : mList){
				eListMuscleGroup = eMapper.getExerciseByMuscleGroup(params[0], m.getId());
				if (!eListMuscleGroup.isEmpty()){
					listComplete.add(new MuscleGroupSectionItem(m.getName()));
					listComplete.addAll(eListMuscleGroup);
				}
			}
			listAdapter = new ExerciseListWithoutSetsRepsAdapter(getActivity(), listComplete);
			return listAdapter;	
	    }

	    @Override
	    protected void onPostExecute(ExerciseListWithoutSetsRepsAdapter result) {
	        super.onPostExecute(result);

	        if (result != null) exerciseListView.setAdapter(result);
	        else exerciseListView.invalidateViews();
	        
	        getActivity().setProgressBarIndeterminateVisibility(false);
	    }
	}
}
	
	
