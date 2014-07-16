package com.workout.log.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.workoutlog.R;
import com.workout.log.TrainingDayExerciseOverview;
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.PerformanceTargetMapper;

@SuppressLint("ValidFragment")
public class ExerciseSpecificUpdateDialogFragment extends DialogFragment{
	private int trainingDayId;
	private int exerciseId;
	private Fragment fragment;
	
	public static ExerciseSpecificUpdateDialogFragment newInstance(Fragment fragment, int trainingDayId, int exerciseId) {
		ExerciseSpecificUpdateDialogFragment exerciseSpecificUpdateDialogFragment = new ExerciseSpecificUpdateDialogFragment(fragment, trainingDayId, exerciseId);
		return exerciseSpecificUpdateDialogFragment;	
	}
	
	public ExerciseSpecificUpdateDialogFragment(Fragment fragment, int trainingDayId, int exerciseId) {
		super();
		this.trainingDayId = trainingDayId;
		this.exerciseId = exerciseId;
		this.fragment = fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialogfragment_exercise_specific, null);
		
		alert.setTitle("Hinzufügen der Übung");

		final NumberPicker eTargetSetCount = (NumberPicker) view.findViewById(R.id.Satzanzahl);
		final NumberPicker eTargetRepCount = (NumberPicker) view.findViewById(R.id.WdhAnzahl);
		
		eTargetSetCount.setMaxValue(100);
		eTargetSetCount.setMinValue(0);
		
		eTargetRepCount.setMaxValue(100);
		eTargetRepCount.setMinValue(0);
		
		alert.setView(view);
		
		/**
		 * Load current Workoutplan and set the value as default
		 */
		final PerformanceTargetMapper pMapper = new PerformanceTargetMapper(fragment.getActivity());
		ExerciseMapper eMapper = new ExerciseMapper(fragment.getActivity());
		final PerformanceTarget pt = pMapper.getPerformanceTargetByExerciseId(eMapper.getExerciseById(exerciseId), trainingDayId);
		
		eTargetSetCount.setValue(pt.getSet());
		eTargetRepCount.setValue(pt.getRepetition());
		
		alert.setView(view);
		
		alert.setPositiveButton("Update", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				pt.setRepetition(eTargetRepCount.getValue());
				pt.setSet(eTargetSetCount.getValue());
				
				pMapper.updatePerformanceTarget(pt);
				
				// Toast einblenden 
				Toast.makeText(getActivity(), "Trainingstag wurde erfolgreich geändert!", Toast.LENGTH_SHORT ).show();
				// ListView aktualisieren 
				updateListView();
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

	private void updateListView(){
		((TrainingDayExerciseOverview) fragment).updateListView();
	}
}
