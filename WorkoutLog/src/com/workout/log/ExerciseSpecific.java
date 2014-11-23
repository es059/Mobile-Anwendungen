package com.workout.log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingRightInAnimationAdapter;
import com.remic.workoutlog.R;
import com.workout.log.SwipeToDelete.SwipeDismissListViewTouchListener;
import com.workout.log.SwipeToDelete.UndoBarController;
import com.workout.log.SwipeToDelete.UndoItem;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceActual;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.PerformanceActualMapper;
import com.workout.log.fragment.ActionBarDatePickerFragment;
import com.workout.log.fragment.SpecificAddSetFragment;
import com.workout.log.fragment.SpecificCounterFragment;
import com.workout.log.listAdapter.PerformanceActualListAdapter;
import com.workout.log.navigation.OnBackPressedListener;
import com.workout.log.navigation.OnHomePressedListener;

@SuppressLint("SimpleDateFormat")
public class ExerciseSpecific extends Fragment implements UndoBarController.UndoListener{
	private ListView exerciseListView = null;
	private Exercise exercise = null;
	private EditText repetition = null;
	private EditText weight = null;
	
	private int exerciseId;
	private int trainingDayId;
	
	private boolean saveMode = false;
	
	private PerformanceActualListAdapter adapter = null;
	private ExerciseOverview exerciseOverview = new ExerciseOverview();
	private PerformanceActualMapper paMapper = null;
	private UndoBarController mUndoBarController = null;
	private ActionBarDatePickerFragment dateFragment = null;
	private static ArrayList<PerformanceActual> performanceActualList = null;

