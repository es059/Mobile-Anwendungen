package com.workout.log.dialog;

import com.workout.log.db.WorkoutplanMapper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

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
	
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle("Trainingstag hinzufügen");
		alert.setMessage("Wollen Sie diesen Trainingstag dem Trainingsplan hinzufügen?");
	
		alert.setPositiveButton("Bestätigen", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				/**
				 * Save the trainingday to the current workoutplan
				 */
				wpMapper.addTrainingDayToWorkoutplan(trainingDayId, workoutPlanId);
				
			/*	*//**
				 * Prepare the transaction and submit the workoutPlanId 
				 *//*
				Bundle data = new Bundle();
				
			    data.putInt("WorkoutplanId",workoutPlanId);
				
			    ManageWorkoutplan manageWorkoutPlan = new ManageWorkoutplan();
			    manageWorkoutPlan.setArguments(data);
			    
			    FragmentTransaction transaction = getFragmentManager().beginTransaction();
		        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		        transaction.replace(R.id.fragment_container, manageWorkoutPlan , "ExerciseOverview");
		        transaction.addToBackStack(null);
		        transaction.commit();*/
				
		        /**
		         * Show a Message that the trainingday was added to the workoutplan
		         */
		        Toast.makeText(getActivity(), "Trainingstag wurde erfolgreich dem Trainingsplan hinzugefügt!", Toast.LENGTH_SHORT ).show();
			  }
			});

			alert.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
			  }
			});

			return alert.show();
		}
}