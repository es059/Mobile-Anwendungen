package com.workout.log.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.workout.log.ManageTrainingDays;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.listAdapter.ExerciseListWithoutSetsRepsAdapter;

public class TrainingDayAddDialogFragment extends DialogFragment {
	
	private TrainingDayMapper tdMapper;

	public static TrainingDayAddDialogFragment newInstance(Context a) {
		TrainingDayAddDialogFragment trainingDayAddDialogFragment = new TrainingDayAddDialogFragment(a);
		
		
		return trainingDayAddDialogFragment;
		
	}
	
	public TrainingDayAddDialogFragment(Context a) {
		super();
		tdMapper = new TrainingDayMapper(a);
		
	}

	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle("Traniningstag hinzufügen");
		alert.setMessage("Bitte geben sie den Namen des Trainingtags hier ein:");

		// Set an EditText view to get user input 
		final EditText input = new EditText(getActivity());
		input.setHint("Name");
		alert.setView(input);
		
		final Toast toast = Toast.makeText(getActivity(), "Trainingstag wurde erfolgreich hinzugefügt!", Toast.LENGTH_SHORT );
		
	

		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			tdMapper.add(input.getText());
			toast.show();
			Intent intent = new Intent();
			intent.setClass(getActivity(), ManageTrainingDays.class);
			startActivity(intent);
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		return alert.show();
	}
	
}
