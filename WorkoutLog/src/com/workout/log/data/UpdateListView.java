package com.workout.log.data;

import java.util.ArrayList;

import com.workout.log.bo.Exercise;
import com.workout.log.bo.MuscleGroup;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.MuscleGroupMapper;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.listAdapter.ExerciseListAdapter;
import com.workout.log.listAdapter.OverviewAdapter;

import android.content.ClipData.Item;
import android.content.Context;
import android.view.View;
import android.widget.ListView;

/*
 * This Class is responsible for updating ListViews in the Application
 * 
 * @author Eric Schmidt
 */
public class UpdateListView {
	
	private ArrayList<TrainingDay> tList;
	private ArrayList<Exercise> eList;
	private ArrayList<MuscleGroup> mList;
	private ArrayList<ExerciseItem> listComplete;
	private ArrayList<Exercise> eListMuscleGroup;
	private MuscleGroupMapper mMapper;
	private static ListView mListView;
	private static UpdateListView updateListView = null;
	private static OverviewAdapter adapter = null;
	
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
	 * Updates Exercise ListViews using the ExerciseListAdapter. 
	 * Ensures that there are no unnecessary Database queries
	 * if the ArrayList<TrainingDay> is already referenced 
	 * 
	 * @param context 
	 * @param trainingDayId 
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
		if (mList == null){	
			//Select all MuscleGroups
			MuscleGroupMapper mMapper = new MuscleGroupMapper(context);
			mList = mMapper.getAll();
		}
		//Select Exercises from Selected Trainingday and MuscleGroup 
		ExerciseMapper eMapper = new ExerciseMapper(context);
		eList = eMapper.getExerciseByTrainingDay(tList.get(0).getID());
		listComplete = new ArrayList<ExerciseItem>();
		for (MuscleGroup m : mList){
			eListMuscleGroup = eMapper.getExerciseByMuscleGroup(eList, m.getId());
			if (!eListMuscleGroup.isEmpty()){
				listComplete.add(new MuscleGroupSectionItem(m.getName()));
				listComplete.addAll(eListMuscleGroup);
			}
		}
		
		adapter = new OverviewAdapter(context, listComplete);
		mListView.setAdapter(adapter);
	}
	/**
	 * If TrainingDayId is known use this method
	 * 
	 * @param context
	 * @param trainingDayId
	 * @author Eric Schmidt
	 */
	public void ExerciseListViewUpdate(Context context, int trainingDayId){
		if (mList == null){	
			//Select all MuscleGroups
			MuscleGroupMapper mMapper = new MuscleGroupMapper(context);
			mList = mMapper.getAll();
		}
		//Select Exercises from Selected Trainingday and MuscleGroup 
		ExerciseMapper eMapper = new ExerciseMapper(context);
		eList = eMapper.getExerciseByTrainingDay(trainingDayId);
		listComplete = new ArrayList<ExerciseItem>();
		for (MuscleGroup m : mList){
			eListMuscleGroup = eMapper.getExerciseByMuscleGroup(eList, m.getId());
			if (!eListMuscleGroup.isEmpty()){
				listComplete.add(new MuscleGroupSectionItem(m.getName()));
				listComplete.addAll(eListMuscleGroup);
			}
		}
		
		adapter = new OverviewAdapter(context, listComplete);
		mListView.setAdapter(adapter);
	}
}
