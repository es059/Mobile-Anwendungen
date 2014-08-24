package com.workout.log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.remic.workoutlog.R;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingRightInAnimationAdapter;
import com.workout.log.SwipeToDelete.SwipeDismissListViewTouchListener;
import com.workout.log.SwipeToDelete.UndoBarController;
import com.workout.log.SwipeToDelete.UndoItem;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceActual;
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.data.MuscleGroupType;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.PerformanceActualMapper;
import com.workout.log.db.PerformanceTargetMapper;
import com.workout.log.db.PlayMode;
import com.workout.log.fragment.ActionBarDatePickerFragment;
import com.workout.log.listAdapter.PerformanceActualListAdapter;
import com.workout.log.navigation.OnBackPressedListener;
import com.workout.log.navigation.OnHomePressedListener;
import com.workout.log.pedometer.PedometerFragment;

@SuppressLint("SimpleDateFormat")
public class ExerciseSpecific extends Fragment implements UndoBarController.UndoListener{
	private ListView exerciseListView = null;
	private Exercise exercise = null;
	private EditText repetition = null;
	private EditText weight = null;
	private Chronometer timerView = null;
	private ImageButton cardioButton = null;
	
	private int exerciseId;
	private int trainingDayId;
	
	private boolean saveMode = false;
	private PlayMode playMode = PlayMode.Record;
	
	private MuscleGroupType muscleGroupType = MuscleGroupType.Normal;
	private PerformanceActualListAdapter adapter = null;
	private ExerciseOverview exerciseOverview = new ExerciseOverview();
	private PerformanceActualMapper paMapper = null;
	private UndoBarController mUndoBarController = null;
	private ActionBarDatePickerFragment dateFragment = null;
	private static ArrayList<PerformanceActual> performanceActualList = null;

	private PerformanceActualMapper pMapper = null;

