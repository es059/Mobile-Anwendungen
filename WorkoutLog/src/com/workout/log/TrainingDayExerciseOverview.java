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
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.RectangleTarget;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.remic.workoutlog.R;
import com.workout.log.SwipeToDelete.SwipeDismissListViewTouchListener;
import com.workout.log.SwipeToDelete.UndoBarController;
import com.workout.log.SwipeToDelete.UndoItem;
import com.workout.log.analytics.MyApplication;
import com.workout.log.analytics.MyApplication.TrackerName;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.MuscleGroup;
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.data.Default;
import com.workout.log.data.DynamicListView;
import com.workout.log.data.ListItem;
import com.workout.log.data.MuscleGroupSectionItem;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.PerformanceTargetMapper;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.dialog.ExerciseSpecificUpdateDialogFragment;
import com.workout.log.fragment.ActionBarTrainingDaySelectFragment;
import com.workout.log.listAdapter.DefaultAddListAdapter;
import com.workout.log.listAdapter.TrainingDayExerciseAdapter;


public class TrainingDayExerciseOverview extends Fragment implements OnItemLongClickListener, UndoBarController.UndoListener{
	private DynamicListView exerciseListView = null;
	
	private ArrayList<Exercise> exerciseList = null;
	private int trainingDayId;
	private boolean sortMode = false;
	private TrainingDayExerciseAdapter listAdapter = null;
		
	private PerformanceTargetMapper ptMapper = null;
	private TrainingDayMapper tMapper = null;
	private ExerciseMapper eMapper = null;
	private SwipeDismissListViewTouchListener touchListener = null;
	
	private UndoBarController mUndoBarController = null;
	
