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
	
	private ArrayList<TrainingDay> tList;
	private ArrayList<Exercise> eList;
	private static ListView mListView;
	private static UpdateListView updateListView = null;
	private static ExerciseListAdapter adapter = null;
	
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
	    if (updateListView == null || listView != mListView ) {
	    	updateListView = new UpdateListView(listView);
	    }
	    return updateListView;
	  }
	  /**
	   * "Constructor" is called if the ListView Object is not known
	   * 
	   * @return updateListView
	   * @author Eric Schmidt
	   */
	  public static UpdateListView updateListView() {
		    return updateListView;
		  }
	  
	/**
	 * Updates Exercise ListViews using the ExerciseListAdapter. Ensures that there are no unnecessary Datebase querys
	 * if the ArrayList<TrainingDay> is already referenced 
	 * @param Context context
	 * @param int trainingDayId
	 * @author Eric Schmidt
	 */
	public void ExerciseListViewUpdate(Context context){
		if (tList == null){
			//Select Current Workoutplan
			WorkoutplanMapper wMapper = new WorkoutplanMapper(context);
			Workoutplan w = wMapper.getCurrent();
			
			//Select all Trainingdays from the current Workoutplan
			TrainingDayMapper tMapper = new TrainingDayMapper(context);
			tList = tMapper.getAll(w.getID());
		}
		//Select Exercises from Selected Trainingday --> TODO
		ExerciseMapper eMapper = new ExerciseMapper(context);
		eList = eMapper.getAllExercise(tList.get(0).getID());
		adapter = new ExerciseListAdapter(context, 0, eList);
		
		mListView.setAdapter(adapter);
	}
	public void ExerciseListViewUpdate(Context context, int trainingDayId){
		//Select Exercises from Selected Trainingday
		ExerciseMapper eMapper = new ExerciseMapper(context);
		eList = eMapper.getAllExercise(trainingDayId);
		adapter = new ExerciseListAdapter(context, 0, eList);
		mListView.setAdapter(adapter);
	}
}
