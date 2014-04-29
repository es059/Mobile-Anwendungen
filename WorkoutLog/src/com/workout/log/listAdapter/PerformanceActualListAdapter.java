package com.workout.log.listAdapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.workout.log.bo.PerformanceActual;
import com.workout.log.customLayout.ListViewPerformanceActual;

public class PerformanceActualListAdapter extends ArrayAdapter<PerformanceActual> {
	public PerformanceActualListAdapter(Context context, int textViewResourceId, List<PerformanceActual> objects) {
		super(context, textViewResourceId, objects);
	}
	@Override
	 public View getView(int position, View convertView, ViewGroup parent) {
		 PerformanceActual performanceActual = getItem(position);
		 ListViewPerformanceActual listViewSet = null;
		 if(convertView != null){
			 listViewSet = (ListViewPerformanceActual) convertView;
		 }
		 else{
			 listViewSet = new ListViewPerformanceActual(getContext());
		 }
		 
		 listViewSet.setPerfromanceActual(performanceActual);
		 return listViewSet;
	 }

}
