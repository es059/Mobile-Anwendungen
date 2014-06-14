package com.workout.log;

import java.util.ArrayList;

import com.workout.log.bo.Exercise;
import com.workout.log.bo.MuscleGroup;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.data.*;
import com.example.workoutlog.R;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.MuscleGroupMapper;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.fragment.ActionBarTrainingDayPickerFragment;
import com.workout.log.listAdapter.OverviewAdapter;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ExerciseOverview extends Fragment implements OnItemClickListener {

	private static ListView exerciseView; 
    
    private static ExerciseSpecific exerciseSpecific = new ExerciseSpecific();
    public static ActionBarTrainingDayPickerFragment actionBarTrainingDayPickerFragment = new ActionBarTrainingDayPickerFragment();
    
    /**
     * Variables for the UpdateListView method
     */
	private ArrayList<TrainingDay> tList;
	private ArrayList<Exercise> eList;
	private ArrayList<MuscleGroup> mList;
	private ArrayList<ExerciseItem> listComplete;
	private ArrayList<Exercise> eListMuscleGroup;
    
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
		
        /**
         * This code block handles the behavior of the Back button
         */
        view.setFocusableInTouchMode(true);
        view.setOnKeyListener( new OnKeyListener(){
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				  if( arg1 == KeyEvent.KEYCODE_BACK )
	                {
	                    return true;
	                }
	                return false;
			}
        } );
               
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
		
		final Bundle transferExtras = getArguments();
		exerciseView = (ListView) getView().findViewById(R.id.exerciseOverviewList);
		if (transferExtras != null){	
			try{
				if (transferExtras.getBoolean("SaveMode")){
					Toast.makeText(getActivity(), "Daten wurden gespeichert", Toast.LENGTH_SHORT).show();
				}
				int trainingDayId = transferExtras.getInt("TrainingDayId");
				actionBarTrainingDayPickerFragment = (ActionBarTrainingDayPickerFragment) getActivity().
						getFragmentManager().findFragmentByTag("TrainingDayPicker");
				actionBarTrainingDayPickerFragment.setCurrentTrainingDay(trainingDayId);
			} catch (Exception e){
				e.printStackTrace();
			}
		}else{	
			ExerciseListViewUpdate();
		}
		exerciseView.setOnItemClickListener(this);
	}
	 
	/**
	 * Updates Exercise ListViews using the ExerciseListAdapter. 
	 * Ensures that there are no unnecessary Database queries
	 * if the ArrayList<TrainingDay> is already referenced.
	 * This methode uses the first trainingDay as the source of exercises.
	 * If you know the current trainingDay use <code>ExerciseListViewUpdate(int TrainingDay)</code>
	 *
	 * @author Eric Schmidt
	 */
	public void ExerciseListViewUpdate(){
		if (tList == null){
			//Select Current Workoutplan
			WorkoutplanMapper wMapper = new WorkoutplanMapper(getActivity());
			Workoutplan w = wMapper.getCurrent();
			//Select all Trainingdays from the current Workoutplan
			TrainingDayMapper tMapper = new TrainingDayMapper(getActivity());
			tList = tMapper.getAll(w.getId());
		}
		if (!tList.isEmpty()){
			if (mList == null){	
				//Select all MuscleGroups
				MuscleGroupMapper mMapper = new MuscleGroupMapper(getActivity());
				mList = mMapper.getAll();
			}
			//Select Exercises from Selected Trainingday and MuscleGroup 
			ExerciseMapper eMapper = new ExerciseMapper(getActivity());
			eList = eMapper.getExerciseByTrainingDay(tList.get(0).getId());
			listComplete = new ArrayList<ExerciseItem>();
			for (MuscleGroup m : mList){
				eListMuscleGroup = eMapper.getExerciseByMuscleGroup(eList, m.getId());
				if (!eListMuscleGroup.isEmpty()){
					listComplete.add(new MuscleGroupSectionItem(m.getName()));
					listComplete.addAll(eListMuscleGroup);
				}
			}
			
			OverviewAdapter adapter = new OverviewAdapter(getActivity(), listComplete, tList.get(0).getId());
			exerciseView.setAdapter(adapter);
		}
	}
	
	/**
	 * If TrainingDayId is known use this method
	 * 
	 * @param trainingDayId
	 * @author Eric Schmidt
	 */
	public void updateListView(int trainingDayId){
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
		exerciseView.setAdapter(adapter);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.exercise_overview_menu, menu);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Exercise exercise;
		exercise = (Exercise) exerciseView.getItemAtPosition(position);
		openExerciseSpecific(exercise);
		
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
        
		exerciseSpecific = new ExerciseSpecific();
        exerciseSpecific.setArguments(data);
        
	    FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.fragment_container, exerciseSpecific , "ExerciseSpecific");
        transaction.addToBackStack(null);
        transaction.commit();
	}
}
