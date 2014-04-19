package com.workout.log.listAdapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.workout.log.bo.TrainingDay;
import com.workout.log.customLayout.ListViewTrainingDay;

public class TrainingDayListAdapter extends ArrayAdapter<TrainingDay> {
	public TrainingDayListAdapter(Context context, int textViewResourceId, List<TrainingDay> objects) {
		super(context, textViewResourceId, objects);
	}
	@Override
	 public View getView(int position, View convertView, ViewGroup parent) {
		TrainingDay trainingDay = getItem(position); 
		ListViewTrainingDay listViewTrainingsDay = null;
		 if(convertView != null){
			 listViewTrainingsDay = (ListViewTrainingDay) convertView;
		 }
		 else{
			 listViewTrainingsDay = new ListViewTrainingDay(getContext());
		 }
		 listViewTrainingsDay.setTrainingDay(trainingDay);
		 return listViewTrainingsDay;
	 }
}
