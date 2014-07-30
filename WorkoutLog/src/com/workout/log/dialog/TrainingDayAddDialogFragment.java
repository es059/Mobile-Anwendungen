package com.workout.log.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.workoutlog.R;
import com.workout.log.bo.TrainingDay;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.listAdapter.TrainingDayListAdapter;

@SuppressLint("ValidFragment") 
public class TrainingDayAddDialogFragment extends DialogFragment {
	private TrainingDayMapper tdMapper;
	private TrainingDayListAdapter trainingDayListAdapter;

	public static TrainingDayAddDialogFragment newInstance(Context context, TrainingDayListAdapter trainingDayListAdapter) {
		TrainingDayAddDialogFragment trainingDayAddDialogFragment = new TrainingDayAddDialogFragment(context, trainingDayListAdapter);	
		return trainingDayAddDialogFragment;
	}
	
	public TrainingDayAddDialogFragment(Context context, TrainingDayListAdapter trainingDayListAdapter) {
		super();
		this.trainingDayListAdapter = trainingDayListAdapter;
		tdMapper = new TrainingDayMapper(context);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialogfragment_trainingday_edit, null);
		
		alert.setTitle("Traniningstag Hinzufügen");
		
		// Set an EditText view to get user input 
		final EditText input = (EditText) view.findViewById(R.id.EditText_TrainingdayName);
		
		alert.setView(view);

		alert.setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
			/**
			 * Create a TrainingDay Object with the given information by the user and 
			 * add it to the database
			 */
			TrainingDay d = new TrainingDay();
			d.setName(input.getText().toString());
			tdMapper.add(d);
			
			/**
			 * Update the ListView in the Fragment with the new TrainingDay
			 */
			trainingDayListAdapter.clear();
			trainingDayListAdapter.addAll(tdMapper.getAllTrainingDay());
			trainingDayListAdapter.notifyDataSetChanged();
		
			Toast.makeText(getActivity(), "Trainingstag wurde erfolgreich hinzugefügt!", Toast.LENGTH_SHORT ).show();
		  }
		});

		alert.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
		  @Override
		public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		return alert.show();
	}
	
}
