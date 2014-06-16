package com.workout.log.fragment;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.workoutlog.R;
import com.workout.log.ExerciseSpecific;
import com.workout.log.bo.PerformanceActual;
import com.workout.log.db.PerformanceActualMapper;

public class ActionBarDatePickerFragment extends Fragment implements OnClickListener{
	private TextView date;
	private ImageButton next;
	private ImageButton previous;
	private SimpleDateFormat dateFormat;
	private static Calendar calendar;
	private String formatedDate;
	private ArrayList<PerformanceActual> performanceActualList = new ArrayList<PerformanceActual>();
	private ExerciseSpecific exerciseSpecific;
	private PerformanceActualMapper paMapper;
	private Boolean isCurrent;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.actionbar_datepicker_fragment, container,false);
		
		date = (TextView) view.findViewById(R.id.date);
		calendar  = Calendar.getInstance();
		dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		
		paMapper = new PerformanceActualMapper(getActivity());
		exerciseSpecific = (ExerciseSpecific) getActivity().getFragmentManager().findFragmentByTag("ExerciseSpecific");
		
		next = (ImageButton) view.findViewById(R.id.Next);
		previous = (ImageButton) view.findViewById(R.id.Previous);
		
		next.setOnClickListener(this);
		previous.setOnClickListener(this);
		setDate();
		return view;
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		switch (v.getId()){
		case R.id.Next:
			performanceActualList = paMapper.getNextPerformanceActual(calendar, exerciseSpecific.getExercise());
			if (!performanceActualList.isEmpty()){
				exerciseSpecific.updateListView(performanceActualList);
				calendar.setTime(performanceActualList.get(0).getTimestamp());
				setDate();
			}else{
				/**
				 * If the ArrayList is empty it means, that the next PerformanceActual is the current one today
				 */
				performanceActualList = exerciseSpecific.prepareStandardListView();
				exerciseSpecific.updateListView(performanceActualList);
				calendar.setTime(new Date());
				setDate();
			}
			break;
		case R.id.Previous:
			performanceActualList = paMapper.getLastPerformanceActual(calendar, exerciseSpecific.getExercise());
			if (!performanceActualList.isEmpty()){
				if (isCurrent){
					exerciseSpecific.savePerformanceActual();
					Toast.makeText(getActivity(), "Inhalte wurden gespeichert", Toast.LENGTH_SHORT).show();
				}
				exerciseSpecific.updateListView(performanceActualList);
				calendar.setTime(performanceActualList.get(0).getTimestamp());
				setDate();
			}else{
				Toast.makeText(getActivity(), "Keine letzte Übung gefunden", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * Set the View Elements to the formated current Date 
	 * 
	 * @author Eric Schmidt
	 */
	private void setDate(){
		Calendar c = Calendar.getInstance();
		if (String.valueOf(calendar.getTime().getDate()) == String.valueOf(c.getTime().getDate())){
			date.setText("Heute");
			next.setVisibility(View.INVISIBLE);
			isCurrent = true;
		}else{
			formatedDate = dateFormat.format(calendar.getTime());
			date.setText(formatedDate);
			next.setVisibility(View.VISIBLE);
			isCurrent = false;
		}
	}
	
	/**
	 * Check if the current Date is Today
	 * 
	 * @return Boolean false if not today
	 * @author Eric Schmidt
	 */
	public Boolean isToday(){
		return isCurrent;
	}
	
}
