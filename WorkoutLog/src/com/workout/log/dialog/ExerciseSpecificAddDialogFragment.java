package com.workout.log.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.remic.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.TrainingDayMapper;

@SuppressLint("ValidFragment")
public class ExerciseSpecificAddDialogFragment extends DialogFragment {
	private int trainingDayId;
	private int exerciseId;
	
	private static TrainingDayMapper tdMapper;
	private static ExerciseMapper eMapper;
	
	private NumberPicker eTargetSetCount;
	private NumberPicker eTargetRepCount;
	
	public static ExerciseSpecificAddDialogFragment newInstance(Context context, int trainingDayId, int exerciseId) {
		ExerciseSpecificAddDialogFragment exerciseClickDialogFragment = new ExerciseSpecificAddDialogFragment(trainingDayId, exerciseId);
		tdMapper = new TrainingDayMapper(context);
		eMapper = new ExerciseMapper(context);
		return exerciseClickDialogFragment;
	}
	
	public ExerciseSpecificAddDialogFragment(){
		
	}
	
	private ExerciseSpecificAddDialogFragment(int trainingDayId, int exerciseId ) {
		super();
		this.trainingDayId = trainingDayId;
		this.exerciseId = exerciseId;	
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialogfragment_exercise_specific, null);
		
		alert.setTitle(getResources().getString(R.string.ExerciseSpecificAddDialogFragment_ExerciseAdd));
		
		Exercise e = eMapper.getExerciseById(exerciseId);
		
		eTargetSetCount = (NumberPicker) view.findViewById(R.id.Satzanzahl);
		eTargetRepCount = (NumberPicker) view.findViewById(R.id.WdhAnzahl);
		
		eTargetSetCount.setMaxValue(100);
		eTargetSetCount.setMinValue(0);
		
		eTargetRepCount.setMaxValue(100);
		eTargetRepCount.setMinValue(0);
		
		alert.setView(view);
		
		alert.setPositiveButton(getResources().getString(R.string.Save), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				String eTargetSet = String.valueOf(eTargetSetCount.getValue());
				String eTargetRep = String.valueOf(eTargetRepCount.getValue());
				
				if(eTargetSet.isEmpty() || eTargetRep.isEmpty()) {
					Toast.makeText(getActivity(), getResources().getString(R.string.MissingField), Toast.LENGTH_SHORT ).show();
				}else {
					if (!tdMapper.checkIfExist(trainingDayId, exerciseId)){
						tdMapper.addExerciseToTrainingDayAndPerformanceTarget(trainingDayId, exerciseId, eTargetSetCount.getValue(), eTargetRepCount.getValue());
						Toast.makeText(getActivity(), getResources().getString(R.string.ExerciseSpecificAddDialogFramgent_AddSuccess), Toast.LENGTH_SHORT ).show();
					}else{
						Toast.makeText(getActivity(), getResources().getString(R.string.ExerciseSpecificAddDialogFramgent_AddFailure), Toast.LENGTH_SHORT ).show();
					}
				}	
			  }
			});
			alert.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {}
			});
		return alert.show();
	}
}
