package com.workout.log.customLayout;

import com.example.workoutlog.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListViewDefaultAdd extends RelativeLayout{
	TextView titel;
	TextView hint;
	
	public ListViewDefaultAdd(Context context) {
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.listview_default_add, null);
		
		titel = (TextView) v.findViewById(R.id.default_add_titel);
		hint = (TextView) v.findViewById(R.id.default_add_hint);
		
		addView(v);
	}
	
	public void setInfo(String titel, String hint){
		this.titel.setText(titel);
		this.hint.setText(hint);
	}

}
