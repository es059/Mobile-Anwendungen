package com.workout.log.listAdapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.workout.log.bo.TrainingDay;
import com.workout.log.customLayout.ListViewTrainingDay;

public class TrainingDayListAdapter extends ArrayAdapter<TrainingDay> {
	private ArrayList<TrainingDay> trainingDayList = null;
	
	public TrainingDayListAdapter(Context context, int textViewResourceId, ArrayList<TrainingDay> objects) {
		super(context, textViewResourceId, objects);
		trainingDayList = objects;
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
	
	/**
	 * Returns the current List<TrainingDay>
	 * 
	 * @return the current List Object
	 * @author Eric Schmidt
	 */
	public ArrayList<TrainingDay> getTrainingDayList(){
		return trainingDayList;
	}
}
