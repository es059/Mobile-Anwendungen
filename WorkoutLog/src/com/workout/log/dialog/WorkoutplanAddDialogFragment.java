package com.workout.log.dialog;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.remic.workoutlog.R;
import com.workout.log.ManageWorkoutplan;
import com.workout.log.bo.Workoutplan;
import com.workout.log.db.WorkoutplanMapper;

@SuppressLint("ValidFragment")
public class WorkoutplanAddDialogFragment extends DialogFragment {
	
	private WorkoutplanMapper wpMapper;
	
	public static WorkoutplanAddDialogFragment newInstance(Context a, ArrayList<Workoutplan> workoutplanList) {
		WorkoutplanAddDialogFragment workoutplanAddDialogFragment = new WorkoutplanAddDialogFragment(a, workoutplanList);
		
		return workoutplanAddDialogFragment;	
	}
	
	public WorkoutplanAddDialogFragment(){
		
	}
	
	private WorkoutplanAddDialogFragment(Context a, ArrayList<Workoutplan> workoutplanList) {
		super();
		wpMapper = new WorkoutplanMapper(a);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialogfragment_workoutplan_edit, null);
		
		alert.setTitle(getResources().getString(R.string.WorkoutplanAddDialogFragment_WorkoutplanAdd));
		
		// Set an EditText view to get user input 
		final EditText workoutplanName = (EditText) view.findViewById(R.id.EditText_WorkoutplanName);
		
		alert.setView(view);

		alert.setPositiveButton(getResources().getString(R.string.Save),new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
			Workoutplan w = new Workoutplan();
			w.setName(workoutplanName.getText().toString());
			wpMapper.add(w);
			
			/**
			 * Refresh the Fragment to show changes
			 */
			Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("ActionBarWorkoutPlanPickerFragment");
			FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
			ft.detach(fragment);
			ft.attach(fragment);
			ft.commit();	
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



