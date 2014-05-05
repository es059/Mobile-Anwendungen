package com.workout.log.dialog;

import com.example.workoutlog.R;
import com.workout.log.TrainingDayExerciseOverview;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.listAdapter.ExerciseListAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ExerciseClickDialogFragment extends DialogFragment {
	
	private int trainingDayId;
	private int exerciseId;
	private static TrainingDayMapper tdMapper;
	private Toast toast;
	private Toast toast1;
	//private TextView TVSatzanzahl;
	//private TextView TVwdhAnzahl;
	private EditText ETSatzanzahl;
	private EditText ETwdhAnzahl;
	private Button button1;
	private Button button2;
	
	
	
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
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialogfragment_exercise_click, null);
		
		ETSatzanzahl = (EditText) view.findViewById(R.id.Satzanzahl);
		ETwdhAnzahl = (EditText) view.findViewById(R.id.WdhAnzahl);
	//	button1 = (Button) view.findViewById(R.id.Save);
	//	button1 = (Button) view.findViewById(R.id.Cancel);
	//	TVSatzanzahl.setText("Sätze");
	//	TVwdhAnzahl.setText("Wiederholungen");
		alert.setView(view);
		toast = Toast.makeText(getActivity(), "Übung wurde erfolgreich dem Trainingtag hinzugefügt!", Toast.LENGTH_SHORT );
		toast1 = Toast.makeText(getActivity(), "Bitte alle Felder ausfüllen!", Toast.LENGTH_SHORT );
		
		alert.setTitle("Bestätigung");
		alert.setMessage("Bitte tragen Sie die Satzanzahl und die Wiederholungsanzahl hier ein:");
	//	alert.setMessage("Wollen Sie diese Übung dem ausgewählten Trainingstag hinzufügen?");
		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String ETS = ETSatzanzahl.toString().trim();
				String ETW = ETwdhAnzahl.toString().trim();
				
				if(ETS.isEmpty() || ETW.isEmpty()) {
					toast1.show();
					
				}else {
				tdMapper.ExerciseAddToTrainingDay(trainingDayId, exerciseId, ETSatzanzahl.getText(), ETwdhAnzahl.getText() );
				toast.show();
				Intent intent = new Intent();
				intent.setClass(getActivity(), TrainingDayExerciseOverview.class);
				startActivity(intent);
				}
					
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
