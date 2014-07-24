package com.workout.log;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.example.workoutlog.R;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingLeftInAnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingRightInAnimationAdapter;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.MuscleGroup;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.data.ExerciseItem;
import com.workout.log.data.MuscleGroupSectionItem;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.MuscleGroupMapper;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.fragment.ActionBarTrainingDayPickerFragment;
import com.workout.log.listAdapter.OverviewAdapter;

public class ExerciseOverview extends Fragment implements OnItemClickListener {
	private static ListView exerciseListView; 
    
	private static int trainingDayId = -1;
    private static ExerciseSpecific exerciseSpecific = new ExerciseSpecific();
    public static ActionBarTrainingDayPickerFragment actionBarTrainingDayPickerFragment = null;
    private SwipeAnimation swipeAnimation = null;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.exercise_overview, container, false);
		/**
		 * Add the top navigation fragment to the current fragment
		 */
	    FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.overview_trainingDayPicker, new ActionBarTrainingDayPickerFragment(), "TrainingDayPicker");
        transaction.commit();
        
        return view;
	}
	
    /**
     * If the Activity was called with a setExtra method, 
     * the ListView is filled by a method call in the fragment
     * 
     * @author Eric Schmidt
     * @date 18.04.2014
     */  
	@Override
	public void onResume(){
		super.onResume();
		
		swipeAnimation = SwipeAnimation.Normal;
		exerciseSpecific = new ExerciseSpecific();
		actionBarTrainingDayPickerFragment = (ActionBarTrainingDayPickerFragment) getActivity().
				getSupportFragmentManager().findFragmentByTag("TrainingDayPicker");
		
		final Bundle transferExtras = getArguments();
		exerciseListView = (ListView) getView().findViewById(R.id.exerciseOverviewList);
		if (transferExtras != null){				
			try{
				if (transferExtras.getBoolean("SaveMode")){
					Toast.makeText(getActivity(), "Daten wurden gespeichert", Toast.LENGTH_SHORT).show();
				}
				trainingDayId = transferExtras.getInt("TrainingDayId");
				actionBarTrainingDayPickerFragment.setCurrentTrainingDay(trainingDayId);
			} catch (Exception e){
				e.printStackTrace();
			}
		}else{	
			updateListView(trainingDayId);
		}
		exerciseListView.setOnItemClickListener(this);
		
		  gestureDetector = new GestureDetector(getActivity(), new MyGestureDetector());
	        gestureListener = new View.OnTouchListener() {
	            public boolean onTouch(View v, MotionEvent event) {
	                return gestureDetector.onTouchEvent(event);
	            }
	        };
		exerciseListView.setOnTouchListener(gestureListener);
	}
	
	private class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    actionBarTrainingDayPickerFragment.onNext();
                    
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	actionBarTrainingDayPickerFragment.onPrevious();
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

            @Override
        public boolean onDown(MotionEvent e) {
              return true;
        }
    }
	 
	public void setSwipeAnimation(SwipeAnimation swipeAnimation){
		this.swipeAnimation = swipeAnimation;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Exercise exercise;
		exercise = (Exercise) exerciseListView.getItemAtPosition(position);
		openExerciseSpecific(exercise);
	}
	
	public static void setTrainingDay(int id){
		trainingDayId = id;
	}
	
	/**
	 * Updates Exercise ListViews using a AsyncTask. 
	 * If the TrainingDay is unknown parse -1 as parameter. This method is then using
	 * the first trainingDay as the source of exercises.
	 * 
	 * @param trainingDayId
	 * @author Eric Schmidt
	 */
	public void updateListView(int trainingDayId){
		new BackGroundTask(exerciseListView).execute(trainingDayId);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent= null;
		switch (item.getItemId()){
			case R.id.menu_add:
				intent = new Intent();
				intent.setClass(getActivity(), ExerciseAdd.class);
				startActivity(intent);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Method to open the ExerciseSpecific Activity with the selected Exercise and the current TrainingDay
	 * 
	 * @param exercise
	 * @author Eric Schmidt
	 */
	private void openExerciseSpecific (Exercise exercise){
		Bundle data = new Bundle();
		ActionBarTrainingDayPickerFragment actionBarTrainingDayPickerFragment = 
				(ActionBarTrainingDayPickerFragment) getFragmentManager().findFragmentByTag("TrainingDayPicker");
		
        data.putInt("ExerciseID",exercise.getId());
        data.putString("ExerciseName",exercise.getName());
        data.putInt("TrainingDayId",actionBarTrainingDayPickerFragment.getCurrentTrainingDay().getId());
        
        exerciseSpecific.setArguments(data);
        
	    FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.fragment_container, exerciseSpecific , "ExerciseSpecific");
        transaction.commit();
	}
		
	/**
	 * Handels the Database queries in an Async Task
	 * 
	 * @author Eric Schmidt
	 */
	public class BackGroundTask extends AsyncTask<Integer, Void, OverviewAdapter> {
	    /**
	     * Variables for the UpdateListView method
	     */
		private ArrayList<TrainingDay> tList;
		private ArrayList<Exercise> eList;
		private ArrayList<MuscleGroup> mList;
		private ArrayList<ExerciseItem> listComplete;
		private ArrayList<Exercise> eListMuscleGroup;
		
		private ListView exerciseView;

		public BackGroundTask (ListView exerciseView){	
			this.exerciseView = exerciseView;

			exerciseView.setAdapter(null);
		}

	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        getActivity().setProgressBarIndeterminateVisibility(true);
	    }

	    @Override
	    protected OverviewAdapter doInBackground(Integer... params) {
	    	/**
	    	 * Ensures that there are no unnecessary Database queries
	    	 * if the ArrayList<TrainingDay> is already referenced.
	    	 */
			if (tList == null){
				//Select Current Workoutplan
				WorkoutplanMapper wMapper = new WorkoutplanMapper(getActivity());
				Workoutplan w = wMapper.getCurrent();
				//Select all Trainingdays from the current Workoutplan
				TrainingDayMapper tMapper = new TrainingDayMapper(getActivity());
				tList = tMapper.getAllTrainingDaysFromWorkoutplan(w.getId());
			}
			if (!tList.isEmpty()){
		    	int trainingDayId;
		    	if (params[0] == -1){
		    		trainingDayId = tList.get(0).getId();
		    	}else{
		    		trainingDayId = params[0];
		    	}
		    	
		    	if (mList == null){	
					//Select all MuscleGroups
					MuscleGroupMapper mMapper = new MuscleGroupMapper(getActivity());
					mList = mMapper.getAll();
				}
				//Select Exercises from Selected Trainingday and MuscleGroup 
				ExerciseMapper eMapper = new ExerciseMapper(getActivity());
				eList = eMapper.getExerciseByTrainingDay(trainingDayId);
				listComplete = new ArrayList<ExerciseItem>();
				for (MuscleGroup m : mList){
					eListMuscleGroup = eMapper.getExerciseByMuscleGroup(eList, m.getId());
					if (!eListMuscleGroup.isEmpty()){
						listComplete.add(new MuscleGroupSectionItem(m.getName()));
						listComplete.addAll(eListMuscleGroup);
					}
				}
		    
				OverviewAdapter adapter = new OverviewAdapter(getActivity(), listComplete, trainingDayId);
				return adapter;	
			}
			return null;
	    }

	    @Override
	    protected void onPostExecute(OverviewAdapter result) {
	        super.onPostExecute(result);

	        if (result != null){
	        	/**
	    		 * Enable animation of the ListView Items
	    		 */
	        	if (swipeAnimation == SwipeAnimation.Normal){
		        	SwingBottomInAnimationAdapter swingButtonInAnimationAdapter = new SwingBottomInAnimationAdapter(result);
		        	swingButtonInAnimationAdapter.setAbsListView(exerciseView);
		        	exerciseView.setAdapter(swingButtonInAnimationAdapter);
	        	}else if (swipeAnimation == SwipeAnimation.Right){
	        		SwingLeftInAnimationAdapter swingLeftInAnimationAdapter = new SwingLeftInAnimationAdapter(result);
	        		swingLeftInAnimationAdapter.setAbsListView(exerciseView);
	        		exerciseView.setAdapter(swingLeftInAnimationAdapter);
	        	}else if (swipeAnimation == SwipeAnimation.Left){
	        		SwingRightInAnimationAdapter swingRightInAnimationAdapter = new SwingRightInAnimationAdapter(result);
	        		swingRightInAnimationAdapter.setAbsListView(exerciseView);
	        		exerciseView.setAdapter(swingRightInAnimationAdapter);		
	        	}
	        }
	        
	        getActivity().setProgressBarIndeterminateVisibility(false);  
	    }
	}
}
