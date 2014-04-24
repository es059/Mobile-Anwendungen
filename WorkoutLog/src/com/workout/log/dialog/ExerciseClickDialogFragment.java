package com.workout.log.dialog;

import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.listAdapter.ExerciseListAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

public class ExerciseClickDialogFragment extends DialogFragment {
	
	private int trainingDayId;
	private int exerciseId;
	private static TrainingDayMapper tdMapper;
	private Toast toast;
	
	public static ExerciseClickDialogFragment newInstance(Context a, int trainingDayId, int exerciseId) {
		ExerciseClickDialogFragment exerciseClickDialogFragment = new ExerciseClickDialogFragment(trainingDayId, exerciseId);
		tdMapper = new TrainingDayMapper(a);
		
		return exerciseClickDialogFragment;
		
	}
	
	public ExerciseClickDialogFragment(int trainingDayId, int exerciseId ) {
		super();
		this.trainingDayId = trainingDayId;
		this.exerciseId = exerciseId;
		
	}
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		toast = Toast.makeText(getActivity(), "�bung wurde erfolgreich dem Trainingtag hinzugef�gt!", Toast.LENGTH_SHORT );
		alert.setTitle("Best�tigung");
		alert.setMessage("Wollen Sie diese �bung dem ausgew�hlten Trainingstag hinzuf�gen?");
		alert.setPositiveButton("JA", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
				tdMapper.ExerciseAddToTrainingDay(trainingDayId, exerciseId);
				
					
			  }
			});

			alert.setNegativeButton("NEIN", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
			  }
			});

			return alert.show();
		}
}
