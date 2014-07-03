package com.workout.log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

import com.example.workoutlog.R;
import com.workout.log.SwipeToDelete.SwipeDismissListViewTouchListener;
import com.workout.log.SwipeToDelete.UndoBarController;
import com.workout.log.SwipeToDelete.UndoItem;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceActual;
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.PerformanceActualMapper;
import com.workout.log.db.PerformanceTargetMapper;
import com.workout.log.fragment.ActionBarDatePickerFragment;
import com.workout.log.listAdapter.PerformanceActualListAdapter;
import com.workout.log.navigation.OnBackPressedListener;
import com.workout.log.navigation.OnHomePressedListener;

@SuppressLint("SimpleDateFormat")
public class ExerciseSpecific extends Fragment implements
		UndoBarController.UndoListener {
	private ListView exerciseListView = null;
	private Exercise exercise = null;
	private int exerciseId;
	private int trainingDayId;
	private EditText repetition = null;
	private EditText weight = null;
	private boolean saveMode = false;

	private PerformanceActualListAdapter adapter = null;
	private ExerciseOverview exerciseOverview = new ExerciseOverview();
	private PerformanceActualMapper paMapper = null;
	private UndoBarController mUndoBarController = null;
	private ActionBarDatePickerFragment dateFragment = null;
	private static ArrayList<PerformanceActual> performanceActualList = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.exercise_specific, container,
				false);
		performanceActualList = new ArrayList<PerformanceActual>();
		/**
		 * Set the visibility of the NavigationDrawer to Invisible
		 */
		((HelperActivity) getActivity()).setNavigationDrawerVisibility(false);

		/**
		 * Load the top navigation fragment into the current fragment
		 */
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.replace(R.id.specific_dateTimePicker,
				new ActionBarDatePickerFragment(), "DateTimePicker");
		transaction.commit();

		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		setHasOptionsMenu(true);

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
						return null;
					}
				});

		/**
		 * Receive the arguments set by ExerciseOverview
		 */
		final Bundle transferExtras = getArguments();
		if (transferExtras != null) {
			try {
				trainingDayId = transferExtras.getInt("TrainingDayId");
				exerciseId = transferExtras.getInt("ExerciseID");
				getActivity().getActionBar().setTitle(
						transferExtras.getString("ExerciseName"));
			} catch (Exception e) {
				e.printStackTrace();
			}
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

		dateFragment = (ActionBarDatePickerFragment) getFragmentManager()
				.findFragmentByTag("DateTimePicker");
		
		ExerciseMapper eMapper = new ExerciseMapper(getActivity());
		exercise = eMapper.getExerciseById(exerciseId);

		paMapper = new PerformanceActualMapper(getActivity());
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");

		exerciseListView = (ListView) getView().findViewById(
				R.id.exerciseSpecificList);
		loadSwipeToDismiss();

		if (performanceActualList.isEmpty()) {
			performanceActualList = paMapper.getCurrentPerformanceActual(
					exercise, sp.format(new Date()));

			/**
			 * If the ArrayList is empty it means, that there where are no
			 * training data for today the ListView is then generated with the
			 * information of the database table performanceTarget
			 */
			if (performanceActualList.isEmpty()) {
				performanceActualList = prepareStandardListView();
				updateListView(performanceActualList);
			} else {
				updateListView(performanceActualList);
			}
		} else {
			updateListView(performanceActualList);
		}
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
	 * Prepares the ListView for the case that there was no current
	 * PerformanceActual Object Mainly for the external call from <@see
	 * ActionBarDatePickerFragment>
	 * 
	 */
	public ArrayList<PerformanceActual> prepareStandardListView() {
		PerformanceTargetMapper ptMapper = new PerformanceTargetMapper(
				getActivity());
		PerformanceTarget performanceTarget = ptMapper
				.getPerformanceTargetByExerciseId(exercise, trainingDayId);

		performanceActualList = new ArrayList<PerformanceActual>();

		for (int i = 1; i <= performanceTarget.getSet(); i++) {
			PerformanceActual pa = new PerformanceActual();
			pa.setExercise(exercise);
			pa.setSet(i);
			performanceActualList.add(pa);
		}
		return performanceActualList;
	}

	/**
	 * Update the ListView with a given ArrayList
	 * 
	 * @param ArrayList
	 *            <PerformanceActual>
	 * @author Eric Schmidt
	 */
	public void updateListView(ArrayList<PerformanceActual> pa) {
		adapter = new PerformanceActualListAdapter(getActivity(), 0, pa);
		exerciseListView.setAdapter(adapter);
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
		switch (id) {
		case R.id.menu_add:
			addPerformanceActualItem();
			break;
		case R.id.menu_save:
			savePerformanceActual();
			break;
		}
		return super.onOptionsItemSelected(item);
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

		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.replace(R.id.fragment_container, exerciseOverview,
				"ExerciseOverview");
		transaction.addToBackStack(null);
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
	public void addPerformanceActualItem() {
		// New PerformanceActual Object
		PerformanceActual pa = new PerformanceActual();
		pa.setExercise(exercise);
		pa.setSet(performanceActualList.size() + 1);
		pa.setTimestamp(dateFragment.getDate());
		// Update Adapter + ListView
		adapter.add(pa);
		// Set the ArrayList on the current value
		performanceActualList = adapter.getPerformanceActualList();
		// Show the User a hint message
		Toast.makeText(getActivity(), "Neuen Satz hinzugefügt!",
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * Save/Update all PerfromanceActual Objects in Database
	 * 
	 */
	public void savePerformanceActual() {
		PerformanceActualMapper pMapper = new PerformanceActualMapper(
				getActivity());
		dateFragment = (ActionBarDatePickerFragment) getFragmentManager()
				.findFragmentByTag("DateTimePicker");
		/**
		 * Variables used to identify if a item is changed or not. True if the
		 * values have not changed
		 */
		boolean sameRep = true;
		boolean sameWeight = true;

		for (PerformanceActual item : performanceActualList) {
			View v = exerciseListView.getChildAt(item.getSet() - 1);
			repetition = (EditText) v
					.findViewById(R.id.specific_edit_repetition);
			weight = (EditText) v.findViewById(R.id.specific_edit_weight);

			if (!repetition.getText().toString().isEmpty()) {
				if (Integer.parseInt(repetition.getText().toString()) != item
						.getRepetition()) {
					item.setRepetition(Integer.parseInt(repetition.getText()
							.toString()));
					sameRep = false;
				} else {
					sameRep = true;
				}
			} else {
				item.setRepetition(-1);
			}

			if (!weight.getText().toString().isEmpty()) {
				if (Double.parseDouble(weight.getText().toString()) != item
						.getWeight()) {
					item.setWeight(Double.parseDouble(weight.getText()
							.toString()));
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
				if (saveMode == true) {
					pMapper.savePerformanceActual(item, new Date());
				} else if (item.getRepetition() != -1 || item.getWeight() != -1) {
					pMapper.savePerformanceActual(item, new Date());
					saveMode = true;
				} else {
					saveMode = false;
				}
			} else {
				if (!sameWeight || !sameRep) {
					pMapper.savePerformanceActual(item, item.getTimestamp());
					saveMode = true;
				}
			}
		}
		if (saveMode)
			Toast.makeText(getActivity(), "Daten wurden gespeichert",
					Toast.LENGTH_SHORT).show();
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
			 int i = 0;
			 	
			 
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
			            i=0;
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
		            			if (pa.getId() != 0) paMapper.savePerformanceActual(pa, pa.getTimestamp());
		            		}
		            	}
		            	
		            	PerformanceActual item=adapter.getItem(position);
		            	adapter.remove(adapter.getItem(position));
		            	
		            	items[i]=item;
		                itemPositions[i]=position;
		                i++;
		                
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
		             String messageUndoBar = count + " Item(s) gelöscht";
		            		 
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
								paMapper.savePerformanceActual(pa,
										pa.getTimestamp());
						}
					}

					if (item.getId() != 0)
						paMapper.savePerformanceActual(item,
								item.getTimestamp());
					adapter.insert(item, itemPosition);
					adapter.notifyDataSetChanged();
				}
			}
		}
	}
}