	private ShowcaseView showcaseView = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.training_day_exercise_overview, container, false);
		
		/**
		 * Set the visibility of the NavigationDrawer to Invisible
		 */
		((HelperActivity) getActivity()).setNavigationDrawerVisibility(false);
		
        /**
		 * Add the ActionBar fragment to the current fragment
		 */		
	    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.training_day_select_fragment, new ActionBarTrainingDaySelectFragment(), "ActionBarTrainingDaySelectFragment");
        transaction.commit();   
        
        //Set the Name of the ActionBar Title
        ((HelperActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.MenuList_Trainingdays));
        
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        ((HelperActivity) getActivity()).setCalledGetParentActivityIntent(false);
        
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
		 * Receive the arguments set by either ManageWorkoutplan or ManageTrainingDays
		 */
		eMapper = new ExerciseMapper(getActivity());
		
		exerciseListView = (DynamicListView) getView().findViewById(R.id.ExerciseListView);
		exerciseListView.setOnItemLongClickListener(this);
		exerciseListView.setDivider(null);
		
		/**
		 * Listener triggers if the listview is completely drawn
		 */
		ViewTreeObserver textViewTreeObserver = exerciseListView.getViewTreeObserver();
        textViewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
        	@Override
            public void onGlobalLayout() {
        		if (exerciseListView.getAdapter() != null && exerciseListView.getChildCount() != 0 && exerciseListView.getAdapter().getItem(0) instanceof Default){ 
    	        	showHelperOverlay();
    	        }
            }
        });
				
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

	/**
     * ShowcaseView which points to the first entry of the listView
     */
    public void showHelperOverlay(){
    	if (showcaseView == null){
	    	RectangleTarget target = new RectangleTarget(exerciseListView.getChildAt(0));
	    	
	    	showcaseView = new ShowcaseView.Builder(getActivity())
	    	.setTarget(target)
		    .setContentTitle(getString(R.string.sithShowcaseViewTitle))
		    .setContentText(getString(R.string.sithShowcaseViewContext))
		    .setStyle(R.style.CustomShowcaseTheme)
		    .singleShot(47)
		    .build();
    	}else{
    		showcaseView.refreshDrawableState();
    	}
    }
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.trainingday_exercise_overview_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_sort) {
			if (sortMode){
				
				/**
				 * Tracker
				 */
				// Get tracker.
		        Tracker t = ((MyApplication) getActivity().getApplication()).getTracker(
		            TrackerName.APP_TRACKER);
		        // Build and send an Event.
		        t.send(new HitBuilders.EventBuilder()
		            .setCategory("ClickEvent")
		            .setAction("Drag&Drop")
		            .setLabel("Change to Drag&Drop")
		            .build());
				
				sortMode = false;
				
				/**
				 * Save the sorted ArrayList
				 */
				int count = 0;
				for (ListItem l : listAdapter.getList()){
					if(!l.isSection()){
						count++;
						Exercise e = (Exercise) l;
						e.setOrderNumber(count);
						tMapper.updateTrainingDayHasExercise(e, trainingDayId, e.getOrderNumber());
					}
				}
				updateListView();
				exerciseListView.setOnItemLongClickListener(this);
				Toast.makeText(getActivity(), getResources().getString(R.string.TrainingDayExerciseOverview_DDInactive), Toast.LENGTH_LONG).show();
			}else{
				sortMode = true;
				
				for (ListItem l : exerciseList){
					if (l.isSection()) exerciseList.remove(l);
				}
				listAdapter.clear();
				listAdapter.addAll(exerciseList);
				listAdapter.notifyDataSetChanged();
				
				exerciseListView.setOnItemLongClickListener(exerciseListView.getItemLongClickListener());
				Toast.makeText(getActivity(), getResources().getString(R.string.TrainingDayExerciseOverview_DDActive), Toast.LENGTH_LONG).show();
			}
		}
		else if( id == R.id.menu_add){
			openExerciseAddToTrainingDay();
		}
		return super.onOptionsItemSelected(item);
	}

	
	public void openExerciseAddToTrainingDay(){
		FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.fragment_container, new ExerciseAddToTrainingDay(), "ExerciseAddToTrainingDay");
        transaction.addToBackStack(null);
        transaction.commit();
	}
	
	public int getTrainingDayId() {
		return trainingDayId;
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if(arg0.getItemAtPosition(arg2) instanceof Exercise) {
			Exercise e = (Exercise) arg0.getItemAtPosition(arg2);
			int currentExerciseId = e.getId();
			showDialogLongClickFragment(currentExerciseId);
			
			return true;
		} 
		return false;
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
		touchListener = new SwipeDismissListViewTouchListener(exerciseListView,
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
     		            String messageUndoBar = count + " " + getResources().getString(R.string.ItemsDeleted);
     		            		 
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
	public class BackGroundTask extends AsyncTask<Void, Void, BaseAdapter> {
	    /**
	     * Variables for the UpdateListView method
	     */
		private ArrayList<ListItem> listComplete;

		
		private DynamicListView exerciseListView;

		public BackGroundTask (DynamicListView exerciseListView){	
			this.exerciseListView = exerciseListView;
		}

	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        getActivity().setProgressBarIndeterminateVisibility(true);
	    }

	    @Override
	    protected BaseAdapter doInBackground(Void... params) {
			/**
			 * Select Exercises from selected Trainingday and MuscleGroup 
			 */
			ExerciseMapper eMapper = new ExerciseMapper(getActivity());
			exerciseList = eMapper.getExerciseByTrainingDay(trainingDayId);
			listComplete = new ArrayList<ListItem>();
			
			if(exerciseList.size() != 0){
				for(int i = 0; i < exerciseList.size(); i++) {
					MuscleGroup mg = exerciseList.get(i).getMuscleGroup();
					
					if (i > 0){
						if(!mg.getName().equals(exerciseList.get(i-1).getMuscleGroup().getName())) {
							listComplete.add(new MuscleGroupSectionItem(mg.getName()));
							listComplete.add(exerciseList.get(i));
						}else{
							listComplete.add(exerciseList.get(i));
						}
					}else{
						listComplete.add(new MuscleGroupSectionItem(mg.getName()));
						listComplete.add(exerciseList.get(i));
					}
					
					listAdapter = new TrainingDayExerciseAdapter(getActivity(), listComplete,trainingDayId);
					exerciseListView.setItemList(listComplete);
				}
				return listAdapter;
			}
			
			/**
			 * If there is no entry 
			 */
			Default d = new Default();
			d.setTitel(getString(R.string.noExerciseInTrainingDay));
			
			ArrayList<Default> ld = new ArrayList<Default>();
			ld.add(d);
			DefaultAddListAdapter defaultListAdapter = new DefaultAddListAdapter(getActivity(), 0, ld);
			return defaultListAdapter;
	    }

	    @Override
	    protected void onPostExecute(BaseAdapter result) {
	        super.onPostExecute(result);

	        if (result != null){
	        	if (result instanceof TrainingDayExerciseAdapter){
	        		exerciseListView.setOnTouchListener(touchListener);
		        	final AlphaInAnimationAdapter animAdapter = new AlphaInAnimationAdapter(result);
		        	animAdapter.setInitialDelayMillis(300);
		    		animAdapter.setAbsListView(exerciseListView);
		        	exerciseListView.setAdapter(animAdapter);
		        	
		        	exerciseListView.setOnItemMovedListener(new DynamicListView.OnItemMovedListener() {
		                 @Override
		                 public void onItemMoved(final int newPosition) {
		                	 animAdapter.notifyDataSetChanged();
		                 }
		             });
	        	}else{
	        		exerciseListView.setOnTouchListener(null);
	        		exerciseListView.setAdapter(result);
	    			exerciseListView.setOnItemClickListener(new OnItemClickListener(){
	    				@Override
	    				public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
	    					if (showcaseView != null && showcaseView.isShown()) showcaseView.hide();
	    					openExerciseAddToTrainingDay();
	    				}	
	    			});
	        	}
	        }
	
	        else exerciseListView.invalidateViews();
	        
	        getActivity().setProgressBarIndeterminateVisibility(false);
	    }
	}
}



