package com.workout.log;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.RectangleTarget;
import com.remic.workoutlog.R;
import com.workout.log.SwipeToDelete.SwipeDismissListViewTouchListener;
import com.workout.log.SwipeToDelete.UndoBarController;
import com.workout.log.SwipeToDelete.UndoItem;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.data.Default;
import com.workout.log.data.ManageWorkoutplanListItem;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.db.WorkoutplanSQLDumpHelper;
import com.workout.log.fragment.ActionBarWorkoutPlanPickerFragment;
import com.workout.log.listAdapter.ManageWorkoutplanListAdapter;
// import com.github.amlcurran.showcaseview.targets.RectangleTarget;

public class ManageWorkoutplan extends Fragment implements OnItemClickListener, UndoBarController.UndoListener{
	private ListView trainingDayListView;
	private ArrayList<TrainingDay> trainingDayList;
	private ArrayList<ManageWorkoutplanListItem> manageWorkoutplanList;
	private ShareActionProvider mShareActionProvider;
	
	private ManageWorkoutplanListAdapter manageWorkoutplanListAdapter;
	private WorkoutplanSQLDumpHelper workoutplanSQLDump;
	private ActionBarWorkoutPlanPickerFragment actionBarWorkoutPlanPickerFragment;
	private TrainingDayMapper tdMapper;
	private WorkoutplanMapper wpMapper;
	private UndoBarController mUndoBarController = null;
	
	private static int workoutplanId = -1;
	private View view;
	private Stack<File> fileStack = null;
	
