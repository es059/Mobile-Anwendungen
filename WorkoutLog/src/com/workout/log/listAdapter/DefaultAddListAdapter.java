package com.workout.log.listAdapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.workout.log.customLayout.ListViewDefaultAdd;
import com.workout.log.data.Default;

public class DefaultAddListAdapter extends ArrayAdapter<Default> {
	public DefaultAddListAdapter(Context context, int textViewResourceId, List<Default> info) {
		super(context, textViewResourceId, info);				
	}
	
	@Override
	 public View getView(int position, View convertView, ViewGroup parent) {
		 Default info = getItem(position);
		 ListViewDefaultAdd listViewDefault = null;
		 if(convertView != null){
			 listViewDefault = (ListViewDefaultAdd) convertView;
		 }
		 else{
			 listViewDefault = new ListViewDefaultAdd(getContext());
		 }
		 
		 listViewDefault.setInfo(info.getTitel(), info.getHint());
		 return listViewDefault;
	 }
	
	@Override
	public boolean hasStableIds() {
		return true;
	}

}
