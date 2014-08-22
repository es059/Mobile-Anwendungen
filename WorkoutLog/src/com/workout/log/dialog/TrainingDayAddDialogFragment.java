package com.workout.log.dialog;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.remic.workoutlog.R;
import com.workout.log.ManageTrainingDays;
import com.workout.log.TrainingDayAddToWorkoutplan;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.TrainingDay;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.listAdapter.TrainingDayListAdapter;

@SuppressLint("ValidFragment") 
public class TrainingDayAddDialogFragment extends DialogFragment {
	private TrainingDayMapper tdMapper;
	private TrainingDayListAdapter trainingDayListAdapter;
	private String trainingDayStringName;
	private Fragment fragment;

	public static TrainingDayAddDialogFragment newInstance(Context context, TrainingDayListAdapter trainingDayListAdapter,
			String trainingDayStringName, Fragment fragment) {
		
		TrainingDayAddDialogFragment trainingDayAddDialogFragment = new TrainingDayAddDialogFragment(context, trainingDayListAdapter,
				trainingDayStringName, fragment);	
		return trainingDayAddDialogFragment;
	}
	
	public TrainingDayAddDialogFragment(){
		
	}
	
	private TrainingDayAddDialogFragment(Context context, TrainingDayListAdapter trainingDayListAdapter,
			String trainingDayStringName, Fragment fragment) {
		super();
		this.trainingDayListAdapter = trainingDayListAdapter;
		this.trainingDayStringName = trainingDayStringName;
		this.fragment = fragment;
		
		tdMapper = new TrainingDayMapper(context);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialogfragment_trainingday_edit, null);
		
		alert.setTitle(getResources().getString(R.string.TrainingDayAdd));
		
		// Set an EditText view to get user input 
		final EditText input = (EditText) view.findViewById(R.id.EditText_TrainingdayName);
		
		if (trainingDayStringName != null){
			input.setText(trainingDayStringName);
		}
		
		alert.setView(view);

		alert.setPositiveButton(getResources().getString(R.string.Save), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				/**
				 * Create a TrainingDay Object with the given information by the user and 
				 * add it to the database
				 */
				TrainingDay d = new TrainingDay();
				d.setName(input.getText().toString());
				d.setExerciseList(new ArrayList<Exercise>());
				tdMapper.add(d);
				
				/**
				 * Update the ListView in the Fragment with the new TrainingDay
				 */
				if (trainingDayListAdapter != null){
					trainingDayListAdapter.clear();
					trainingDayListAdapter.addAll(tdMapper.getAllTrainingDay());
					trainingDayListAdapter.notifyDataSetChanged();
				}else{
					if (fragment instanceof ManageTrainingDays) ((ManageTrainingDays) 
							fragment).updateListView(tdMapper.getAllTrainingDay(), null);   
					if (fragment instanceof TrainingDayAddToWorkoutplan) ((TrainingDayAddToWorkoutplan) 
							fragment).updateListView(tdMapper.getAllTrainingDay(), null);  
				}
			
				Toast.makeText(getActivity(), getResources().getString(R.string.TrainingDayAddDialogFragment_AddSuccess), Toast.LENGTH_SHORT ).show();
			  }
		});

		alert.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
		  @Override
		public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		return alert.show();
	}
	
}
