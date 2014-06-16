package com.workout.log;

import java.util.ArrayList;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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
import com.workout.log.dialog.ExerciseClickDialogFragment;
import com.workout.log.fragment.ExerciseBarSearchBarFragment;
import com.workout.log.listAdapter.ExerciseListWithoutSetsRepsAdapter;
import com.workout.log.listAdapter.SwipeDismissListViewTouchListener;

public class ExerciseAddToTrainingDay extends Fragment implements OnItemClickListener {
    private ExerciseListWithoutSetsRepsAdapter listAdapter;
    private ListView exerciseListView;
    private DialogFragment dialogFragment;
    private  ExerciseMapper eMapper;
	private int trainingDayId;
	private int exerciseId;
	private boolean firstTime = true;
	
	private TrainingDayExerciseOverview trainingDayExerciseOverview;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.exercise_add, container, false);
		
		eMapper = new ExerciseMapper(getActivity());
		
		// �bergabe der Trainingtags-ID �ber das Intent 
		trainingDayExerciseOverview = (TrainingDayExerciseOverview) getActivity().getFragmentManager().findFragmentByTag("TrainingDayExerciseOverview");
		trainingDayId = trainingDayExerciseOverview.getTrainingDayId();
        
        /**
		 * Add the searchBar fragment to the current fragment
		 */
	    FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.add_searchBar, new ExerciseBarSearchBarFragment(), "ActionBarSearchBarFragment");
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
		exerciseListView.setOnItemClickListener(this);
		
		updateAdapter(eMapper.getAll());
		loadSwipeToDismiss();
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
		DialogFragment dialogFragment = ExerciseAddDialogFragment.newInstance(getActivity(), listAdapter);
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
	}

	public void listViewActual() {
		 listAdapter.clear();
		 listAdapter.addAll(eMapper.getAll());
		 listAdapter.notifyDataSetChanged();
		 exerciseListView.invalidateViews();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Exercise e = (Exercise) arg0.getItemAtPosition(arg2);
		exerciseId = e.getId();
		showDialogClickFragment(getActivity(), trainingDayId, exerciseId);
	}
	private void showDialogClickFragment(Context context, int  trainingDayId, int exerciseId) {
		dialogFragment = ExerciseClickDialogFragment.newInstance(context, trainingDayId, exerciseId);
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");	
	}
	
	/**
	 * Update the ListView with a given Exercise ArrayList
	 * 
	 * @param List the updated ArrayList
	 * @author Eric Schmidt
	 */
	@SuppressWarnings("unchecked")
	public void updateAdapter(ArrayList<Exercise> List){
		new BackGroundTask(exerciseListView, getActivity()).execute(List); 
	}

	/**
	 * Create a ListView-specific touch listener. ListViews are given special treatment because
	 * by default they handle touches for their list items... i.e. they're in charge of drawing
	 * the pressed state (the list selector), handling list item clicks, etc.
	 * 
	 * @author Remi Tessier
	 */
	private void loadSwipeToDismiss(){
		 // Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
        SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
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
                    	
                    	listAdapter.remove(listAdapter.getItem(position));
                    	Toast.makeText(getActivity(), "�bung wurde gel�scht!", Toast.LENGTH_SHORT ).show();          
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
		private ProgressDialog mDialog;

		public BackGroundTask (ListView exerciseListView, Context context){	
			this.exerciseListView = exerciseListView;
			
		    mDialog = new ProgressDialog(getActivity());
		    mDialog.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
		    mDialog.setMessage("Lade �bungen");
		    mDialog.setCancelable(false);
		    mDialog.setCanceledOnTouchOutside(false);
		}

	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        if (firstTime) mDialog.show();
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
	        
	        if (mDialog.isShowing()) {
	        	mDialog.dismiss();
	        }   
	        
			/**
			 * Set the boolean firstTime to false if the Fragment it paused for the
			 * first time ensuring that the asyncTask is doing the right routine
			 */
			firstTime = false;
	    }

	}
}
