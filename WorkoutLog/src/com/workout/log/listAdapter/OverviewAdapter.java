package com.workout.log.listAdapter;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.data.ExerciseItem;
import com.workout.log.data.MuscleGroupSectionItem;
import com.workout.log.db.PerformanceTargetMapper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class OverviewAdapter  extends ArrayAdapter<ExerciseItem>{
	private Context context;
	private ArrayList<ExerciseItem> items;
	private LayoutInflater vi;
	
	public OverviewAdapter(Context context, ArrayList<ExerciseItem> items){
		super(context,0,items);
		this.context = context;
		this.items = items;
		vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		
		final ExerciseItem i = items.get(position);
		if (i != null){
			if (i.isSection()){
				MuscleGroupSectionItem si = (MuscleGroupSectionItem) i;
				v = vi.inflate(R.layout.listview_exercise_header, null);
				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);
				
				final TextView sectionView = (TextView) v.findViewById(R.id.listview_exericse_header_text);
				sectionView.setText(si.getTitle());
			}else{
				Exercise exercise = (Exercise) i;
				v = vi.inflate(R.layout.listview_exercise, null);
				final TextView 	exerciseView = (TextView) v.findViewById(R.id.exercise);
				final TextView 	setView = (TextView) v.findViewById(R.id.set);
				final TextView 	repetitionView = (TextView) v.findViewById(R.id.repetitions);
				
				exerciseView.setText(exercise.getName());
				//Get target performance information (Set & Repetition)
				PerformanceTargetMapper pMapper = new PerformanceTargetMapper(getContext());
				PerformanceTarget performanceTarget = pMapper.getPerformanceTargetByExerciseId(exercise);
				setView.setHint("(S�tze: " + String.valueOf(performanceTarget.getSet()) + ")");
				repetitionView.setHint("(Wdh: " + String.valueOf(performanceTarget.getRepetition())+ ")");
			}
		}
		return v;
	}
}
