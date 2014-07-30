package com.workout.log;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.MuscleGroup;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.data.ListItem;
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
		int itemId = item.getItemId();
		if (itemId == R.id.menu_add) {
			intent = new Intent();
			intent.setClass(getActivity(), ExerciseAdd.class);
			startActivity(intent);
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
		private ArrayList<ListItem> listComplete;
		
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
				listComplete = new ArrayList<ListItem>();
				
				for(int i = 0; i < eList.size(); i++) {
					MuscleGroup mg = eList.get(i).getMuscleGroup();
					
					if (i > 0){
						if(!mg.getName().equals(eList.get(i-1).getMuscleGroup().getName())) {
							listComplete.add(new MuscleGroupSectionItem(mg.getName()));
							listComplete.add(eList.get(i));
						}else{
							listComplete.add(eList.get(i));
						}
					}else{
						listComplete.add(new MuscleGroupSectionItem(mg.getName()));
						listComplete.add(eList.get(i));
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
	        	exerciseView.setAdapter(result);
	        }
	        getActivity().setProgressBarIndeterminateVisibility(false);  
	    }
	}
}
