package com.workout.log.listAdapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.workout.log.bo.PerformanceActual;
import com.workout.log.customLayout.ListViewPerformanceActual;

public class PerformanceActualListAdapter extends ArrayAdapter<PerformanceActual> {
	
	ArrayList<PerformanceActual> performanceActualList = null;
	
	public PerformanceActualListAdapter(Context context, int textViewResourceId, ArrayList<PerformanceActual> objects) {
		super(context, textViewResourceId, objects);
		performanceActualList = objects;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 PerformanceActual performanceActual = getItem(position);
		 ListViewPerformanceActual listViewPerformanceActual = null;
		 if(convertView != null){
			 listViewPerformanceActual = (ListViewPerformanceActual) convertView;
		 }
		 else{
			 listViewPerformanceActual = new ListViewPerformanceActual(getContext());
		 }
		 
		 listViewPerformanceActual.setPerfromanceActual(performanceActual);
		 return listViewPerformanceActual;
	 }
	
	/**
	 * Returns the current List<PerformanceActual>
	 * 
	 * @return the current List Object
	 * @author Eric Schmidt
	 */
	public ArrayList<PerformanceActual> getPerformanceActualList(){
		return performanceActualList;
	}

}
