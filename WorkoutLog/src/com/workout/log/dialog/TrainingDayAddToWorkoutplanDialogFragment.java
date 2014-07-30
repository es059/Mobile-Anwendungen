package com.workout.log.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.workout.log.db.WorkoutplanMapper;

@SuppressLint("ValidFragment")
public class TrainingDayAddToWorkoutplanDialogFragment extends DialogFragment {
	
private int workoutPlanId;
private int trainingDayId;
private static WorkoutplanMapper wpMapper;

	public static TrainingDayAddToWorkoutplanDialogFragment newInstance(Context a, int trainingDayId, int workoutPlanId) {
		TrainingDayAddToWorkoutplanDialogFragment trainingDayAddToWorkoutplan = new TrainingDayAddToWorkoutplanDialogFragment(a, trainingDayId, workoutPlanId );
		return trainingDayAddToWorkoutplan;
	}

	public TrainingDayAddToWorkoutplanDialogFragment(Context c, int a, int b) {
		super();
		wpMapper = new WorkoutplanMapper(c);
		workoutPlanId = b;
		trainingDayId = a;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle("Trainingstag hinzufügen");
	
		alert.setPositiveButton("Bestätigen", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				/**
				 * Save the trainingday to the current workoutplan
				 */
				wpMapper.addTrainingDayToWorkoutplan(trainingDayId, workoutPlanId);
						
		        /**
		         * Show a Message that the trainingday was added to the workoutplan
		         */
		        Toast.makeText(getActivity(), "Trainingstag wurde erfolgreich dem Trainingsplan hinzugefügt!", Toast.LENGTH_SHORT ).show();
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