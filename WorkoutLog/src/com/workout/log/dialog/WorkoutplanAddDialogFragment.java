package com.workout.log.dialog;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.workout.log.ManageWorkoutplan;
import com.workout.log.bo.Workoutplan;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.listAdapter.ExerciseListWithoutSetsRepsAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class WorkoutplanAddDialogFragment extends DialogFragment {
	
	private WorkoutplanMapper wpMapper;
	int tdID;
	
	public static WorkoutplanAddDialogFragment newInstance(Context a, ArrayList<Workoutplan> workoutplanList, int tdID) {
		WorkoutplanAddDialogFragment workoutplanAddDialogFragment = new WorkoutplanAddDialogFragment(a, workoutplanList, tdID);
		
		
		return workoutplanAddDialogFragment;
		
	}
	
	public WorkoutplanAddDialogFragment(Context a, ArrayList<Workoutplan> workoutplanList,int tdID) {
		super();
		wpMapper = new WorkoutplanMapper(a);
		this.tdID = tdID;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		
		
		alert.setTitle("Trainingsplan hinzufügen");
		final EditText input = new EditText(getActivity());
		input.setHint("Name");
		alert.setView(input);
		
		
		
		
		
		
		
	

		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			wpMapper.addWP(input.getText());
			Intent intent = new Intent();
			intent.putExtra("WorkoutplanId", tdID);
			intent.setClass(getActivity(), ManageWorkoutplan.class);
			startActivity(intent);
			
			
			
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



