package com.workout.log.listAdapter;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.data.ExerciseItem;
import com.workout.log.data.MuscleGroupSectionItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ExerciseListWithoutSetsRepsAdapter extends ArrayAdapter<ExerciseItem>{
	private ArrayList<ExerciseItem> items;
	private LayoutInflater layoutInflater;
	
	public ExerciseListWithoutSetsRepsAdapter(Context context, ArrayList<ExerciseItem> items){
		super(context,0,items);
		this.items = items;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		
		final ExerciseItem i = items.get(position);
		if (i != null){
			if (i.isSection()){
				MuscleGroupSectionItem si = (MuscleGroupSectionItem) i;
				v = layoutInflater.inflate(R.layout.listview_exercise_header, null);
				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);
				
				final TextView sectionView = (TextView) v.findViewById(R.id.listview_exericse_header_text);
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
}

