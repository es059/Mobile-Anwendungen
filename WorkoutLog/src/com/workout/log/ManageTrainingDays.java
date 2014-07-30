package com.workout.log;

import java.util.ArrayList;

import android.app.DialogFragment;
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
import com.workout.log.SwipeToDelete.SwipeDismissListViewTouchListener;
import com.workout.log.SwipeToDelete.UndoBarController;
import com.workout.log.SwipeToDelete.UndoItem;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.db.PerformanceTargetMapper;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.dialog.TrainingDayAddDialogFragment;
import com.workout.log.dialog.TrainingDayUpdateDialogFragment;
import com.workout.log.fragment.TrainingDaysSearchBarFragment;
import com.workout.log.listAdapter.TrainingDayListAdapter;

public class ManageTrainingDays extends Fragment implements OnItemClickListener, OnItemLongClickListener, UndoBarController.UndoListener{

	private TrainingDayListAdapter trainingDayListAdapter = null;
	private ListView trainingDayListView = null; 
	private TrainingDayMapper tdMapper = null;
	private View view = null;
	
	private TrainingDayMapper tMapper = null;
	private WorkoutplanMapper wMapper = null;
	private PerformanceTargetMapper pMapper = null;
	
	private ArrayList<TrainingDay> trainingDayList = null;
	private UndoBarController mUndoBarController = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.training_day_add, container,false);
		
	    /**
		 * Add the searchBar fragment to the current fragment
		 */
	    FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.add_searchBar, new TrainingDaysSearchBarFragment(this), "ManageTrainingDaysSearchBar");
        transaction.commit();
		
		return view;
	}
	
	/**
	 * Implements the LiveSearch capability of the EditView and loads 
	 * the Training Days into the ListView
	 * 
	 * @author Eric Schmidt
	 */
	@Override
	public void onResume(){
		super.onResume();
		
		tMapper = new TrainingDayMapper(getActivity());
		wMapper = new WorkoutplanMapper(getActivity());
		pMapper = new PerformanceTargetMapper(getActivity());
		
		/**
		 * If more than one xml layout file uses the Layout for the UndoBar than 
		 * use this line to ensure that the reference is always correct
		 */
		mUndoBarController = null;
		
		tdMapper = new TrainingDayMapper(getActivity());
		trainingDayListView = (ListView) view.findViewById(R.id.trainingDay_add_list);
		trainingDayListView.setOnItemClickListener(this);
		trainingDayListView.setOnItemLongClickListener(this);
		
		updateListView(null);
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
		int itemId = item.getItemId();
		if (itemId == R.id.menu_add) {
			TrainingDayAddDialogFragment dialogFragment = TrainingDayAddDialogFragment.newInstance(getActivity(), trainingDayListAdapter);
			dialogFragment.show(this.getFragmentManager(), "Open Exercise Add Dialog on Click");
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TrainingDay td = (TrainingDay) parent.getItemAtPosition(position);
		openTrainingDayExerciseOverview(td);
	}
	
	private void openTrainingDayExerciseOverview(TrainingDay td){
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

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		TrainingDay t = (TrainingDay) arg0.getItemAtPosition(arg2);
		int trainingDayId = t.getId();
		showDialogLongClickFragment(trainingDayId, trainingDayListAdapter);
		return true;
	}
	
	/**
	 * Method which opens a DialogFragment to update the selected item
	 * 
	 * @param i is the ID of the Exercise 
	 * @param a is the Adapter
	 */
	private void showDialogLongClickFragment(int trainingDayId, TrainingDayListAdapter adapter) {	
		DialogFragment dialogFragment = TrainingDayUpdateDialogFragment.newInstance(this, trainingDayId);
		dialogFragment.show(getActivity().getFragmentManager(), "Open Exercise Update Dialog on Long Click");
	}

	@SuppressWarnings("unchecked")
	public void updateListView(ArrayList<TrainingDay> trainingDayList) {
		new BackGroundTask().execute(trainingDayList);
	}
	
	/**
	 * Implementation of Swipe to dismiss function
	 */
	private void loadSwipeToDismiss(){
		SwipeDismissListViewTouchListener touchListener =
            new SwipeDismissListViewTouchListener(trainingDayListView,new SwipeDismissListViewTouchListener.DismissCallbacks() {
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
                    	TrainingDay trainingDay = (TrainingDay) trainingDayListView.getItemAtPosition(position);                  		
                		/**
                		 * So ensure referential integrity the data is to be deleted from the tables
                		 * TrainingDay, WorkoutplanhasTrainingDay, TrainingDayhasExercise, PerformanceTarget
                		 */
                		TrainingDayMapper tMapper = new TrainingDayMapper(getActivity());
                		PerformanceTargetMapper pMapper = new PerformanceTargetMapper(getActivity());
                		
                		tMapper.delete(trainingDay);
                		for (Exercise e : trainingDay.getExerciseList()){
                			tMapper.deleteExerciseFromTrainingDay(trainingDay.getId(), e);
                		}
                		tMapper.deleteTrainingDayFromAllWorkoutplan(trainingDay.getId());
                		pMapper.deleteTrainingDayFromAllPerformanceTarget(trainingDay.getId());
                		
                		TrainingDay item= trainingDayListAdapter.getItem(position);
                		trainingDayListAdapter.remove(item);
                		
                        items[arrayCount]=item;
	 	               	itemPositions[arrayCount]=position;
	 	               	arrayCount++;
	 	               	
	 	        		/**
		            	 * Set the ArrayList on the current value
		            	 */
	               		trainingDayList = trainingDayListAdapter.getTrainingDayList();	
                    }
                    trainingDayListAdapter.notifyDataSetChanged();
                    
 		            UndoItem itemUndo=new UndoItem(items,itemPositions);
 		   
 		            /**
 		             * Undobar message
 		             */
 		            int count = 0;
 		            for (TrainingDay t : items){
 		            	if (t != null){
 		            		count++;
 		            	}
 		            }
 		            String messageUndoBar = count + " " + getResources().getString(R.string.ItemsDeleted);
 		            		 
 		            mUndoBarController.showUndoBar(false,messageUndoBar,itemUndo);	         
                }
    		});
		trainingDayListView.setOnTouchListener(touchListener);
		
        /** Setting this scroll listener is required to ensure that during ListView scrolling,
         * we don't look for swipes.
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
			    for (TrainingDay t : items){
	            	 if (t != null){
	            		 end++;
	            	 }
	             }
			    
				for (int i = end - 1; i >= 0; i--) {
					TrainingDay item = items[i];
					int itemPosition = itemPositions[i];

					tMapper.add(item);
					for (Workoutplan w : item.getWorkoutplanList()){
						wMapper.addTrainingDayToWorkoutplan(item.getId(), w.getId());
					}
					for (PerformanceTarget p : item.getPerformanceTargetList()){
						pMapper.addPerformanceTarget(p);
					}
					for (Exercise e : item.getExerciseList()){
						tMapper.addExerciseToTrainingDay(item.getId(), e.getId());
					}
					trainingDayListAdapter.insert(item, itemPosition);
					trainingDayListAdapter.notifyDataSetChanged();
				}
			}
		}
	}
	
	/**
	 * Handels the Database queries in an Async Task
	 * 
	 * @author Eric Schmidt
	 */
	public class BackGroundTask extends AsyncTask<ArrayList<TrainingDay>, Void, TrainingDayListAdapter> {
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        getActivity().setProgressBarIndeterminateVisibility(true);
	    }

	    @Override
	    protected TrainingDayListAdapter doInBackground(ArrayList<TrainingDay>... params) {	
			if(params[0] == null) params[0] = tdMapper.getAllTrainingDay();
			trainingDayList = params[0];
			trainingDayListAdapter = new TrainingDayListAdapter(getActivity(), R.layout.listview_training_day, params[0]);
			
			return trainingDayListAdapter;
	    }

	    @Override
	    protected void onPostExecute(TrainingDayListAdapter result) {
	        super.onPostExecute(result);
	        
	        if (result != null) trainingDayListView.setAdapter(result);
	        
	        getActivity().setProgressBarIndeterminateVisibility(false);
	    }
	}
}