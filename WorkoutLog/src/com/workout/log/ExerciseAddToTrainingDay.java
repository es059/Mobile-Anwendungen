package com.workout.log;

import java.util.ArrayList;

import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import com.example.workoutlog.R;
import com.workout.log.ExerciseAdd.BackGroundTask;
import com.workout.log.SwipeToDelete.SwipeDismissListViewTouchListener;
import com.workout.log.SwipeToDelete.UndoBarController;
import com.workout.log.SwipeToDelete.UndoItem;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.MuscleGroup;
import com.workout.log.bo.PerformanceActual;
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.data.Default;
import com.workout.log.data.ListItem;
import com.workout.log.data.MuscleGroupSectionItem;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.MuscleGroupMapper;
import com.workout.log.db.PerformanceActualMapper;
import com.workout.log.db.PerformanceTargetMapper;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.dialog.ExerciseAddDialogFragment;
import com.workout.log.dialog.ExerciseSpecificAddDialogFragment;
import com.workout.log.dialog.ExerciseUpdateDialogFragment;
import com.workout.log.fragment.ExerciseSearchBarFragment;
import com.workout.log.listAdapter.DefaultAddListAdapter;
import com.workout.log.listAdapter.ExerciseListWithoutSetsRepsAdapter;

public class ExerciseAddToTrainingDay extends Fragment implements OnItemClickListener, OnItemLongClickListener, UndoBarController.UndoListener{
    private ExerciseListWithoutSetsRepsAdapter listAdapter;
    private ListView exerciseListView;
    
    private PerformanceActualMapper paMapper = null;
	private PerformanceTargetMapper ptMapper = null;
    private ExerciseMapper eMapper = null;
    private TrainingDayMapper tMapper = null;
    
	private int trainingDayId;
	
	private TrainingDayExerciseOverview trainingDayExerciseOverview;
	
