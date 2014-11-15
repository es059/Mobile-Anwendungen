package com.workout.log.listAdapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.remic.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.data.ListItem;
import com.workout.log.data.MuscleGroupSectionItem;

public class ExerciseListWithoutSetsRepsAdapter extends ArrayAdapter<ListItem>{
	private ArrayList<ListItem> exerciseList;
	private LayoutInflater layoutInflater;
	
	public ExerciseListWithoutSetsRepsAdapter(Context context, ArrayList<ListItem> items){
		super(context,0,items);
		this.exerciseList = items;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		
		final ListItem i = exerciseList.get(position);
		if (i != null){
			if (i.isSection()){
				MuscleGroupSectionItem si = (MuscleGroupSectionItem) i;
				v = layoutInflater.inflate(R.layout.listview_exercise_header, null);
				
				v.setBackgroundColor(Color.parseColor("#303030"));
				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);
				
				final TextView sectionView = (TextView) v.findViewById(R.id.listview_exericse_header_text);
				sectionView.setTextColor(Color.WHITE);
				sectionView.setText(si.getTitle());
			}else{
				Exercise exercise = (Exercise) i;
				v = layoutInflater.inflate(R.layout.listview_exercise_without_repssets, null);
				TextView exerciseView = (TextView) v.findViewById(R.id.exercise);
				
				exerciseView.setText(exercise.getName());
			}
		}
		return v;
	}
	
	/**
	 * Returns the current List<TrainingDay>
	 * 
	 * @return the current List Object
	 * @author Eric Schmidt
	 */
	public ArrayList<ListItem> getTrainingDayList(){
		return exerciseList;
	}
}

