package com.workout.log.listAdapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.workout.log.bo.Set;
import com.workout.log.customLayout.ListViewSet;

public class SetListAdapter extends ArrayAdapter<Set> {
	public SetListAdapter(Context context, int textViewResourceId, List<Set> objects) {
		super(context, textViewResourceId, objects);
	}
	@Override
	 public View getView(int position, View convertView, ViewGroup parent) {
		 Set set = getItem(position);
		 ListViewSet listViewSet = null;
		 if(convertView != null){
			 listViewSet = (ListViewSet) convertView;
		 }
		 else{
			 listViewSet = new ListViewSet(getContext());
		 }
		 
		 listViewSet.setSet(set);
		 return listViewSet;
	 }

}
