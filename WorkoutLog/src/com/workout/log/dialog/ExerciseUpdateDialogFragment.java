package com.workout.log.dialog;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.workoutlog.R;
import com.workout.log.ExerciseAdd;
import com.workout.log.ExerciseAddToTrainingDay;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.MuscleGroup;
import com.workout.log.db.ExerciseMapper;

@SuppressLint("ValidFragment")
public class ExerciseUpdateDialogFragment extends DialogFragment {
	private static ExerciseMapper eMapper;
	private Fragment fragment;
	private int exerciseId;
	private String selectedMuscleGroup = "";
	
	public static ExerciseUpdateDialogFragment newInstance(Fragment fragment, int exerciseId) {
		ExerciseUpdateDialogFragment ExerciseUpdateDialogFragment = new ExerciseUpdateDialogFragment(fragment, exerciseId);
		return ExerciseUpdateDialogFragment;	
	}
	
	public ExerciseUpdateDialogFragment(Fragment fragment, int exerciseId) {
		super();
		eMapper = new ExerciseMapper(fragment.getActivity());
		this.fragment = fragment;
		this.exerciseId = exerciseId;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialogfragment_exercise_edit, null);
		
		final EditText exerciseName = (EditText) view.findViewById(R.id.EditText_ExerciseName);
		final Spinner muscleGroup = (Spinner) view.findViewById(R.id.Spinner_MuscleGroup);		

		alert.setTitle("Übung bearbeiten");
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(fragment.getActivity(),
		        R.array.MuscleGroup, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		muscleGroup.setAdapter(adapter);
		muscleGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	selectedMuscleGroup = (String) parent.getItemAtPosition(pos);
		    }
			@Override
		    public void onNothingSelected(AdapterView<?> parent) {}
		});
		
		/**
		 * Load current Exercise and set the value as default
		 */
		Exercise e = eMapper.getExerciseById(exerciseId);
		MuscleGroup m = e.getMuscleGroup();
		exerciseName.setText(e.getName());
		for (int i = 0; i <= muscleGroup.getCount() -1; i++){
			if(m.getName().toString().contentEquals(muscleGroup.getItemAtPosition(i).toString())){
				muscleGroup.setSelection(i);
			}
		}
		
		alert.setView(view);
		
		alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
			// String aus Textfeld holen  
			String value = String.valueOf(exerciseName.getText());
			if(!value.isEmpty() && !selectedMuscleGroup.isEmpty()){
				// Mapper-Methode aufrufen zum Hinzufügen einer neuen Übung
				eMapper.update(exerciseId, value, selectedMuscleGroup);
				// Toast einblenden 
				Toast.makeText(getActivity(), "Übung wurde erfolgreich geändert!", Toast.LENGTH_SHORT ).show();
				// ListView aktualisieren 
				updateListView(eMapper.getAllExercise());
			}else{
				Toast.makeText(getActivity(), "Bitte sätmliche Felder ausfüllen", Toast.LENGTH_SHORT ).show();
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
	
	/**
	 * Update the ListView after the exercise was updated
	 * 
	 * @param List the updated List
	 * @author Eric Schmidt
	 */
	public void updateListView(ArrayList<Exercise> List){
		if (fragment instanceof ExerciseAdd){
			((ExerciseAdd) fragment).updateListView(eMapper.getAllExercise(), true, null);
		}else{
			((ExerciseAddToTrainingDay) fragment).updateListView(eMapper.getAllExercise(),null);
		}
	}


}
