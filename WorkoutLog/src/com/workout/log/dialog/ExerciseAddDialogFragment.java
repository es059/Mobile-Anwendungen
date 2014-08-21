package com.workout.log.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.remic.workoutlog.R;
import com.workout.log.ExerciseAdd;
import com.workout.log.ExerciseAddToTrainingDay;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.listAdapter.ExerciseListWithoutSetsRepsAdapter;

public class ExerciseAddDialogFragment extends DialogFragment {
	private static ExerciseMapper eMapper;
	private EditText exerciseName;
	private Spinner muscleGroup;
	private Context applicationContext;
	private int selectedMuscleGroupId = -1;
	private String exerciseStringName = "";
	private Fragment fragment;

	public static ExerciseAddDialogFragment newInstance(Context context, ExerciseListWithoutSetsRepsAdapter exerciseListWithoutSetsRepsAdapter,
			String exerciseStringName, Fragment fragment) {
		ExerciseAddDialogFragment exerciseAddDialogFragment = new ExerciseAddDialogFragment(context, exerciseListWithoutSetsRepsAdapter, exerciseStringName, fragment);
		eMapper = new ExerciseMapper(context);
		
		return exerciseAddDialogFragment;
	}
	
	public ExerciseAddDialogFragment(){
		
	}
	
	private ExerciseAddDialogFragment(Context context, ExerciseListWithoutSetsRepsAdapter exerciseListWithoutSetsRepsAdapter, String exerciseStringName, Fragment fragment) {
		super();
		this.exerciseStringName = exerciseStringName;
		this.fragment = fragment;
		
		applicationContext = context;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialogfragment_exercise_edit, null);
		
		alert.setTitle(getResources().getString(R.string.ExerciseAddDialogFragment_ExerciseAdd));	
		
		// Set an EditText view to get user input 
		exerciseName = (EditText) view.findViewById(R.id.EditText_ExerciseName);
		if (exerciseStringName != null){
			exerciseName.setText(exerciseStringName);
		}
		// initialize Spinner to get muscleGroup
		muscleGroup = (Spinner) view.findViewById(R.id.Spinner_MuscleGroup);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(applicationContext,
		        R.array.MuscleGroup, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		muscleGroup.setAdapter(adapter);
		//Auslesen des Spinners 
		muscleGroup.setOnItemSelectedListener(new OnItemSelectedListener() {
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
		
		alert.setView(view);

		alert.setPositiveButton(getResources().getString(R.string.Save), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// String aus Textfeld holen  
				String value = String.valueOf(exerciseName.getText());
				
				if(!value.isEmpty() && selectedMuscleGroupId != -1){
					// Mapper-Methode aufrufen zum Hinzufügen einer neuen Übung
					boolean success = eMapper.add(value, selectedMuscleGroupId);
					if (success){
						// Toast einblenden 
						Toast.makeText(getActivity(), getResources().getString(R.string.ExerciseAddDialogFragment_AddSuccess), Toast.LENGTH_SHORT ).show();
						// ListView aktualisieren 
						if (fragment instanceof ExerciseAdd) ((ExerciseAdd) 
								fragment).updateListView(eMapper.getAllExercise(), false, null);   
						if (fragment instanceof ExerciseAddToTrainingDay) ((ExerciseAddToTrainingDay) 
								fragment).updateListView(eMapper.getAllExercise(), null);   
					}else{
						Toast.makeText(getActivity(), getResources().getString(R.string.ExerciseAddDialogFragment_ExerciseExists), Toast.LENGTH_SHORT ).show();
					}
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
}
