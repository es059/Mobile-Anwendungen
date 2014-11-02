package com.workout.log;

import java.util.ArrayList;

import android.content.Intent;
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
import android.widget.ListView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.RectangleTarget;
import com.remic.workoutlog.R;
import com.workout.log.SwipeToDelete.SwipeDismissListViewTouchListener;
import com.workout.log.SwipeToDelete.UndoBarController;
import com.workout.log.SwipeToDelete.UndoItem;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.data.Default;
import com.workout.log.data.TrainingDayAddToWorkoutPlanCustomToast;
import com.workout.log.db.PerformanceTargetMapper;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.dialog.TrainingDayAddDialogFragment;
import com.workout.log.dialog.TrainingDayAddToWorkoutplanDialogFragment;
import com.workout.log.dialog.TrainingDayUpdateDialogFragment;
import com.workout.log.fragment.TrainingDaysSearchBarFragment;
import com.workout.log.listAdapter.DefaultAddListAdapter;
import com.workout.log.listAdapter.TrainingDayListAdapter;
import com.workout.log.navigation.OnBackPressedListener;
import com.workout.log.navigation.OnHomePressedListener;

/**
 * Same Layout as ManageTrainingDays but with a redirect to ManageWorkoutplan
 * 
 * @author Eric Schmidt
 */
