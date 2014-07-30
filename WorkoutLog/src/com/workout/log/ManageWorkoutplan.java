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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.workoutlog.R;
import com.workout.log.SwipeToDelete.SwipeDismissListViewTouchListener;
import com.workout.log.SwipeToDelete.UndoBarController;
import com.workout.log.SwipeToDelete.UndoItem;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.fragment.ActionBarWorkoutPlanPickerFragment;
import com.workout.log.listAdapter.StableArrayAdapter;

public class ManageWorkoutplan extends Fragment implements OnItemClickListener, UndoBarController.UndoListener {
	private ListView listView;
	private ArrayList<TrainingDay> trainingDayList;
	private StableArrayAdapter stableArrayAdapter;
	private TrainingDayMapper tdMapper;
	private WorkoutplanMapper wpMapper;
	
	private UndoBarController mUndoBarController = null;
	private static int workoutplanId =1;
	int currentListId = -1;
	private View view;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.activity_manage_workoutplan, container,false);
		/**
		 * Add the WorkoutplanPicker fragment to the current fragment
		 */
	    FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.specific_dateTimePicker, new ActionBarWorkoutPlanPickerFragment(), "ActionBarWorkoutPlanPickerFragment");
        transaction.commit();
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

		/**
		 * If more than one xml layout file uses the Layout for the UndoBar than 
		 * use this line to ensure that the reference is always correct
		 */
		mUndoBarController = null;
		
		wpMapper = new WorkoutplanMapper(getActivity());
		
		tdMapper = new TrainingDayMapper(getActivity());
		listView = (ListView) view.findViewById(R.id.TrainingDayList);
		
		listView.setOnItemClickListener(this);
		listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE); 
		
		updateListView(null);
		loadSwipeToDimiss();

		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.workoutplan_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.menu_add) {
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			transaction.replace(R.id.fragment_container, new TrainingDayAddToWorkoutplan(), "TrainingDayAddToWorkoutplan");
			transaction.addToBackStack(null);
			transaction.commit();
		}
		return super.onOptionsItemSelected(item);
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
		new BackGroundTask(listView).execute(trainingdayList);
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
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(listView,
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
	               		
	               		TrainingDay item= stableArrayAdapter.getItem(position);
	               		stableArrayAdapter.remove(item);
		            	
	                    items[arrayCount]=item;
	 	               	itemPositions[arrayCount]=position;
	 	               	arrayCount++;
	               		
	               		/**
		            	 * Set the ArrayList on the current value
		            	 */
	               		trainingDayList = stableArrayAdapter.getTrainingDayList();		    
                   }      
                   stableArrayAdapter.notifyDataSetChanged();
                   
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
               }
           });
		listView.setOnTouchListener(touchListener);
		
		/** Setting this scroll listener is required to ensure that during ListView scrolling,
		 *  we don't look for swipes.
		 */
		listView.setOnScrollListener(touchListener.makeScrollListener());
		
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
					stableArrayAdapter.insert(item, itemPosition);
					stableArrayAdapter.notifyDataSetChanged();
				}
			}
		}
	}
	
	/**
	 * Handels the Database queries in an Async Task
	 * 
	 * @author Eric Schmidt
	 */
	public class BackGroundTask extends AsyncTask<ArrayList<TrainingDay>, Void, StableArrayAdapter> {
	    /**
	     * Variables for the UpdateListView method
	     */
		private ArrayList<Workoutplan> workoutplanList;
		
		private ListView trainingDayListView;

		public BackGroundTask (ListView trainingDayListView){	
			this.trainingDayListView = trainingDayListView;
		}

	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        getActivity().setProgressBarIndeterminateVisibility(true);
	    }

	    @SuppressWarnings("unchecked")
		@Override
	    protected StableArrayAdapter doInBackground(ArrayList<TrainingDay>... params) {
	    	stableArrayAdapter = null;
	    	currentListId = -1;
	    	
			wpMapper = new WorkoutplanMapper(getActivity());
			
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
				for(int i = 0; i < workoutplanList.size(); i++) {
					if(workoutplanList.get(i).getId() == workoutplanId) {
						currentListId = i;
					}
				}
				if (params[0] == null) params[0] = tdMapper.getAllTrainingDaysFromWorkoutplan(workoutplanList.get(currentListId).getId());	
				stableArrayAdapter = new StableArrayAdapter(getActivity(), R.layout.listview_training_day, params[0]);
				setTrainingDayList(params[0]);
			}
						
			return stableArrayAdapter;	
	    }

	    @Override
	    protected void onPostExecute(StableArrayAdapter result) {
	        super.onPostExecute(result);
	        trainingDayListView.setAdapter(null);
	        if (result != null) trainingDayListView.setAdapter(result);
	       	        
	        getActivity().setProgressBarIndeterminateVisibility(false); 
	    }
	}
}