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
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.workoutlog.R;
import com.workout.log.db.TrainingDayMapper;

@SuppressLint("ValidFragment")
public class ExerciseSpecificAddDialogFragment extends DialogFragment {
	private int trainingDayId;
	private int exerciseId;
	private static TrainingDayMapper tdMapper;
	private NumberPicker eTargetSetCount;
	private NumberPicker eTargetRepCount;
	
	public static ExerciseSpecificAddDialogFragment newInstance(Context context, int trainingDayId, int exerciseId) {
		ExerciseSpecificAddDialogFragment exerciseClickDialogFragment = new ExerciseSpecificAddDialogFragment(trainingDayId, exerciseId);
		tdMapper = new TrainingDayMapper(context);
		
		return exerciseClickDialogFragment;
	}
	
	public ExerciseSpecificAddDialogFragment(int trainingDayId, int exerciseId ) {
		super();
		this.trainingDayId = trainingDayId;
		this.exerciseId = exerciseId;	
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialogfragment_exercise_specific, null);
		
		alert.setTitle("Hinzufügen der Übung");
		
		eTargetSetCount = (NumberPicker) view.findViewById(R.id.Satzanzahl);
		eTargetRepCount = (NumberPicker) view.findViewById(R.id.WdhAnzahl);
		
		eTargetSetCount.setMaxValue(100);
		eTargetSetCount.setMinValue(0);
		
		eTargetRepCount.setMaxValue(100);
		eTargetRepCount.setMinValue(0);
		
		alert.setView(view);
		
		
		alert.setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				String eTargetSet = String.valueOf(eTargetSetCount.getValue());
				String eTargetRep = String.valueOf(eTargetRepCount.getValue());
				
				if(eTargetSet.isEmpty() || eTargetRep.isEmpty()) {
					Toast.makeText(getActivity(), "Bitte alle Felder ausfüllen!", Toast.LENGTH_SHORT ).show();
				}else {
					if (!tdMapper.checkIfExist(trainingDayId, exerciseId)){
						tdMapper.addExerciseToTrainingDayAndPerformanceTarget(trainingDayId, exerciseId, eTargetSetCount.getValue(), eTargetRepCount.getValue());
						Toast.makeText(getActivity(), "Übung wurde erfolgreich dem Trainingtag hinzugefügt!", Toast.LENGTH_SHORT ).show();
					}else{
						Toast.makeText(getActivity(), "Übung kann nicht doppelt hinzugefügt werden", Toast.LENGTH_SHORT ).show();
					}
					
				}		
			  }
			});
			alert.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {}
			});
		return alert.show();
	}
}
