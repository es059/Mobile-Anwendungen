package com.workout.log.data;

import java.util.ArrayList;

import com.workout.log.bo.Exercise;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.listAdapter.ExerciseListAdapter;

import android.content.Context;
import android.widget.ListView;

/*
 * This Class is responsible for updating ListViews in the Application
 * 
 * @author Eric Schmidt
 */
public class UpdateListView {
	
	private ListView mListView;
	private ArrayList<TrainingDay> tList;
	private static UpdateListView updateListView = null;
	
	 protected UpdateListView(ListView listView) {
		 mListView = listView;
	  }

	  /**
	   * Class uses Singleton to ensure that the ListView is properly referenced 
	   * 
	   * @return A <code>UpdateListView</code>-Objekt.
	   * @see updateListView
	   * @author Eric Schmidt
	   */
	  public static UpdateListView updateListView(ListView listView) {
	    if (updateListView == null) {
	    	updateListView = new UpdateListView(listView);
	    }

	    return updateListView;
	  }
	  
	/**
	 * Updates Exercise ListViews using the ExerciseListAdapter. Ensures that there are no unnecessary Datebase querys
	 * if the ArrayList<TrainingDay> is already referenced 
	 * @param Context context
	 * @param int trainingDayId
	 * @author Eric Schmidt
	 */
	public void ExerciseListViewUpdate(Context context, int trainingDayId){
		if (tList == null){
			//Select Current Workoutplan
			WorkoutplanMapper wMapper = new WorkoutplanMapper(context);
			Workoutplan w = wMapper.getCurrent();
			
			//Select all Trainingdays from the current Workoutplan
			TrainingDayMapper tMapper = new TrainingDayMapper(context);
			ArrayList<TrainingDay> tList = tMapper.getAll(w.getID());
		}
		//Select Exercises from Selected Trainingday --> TODO
		ExerciseMapper eMapper = new ExerciseMapper(context);
		ArrayList<Exercise> eList = eMapper.getAllExercise(trainingDayId);
		
		ExerciseListAdapter adapter = new ExerciseListAdapter(context, 0, eList);
		
		mListView.setAdapter(adapter);
	}
}
