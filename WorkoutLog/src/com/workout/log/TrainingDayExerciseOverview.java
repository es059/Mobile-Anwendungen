package com.workout.log;

import java.util.ArrayList;

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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.example.workoutlog.R;
import com.workout.log.SwipeToDelete.SwipeDismissListViewTouchListener;
import com.workout.log.SwipeToDelete.UndoBarController;
import com.workout.log.SwipeToDelete.UndoItem;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.MuscleGroup;
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.data.ListItem;
import com.workout.log.data.MuscleGroupSectionItem;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.MuscleGroupMapper;
import com.workout.log.db.PerformanceTargetMapper;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.dialog.ExerciseSpecificUpdateDialogFragment;
import com.workout.log.fragment.ActionBarTrainingDaySelectFragment;
import com.workout.log.listAdapter.OverviewAdapter;


public class TrainingDayExerciseOverview extends Fragment implements OnItemLongClickListener, UndoBarController.UndoListener{
	private ListView exerciseListView = null;
	private ArrayList<Exercise> exerciseList = null;
	private int trainingDayId;
	private OverviewAdapter listAdapter = null;
		
	private PerformanceTargetMapper ptMapper = null;
	private TrainingDayMapper tMapper = null;
	private ExerciseMapper eMapper = null;
	
	private UndoBarController mUndoBarController = null;
	
	@Override
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
		
		/**
		 * If more than one xml layout file uses the Layout for the UndoBar than 
		 * use this line to ensure that the reference is always correct
		 */
		mUndoBarController = null;
		
		tMapper = new TrainingDayMapper(getActivity());
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
		exerciseListView.setOnItemLongClickListener(this);
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
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		Exercise e = (Exercise) arg0.getItemAtPosition(arg2);
		int currentExerciseId = e.getId();
		showDialogLongClickFragment(currentExerciseId);
		
		return true;
	}
	
	/**
	 * Method which opens a DialogFragment to update the selected item
	 * 
	 * @param i is the ID of the Exercise 
	 * @param a is the Adapter
	 */
	private void showDialogLongClickFragment(int currentExerciseId) {	
		ExerciseSpecificUpdateDialogFragment dialogFragment = ExerciseSpecificUpdateDialogFragment.newInstance(this, trainingDayId, currentExerciseId);
		dialogFragment.show(getActivity().getSupportFragmentManager(), "Open Exercise Update Dialog on Long Click");
	}

	
	/**
	 * Update the ListView and provides for header for each
	 * MuscleGroup.
	 *
	 * @author Eric Schmidt
	 */
	public void updateListView(){
		new BackGroundTask(exerciseListView).execute();
	}
	
	/**
	 * Implementation of Swipe to dissmiss function
	 */
	private void loadSwipeToDismiss(){
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(exerciseListView,
                new SwipeDismissListViewTouchListener.DismissCallbacks() {
					Exercise[] items= null;
		 			int[] itemPositions = null;
		 			int arrayCount = 0;
		 			
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
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
                        	
                    		ptMapper.deletePerformanceTarget(trainingDayId, e.getId());
                        	tMapper.deleteExerciseFromTrainingDay(trainingDayId, e);
                        	
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
     		            String messageUndoBar = count + " Item(s) gelöscht";
     		            		 
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
					
					for (Integer trainingDayId : item.getTrainingDayIdList()){
						/**
						 * Ensure that only the current TrainingDay is revive
						 */
						if (trainingDayId == this.trainingDayId){
							tMapper.addExerciseToTrainingDay(trainingDayId, item.getId());
						}
					}
                	for (PerformanceTarget pt : item.getPerformanceTargetList()){
                		/**
						 * Ensure that only the current TrainingDay is revive
						 */
                		if (pt.getTrainingDayId() == this.trainingDayId){
                			ptMapper.addPerformanceTarget(pt);
                		}
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
	public class BackGroundTask extends AsyncTask<Void, Void, OverviewAdapter> {
	    /**
	     * Variables for the UpdateListView method
	     */
		private ArrayList<Exercise> eListMuscleGroup;
		private ArrayList<MuscleGroup> mList = null;
		private ArrayList<ListItem> listComplete;

		
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
	    protected OverviewAdapter doInBackground(Void... params) {
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
			listComplete = new ArrayList<ListItem>();
			for (MuscleGroup m : mList){
				eListMuscleGroup = eMapper.getExerciseByMuscleGroup(exerciseList, m.getId());
				if (!eListMuscleGroup.isEmpty()){
					listComplete.add(new MuscleGroupSectionItem(m.getName()));
					listComplete.addAll(eListMuscleGroup);
				}
			}
			
			listAdapter = new OverviewAdapter(getActivity(), listComplete,trainingDayId);
			
			return listAdapter;
	    }

	    @Override
	    protected void onPostExecute(OverviewAdapter result) {
	        super.onPostExecute(result);

	        if (result != null) exerciseListView.setAdapter(listAdapter);
	        
	        else exerciseListView.invalidateViews();
	        
	        getActivity().setProgressBarIndeterminateVisibility(false);
	    }
	}
}