	private ArrayList<Exercise> exerciseList = null;
	private UndoBarController mUndoBarController = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.exercise_add, container, false);
		
		eMapper = new ExerciseMapper(getActivity());
		paMapper = new PerformanceActualMapper(getActivity());
		ptMapper = new PerformanceTargetMapper(getActivity());
		tMapper = new TrainingDayMapper(getActivity());
		
		/**
		 * Get TrainingDayId from ExerciseOverview
		 */
		trainingDayExerciseOverview = (TrainingDayExerciseOverview) getActivity().getSupportFragmentManager().findFragmentByTag("TrainingDayExerciseOverview");
		trainingDayId = trainingDayExerciseOverview.getTrainingDayId();
        
        /**
		 * Add the searchBar fragment to the current fragment
		 */
	    FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.add_searchBar, new ExerciseSearchBarFragment(this), "ActionBarSearchBarFragment");
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
		
		/**
		 * If more than one xml layout file uses the Layout for the UndoBar than 
		 * use this line to ensure that the reference is always correct
		 */
		mUndoBarController = null;
		
		eMapper = new ExerciseMapper(getActivity());
		
		exerciseListView = (ListView) getView().findViewById(R.id.add_exerciseList);
		exerciseListView.setOnItemClickListener(this);
		exerciseListView.setOnItemLongClickListener(this);
		
		updateListView(eMapper.getAllExercise(), null);
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
			this.showDialogAddFragment(null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Method which opens a DialogFragment to create a new exercise
	 */
	public void showDialogAddFragment(String exerciseStringName){
		ExerciseAddDialogFragment dialogFragment = ExerciseAddDialogFragment.newInstance(getActivity(), listAdapter, exerciseStringName);
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Exercise e = (Exercise) arg0.getItemAtPosition(arg2);
		int currentExerciseId = e.getId();
		showDialogClickFragment(getActivity(), trainingDayId, currentExerciseId);
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		Exercise e = (Exercise) arg0.getItemAtPosition(arg2);
		int exerciseId = e.getId();
		showDialogLongClickFragment(exerciseId);
		
		return true;
	}
	
	/**
	 * Method which opens a DialogFragment to update the selected item
	 * 
	 * @param i is the ID of the Exercise 
	 * @param a is the Adapter
	 */
	private void showDialogLongClickFragment(int currentExerciseId) {	
		DialogFragment dialogFragment = ExerciseUpdateDialogFragment.newInstance(this, currentExerciseId);
		dialogFragment.show(getActivity().getFragmentManager(), "Open Exercise Update Dialog on Long Click");
	}

	
	private void showDialogClickFragment(Context context, int  trainingDayId, int exerciseId) {
		ExerciseSpecificAddDialogFragment dialogFragment = ExerciseSpecificAddDialogFragment.newInstance(context, trainingDayId, exerciseId);
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");	
	}
	
	/**
	 * Update the ListView with a given Exercise ArrayList
	 * 
	 * @param List the updated ArrayList
	 * @author Eric Schmidt
	 */
	@SuppressWarnings("unchecked")
	public void updateListView(ArrayList<Exercise> list, final String exerciseStringName){
		exerciseList = list;
		if (exerciseList.size() == 0){
			Default d = new Default();
			d.setTitel(exerciseStringName + " nicht gefunden");
			d.setHint("Hinzuf�gen");
			ArrayList<Default> ld = new ArrayList<Default>();
			ld.add(d);
			DefaultAddListAdapter l = new DefaultAddListAdapter(getActivity(), 0, ld);
			exerciseListView.setAdapter(l);
			exerciseListView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
					showDialogAddFragment(exerciseStringName);
				}	
			});
			
		}else{
			exerciseListView.setOnItemClickListener(this);
			new BackGroundTask(exerciseListView).execute(list);
		}
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
    		exerciseListView, new SwipeDismissListViewTouchListener.DismissCallbacks() {
    			Exercise[] items= null;
	 			int[] itemPositions = null;
	 			int arrayCount = 0;
	 			
                @Override
                public boolean canDismiss(int position) {
                    return true;
                }

                @Override
                public void onDismiss(ListView listView, int[] reverseSortedPositions){
                	/**
		         	 * Used for the undo actions
		         	 */
		        	if(mUndoBarController.getUndoBar().getVisibility() == View.GONE){
			         	items=new Exercise[exerciseList.size()];
			            itemPositions =new int[exerciseList.size()];
			            arrayCount=0;
		        	}
		        	
                    for (int position : reverseSortedPositions) {
                    	Exercise e = (Exercise) exerciseListView.getItemAtPosition(position);
                    	
                    	/**
		            	 * Fill the exercise Object with additional data to ensure that 
		            	 * the undo method is working
		            	 */
		            	e = eMapper.addAdditionalInfo(e);
                    	
                    	/**
                    	 * Delete from TrainingDayHasExercise, PerformanceActual
                    	 * and PerformanceTarget
                    	 */
                    	eMapper.deleteExerciseFromAllTrainingDays(e);
                    	eMapper.delete(e);
                    	
                    	ptMapper.deleteExerciseFromPerfromanceTarget(e);
                    	paMapper.deleteExerciseFromPerfromanceActual(e);
                    	
                    	Exercise item= (Exercise) listAdapter.getItem(position);
		            	listAdapter.remove(item);
		            	
		                items[arrayCount]=item;
	 	               	itemPositions[arrayCount]=position;
	 	               	arrayCount++;         
                    }
                    listAdapter.notifyDataSetChanged();
                    
                    UndoItem itemUndo=new UndoItem(items,itemPositions);
 		  		   
 		            /**
 		             * Undobar message
 		             */
 		            int count = 0;
 		            for (Exercise e : items){
 		            	if (e != null){
 		            		count++;
 		            	}
 		            }
 		            String messageUndoBar = count + " Item(s) gel�scht";
 		            		 
 		            mUndoBarController.showUndoBar(false,messageUndoBar,itemUndo);	
                }
            });
        exerciseListView.setOnTouchListener(touchListener);
        /** Setting this scroll listener is required to ensure that during ListView scrolling,
         * we don't look for swipes.
         */
        exerciseListView.setOnScrollListener(touchListener.makeScrollListener());
        
        /**
		 * UndoController
		 */
	    if (mUndoBarController==null) mUndoBarController = new UndoBarController(getView().findViewById(R.id.undobar), this);
	}
	
	/**
	 * Handels the click on the Undo Button. Revive the Data that was deletet in the
	 * onDismiss function
	 * 
	 * @author Eric Schmidt
	 */
	@Override
	public void onUndo(Parcelable token) {
		/**
		 * Restore items in lists (use reverseSortedOrder)
		 */
		if (token != null) {
			/**
			 * Retrieve data from token
			 */
			UndoItem itemRetrieve = (UndoItem) token;
			Exercise[] items = (Exercise[]) itemRetrieve.items;
			int[] itemPositions = itemRetrieve.itemPosition;

			if (items != null && itemPositions != null) {
				int end= 0;
			    for (Exercise e : items){
	            	 if (e != null){
	            		 end++;
	            	 }
	             }
			    
				for (int i = end - 1; i >= 0; i--) {
					Exercise item = items[i];
					int itemPosition = itemPositions[i];
					
					eMapper.add(item.getName(), item.getMuscleGroup().getName());
					for (Integer trainingDayId : item.getTrainingDayIdList()){
						tMapper.addExerciseToTrainingDay(trainingDayId, item.getId());
					}
                	for (PerformanceTarget pt : item.getPerformanceTargetList()){
                		ptMapper.addPerformanceTarget(pt);
                	}
                	for (PerformanceActual pt : item.getPerformanceActualList()){
                		paMapper.addPerformanceActual(pt, pt.getTimestamp());
                	}
					
					listAdapter.insert(item, itemPosition);
					listAdapter.notifyDataSetChanged();
				}
			}
		}
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
			ArrayList<ListItem> listComplete = new ArrayList<ListItem>();
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
