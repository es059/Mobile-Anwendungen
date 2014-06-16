package com.workout.log.dialog;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.MuscleGroup;
import com.workout.log.data.ExerciseItem;
import com.workout.log.data.MuscleGroupSectionItem;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.MuscleGroupMapper;
import com.workout.log.listAdapter.ExerciseListWithoutSetsRepsAdapter;

@SuppressLint("ValidFragment")
public class ExerciseUpdateDialogFragment extends DialogFragment {
	private ExerciseListWithoutSetsRepsAdapter exerciseListAdapter;
	private static ExerciseMapper eMapper;
	private Context context;
	private int exerciseId;
	private String selectedMuscleGroup = "";
	
	public static ExerciseUpdateDialogFragment newInstance(Context context, ExerciseListWithoutSetsRepsAdapter adapter, int exerciseId) {
		ExerciseUpdateDialogFragment ExerciseUpdateDialogFragment = new ExerciseUpdateDialogFragment(context,adapter, exerciseId);
		return ExerciseUpdateDialogFragment;	
	}
	
	public ExerciseUpdateDialogFragment(Context context, 
			ExerciseListWithoutSetsRepsAdapter adapter, int exerciseId) {
		super();
		eMapper = new ExerciseMapper(context);
		this.context = context;
		this.exerciseListAdapter = adapter;
		this.exerciseId = exerciseId;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialogfragment_exercise_edit, null);
		
		final EditText exerciseName = (EditText) view.findViewById(R.id.EditText_ExerciseName);
		final Spinner muscleGroup = (Spinner) view.findViewById(R.id.Spinner_MuscleGroup);
		TextView informationText = (TextView) view.findViewById(R.id.TextView_Information);
		

		alert.setTitle("Übung bearbeiten");
		// Set an TextView view to view the InformationText
		informationText.setText("Nehmen Sie die Änderungen an Ihrer Übung hier vor:");
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
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
				updateAdapter(eMapper.getAll());
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
		ArrayList<ExerciseItem> listComplete = new ArrayList<ExerciseItem>();
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
