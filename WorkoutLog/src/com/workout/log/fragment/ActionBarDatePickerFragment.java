package com.workout.log.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.db.PerformanceActualMapper;
import com.workout.log.db.PerformanceTargetMapper;

@SuppressLint("SimpleDateFormat")
public class ActionBarDatePickerFragment extends Fragment implements OnClickListener{
	private TextView date;
	private TextView set;
	private TextView rep;
	private ImageButton next;
	private ImageButton previous;
	
	private SimpleDateFormat dateFormat;
	private static Calendar calendar;
	
	private ArrayList<PerformanceActual> performanceActualList = new ArrayList<PerformanceActual>();
	private ExerciseSpecific exerciseSpecific;
	private PerformanceActualMapper paMapper;
	
	private Boolean isCurrent;
	private String formatedDate;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.actionbar_datepicker_fragment, container,false);
		
		date = (TextView) view.findViewById(R.id.date);
		set = (TextView) view.findViewById(R.id.Set);
		rep = (TextView) view.findViewById(R.id.Rep);
		
		next = (ImageButton) view.findViewById(R.id.Next);
		previous = (ImageButton) view.findViewById(R.id.Previous);
		
		calendar  = Calendar.getInstance();
		dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		
		paMapper = new PerformanceActualMapper(getActivity());
		exerciseSpecific = (ExerciseSpecific) getActivity().getSupportFragmentManager().findFragmentByTag("ExerciseSpecific");
				
		next.setOnClickListener(this);
		previous.setOnClickListener(this);
		
		/**
		 * Fill the Information of the Exercise
		 */
		PerformanceTargetMapper ptMapper = new PerformanceTargetMapper(getActivity());
		PerformanceTarget performanceTarget = ptMapper.getPerformanceTargetByExerciseId(exerciseSpecific.getExercise(), exerciseSpecific.getTrainingDayId());
		
		if(!exerciseSpecific.getExercise().getMuscleGroup().getName().equals(getResources().getString(R.string.Cardio))){
			
			set.setText(getString(R.string.Set) + ":" + String.valueOf(performanceTarget.getSet()));
			rep.setText(getString(R.string.Rep) + ":" + String.valueOf(performanceTarget.getRepetition()));
		}else{
			((View) view).findViewById(R.id.divider).setVisibility(View.GONE);
			
			set.setVisibility(View.GONE);
			rep.setText(getString(R.string.Min) + ":" + String.valueOf(performanceTarget.getRepetition()));
		}
		
		
		setDate();
		
		return view;	
	}

	@SuppressWarnings("unused")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		int id = v.getId();
		if (id == R.id.Next) {
			performanceActualList = paMapper.getNextPerformanceActual(calendar, exerciseSpecific.getExercise());
			if (!performanceActualList.isEmpty()){
				exerciseSpecific.savePerformanceActual();
				exerciseSpecific.updateListView(performanceActualList);
				exerciseSpecific.closeKeyboard();
				
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
		} else if (id == R.id.Previous) {
			performanceActualList = paMapper.getPreviousPerformanceActual(calendar, exerciseSpecific.getExercise());
			if (!performanceActualList.isEmpty()){	
				exerciseSpecific.savePerformanceActual();
				exerciseSpecific.updateListView(performanceActualList);
				exerciseSpecific.closeKeyboard();
				
				calendar.setTime(performanceActualList.get(0).getTimestamp());
				setDate();
			}else{
				Toast.makeText(getActivity(), "Keine letzte Übung gefunden", Toast.LENGTH_SHORT).show();
			}
		} else {
		}
	}
	
	/**
	 * Set the View Elements to the formated current Date 
	 * 
	 * @author Eric Schmidt
	 */
	@SuppressWarnings("deprecation")
	private void setDate(){
		Calendar c = Calendar.getInstance();
		if (String.valueOf(calendar.getTime().getDate()) == String.valueOf(c.getTime().getDate())){
			date.setText(getResources().getString(R.string.ActionBarDatePickerFragment_Today));
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
	 * Returns the current Date
	 * 
	 * @return Date the current Date
	 * @author Eric Schmidt
	 */
	public Date getDate(){
		return calendar.getTime();
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
