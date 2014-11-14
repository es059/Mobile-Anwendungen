package com.workout.log.listAdapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.remic.workoutlog.R;
import com.workout.log.ExerciseSpecific;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.data.ListItem;
import com.workout.log.data.MuscleGroupSectionItem;
import com.workout.log.data.MuscleGroupType;
import com.workout.log.db.PerformanceTargetMapper;

public class OverviewAdapter  extends ArrayAdapter<ListItem>{
	private ArrayList<ListItem> items;
	private LayoutInflater layoutInflater;
	private PerformanceTargetMapper pMapper = null;
	private int trainingDayId;
	private Context activityContext = null;
	private static Context fragmentContext = null;
	private static FragmentActivity mainActivity;
	
	public OverviewAdapter(FragmentActivity activityContext, ArrayList<ListItem> items, int trainingDayId){
		super(activityContext,0,items);
		this.items = items;
		this.trainingDayId = trainingDayId;
		this.activityContext = activityContext;
		this.mainActivity = activityContext;
		
		
		pMapper = new PerformanceTargetMapper(getContext());
		layoutInflater = (LayoutInflater) activityContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		
		final ListItem item = items.get(position);
		if (item != null){
			if (item.isSection()){
				MuscleGroupSectionItem si = (MuscleGroupSectionItem) item;
				v = layoutInflater.inflate(R.layout.listview_exercise_header, null);
				
				v.setBackgroundColor(Color.parseColor("#303030"));
				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);
				
				final TextView sectionView = (TextView) v.findViewById(R.id.listview_exericse_header_text);
				sectionView.setTextColor(Color.WHITE);
				sectionView.setText(si.getTitle());
			}else{
				final Exercise exercise = (Exercise) item;
				
				MuscleGroupType muscleGroupType = MuscleGroupType.Normal;
				if(exercise.getMuscleGroup().getName().equals(activityContext.getResources().getString(R.string.Cardio))) muscleGroupType = MuscleGroupType.Cardio;
				
				v = layoutInflater.inflate(R.layout.listview_exercise, null);
				
				final TextView 	exerciseView = (TextView) v.findViewById(R.id.exercise);
				final TextView 	setView = (TextView) v.findViewById(R.id.set);
				final TextView 	repetitionView = (TextView) v.findViewById(R.id.repetitions);
				v.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						openExerciseSpecific(exercise);
						
					}
					
				});
				if (muscleGroupType == MuscleGroupType.Cardio){
					exerciseView.setText(exercise.getName());
					//Get target performance information (Set & Repetition)
					PerformanceTarget performanceTarget = pMapper.getPerformanceTargetByExerciseId(exercise, trainingDayId);
					
					v.findViewById(R.id.setDivider).setVisibility(View.GONE);
					setView.setVisibility(View.GONE);
					
					repetitionView.setHint(activityContext.getResources().getString((R.string.Min)) + ": " + String.valueOf(performanceTarget.getRepetition()));
				}else{					
					exerciseView.setText(exercise.getName());
					//Get target performance information (Set & Repetition)
					PerformanceTarget performanceTarget = pMapper.getPerformanceTargetByExerciseId(exercise, trainingDayId);
					
					setView.setHint(activityContext.getResources().getString((R.string.Set)) + ": " + String.valueOf(performanceTarget.getSet()));
					repetitionView.setHint(activityContext.getResources().getString((R.string.Rep)) + ": " + String.valueOf(performanceTarget.getRepetition()));
				}
			}
		}
		return v;
	}
	
	/**
	 * Method to open the ExerciseSpecific Activity with the selected Exercise and the current TrainingDay
	 * 
	 * @param exercise
	 * @author Eric Schmidt
	 */
	
	private void openExerciseSpecific (Exercise exercise){
		ExerciseSpecific exerciseSpecific = new ExerciseSpecific();
		Bundle data = new Bundle();
        data.putInt("ExerciseID",exercise.getId());
        data.putString("ExerciseName",exercise.getName());
        data.putInt("TrainingDayId",trainingDayId);
        
        exerciseSpecific.setArguments(data);
        
	    FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.fragment_container, exerciseSpecific , "ExerciseSpecific");
        transaction.commit();
	}
}
