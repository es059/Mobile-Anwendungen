package com.workout.log.dialog;

import com.workout.log.db.ExerciseMapper;
import com.workout.log.listAdapter.ExerciseListAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class ExerciseUpdateDialogFragment extends DialogFragment {
	private ExerciseListAdapter exerciseListAdapter;
	private static ExerciseMapper em;
	private Toast toast;
	private int b;
	// Konstruktor 
	public static ExerciseUpdateDialogFragment newInstance(Context a, ExerciseListAdapter c, int i) {
		ExerciseUpdateDialogFragment ExerciseUpdateDialogFragment = new ExerciseUpdateDialogFragment(c, i);
		em = new ExerciseMapper(a);
		
		return ExerciseUpdateDialogFragment;
		
	}
	
	public ExerciseUpdateDialogFragment(ExerciseListAdapter c, int i) {
		super();
		exerciseListAdapter = c;
		b = i;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle("Übung bearbeiten");
		alert.setMessage("Bitte geben sie den verbesserten Namen Ihrer Übung hier ein:");

		// Set an EditText view to get user input 
		final EditText input = new EditText(getActivity());
		alert.setView(input);
		
		toast = Toast.makeText(getActivity(), "Übung wurde erfolgreich geändert!", Toast.LENGTH_SHORT );
		
		
	//	final Spinner muskelgruppe = new Spinner(getActivity());
	//	alert.setView(muskelgruppe);
	//	ArrayAdapter<String> a = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.MuscleGroup));
	//	muskelgruppe.setAdapter(a);

		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			// String aus Textfeld holen  
				String value = String.valueOf(input.getText());
			// Mapper-Methode aufrufen zum Hinzufügen einer neuen Übung
				em.update(b, value);
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
