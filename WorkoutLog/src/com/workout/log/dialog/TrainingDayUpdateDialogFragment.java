package com.workout.log.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.workoutlog.R;
import com.workout.log.ManageTrainingDays;
import com.workout.log.TrainingDayAddToWorkoutplan;
import com.workout.log.bo.TrainingDay;
import com.workout.log.db.TrainingDayMapper;

@SuppressLint("ValidFragment")
public class TrainingDayUpdateDialogFragment extends DialogFragment {
	private static TrainingDayMapper tMapper;
	private int trainingDayId;
	private Fragment fragment;
	
	public static TrainingDayUpdateDialogFragment newInstance(Fragment fragment, int trainingDayId) {
		TrainingDayUpdateDialogFragment trainingDayUpdateDialogFragment = new TrainingDayUpdateDialogFragment(fragment, trainingDayId);
		return trainingDayUpdateDialogFragment;	
	}
	
	public TrainingDayUpdateDialogFragment(Fragment fragment, int trainingDayId) {
		super();
		this.trainingDayId = trainingDayId;
		this.fragment = fragment;
		
		tMapper = new TrainingDayMapper(fragment.getActivity());
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialogfragment_trainingday_edit, null);
		
		alert.setTitle("Trainingsplan �ndern");
		
		// Set an EditText view to get user input 
		final EditText trainingDayName = (EditText) view.findViewById(R.id.EditText_TrainingdayName);
		
		/**
		 * Load current Workoutplan and set the value as default
		 */
		final TrainingDay  t = tMapper.getTrainingDayById(trainingDayId);
		trainingDayName.setText(t.getName());
		
		alert.setView(view);
		
		alert.setPositiveButton("Update", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String value = String.valueOf(trainingDayName.getText());
				t.setName(value);
				if(!value.isEmpty()){
					// Mapper-Methode aufrufen zum Hinzuf�gen einer neuen �bung
					tMapper.update(t);
					// Toast einblenden 
					Toast.makeText(getActivity(), "Trainingstag wurde erfolgreich ge�ndert!", Toast.LENGTH_SHORT ).show();
					// ListView aktualisieren 
					updateListView();
				}else{
					Toast.makeText(getActivity(), "Bitte s�tmliche Felder ausf�llen", Toast.LENGTH_SHORT ).show();
				}
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
		if(fragment instanceof ManageTrainingDays){
			((ManageTrainingDays) fragment).updateListView(tMapper.getAllTrainingDay());
		}else{
			((TrainingDayAddToWorkoutplan) fragment).updateListView(tMapper.getAllTrainingDay());
		}
	}
}
