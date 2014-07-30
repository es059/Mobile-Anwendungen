package com.workout.log.dialog;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.MuscleGroup;
import com.workout.log.data.ListItem;
import com.workout.log.data.MuscleGroupSectionItem;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.MuscleGroupMapper;
import com.workout.log.listAdapter.ExerciseListWithoutSetsRepsAdapter;

public class ExerciseAddDialogFragment extends DialogFragment {
	private ExerciseListWithoutSetsRepsAdapter exerciseListAdapter;
	private static ExerciseMapper eMapper;
	private EditText exerciseName;
	private Spinner muscleGroup;
	private Context applicationContext;
	private String selectedMuscleGroup = "";
	private String exerciseStringName = "";

	public static ExerciseAddDialogFragment newInstance(Context context, ExerciseListWithoutSetsRepsAdapter exerciseListWithoutSetsRepsAdapter, String exerciseStringName) {
		ExerciseAddDialogFragment exerciseAddDialogFragment = new ExerciseAddDialogFragment(context, exerciseListWithoutSetsRepsAdapter, exerciseStringName);
		eMapper = new ExerciseMapper(context);
		
		return exerciseAddDialogFragment;
	}
	
	public ExerciseAddDialogFragment(Context context, ExerciseListWithoutSetsRepsAdapter exerciseListWithoutSetsRepsAdapter, String exerciseStringName) {
		super();
		this.exerciseStringName = exerciseStringName;
		exerciseListAdapter = exerciseListWithoutSetsRepsAdapter;
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
		    	selectedMuscleGroup = (String) parent.getItemAtPosition(pos);
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
				
				if(!value.isEmpty() && !selectedMuscleGroup.isEmpty()){
					// Mapper-Methode aufrufen zum Hinzuf�gen einer neuen �bung
					eMapper.add(value, selectedMuscleGroup);
					// Toast einblenden 
					Toast.makeText(getActivity(), getResources().getString(R.string.ExerciseAddDialogFragment_AddSuccess), Toast.LENGTH_SHORT ).show();
					// ListView aktualisieren 
					updateAdapter(eMapper.getAllExercise());
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
	
	public void updateAdapter(ArrayList<Exercise> List){
		/**
		 * Variables to implement header in the ListView
		 */
		ArrayList<Exercise> eListMuscleGroup = null;
		ArrayList<MuscleGroup> mList = null;
		MuscleGroupMapper mMapper = new MuscleGroupMapper(getActivity());
		
		
		exerciseListAdapter.clear();
		
		/**
		 * Build a ArrayList containing the muscleGroup and exercises
		 */
		//Select all MuscleGroups
		mList = mMapper.getAll();
		//Select All Exercises from MuscleGroup 
		eMapper = new ExerciseMapper(getActivity());
		ArrayList<ListItem> listComplete = new ArrayList<ListItem>();
		for (MuscleGroup m : mList){
			eListMuscleGroup = eMapper.getExerciseByMuscleGroup(List, m.getId());
			if (!eListMuscleGroup.isEmpty()){
				listComplete.add(new MuscleGroupSectionItem(m.getName()));
				listComplete.addAll(eListMuscleGroup);
			}
		}
		
		exerciseListAdapter.addAll(listComplete);
		exerciseListAdapter.notifyDataSetChanged();
	}

}
