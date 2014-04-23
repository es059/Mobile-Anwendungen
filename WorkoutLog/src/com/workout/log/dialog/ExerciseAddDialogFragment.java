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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ExerciseAddDialogFragment extends DialogFragment {
	// private MuscleGroup list = new MuscleGroup();
	
	private ExerciseListAdapter exerciseListAdapter;
	private static ExerciseMapper em;
	private Toast toast;
	// Konstruktor 
	public static ExerciseAddDialogFragment newInstance(Context a, ExerciseListAdapter c) {
		ExerciseAddDialogFragment exerciseAddDialogFragment = new ExerciseAddDialogFragment(c);
		em = new ExerciseMapper(a);
		
		return exerciseAddDialogFragment;
		
	}
	
	public ExerciseAddDialogFragment(ExerciseListAdapter c) {
		super();
		exerciseListAdapter = c;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle("�bung hinzuf�gen");
		alert.setMessage("Bitte geben sie den Namen Ihrer �bung hier ein:");

		// Set an EditText view to get user input 
		final EditText input = new EditText(getActivity());
		alert.setView(input);
		
		toast = Toast.makeText(getActivity(), "�bung wurde erfolgreich hinzugef�gt!", Toast.LENGTH_SHORT );
		
		
	//	final Spinner muskelgruppe = new Spinner(getActivity());
	//	alert.setView(muskelgruppe);
	//	ArrayAdapter<String> a = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.MuscleGroup));
	//	muskelgruppe.setAdapter(a);

		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			// String aus Textfeld holen  
				String value = String.valueOf(input.getText());
			// Mapper-Methode aufrufen zum Hinzuf�gen einer neuen �bung
				em.add(value);
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
