package com.workout.log.dialog;

import com.example.workoutlog.R;
import com.workout.log.ManageWorkoutplan;
import com.workout.log.TrainingDayExerciseOverview;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
		// TODO Auto-generated constructor stub
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
	

		final Toast toast = Toast.makeText(getActivity(), "Trainingstag wurde erfolgreich dem Trainingsplan hinzugefügt!", Toast.LENGTH_SHORT );
		
		alert.setTitle("Trainingstag hinzufügen");
		alert.setMessage("Wollen Sie diesen Trainingstag dem Trainingsplan hinzufügen?");
	
		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			
				wpMapper.addTrainingDayToWorkoutplan(trainingDayId, workoutPlanId);
				toast.show();
				Intent intent = new Intent();
				intent.setClass(getActivity(), ManageWorkoutplan.class);
				intent.putExtra("WorkoutplanId", workoutPlanId);
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