package com.workout.log.listAdapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.remic.workoutlog.R;
import com.workout.log.TrainingDayAddToWorkoutplan;
import com.workout.log.bo.TrainingDay;
import com.workout.log.data.ManageWorkoutplanListItem;

public class ManageWorkoutplanListAdapter extends ArrayAdapter<ManageWorkoutplanListItem>{
	
	private ArrayList<ManageWorkoutplanListItem> items;
	private LayoutInflater layoutInflater;
	private Context context;
	private Fragment fragment;
	
	public ManageWorkoutplanListAdapter(Context context, ArrayList<ManageWorkoutplanListItem> items, Fragment fragment){
		super(context,0,items);
		this.items = items;
		this.context = context;
		this.fragment = fragment;
		
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent){
		View v = convertView;
		TrainingDay td = new TrainingDay();
		final ManageWorkoutplanListItem item = items.get(position);
		if (item != null){
			if (item instanceof TrainingDay){
				td = (TrainingDay) item;
				v = layoutInflater.inflate(R.layout.listview_training_day, null);

				final TextView	titel = (TextView) v.findViewById(R.id.trainingDay_listview_titel);
				final TextView	exerciseCount = (TextView) v.findViewById(R.id.exerciseCount);
				
				titel.setText(td.getName());
				exerciseCount.setHint("Übungen: " + String.valueOf(td.getExerciseList().size()));	
			}else{
				v = layoutInflater.inflate(R.layout.listview_default_add, null);
				
				v.setBackgroundColor(Color.parseColor("#3a3a3a"));
				
				v.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						FragmentTransaction transaction = fragment.getFragmentManager().beginTransaction();
						transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						transaction.replace(R.id.fragment_container, new TrainingDayAddToWorkoutplan(), "TrainingDayAddToWorkoutplan");
						transaction.addToBackStack(null);
						transaction.commit();
					}	
				}); 
				
				v.setOnLongClickListener(null);
				v.setLongClickable(false);
				
				final TextView title = (TextView) v.findViewById(R.id.default_add_titel);
				final TextView hint = (TextView) v.findViewById(R.id.default_add_hint);
				
				hint.setVisibility(View.GONE);
				title.setTextColor(Color.WHITE);
				title.setText(context.getString(R.string.AddTrainingDayToWorkoutplan));
			}
		}	
		return v;
	}
 
	public ArrayList<ManageWorkoutplanListItem> getList(){
		return items;
	}

	@Override
	public long getItemId(final int position) {
		return super.getItemId(position);
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}
	
	/**
	 * Returns the current List<TrainingDay>
	 * 
	 * @return the current List Object
	 * @author Eric Schmidt
	 */
	public ArrayList<TrainingDay> getTrainingDayList(){
		ArrayList<TrainingDay> trainingDayList = new ArrayList<TrainingDay>();
		for (ManageWorkoutplanListItem item : items){
			if (item instanceof TrainingDay){
				trainingDayList.add((TrainingDay) item);
			}
		}
		return trainingDayList;
	}

}

