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

import com.remic.workoutlog.R;
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
	private int selectedMuscleGroupId = -1;
	
	public static ExerciseUpdateDialogFragment newInstance(Fragment fragment, int exerciseId) {
		ExerciseUpdateDialogFragment ExerciseUpdateDialogFragment = new ExerciseUpdateDialogFragment(fragment, exerciseId);
		return ExerciseUpdateDialogFragment;	
	}
	
	public ExerciseUpdateDialogFragment(){
		
	}
	
	private ExerciseUpdateDialogFragment(Fragment fragment, int exerciseId) {
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

		alert.setTitle(getResources().getString(R.string.ExerciseEdit));
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
				String selectedMuscleGroup = (String) parent.getItemAtPosition(pos);
		    	
		    	switch (selectedMuscleGroup){
		    	case "Rücken":
		    		selectedMuscleGroupId = 1;
		    		break;
		    	case "Bauch":
		    		selectedMuscleGroupId = 2;
		    		break;
		    	case "Brust":
		    		selectedMuscleGroupId = 3;
		    		break;
		    	case "Beine":
		    		selectedMuscleGroupId = 4;
		    		break;
		    	case "Bizeps":
		    		selectedMuscleGroupId = 5;
		    		break;
		    	case "Trizeps":
		    		selectedMuscleGroupId = 6;
		    		break;
		    	case "Cardio":
		    		selectedMuscleGroupId = 7;
		    		break;
		    	case "Schulter":
		    		selectedMuscleGroupId = 8;
		    		break;
		    		
		    	case "Back":
		    		selectedMuscleGroupId = 1;
		    		break;
		    	case "Abs":
		    		selectedMuscleGroupId = 2;
		    		break;
		     	case "Chest":
		    		selectedMuscleGroupId = 3;
		    		break;
		    	case "Legs":
		    		selectedMuscleGroupId = 4;
		    		break;
		    	case "Biceps":
		    		selectedMuscleGroupId = 5;
		    		break;
		    	case "Triceps":
		    		selectedMuscleGroupId = 6;
		    		break;
		     	case "Shoulder":
		    		selectedMuscleGroupId = 8;
		    		break;
		    	}
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
		switch (m.getId()){
	    	case 1:
	    		muscleGroup.setSelection(0); //Back
	    		break;
	    	case 2:
	    		muscleGroup.setSelection(1); //Abs
	    		break;
	    	case 3:
	    		muscleGroup.setSelection(3); //Chest
	    		break;
	    	case 4:
	    		muscleGroup.setSelection(4); //Legs
	    		break;
	    	case 5:
	    		muscleGroup.setSelection(6); //Biceps
	    		break;
	    	case 6:
	    		muscleGroup.setSelection(5); //Triceps
	    		break;
	    	case 8:
	    		muscleGroup.setSelection(2); //Shoulder
	    		break;
	    	case 7:
	    		//muscleGroup.setSelection(7); --> Cardio
	    		break;
		}
		
		alert.setView(view);
		
		alert.setPositiveButton(getResources().getString(R.string.Update), new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
			// String aus Textfeld holen  
			String value = String.valueOf(exerciseName.getText());
			if(!value.isEmpty() && selectedMuscleGroupId != -1){
				// Mapper-Methode aufrufen zum Hinzufügen einer neuen Übung
				eMapper.update(exerciseId, value, selectedMuscleGroupId);
				// Toast einblenden 
				Toast.makeText(getActivity(), getResources().getString(R.string.ExerciseUpdateDialogFragment_EditSuccess), Toast.LENGTH_SHORT ).show();
				// ListView aktualisieren 
				updateListView(eMapper.getAllExercise());
			}else{
				Toast.makeText(getActivity(), getResources().getString(R.string.MissingField), Toast.LENGTH_SHORT ).show();
			}
		  }
		});

		alert.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
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