public class TrainingDayAddToWorkoutplan extends Fragment implements OnItemClickListener, OnItemLongClickListener, UndoBarController.UndoListener,
		TrainingDayAddToWorkoutPlanCustomToast.ShortCutListener{
	
	private TrainingDayListAdapter trainingDayListAdapter;
	private TrainingDayMapper tdMapper;
	private ListView trainingDayListView; 
	private int workoutplanId;
	private ManageWorkoutplan manageWorkoutplan;
	private View view;
	
	private TrainingDayMapper tMapper = null;
	private WorkoutplanMapper wMapper = null;
	private PerformanceTargetMapper pMapper = null;
	
	private ArrayList<TrainingDay> trainingDayList = null;
	private UndoBarController mUndoBarController = null;
	
	private ShowcaseView showcaseView1 = null;
	private ShowcaseView showcaseView2 = null;
	
	private TrainingDayAddToWorkoutPlanCustomToast customToast = null;
	private TrainingDayAddToWorkoutplanDialogFragment AddTrainingDayDialogFragment = null;
	private TrainingDay selectedTrainingDay = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.training_day_add, container,false);			
		
		/**
		 * Set the visibility of the NavigationDrawer to Invisible
		 */
		((HelperActivity) getActivity()).setNavigationDrawerVisibility(false);
		((HelperActivity) getActivity()).setCalledGetParentActivityIntent(false);
		
		/**
		 * Handles the behavior if the back button is pressed
		 */
		((HelperActivity)getActivity()).setOnBackPressedListener(new OnBackPressedListener(){
			@Override
			public void doBack() {
				openManageWorkoutplan();
				((HelperActivity) getActivity()).setOnBackPressedListener(null);
			}		
		});
		
		/**
		 * Handles the behavior if the Home button in the actionbar is pressed
		 */
		((HelperActivity)getActivity()).setOnHomePressedListener(new OnHomePressedListener(){
			@Override
			public Intent doHome() {
				openManageWorkoutplan();
				((HelperActivity) getActivity()).setOnBackPressedListener(null);
				return null;
			}		
		});
		
	    /**
		 * Add the searchBar fragment to the current fragment
		 */
	    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.add_searchBar, TrainingDaysSearchBarFragment.newInstance(this), "ManageTrainingDaysSearchBar");
        transaction.commit();
		
        //Set the Name of the ActionBar Title
        ((HelperActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.MenuList_Trainingdays));
        
		setHasOptionsMenu(true);
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
		
		manageWorkoutplan = (ManageWorkoutplan) getActivity().getSupportFragmentManager().findFragmentByTag("ManageWorkoutplan");
		workoutplanId = manageWorkoutplan.getWorkoutplanId();
		
		tdMapper = new TrainingDayMapper(getActivity());
		
		trainingDayListView = (ListView) view.findViewById(R.id.trainingDay_add_list);
		trainingDayListView.setOnItemClickListener(this);
		trainingDayListView.setOnItemLongClickListener(this);
		
		/**
		 * Listener triggers if the listview is completely drawn
		 */
		ViewTreeObserver textViewTreeObserver = trainingDayListView.getViewTreeObserver();
        textViewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
        	@Override
            public void onGlobalLayout() {
        		if (trainingDayListView.getAdapter() != null && trainingDayListView.getChildCount() != 0 && trainingDayListView.getAdapter().getItem(0) instanceof Default){ 
        			showHelperOverlay1();
    	        }else if (trainingDayListView.getAdapter() != null && trainingDayListView.getChildCount() != 0 && trainingDayListView.getAdapter().getItem(0) instanceof TrainingDay){
    	        	showHelperOverlay2();
    	        }
            }
        });
		
		updateListView(tdMapper.getAllTrainingDay(), null);
		loadSwipeToDismiss();
		setHasOptionsMenu(true);
		
		customToast = new TrainingDayAddToWorkoutPlanCustomToast(getView().findViewById(R.id.undobar), this);
	}
	
	/**
     * ShowcaseView which points to the first entry of the listView
     */
    public void showHelperOverlay1(){
    	if (showcaseView1 == null){
    		RectangleTarget target = new RectangleTarget(trainingDayListView.getChildAt(0));
	   
	    	showcaseView1 = new ShowcaseView.Builder(getActivity())
	    	.setTarget(target)
		    .setContentTitle(getString(R.string.thirdShowcaseViewTitle))
		    .setContentText(getString(R.string.thirdShowcaseViewContext))
		    .setStyle(R.style.CustomShowcaseTheme)
		    .singleShot(44)
		    .build();
    	}else{
    		showcaseView1.refreshDrawableState();
    	}
    }
    
	/**
     * ShowcaseView which points to the first entry of the listView
     */
    public void showHelperOverlay2(){
    	if (showcaseView2 == null){	
    		RectangleTarget target = new RectangleTarget(trainingDayListView.getChildAt(0));
    		
	    	showcaseView2 = new ShowcaseView.Builder(getActivity())
	    	.setTarget(target)
		    .setContentTitle(getString(R.string.fourthShowcaseViewTitle))
		    .setContentText(getString(R.string.fourthShowcaseViewContext))
		    .setStyle(R.style.CustomShowcaseTheme)
		    .singleShot(45)
		    .build();
    	}else{
    		showcaseView2.refreshDrawableState();
    	}
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
			showDialogAddFragment(null);
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void showDialogAddFragment(String trainingDayStringName){
		TrainingDayAddDialogFragment dialogFragment = TrainingDayAddDialogFragment.newInstance(getActivity(),
				trainingDayListAdapter, trainingDayStringName);
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Add Dialog on Click");
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (showcaseView2 != null && showcaseView2.isShown()) showcaseView2.hide();
		
		selectedTrainingDay = (TrainingDay) arg0.getItemAtPosition(arg2);
		int trainingDayID = selectedTrainingDay.getId();
		AddTrainingDayDialogFragment = TrainingDayAddToWorkoutplanDialogFragment.newInstance(getActivity(), trainingDayID, workoutplanId);
		AddTrainingDayDialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
		
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
		TrainingDayUpdateDialogFragment dialogFragment = TrainingDayUpdateDialogFragment.newInstance(this, trainingDayId);
		dialogFragment.show(getActivity().getFragmentManager(), "Open Exercise Update Dialog on Long Click");
	}

	
	@SuppressWarnings("unchecked")
	public void updateListView(ArrayList<TrainingDay> trainingDayList, final String trainingDayStringName) {
		if (trainingDayList.size() == 0){
			Default d = new Default();
			if (trainingDayStringName == null || trainingDayStringName.equals("")){
				d.setTitel(getString(R.string.noTrainingDay));
			}else{
				d.setTitel(trainingDayStringName + " " + getResources().getString(R.string.NotFound));
				d.setHint(getResources().getString(R.string.Add));
			}
			ArrayList<Default> ld = new ArrayList<Default>();
			ld.add(d);
			DefaultAddListAdapter l = new DefaultAddListAdapter(getActivity(), 0, ld);
			trainingDayListView.setAdapter(l);
			trainingDayListView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
					if (showcaseView1 != null && showcaseView1.isShown()) showcaseView1.hide();
					showDialogAddFragment(trainingDayStringName);
				}	
			});
			
		}else{
			trainingDayListView.setOnItemClickListener(this);
			new BackGroundTask().execute(trainingDayList);
		}
	}
	
	public void openManageWorkoutplan(){
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
    	transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    	transaction.replace(R.id.fragment_container, new ManageWorkoutplan() , "ManageWorkoutplan");
    	transaction.addToBackStack(null);
    	transaction.commit();
    	
		/**
		 * Set the visibility of the NavigationDrawer to Visible
		 */
		((HelperActivity) getActivity()).setNavigationDrawerVisibility(true);
	}
	
	/**
	 * Implementation of Swipe to dismiss function
	 */
	private void loadSwipeToDismiss(){
		SwipeDismissListViewTouchListener touchListener =
            new SwipeDismissListViewTouchListener(trainingDayListView, new SwipeDismissListViewTouchListener.DismissCallbacks() {
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
                		if (trainingDay.getExerciseList() != null){
	                		for (Exercise e : trainingDay.getExerciseList()){
	                			tMapper.deleteExerciseFromTrainingDay(trainingDay.getId(), e);
	                		}
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
 		            for (TrainingDay pa : items){
 		            	if (pa != null){
 		            		count++;
 		            	}
 		            }
 		            String messageUndoBar = count + " " + getResources().getString(R.string.ItemsDeleted);
 		            		 
 		            mUndoBarController.showUndoBar(false,messageUndoBar,itemUndo);	 
                }
    		});
		trainingDayListView.setOnTouchListener(touchListener);
		
        /**
         *  Setting this scroll listener is required to ensure that during ListView scrolling,
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
            		
					tMapper.add(item);
					for (Workoutplan w : item.getWorkoutplanList()){
						wMapper.addTrainingDayToWorkoutplan(item.getId(), w.getId());
					}
					for (PerformanceTarget p : item.getPerformanceTargetList()){
						pMapper.updatePerformanceTarget(p);
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
		public BackGroundTask (){}

	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        getActivity().setProgressBarIndeterminateVisibility(true);
	    }

	    @SuppressWarnings("unchecked")
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

	@Override
	public void onShortCut() {
		openTrainingDayExerciseOverview(selectedTrainingDay);
	}

	public TrainingDayAddToWorkoutPlanCustomToast getCustomToast() {
		return customToast;
	}
	
	private void openTrainingDayExerciseOverview(TrainingDay td){
		if (AddTrainingDayDialogFragment.getShowcaseView().isShown()) AddTrainingDayDialogFragment.getShowcaseView().hide();
		
		Bundle data = new Bundle();
	    data.putInt("TrainingDayId", td.getId());
		
	    TrainingDayExerciseOverview trainingDayExerciseOverview = new TrainingDayExerciseOverview();
	    trainingDayExerciseOverview.setArguments(data);

		FragmentTransaction transaction =  getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.fragment_container, trainingDayExerciseOverview, "TrainingDayExerciseOverview");
        transaction.addToBackStack(null);
        transaction.commit();
	}
}
