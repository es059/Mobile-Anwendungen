package com.workout.log.dialog;

import com.example.workoutlog.R;
import com.workout.log.ExerciseAdd;
import com.workout.log.db.ExerciseMapper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class ExerciseAddDialogFragment extends DialogFragment {
	// private MuscleGroup list = new MuscleGroup();
	
	static ExerciseMapper em;
	// Konstruktor 
	public static ExerciseAddDialogFragment newInstance(Context a) {
		ExerciseAddDialogFragment exerciseAddDialogFragment = new ExerciseAddDialogFragment();
		em = new ExerciseMapper(a);
		
		return exerciseAddDialogFragment;
		
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle("Übung hinzufügen");
		alert.setMessage("Bitte geben sie den Namen Ihrer Übung hier ein:");

		// Set an EditText view to get user input 
		final EditText input = new EditText(getActivity());
		alert.setView(input);
		
		
	//	final Spinner muskelgruppe = new Spinner(getActivity());
	//	alert.setView(muskelgruppe);
	//	ArrayAdapter<String> a = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.MuscleGroup));
	//	muskelgruppe.setAdapter(a);

		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		   String value = String.valueOf(input.getText());
		   em.add(value);
		   
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
