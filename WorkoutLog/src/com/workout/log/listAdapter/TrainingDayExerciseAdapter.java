package com.workout.log.listAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.data.ListItem;
import com.workout.log.data.MuscleGroupSectionItem;
import com.workout.log.db.PerformanceTargetMapper;

public class TrainingDayExerciseAdapter  extends ArrayAdapter<ListItem>{
	private ArrayList<ListItem> items;
	private LayoutInflater layoutInflater;
	private PerformanceTargetMapper pMapper = null;
	private int trainingDayId;
	private Context context = null;
	
	private final int INVALID_ID = -1;
	private HashMap<ListItem, Integer> mIdMap = new HashMap<ListItem, Integer>();
	
	private TextView exerciseView;
	private TextView setView;
	private TextView repetitionView; 
	
	public TrainingDayExerciseAdapter(Context context, ArrayList<ListItem> items, int trainingDayId){
		super(context,0,items);
		this.items = items;
		this.trainingDayId = trainingDayId;
		this.context = context;
		
		pMapper = new PerformanceTargetMapper(getContext());
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
		
	     for (int i = 0; i < items.size(); ++i) {
	            mIdMap.put(items.get(i), i);
	      }
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent){
		View v = convertView;
		ViewGroup vg = (ViewGroup) v;
		
		final ListItem item = items.get(position);
		if (item != null){
			if (item.isSection()){
				MuscleGroupSectionItem si = (MuscleGroupSectionItem) item;
				if (v == null || (vg.getChildAt(0) instanceof TableLayout)){
					v = layoutInflater.inflate(R.layout.listview_exercise_header, null);
				}
				
				v.setBackgroundColor(Color.parseColor("#303030"));
				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);
				
				final TextView sectionView = (TextView) v.findViewById(R.id.listview_exericse_header_text);
				sectionView.setTextColor(Color.WHITE);
				sectionView.setText(si.getTitle());
			}else{
				Exercise exercise = (Exercise) item;
				if (v == null || !(vg.getChildAt(0) instanceof TableLayout)){
					v = layoutInflater.inflate(R.layout.listview_trainingday_exercise, null);
				}
								
				final TextView 	exerciseView = (TextView) v.findViewById(R.id.exercise);
				final TextView 	setView = (TextView) v.findViewById(R.id.set);
				final TextView 	repetitionView = (TextView) v.findViewById(R.id.repetitions);
							
				exerciseView.setText(exercise.getName());
				
				/**
				 * Get target performance information (Set & Repetition)
				 */
				PerformanceTarget performanceTarget = pMapper.getPerformanceTargetByExerciseId(exercise, trainingDayId);
				setView.setHint(context.getResources().getString((R.string.Set)) + ": " + String.valueOf(performanceTarget.getSet()));
				repetitionView.setHint(context.getResources().getString((R.string.Rep)) + ": " + String.valueOf(performanceTarget.getRepetition()));
			}
		}
		return v;

	}
	 
	public ArrayList<ListItem> getList(){
		return items;
	}

	@Override
	public long getItemId(final int position) {
		return getItem(position).hashCode();
	}
	 
	@Override
	public boolean hasStableIds() {
		return true;
	}

}
