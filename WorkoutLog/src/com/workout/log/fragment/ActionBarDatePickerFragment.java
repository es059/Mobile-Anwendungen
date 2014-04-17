package com.workout.log.fragment;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.example.workoutlog.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class ActionBarDatePickerFragment extends Fragment implements OnClickListener{
	TextView date;
	ImageButton next;
	ImageButton previous;
	SimpleDateFormat dateFormat;
	Calendar calendar;
	String formatedDate;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.actionbar_datepicker_fragment, container,false);
		
		//Erstelle einen Calender. Erstelle passendes Format und weiﬂe TextView date aktuelles Datum zu
		date = (TextView) view.findViewById(R.id.date);
		calendar  = Calendar.getInstance();
		dateFormat = new SimpleDateFormat("EEE, MMM d");
		getDate();
		
		//ImageButton referenzieren
		next = (ImageButton) view.findViewById(R.id.Next);
		previous = (ImageButton) view.findViewById(R.id.Previous);
		
		next.setOnClickListener(this);
		previous.setOnClickListener(this);
		return view;
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.Next:
			calendar.add(Calendar.DATE, +1);
			getDate();
			break;
		case R.id.Previous:
			calendar.add(Calendar.DATE, -1);
			getDate();
			break;
		default:
			break;
		}
	}
	
	private void getDate(){
		Calendar c = Calendar.getInstance();
		if (String.valueOf(calendar.getTime().getDate()) == String.valueOf(c.getTime().getDate())){
			date.setText("Heute");
		}else{
			formatedDate = dateFormat.format(calendar.getTime());
			date.setText(formatedDate);
		}
	}
}
