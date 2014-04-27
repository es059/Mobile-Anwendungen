package com.workout.log.listAdapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.workout.log.bo.PerformanceActual;
import com.workout.log.customLayout.ListViewSet;

public class PerformanceActualListAdapter extends ArrayAdapter<PerformanceActual> {
	public PerformanceActualListAdapter(Context context, int textViewResourceId, List<PerformanceActual> objects) {
		super(context, textViewResourceId, objects);
	}
	@Override
	 public View getView(int position, View convertView, ViewGroup parent) {
		 PerformanceActual performanceActual = getItem(position);
		 ListViewSet listViewSet = null;
		 if(convertView != null){
			 listViewSet = (ListViewSet) convertView;
		 }
		 else{
			 listViewSet = new ListViewSet(getContext());
		 }
		 
		 listViewSet.setPerfromanceActual(performanceActual);
		 return listViewSet;
	 }

}