	private PerformanceActualMapper pMapper = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.exercise_specific, container,false);
		
		performanceActualList = new ArrayList<PerformanceActual>();
		
		FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.replace(R.id.specific_dateTimePicker, new ActionBarDatePickerFragment(), "DateTimePicker");
		transaction.commit();
		
		transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.replace(R.id.specific_timer, new SpecificCounterFragment(), "SpecificCounterFragment");
		transaction.commit();
		
		transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.replace(R.id.specific_centralAddSet, new SpecificAddSetFragment(), "SpecificAddSetFragment");
		transaction.commit();
			
		/**
		 * Receive the arguments set by ExerciseOverview
		 */		
		final Bundle transferExtras = getArguments();
		if (transferExtras != null) {
			try {
				ExerciseMapper eMapper = new ExerciseMapper(getActivity());
				trainingDayId = transferExtras.getInt("TrainingDayId");
				exerciseId = transferExtras.getInt("ExerciseID");
				exercise = eMapper.getExerciseById(exerciseId);
				((HelperActivity) getActivity()).setActionBarTitle(transferExtras.getString("ExerciseName"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		((HelperActivity) getActivity()).setNavigationDrawerVisibility(false);
		((HelperActivity) getActivity()).hamburgerToArrow();
		setHasOptionsMenu(true);
		
		if (trainingDayId != -1){
			
			((HelperActivity) getActivity()).setCalledGetParentActivityIntent(false);
			
			/**
			 * Handles the behavior if the back button is pressed
			 */
			((HelperActivity) getActivity())
					.setOnBackPressedListener(new OnBackPressedListener() {
						@Override
						public void doBack() {
							savePerformanceActual();
							openExerciseOverview();
							((HelperActivity) getActivity())
									.setOnBackPressedListener(null);
							((HelperActivity) getActivity()).arrowToHamburger();
						}
					});
			/**
			 * Handles the behavior if the Home button in the actionbar is pressed
			 */
			((HelperActivity) getActivity())
					.setOnHomePressedListener(new OnHomePressedListener() {
						@Override
						public Intent doHome() {
							savePerformanceActual();
							openExerciseOverview();
							((HelperActivity) getActivity())
									.setOnBackPressedListener(null);
							((HelperActivity) getActivity()).arrowToHamburger();
							return null;
						}
					});
		}

		return view;
	}

	/**
	 * Initialization of the mapper classes and retrieve of the saved values of
	 * the list view
	 * 
	 */
	@Override
	public void onResume() {
		super.onResume();

		pMapper = new PerformanceActualMapper(getActivity());
		
		dateFragment = (ActionBarDatePickerFragment) getFragmentManager().findFragmentByTag("DateTimePicker");

		paMapper = new PerformanceActualMapper(getActivity());
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");

		exerciseListView = (ListView) getView().findViewById(R.id.exerciseSpecificList);
		loadSwipeToDismiss();
				
		if (performanceActualList.isEmpty()) {
			performanceActualList = paMapper.getCurrentPerformanceActual(exercise, sp.format(new Date()));
		}
		
		updateListView(performanceActualList);
	}

	/**
	 * Save the data which is currently in the rows of the listview
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		closeKeyboard();
		if (this.isVisible()) {
			for (PerformanceActual item : performanceActualList) {
				View v = exerciseListView.getChildAt(item.getSet() - 1);

				repetition = (EditText) v
						.findViewById(R.id.specific_edit_repetition);
				weight = (EditText) v.findViewById(R.id.specific_edit_weight);

				if (!repetition.getText().toString().isEmpty()) {
					item.setRepetition(Integer.parseInt(repetition.getText()
							.toString()));
				}
				if (!weight.getText().toString().isEmpty()) {
					item.setWeight(Double.parseDouble(weight.getText()
							.toString()));
				}
			}
		}
	}

	/**
	 * Hide the AddSet-Fragment in the View
	 */
	public void hideAddSetFragment(){
		getView().findViewById(R.id.specific_centralAddSet).setVisibility(View.GONE);
	}
	
	/**
	 * Show the AddSet-Fragment in the View
	 */
	public void showAddSetFragment(){
		getView().findViewById(R.id.specific_centralAddSet).setVisibility(View.VISIBLE);
	}
	
	/**
	 * Return the current trainingDay
	 * 
	 * @author Eric Schmidt
	 */
	public int getTrainingDayId(){
		return trainingDayId;
	}
	
	/**
	 * Update the ListView with a given ArrayList
	 * 
	 * @param ArrayList
	 *            <PerformanceActual>
	 * @author Eric Schmidt
	 */
	public void updateListView(ArrayList<PerformanceActual> pa) {
		adapter = new PerformanceActualListAdapter(getActivity(), pa);
			
		/**
		 * Enable animation of the ListView Items
		 */
		SwingRightInAnimationAdapter swingRightInAnimationAdapter = new SwingRightInAnimationAdapter(adapter);
		swingRightInAnimationAdapter.setAbsListView(exerciseListView);
		
		exerciseListView.setAdapter(swingRightInAnimationAdapter);
		performanceActualList = pa;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.exercise_specific_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.menu_statistic){
			savePerformanceActual();
			openDailyStatistic();
		}
		return super.onOptionsItemSelected(item);
	}

	public void openDailyStatistic(){
		if (paMapper.getAllStatisticElements(exercise.getId(), paMapper.getAllDates(exercise)).size() != 0){
			DailyStatistic dailyStatistic = new DailyStatistic();
			Bundle args = new Bundle();
			
			args.putInt("exercise_Id", exercise.getId());
			args.putString("exerciseName", exercise.getName());
			dailyStatistic.setArguments(args);
			
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			transaction.replace(R.id.fragment_container, dailyStatistic, "DailyStatistic");
			transaction.commit();
		}else{
			Toast.makeText(getActivity(), getResources().getString(R.string.ExerciseSpecific_NoDataAvailable), Toast.LENGTH_SHORT).show();
		}
	}
	
	
	/**
	 * Opens the ExerciseOverview Activity and tells it which TrainingDay to
	 * open
	 * 
	 * @author Eric Schmidt
	 */
	public void openExerciseOverview() {
		Bundle data = new Bundle();

		data.putInt("TrainingDayId", trainingDayId);
		data.putBoolean("SaveMode", saveMode);

		exerciseOverview = new ExerciseOverview();
		exerciseOverview.setArguments(data);

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.replace(R.id.fragment_container, exerciseOverview, "ExerciseOverview");
		transaction.commit();

		closeKeyboard();

		/**
		 * Set the visibility of the NavigationDrawer to Visible
		 */
		((HelperActivity) getActivity()).setNavigationDrawerVisibility(true);
	}

	/**
	 * Add a new PerformanceActual Object to the ListView
	 * 
	 */
	public void addPerformanceActualItem(double weight, int rep) {
		if (dateFragment == null) dateFragment = (ActionBarDatePickerFragment) getFragmentManager().findFragmentByTag("DateTimePicker");
		// New PerformanceActual Object
		PerformanceActual pa = new PerformanceActual();
		pa.setExercise(exercise);
		pa.setSet(performanceActualList.size() + 1);
		pa.setTimestamp(dateFragment.getDate());

		pa.setWeight(weight);
		pa.setRepetition(rep);
		
		// Update Adapter + ListView
		adapter.add(pa);
		//Save the data into the ArrayList
		savePerformanceActualToday();
		// Set the ArrayList on the current value
		performanceActualList = adapter.getPerformanceActualList();
		// Show the User a hint message
		Toast.makeText(getActivity(), getResources().getString(R.string.ExerciseSpecific_NewSet),Toast.LENGTH_SHORT).show();
		
	}

	/**
	 * Save the current data in the ListView into the ArrayList
	 */
	public void savePerformanceActualToday(){
		for (PerformanceActual item : performanceActualList) {
			View v = getViewByPosition(item.getSet() - 1, exerciseListView);			
			
			repetition = (EditText) v.findViewById(R.id.specific_edit_repetition);
			weight = (EditText) v.findViewById(R.id.specific_edit_weight);

			if (!repetition.getText().toString().isEmpty()) {
				item.setRepetition(Integer.parseInt(repetition.getText().toString()));
			}else{
				item.setRepetition(-1);
			}
			
			if (!weight.getText().toString().isEmpty()) {
				item.setWeight(Double.parseDouble(weight.getText().toString()));
			}else{
				item.setWeight(-1);
			}
			
			if (item.getWeight() != -1 && item.getRepetition() != -1){
				pMapper.addPerformanceActual(item, new Date());
			}
		}
	}
	
	
	/**
	 * Helps to determine the view of the current item in the listview.
	 * Even if it is out of sight
	 * 
	 * @param position
	 * @param listView
	 * @return the view of the item
	 * 
	 * @author Eric Schmidt
	 */
	public View getViewByPosition(int position, ListView listView) {
	    final int firstListItemPosition = listView.getFirstVisiblePosition();
	    final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

	    if (position < firstListItemPosition || position > lastListItemPosition ) {
	        return listView.getAdapter().getView(position, listView.getChildAt(position), listView);
	    } else {
	        final int childIndex = position - firstListItemPosition;
	        return listView.getChildAt(childIndex);
	    }
	}
	
	/**
	 * Save/Update all PerfromanceActual Objects in Database
	 * 
	 */
	public void savePerformanceActual() {
		saveMode = false;
		dateFragment = (ActionBarDatePickerFragment) getFragmentManager().findFragmentByTag("DateTimePicker");
		/**
		 * Variables used to identify if a item is changed or not. True if the
		 * values have not changed
		 */
		boolean sameRep = true;
		boolean sameWeight = true;

		for (PerformanceActual item : performanceActualList) {
			View v = getViewByPosition(item.getSet() - 1, exerciseListView);			
			
			repetition = (EditText) v.findViewById(R.id.specific_edit_repetition);
			weight = (EditText) v.findViewById(R.id.specific_edit_weight);

			if (!repetition.getText().toString().isEmpty()) {
				if (Integer.parseInt(repetition.getText().toString()) != item.getRepetition()) {
					item.setRepetition(Integer.parseInt(repetition.getText().toString()));
					sameRep = false;
				} else {
					sameRep = true;
				}
			} else {
				item.setRepetition(-1);
			}

			if (!weight.getText().toString().isEmpty()) {
				if (Double.parseDouble(weight.getText().toString()) != item.getWeight()) {
					item.setWeight(Double.parseDouble(weight.getText().toString()));
					sameWeight = false;
				} else {
					sameWeight = true;
				}
			} else {
				item.setWeight(-1);
			}

			/**
			 * This block checks if the current Day is Today. Afterwards the
			 * Recordset is saved if either the repetition or weight TextView is
			 * filled. After this every Entry in the ListView is saved. This
			 * ensures that if a user goes back to an old Entry and returns to
			 * the current all of the sets are saved.
			 */
			if (dateFragment.isToday()) {
				if ((item.getRepetition() != -1 || item.getWeight() != -1) && (!sameWeight || !sameRep)) {
					pMapper.addPerformanceActual(item, new Date());
					saveMode = true;
				}
			} else {
				if (!sameWeight || !sameRep) {
					pMapper.addPerformanceActual(item, item.getTimestamp());
					saveMode = true;
				}
			}
		}		
		if (saveMode) Toast.makeText(getActivity(), getResources().getString(R.string.DataSaved), Toast.LENGTH_SHORT).show();
	}

	/**
	 * Returns the current Exercise
	 * 
	 * @param Exercise
	 * @author Eric Schmidt
	 */
	public Exercise getExercise() {
		return exercise;
	}

	/**
	 * Close the Keyboard if still visible
	 * 
	 * @author Eric Schmidt
	 */
	public void closeKeyboard() {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(exerciseListView.getWindowToken(), 0);
	}

	/**
	 * Create a ListView-specific touch listener. ListViews are given special
	 * treatment because by default they handle touches for their list items...
	 * i.e. they're in charge of drawing the pressed state (the list selector),
	 * handling list item clicks, etc.
	 * 
	 * @author Remi Tessier
	 */
	private void loadSwipeToDismiss(){ 
		 SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(exerciseListView,
		    new SwipeDismissListViewTouchListener.DismissCallbacks() {
			 PerformanceActual[] items= null;
			 int[] itemPositions = null;
			 int arrayCount = 0;
			 	
			 
			   @Override
		       public boolean canDismiss(int position) {
				   return position <= adapter.getCount() - 1;
		       }
			   
		        @Override
		        public void onDismiss(ListView listView, int[] reverseSortedPositions) {
		         	/**
		         	 * Used for the undo actions
		         	 */
		        	if(mUndoBarController.getUndoBar().getVisibility() == View.GONE){
			         	items=new PerformanceActual[performanceActualList.size()];
			            itemPositions =new int[performanceActualList.size()];
			            arrayCount=0;
		        	}
		        	
		            for (int position : reverseSortedPositions) {
		            	PerformanceActual performanceActual = (PerformanceActual) exerciseListView.getItemAtPosition(position);
	                	/**
	                	 * Delete from performanceActual
	                	 */
		            	paMapper = new PerformanceActualMapper(getActivity());   
		            	if(performanceActual.getId()!= 0) paMapper.deletePerformanceActualById(performanceActual.getId());
		            	
		        		/**
		        		 * Fix the set numbers from the other performanceActual items
		        		 */
		            	for (PerformanceActual pa : performanceActualList){
		            		if(pa.getSet() > performanceActual.getSet()){
		            			pa.setSet(pa.getSet()-1);
		            			/**
		            			 * If the id is 0, the current performanceActual was not saved into the database and therefore
		            			 * should not be updated or rather inserted into the database
		            			 */
		            			if (pa.getId() != 0) paMapper.addPerformanceActual(pa, pa.getTimestamp());
		            		}
		            	}		            	
		            	PerformanceActual item=adapter.getItem(position);
		            	adapter.remove(item);
		            	
		            	items[arrayCount]=item;
		                itemPositions[arrayCount]=position;
		                arrayCount++;
		                
		                
		            	/**
		            	 * Set the ArrayList on the current value
		            	 */
		    			performanceActualList = adapter.getPerformanceActualList();
		            }
		            adapter.notifyDataSetChanged();
		            
		            /**
		             * Show UndoBar
		             */
		             UndoItem itemUndo=new UndoItem(items,itemPositions);
		   
		             /**
		              * Undobar message
		              */
		             int count = 0;
		             for (PerformanceActual pa : items){
		            	 if (pa != null){
		            		 count++;
		            	 }
		             }
		             String messageUndoBar = count + " " + getResources().getString(R.string.ItemsDeleted);
		            		 
		             mUndoBarController.showUndoBar(false,messageUndoBar,itemUndo);
		         }
		        
		    });
		 
		 exerciseListView.setOnTouchListener(touchListener);
		 
		 /**
		  *  Setting this scroll listener is required to ensure that during ListView scrolling,
		  *  we don't look for swipes.
		  */
		 exerciseListView.setOnScrollListener(touchListener.makeScrollListener());
		 
		/**
		 * UndoController
		 */
	    if (mUndoBarController==null)mUndoBarController = new UndoBarController(getView().findViewById(R.id.undobar), this);
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
			paMapper = new PerformanceActualMapper(getActivity());

			UndoItem itemRetrieve = (UndoItem) token;
			PerformanceActual[] items = (PerformanceActual[]) itemRetrieve.items;
			int[] itemPositions = itemRetrieve.itemPosition;

			if (items != null && itemPositions != null) {
				int end= 0;
			    for (PerformanceActual pa : items){
	            	 if (pa != null){
	            		 end++;
	            	 }
	             }


				for (int i = end - 1; i >= 0; i--) {
					PerformanceActual item = items[i];
					int itemPosition = itemPositions[i];

					/**
					 * Fix the set numbers from the other performanceActual
					 * items
					 */
					for (PerformanceActual pa : performanceActualList) {
						if (pa.getSet() >= item.getSet()) {
							pa.setSet(pa.getSet() + 1);
							/**
							 * If the id is 0, the current performanceActual was
							 * not saved into the database and therefore should
							 * not be updated or rather inserted into the
							 * database
							 */
							if (pa.getId() != 0)
								paMapper.addPerformanceActual(pa,
										pa.getTimestamp());
						}
					}

					if (item.getId() != 0)
						paMapper.addPerformanceActual(item,
								item.getTimestamp());
					adapter.insert(item, itemPosition);
					adapter.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("SpecificCounterFragment");
		if(fragment != null)
		    getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
		Fragment fragment2 = getActivity().getSupportFragmentManager().findFragmentByTag("DateTimePicker");
		if(fragment2 != null)
		    getActivity().getSupportFragmentManager().beginTransaction().remove(fragment2).commit();
		Fragment fragment3 = getActivity().getSupportFragmentManager().findFragmentByTag("ExerciseSpecific");
		if(fragment2 != null)
		    getActivity().getSupportFragmentManager().beginTransaction().remove(fragment3).commit();
		
		((HelperActivity) getActivity()).setCalledGetParentActivityIntent(false);
		super.onDestroy();
	}
	
}