	private Fragment pedometer;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.exercise_specific, container,false);
		
		performanceActualList = new ArrayList<PerformanceActual>();
		/**
		 * Set the visibility of the NavigationDrawer to Invisible
		 */
		((HelperActivity) getActivity()).setNavigationDrawerVisibility(false);
		((HelperActivity) getActivity()).setCalledGetParentActivityIntent(false);
		
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
				ExerciseMapper eMapper = new ExerciseMapper(getActivity());
				trainingDayId = transferExtras.getInt("TrainingDayId");
				exerciseId = transferExtras.getInt("ExerciseID");
				exercise = eMapper.getExerciseById(exerciseId);
				getActivity().getActionBar().setTitle(transferExtras.getString("ExerciseName"));
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

		pMapper = new PerformanceActualMapper(getActivity());
		
		dateFragment = (ActionBarDatePickerFragment) getFragmentManager().findFragmentByTag("DateTimePicker");

		paMapper = new PerformanceActualMapper(getActivity());
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");

		exerciseListView = (ListView) getView().findViewById(R.id.exerciseSpecificList);
		loadSwipeToDismiss();

		LinearLayout cardioWrapper = (LinearLayout) getView().findViewById(R.id.CardioWrapper);
		TableLayout headerView = (TableLayout) getView().findViewById(R.id.tableLayout);
		cardioButton = (ImageButton) getView().findViewById(R.id.CardioButton);
		
		Typeface timerTypeface = Typeface.createFromAsset(getActivity().getAssets(),"DS-DIGIB.TTF");
		timerView = (Chronometer) getView().findViewById(R.id.CardioTimer);
		timerView.setTypeface(timerTypeface);
		
		/**
		 * Load the top navigation fragment into the current fragment
		 */
		FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.replace(R.id.specific_dateTimePicker, new ActionBarDatePickerFragment(), "DateTimePicker");
		transaction.commit();
		
		/**
		 * Choose which MuscleGroup is the current exercise and act accordingly
		 */
		if(!exercise.getMuscleGroup().getName().equals(getResources().getString(R.string.Cardio))){
			cardioWrapper.setVisibility(View.INVISIBLE);
			muscleGroupType = MuscleGroupType.Normal;
			
			
			if (performanceActualList.isEmpty()) {
				performanceActualList = paMapper.getCurrentPerformanceActual(exercise, sp.format(new Date()));
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
		}else{
			/**
			 * Cardio Settings
			 */	
			
			/**
			 * Load the pedometer into the current fragment
			 */
			FragmentTransaction Pedometertransaction = getFragmentManager().beginTransaction();
			Pedometertransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			Pedometertransaction.replace(R.id.specific_pedometer, new PedometerFragment(), "Pedometer");
			Pedometertransaction.commit();
						    
			cardioWrapper.setVisibility(View.VISIBLE);
			headerView.setVisibility(View.INVISIBLE);
			exerciseListView.setVisibility(View.INVISIBLE);

			muscleGroupType = MuscleGroupType.Cardio;

			Date currentDate = null;
			if (dateFragment == null){
				currentDate = new Date();
			}else{
				currentDate = dateFragment.getDate();
			}
			
			final PerformanceActual item = pMapper.getPerformanceActualByExerciseAndDate(exercise, currentDate);
			
			if (item != null){
				String tempTimeString = String.valueOf(item.getWeight());
				String[] tempTime = tempTimeString.toString().split("\\.");
				
				if (tempTime[0].length() <= 1){
					tempTimeString = "0" + tempTime[0] + ":";
				}else{
					tempTimeString = tempTime[0] + ":";
				}
				if (tempTime[1].length() <= 1){
					tempTimeString += tempTime[1] + "0";
				}else{
					tempTimeString += tempTime[1];
				}
				
				timerView.setText(tempTimeString);
				if(!tempTimeString.equals("00:00")){
					cardioButton.setImageDrawable(getResources().getDrawable(R.drawable.play));
					playMode = PlayMode.Pause;
				}else{
					cardioButton.setImageDrawable(getResources().getDrawable(R.drawable.record));
					playMode = PlayMode.Record;
				}
			}
						
			cardioButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pedometer = getFragmentManager().findFragmentByTag("Pedometer");
					if (playMode == PlayMode.Record || playMode == PlayMode.Pause){
						int stoppedMilliseconds = 0;

				        String chronoText = timerView.getText().toString();
				        String array[] = chronoText.split(":");
				        
				        if (array.length == 2) {
				          stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 1000+ Integer.parseInt(array[1]) * 1000;
				        } else if (array.length == 3) {
				          stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 60 * 1000 + Integer.parseInt(array[1]) * 60 * 1000 + Integer.parseInt(array[2]) * 1000;
				        }

				        timerView.setBase(SystemClock.elapsedRealtime() - stoppedMilliseconds);
						
						cardioButton.setImageDrawable(getResources().getDrawable(R.drawable.pause)); 
						playMode = PlayMode.Play;
						
						timerView.start();	
						
						((PedometerFragment) pedometer).startStepService();
						((PedometerFragment) pedometer).bindStepService();
					}else{
						cardioButton.setImageDrawable(getResources().getDrawable(R.drawable.play)); 
						timerView.stop();

						playMode = PlayMode.Pause;	
						
						((PedometerFragment) pedometer).unbindStepService();
						((PedometerFragment) pedometer).stopStepService();
					}
					
				}
				
			});
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
		PerformanceTargetMapper ptMapper = new PerformanceTargetMapper(getActivity());
		performanceActualList = new ArrayList<PerformanceActual>();
		if(trainingDayId != -1) {
			PerformanceTarget performanceTarget = ptMapper.getPerformanceTargetByExerciseId(exercise, trainingDayId);
			for (int i = 1; i <= performanceTarget.getSet(); i++) {
				PerformanceActual pa = new PerformanceActual();
				pa.setExercise(exercise);
				pa.setSet(i);
				performanceActualList.add(pa);
			}
			return performanceActualList; }
		else{
			PerformanceActual pa = new PerformanceActual();
			pa.setExercise(exercise);
			pa.setSet(1);
			performanceActualList.add(pa);
			return performanceActualList;
		}
		
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
		if (muscleGroupType == MuscleGroupType.Cardio) menu.findItem(R.id.menu_add).setIcon(getResources().getDrawable(R.drawable.stop));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.menu_add) {
			if(muscleGroupType != MuscleGroupType.Cardio){
				addPerformanceActualItem();
			}else{
				timerView.setBase(SystemClock.elapsedRealtime());
				timerView.stop();
				cardioButton.setImageDrawable(getResources().getDrawable(R.drawable.record)); 
				playMode = playMode.Record;
				
				/**
				 * Stop Pedometer
				 */
				if(((PedometerFragment) pedometer).isRunning()){
					((PedometerFragment) pedometer).resetValues(true);
					((PedometerFragment) pedometer).unbindStepService();
					((PedometerFragment) pedometer).stopStepService();
				}
                return true;
			}
		}
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
		
		if (muscleGroupType == MuscleGroupType.Cardio){
			/**
			 * Stop Pedometer
			 */
			if(((PedometerFragment) pedometer).isRunning()){
				((PedometerFragment) pedometer).unbindStepService();
				((PedometerFragment) pedometer).stopStepService();
			}
			}
	}

	/**
	 * Add a new PerformanceActual Object to the ListView
	 * 
	 */
	public void addPerformanceActualItem() {
		if (dateFragment == null) dateFragment = (ActionBarDatePickerFragment) getFragmentManager().findFragmentByTag("DateTimePicker");
		// New PerformanceActual Object
		PerformanceActual pa = new PerformanceActual();
		pa.setExercise(exercise);
		pa.setSet(performanceActualList.size() + 1);
		pa.setTimestamp(dateFragment.getDate());
		// Update Adapter + ListView
		adapter.add(pa);
		//Save the data into the ArrayList
		saveIntoList();
		// Set the ArrayList on the current value
		performanceActualList = adapter.getPerformanceActualList();
		// Show the User a hint message
		Toast.makeText(getActivity(), getResources().getString(R.string.ExerciseSpecific_NewSet),Toast.LENGTH_SHORT).show();
	}

	/**
	 * Save the current data in the ListView into the ArrayList
	 */
	public void saveIntoList(){
		for (PerformanceActual item : performanceActualList) {
			View v = getViewByPosition(item.getSet() - 1, exerciseListView);			
			
			repetition = (EditText) v.findViewById(R.id.specific_edit_repetition);
			weight = (EditText) v.findViewById(R.id.specific_edit_weight);

			if (!repetition.getText().toString().isEmpty()) {
				item.setRepetition(Integer.parseInt(repetition.getText().toString()));
			}

			if (!weight.getText().toString().isEmpty()) {
				item.setWeight(Double.parseDouble(weight.getText().toString()));
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
		dateFragment = (ActionBarDatePickerFragment) getFragmentManager().findFragmentByTag("DateTimePicker");
			
		/**
		 * If the MuscleGoup is anything but Cardio
		 */
		if (muscleGroupType == MuscleGroupType.Normal){
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
					if (saveMode == true) {
						pMapper.addPerformanceActual(item, new Date());
					} else if (item.getRepetition() != -1 || item.getWeight() != -1) {
						pMapper.addPerformanceActual(item, new Date());
						saveMode = true;
					} else {
						saveMode = false;
					}
				} else {
					if (!sameWeight || !sameRep) {
						pMapper.addPerformanceActual(item, item.getTimestamp());
						saveMode = true;
					}
				}
			}
		}else{	
			PerformanceActual item = pMapper.getPerformanceActualByExerciseAndDate(exercise, dateFragment.getDate());
			if (item == null){
				item = new PerformanceActual();
			}else{
				saveMode = true;
			}
			
			if (!timerView.getText().toString().equals("00:00") || saveMode == true){
				/**
				 * Time is stored in the Weight Column of the database
				 */
				

				/**
				 * Modify the text of the timerView to fit a double
				 */
				String[] tempTime = timerView.getText().toString().split(":");
				
				String tempTimeString = tempTime[0] + ".";
				tempTimeString += tempTime[1];
				
				item.setExercise(exercise);
				item.setWeight(Double.parseDouble(tempTimeString));
				
				if (dateFragment.isToday()) {
					pMapper.addPerformanceActual(item, new Date());
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
		            
	            	/**
	              	 * Save the data into the ArrayList
	              	 */
	        		saveIntoList();
		        	
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
}
