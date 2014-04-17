package com.workout.log.dialog;


import com.example.workoutlog.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ExerciseLongClickDialogFragment extends DialogFragment{
	ExerciseSelectionDialogListener rSDListener;
	
	public static ExerciseLongClickDialogFragment newInstance() {
		ExerciseLongClickDialogFragment exerciseLongClickDialogFragment = new ExerciseLongClickDialogFragment();
		return exerciseLongClickDialogFragment;
	}
	
	@Override 
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Einstellungen").setItems(R.array.exerciseLongClickDialogArray, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					break;
				case 1:
					break;
				case 2: 
					break;
				default:
					break;
				}
				rSDListener.onExerciseSelectionItemLongClick(ExerciseLongClickDialogFragment.this);
			}
		});
		return builder.create();
	}
	
	public interface ExerciseSelectionDialogListener{
		public void onExerciseSelectionItemLongClick(ExerciseLongClickDialogFragment dialog);
		}
	ExerciseSelectionDialogListener rSDListner;
	
	@Override 
	public void onAttach (Activity activity){
		super.onAttach(activity);
		try{
			rSDListener = (ExerciseSelectionDialogListener) activity;
		}catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement ExerciseLongClickDialogFragment");
		}
	}
}
