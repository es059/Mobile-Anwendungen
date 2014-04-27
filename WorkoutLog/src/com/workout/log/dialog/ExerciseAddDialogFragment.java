package com.workout.log.dialog;

import com.example.workoutlog.R;
import com.workout.log.ExerciseAdd;
import com.workout.log.data.MuscleGroup;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.listAdapter.ExerciseListAdapter;

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
import android.widget.Toast;

public class ExerciseAddDialogFragment extends DialogFragment {
	// private MuscleGroup list = new MuscleGroup();
	
	private ExerciseListAdapter exerciseListAdapter;
	private static ExerciseMapper em;
	private Toast toast;
	private EditText exerciseName;
	private Spinner muscleGroup;
	private ArrayAdapter<String> muscleGroupAdapter;
	private MuscleGroup List;
	private Context applicationContext;
	private String selectedMuscleGroup;
	// Konstruktor 
	public static ExerciseAddDialogFragment newInstance(Context a, ExerciseListAdapter c) {
		ExerciseAddDialogFragment exerciseAddDialogFragment = new ExerciseAddDialogFragment(a, c);
		em = new ExerciseMapper(a);
		
		return exerciseAddDialogFragment;
		
	}
	
	public ExerciseAddDialogFragment(Context a, ExerciseListAdapter c) {
		super();
		exerciseListAdapter = c;
		applicationContext = a;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialogfragment_exercise_add, null);
		
		alert.setTitle("Übung hinzufügen");
		alert.setMessage("Bitte geben sie den Namen Ihrer Übung hier ein und wählen Sie passende Muskelgruppe aus:");

		// Set an EditText view to get user input 
		exerciseName = (EditText) view.findViewById(R.id.EditText_ExerciseName);
		//
		// initialize Spinner to get muscleGroup
		muscleGroup = (Spinner) view.findViewById(R.id.Spinner_MuscleGroup);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(applicationContext,
		        R.array.MuscleGroup, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		muscleGroup.setAdapter(adapter);
		
		
		alert.setView(view);
		
		toast = Toast.makeText(getActivity(), "Übung wurde erfolgreich hinzugefügt!", Toast.LENGTH_SHORT );
		
		
	//	final Spinner muskelgruppe = new Spinner(getActivity());
	//	alert.setView(muskelgruppe);
	//	ArrayAdapter<String> a = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.MuscleGroup));
	//	muskelgruppe.setAdapter(a);

		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			// String aus Textfeld holen  
				String value = String.valueOf(exerciseName.getText());
			//	Auslesen des Spinners 
				muscleGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				    selectedMuscleGroup = (String) parent.getItemAtPosition(pos);
				    }
				    public void onNothingSelected(AdapterView<?> parent) {
				    }
				});
			// Mapper-Methode aufrufen zum Hinzufügen einer neuen Übung
				em.add(value, selectedMuscleGroup);
			// Toast einblenden 
				toast.show();
			// ListView aktualisieren 
				exerciseListAdapter.clear();
				exerciseListAdapter.addAll(em.getAll());
				exerciseListAdapter.notifyDataSetChanged();
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
