package com.workout.log.listAdapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.data.ListItem;
import com.workout.log.data.MuscleGroupSectionItem;
import com.workout.log.db.PerformanceTargetMapper;

public class OverviewAdapter  extends ArrayAdapter<ListItem>{
	private ArrayList<ListItem> items;
	private LayoutInflater layoutInflater;
	private PerformanceTargetMapper pMapper = null;
	private int trainingDayId;
	
	public OverviewAdapter(Context context, ArrayList<ListItem> items, int trainingDayId){
		super(context,0,items);
		this.items = items;
		this.trainingDayId = trainingDayId;
		pMapper = new PerformanceTargetMapper(getContext());
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		
		final ListItem item = items.get(position);
		if (item != null){
			if (item.isSection()){
				MuscleGroupSectionItem si = (MuscleGroupSectionItem) item;
				v = layoutInflater.inflate(R.layout.listview_exercise_header, null);
				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);
				
				final TextView sectionView = (TextView) v.findViewById(R.id.listview_exericse_header_text);
				sectionView.setText(si.getTitle());
			}else{
				Exercise exercise = (Exercise) item;
				v = layoutInflater.inflate(R.layout.listview_exercise, null);
				final TextView 	exerciseView = (TextView) v.findViewById(R.id.exercise);
				final TextView 	setView = (TextView) v.findViewById(R.id.set);
				final TextView 	repetitionView = (TextView) v.findViewById(R.id.repetitions);
				
				exerciseView.setText(exercise.getName());
				//Get target performance information (Set & Repetition)
				PerformanceTarget performanceTarget = pMapper.getPerformanceTargetByExerciseId(exercise, trainingDayId);
				setView.setHint("(Sätze: " + String.valueOf(performanceTarget.getSet()) + ")");
				repetitionView.setHint("(Wdh: " + String.valueOf(performanceTarget.getRepetition())+ ")");
			}
		}
		return v;
	}
}