	private ShowcaseView showcaseView = null;
	private AsyncTask createSqlDump = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.activity_manage_workoutplan, container,false);
		/**
		 * Add the WorkoutplanPicker fragment to the current fragment
		 */
		this.hasOptionsMenu();
		
		fileStack = new Stack<File>();
		
	    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.specific_dateTimePicker, new ActionBarWorkoutPlanPickerFragment(), "ActionBarWorkoutPlanPickerFragment");
        transaction.commit();
                
	    //Set the Name of the ActionBar Title
        ((HelperActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.MenuList_Workoutplans));
        
        return view;            
	}

	/**
	 * Implements the SwipeToDelete capability of the ListView and loads 
	 * the Workoutplan into the ListView
	 * 
	 * @author Eric Schmidt
	 */
	@Override
	public void onResume(){
		super.onResume();

		actionBarWorkoutPlanPickerFragment = (ActionBarWorkoutPlanPickerFragment)
				getActivity().getSupportFragmentManager().findFragmentByTag("ActionBarWorkoutPlanPickerFragment");
		
		/**
		 * If more than one xml layout file uses the Layout for the UndoBar than 
		 * use this line to ensure that the reference is always correct
		 */
		mUndoBarController = null;
	
		
		wpMapper = new WorkoutplanMapper(getActivity());
		tdMapper = new TrainingDayMapper(getActivity());
		
		trainingDayListView = (ListView) view.findViewById(R.id.TrainingDayList);
		trainingDayListView.setOnItemClickListener(this);
		trainingDayListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE); 
		
		/**
		 * Listener triggers if the listview is completely drawn
		 */
		ViewTreeObserver textViewTreeObserver = trainingDayListView.getViewTreeObserver();
        textViewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
        	@Override
            public void onGlobalLayout() {
    	        if (manageWorkoutplanList != null && manageWorkoutplanList.size() == 1 && trainingDayListView.getChildCount() != 0) {
    	        	if(actionBarWorkoutPlanPickerFragment.getFirstShowcaseView() == null) {
    	        		showHelperOverlay();	
    	        	} else if(!actionBarWorkoutPlanPickerFragment.getFirstShowcaseView().isShown()) {
    	        		showHelperOverlay();
    	        	}
    	        		
    	        }
            }
        });
		
		updateListView(null);
		loadSwipeToDimiss();

		setHasOptionsMenu(true);
	}
    
    /**
     * ShowcaseView which points to the first entry of the listView
     */
    public void showHelperOverlay(){
    	if (showcaseView == null){
    		RectangleTarget target = new RectangleTarget(trainingDayListView.getChildAt(0));
	    	
	    	showcaseView = new ShowcaseView.Builder(getActivity())
	    	.setTarget(target)
		    .setContentTitle(getString(R.string.secondShowcaseViewTitle))
	    	.setContentText(getString(R.string.secondShowcaseViewContext))
		    .setStyle(R.style.CustomShowcaseTheme)
		    .singleShot(43)
		    .build();
	    	
    	}else{
    		showcaseView.refreshDrawableState();
    	}
    }
    
    public ShowcaseView getShowcaseView(){
    	return showcaseView;
    }
    
	/**
	 * Destroy any SqlDumpFile which was created
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Stack<File> fileStack = getFileStack();
		
		while (!fileStack.empty()){
			File file = (File) fileStack.pop();
			if(file.exists()){
				file.delete();
			}
		}
	}
    
	@Override
	public void onPause() {
		super.onPause();
		
		createSqlDump.cancel(true);	
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.manage_workoutplan_menu, menu);
		
		 // Locate MenuItem with ShareActionProvider
	    MenuItem shareItem = menu.findItem(R.id.menu_item_share);
	    
		// Fetch and store ShareActionProvider
	    mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
	    
	    /**
	     * Update the ShareIntent
	     */
	    this.createCurrentSqlDump(); 
	}
	
	private Intent createShareIntent(File sqlDump){
		if (sqlDump != null){
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(sqlDump));
			shareIntent.setType("text/plain");
			return shareIntent;
		}
		return null;
	}
	
	// Call to update the share intent
	public void setShareIntent(Intent shareIntent) {
	    if (mShareActionProvider != null) {
	    	/**
	    	 * First delete the old databaseFile then update the ShareIntent
	    	 */
	        mShareActionProvider.setShareIntent(shareIntent);
	    }
	}
	
	/**
	 * Create the sql dump in an AsyncTask. The AsyncTask will handle the
	 * call of the createShareIntent method.
	 * 
	 * @author Eric Schmidt
	 */
	public void createCurrentSqlDump(){
		if (createSqlDump != null){
			createSqlDump.cancel(true);
		}
		createSqlDump = new BackGroundTaskSQLDump().execute();		
	}
	
	/**
	 * Returns the fileStack Objects which contains all Files that where created
	 * in order to share the Workoutplans
	 * 
	 * @author Eric Schmidt
	 */
	public Stack<File> getFileStack(){
		return fileStack;
	}
	
	/**
	 * Redirects to TraininDayExerciseOverview Fragment
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TrainingDay td = (TrainingDay) parent.getItemAtPosition(position);
		
		Bundle data = new Bundle();
	    data.putInt("TrainingDayId", td.getId());
		
	    TrainingDayExerciseOverview trainingDayExerciseOverview = new TrainingDayExerciseOverview();
	    trainingDayExerciseOverview.setArguments(data);

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.fragment_container, trainingDayExerciseOverview, "TrainingDayExerciseOverview");
        transaction.addToBackStack(null);
        transaction.commit();
	}
	
	/**
	 * Update the listView with a given trainingDayList. Submit null if you want to
	 * choose the current workoutplan and the corresponding trainingDayList
	 * @param trainingdayList
	 */
	@SuppressWarnings("unchecked")
	public void updateListView(ArrayList<TrainingDay> trainingdayList) {
		/**
		 * Clear ListAdapter if already referenced
		 */
		if (trainingDayListView.getAdapter() != null){
			((ManageWorkoutplanListAdapter) trainingDayListView.getAdapter()).clear();
			((ManageWorkoutplanListAdapter) trainingDayListView.getAdapter()).notifyDataSetChanged();
		}
		new BackGroundTask().execute(trainingdayList);
	}

	public void setWorkoutplanId(int id) {
		workoutplanId = id;
	}

	public int getWorkoutplanId() {
		return workoutplanId;
	}
	
	public void setTrainingDayList(ArrayList<TrainingDay> trainingDayList) {
		this.trainingDayList = trainingDayList;
	}

	/**
	 * Create a ListView-specific touch listener. ListViews are given special treatment because
	 * by default they handle touches for their list items... i.e. they're in charge of drawing
	 * the pressed state (the list selector), handling list item clicks, etc.
	 * 
	 * @author Remi Tessier
	 */
	private void loadSwipeToDimiss(){
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(trainingDayListView,
           new SwipeDismissListViewTouchListener.DismissCallbacks() {
			TrainingDay[] items= null;
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
			         	items=new TrainingDay[trainingDayList.size()];
			            itemPositions =new int[trainingDayList.size()];
			            arrayCount=0;
		        	}
		        	
                   for (int position : reverseSortedPositions) {
	                   	TrainingDay trainingDay = (TrainingDay) listView.getItemAtPosition(position);
	                   	               		
	               		tdMapper.deleteTrainingDayFromWorkoutplan(trainingDay, workoutplanId);
	               		
	               		TrainingDay item= (TrainingDay) manageWorkoutplanListAdapter.getItem(position);
	               		manageWorkoutplanListAdapter.remove(item);
		            	
	                    items[arrayCount]=item;
	 	               	itemPositions[arrayCount]=position;
	 	               	arrayCount++;
	               		
	               		/**
		            	 * Set the ArrayList on the current value
		            	 */
	               		trainingDayList = manageWorkoutplanListAdapter.getTrainingDayList();		    
                   }      
                   manageWorkoutplanListAdapter.notifyDataSetChanged();
                   
		           UndoItem itemUndo=new UndoItem(items,itemPositions);
		   
		            /**
		             * Undobar message
		             */
		            int count = 0;
		            for (TrainingDay pa : items){
		            	if (pa != null){
		            		count++;
		            	}
		            }
		            String messageUndoBar = count + " " + getResources().getString(R.string.ItemsDeleted);
		            		 
		            mUndoBarController.showUndoBar(false,messageUndoBar,itemUndo);
		            
		    	    /**
		    	     * Update the ShareIntent
		    	     */
		    	    createCurrentSqlDump(); 
               }
           });
		trainingDayListView.setOnTouchListener(touchListener);
		
		/** Setting this scroll listener is required to ensure that during ListView scrolling,
		 *  we don't look for swipes.
		 */
		trainingDayListView.setOnScrollListener(touchListener.makeScrollListener());
		
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
			TrainingDay[] items = (TrainingDay[]) itemRetrieve.items;
			int[] itemPositions = itemRetrieve.itemPosition;

			if (items != null && itemPositions != null) {
				int end= 0;
			    for (TrainingDay pa : items){
	            	 if (pa != null){
	            		 end++;
	            	 }
	             }
			    
				for (int i = end - 1; i >= 0; i--) {
					TrainingDay item = items[i];
					int itemPosition = itemPositions[i];
					
					wpMapper.addTrainingDayToWorkoutplan(item.getId(), workoutplanId);
					manageWorkoutplanListAdapter.insert(item, itemPosition);
					manageWorkoutplanListAdapter.notifyDataSetChanged();
					
					/**
	            	 * Set the ArrayList on the current value
	            	 */
               		trainingDayList = manageWorkoutplanListAdapter.getTrainingDayList();	
				}
				
			    /**
	    	     * Update the ShareIntent
	    	     */
	    	    createCurrentSqlDump(); 
			}
		}
	}
	
	/**
	 * Handles the Database queries in an Async Task
	 * 
	 * @author Eric Schmidt
	 */
	public class BackGroundTask extends AsyncTask<ArrayList<TrainingDay>, Void, ManageWorkoutplanListAdapter> {
	    /**
	     * Variables for the UpdateListView method
	     */
		private ArrayList<Workoutplan> workoutplanList;
		
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        getActivity().setProgressBarIndeterminateVisibility(true);
	    }

	    @Override
	    protected ManageWorkoutplanListAdapter doInBackground(ArrayList<TrainingDay>... params) {
	    	manageWorkoutplanListAdapter = null;
	    	
			wpMapper = new WorkoutplanMapper(getActivity());
			
			manageWorkoutplanList = new ArrayList<ManageWorkoutplanListItem>();
			workoutplanList = new ArrayList<Workoutplan>();
			
			workoutplanList = wpMapper.getAll();
			
			/**
			 * Device is working to fast. To avoid crashes the application waits
			 * for 100 ms.
			 */
			try {
				synchronized (this){
					this.wait(100);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (workoutplanList.size() != 0){
				if (params[0] == null) params[0] = tdMapper.getAllTrainingDaysFromWorkoutplan(workoutplanId);
				for(int i = 0; i < params[0].size(); i++) {
					manageWorkoutplanList.add(params[0].get(i));
				}
				
				Default d = new Default();
				manageWorkoutplanList.add(d);
				manageWorkoutplanListAdapter = new ManageWorkoutplanListAdapter(getActivity(), manageWorkoutplanList, ManageWorkoutplan.this);
				setTrainingDayList(params[0]);
			}	
			return manageWorkoutplanListAdapter;	
	    }

	    @Override
	    protected void onPostExecute(ManageWorkoutplanListAdapter result) {
	        super.onPostExecute(result);
	        trainingDayListView.setAdapter(null);
	        if (result != null) trainingDayListView.setAdapter(result);
	        	        
	        getActivity().setProgressBarIndeterminateVisibility(false); 
	    }
	}
	
	/**
	 * Handles creation of the sql dump in an Async Task
	 * 
	 * @author Eric Schmidt
	 */
	public class BackGroundTaskSQLDump extends AsyncTask<Void, Void, File> {
		public void BackGroundTask(){	

		}

	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        getActivity().setProgressBarIndeterminateVisibility(true);
	    }

	    @Override
	    protected File doInBackground(Void... params) {
	    	/**
			 * Get the current Workoutplan
			 */
			if (actionBarWorkoutPlanPickerFragment == null){
				actionBarWorkoutPlanPickerFragment = (ActionBarWorkoutPlanPickerFragment)
						getActivity().getSupportFragmentManager().findFragmentByTag("ActionBarWorkoutPlanPickerFragment");
			}
			
			if(actionBarWorkoutPlanPickerFragment != null){
				/**
				 * Create a database with the current databse
				 */
				if (actionBarWorkoutPlanPickerFragment.getCurrentWorkoutplan() != null){
					workoutplanSQLDump = new WorkoutplanSQLDumpHelper(getActivity());
					
					/**
					 * Get the File of the database and add it to the Stack so it can be deleted if you close the app
					 */
					File sqlDump = workoutplanSQLDump.createSQLDump(actionBarWorkoutPlanPickerFragment.getCurrentWorkoutplan());
					fileStack.push(sqlDump);
					
					
					return sqlDump;
				}
			}
			return null;
	    }

	    @Override
	    protected void onPostExecute(File result) {
	        super.onPostExecute(result);
	       	        
	        getActivity().setProgressBarIndeterminateVisibility(false); 
	        
	        if (mShareActionProvider != null) mShareActionProvider.setShareIntent(createShareIntent(result));
	    }
	}
}