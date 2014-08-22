package com.workout.log.customLayout;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.remic.workoutlog.R;

public class ListViewDefaultAdd extends RelativeLayout{
	private TextView titel;
	private TextView hint;
	private View v;
	
	public ListViewDefaultAdd(Context context) {
		super(context);
		
		LayoutInflater inflater = LayoutInflater.from(context);
		v = inflater.inflate(R.layout.listview_default_add, null);
		
		v.setOnLongClickListener(null);
		v.setLongClickable(false);
		
		titel = (TextView) v.findViewById(R.id.default_add_titel);
		hint = (TextView) v.findViewById(R.id.default_add_hint);
		
		addView(v);
	}
	
	public void setInfo(String titel, String hint){
		this.titel.setText(titel);
		
		if(hint != ""){
			this.hint.setText(hint);
		}else{
			v.setBackgroundColor(Color.parseColor("#3a3a3a"));
			this.titel.setTextColor(Color.WHITE);
			this.hint.setVisibility(View.GONE);
		}
	}

}
