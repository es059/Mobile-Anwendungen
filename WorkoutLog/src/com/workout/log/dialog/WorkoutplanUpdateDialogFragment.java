package com.workout.log.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.workoutlog.R;
import com.workout.log.bo.Workoutplan;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.fragment.ActionBarWorkoutPlanPickerFragment;

@SuppressLint("ValidFragment")
public class WorkoutplanUpdateDialogFragment extends DialogFragment {
	private static WorkoutplanMapper wMapper;
	private int workoutPlanId;
	
	public static WorkoutplanUpdateDialogFragment newInstance(Context context, int workoutPlanId) {
		WorkoutplanUpdateDialogFragment ExerciseUpdateDialogFragment = new WorkoutplanUpdateDialogFragment(context, workoutPlanId);
		return ExerciseUpdateDialogFragment;	
	}
	
	public WorkoutplanUpdateDialogFragment(Context context, int workoutPlanId) {
		super();
		wMapper = new WorkoutplanMapper(context);
		this.workoutPlanId = workoutPlanId;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialogfragment_workoutplan_edit, null);
		
		alert.setTitle("Trainingsplan ändern");
		// Set an TextView view to view the InformationText
		TextView informationText = (TextView) view.findViewById(R.id.TextView_Information);
		informationText.setText("Bitte geben Sie einen neuen Namen des Trainingsplans ein:");
		
		// Set an EditText view to get user input 
		final EditText workoutplanName = (EditText) view.findViewById(R.id.EditText_WorkoutplanName);
		
		/**
		 * Load current Workoutplan and set the value as default
		 */
		final Workoutplan  w = wMapper.getWorkoutPlanById(workoutPlanId);
		workoutplanName.setText(w.getName());
		
		alert.setView(view);
		
		alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
			// String aus Textfeld holen  
			String value = String.valueOf(workoutplanName.getText());
			if(!value.isEmpty()){
				w.setName(value);
				// Mapper-Methode aufrufen zum Hinzufügen einer neuen Übung
				wMapper.update(w);
				// Toast einblenden 
				Toast.makeText(getActivity(), "Trainingstag wurde erfolgreich geändert!", Toast.LENGTH_SHORT ).show();

				/**
				 * Refresh the Fragment to show changes
				 */
				Fragment fragment = getActivity().getFragmentManager().findFragmentByTag("ActionBarWorkoutPlanPickerFragment");
				FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
				ft.detach(fragment);
				ft.attach(fragment);
				ft.commit();				
			}else{
				Toast.makeText(getActivity(), "Bitte einen Namen angeben", Toast.LENGTH_SHORT ).show();
			}
		  }
		});

		alert.setNeutralButton("Löschen", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				wMapper.delete(w);
				wMapper.deleteWorkoutPlanFromTrainingDay(w);
				
				Workoutplan w = ((ActionBarWorkoutPlanPickerFragment) getActivity().getFragmentManager().
						findFragmentByTag("ActionBarWorkoutPlanPickerFragment")).getPreviousWorkoutplan();
				
				/**
				 * if w equals null then the last workoutplan was deleted and there is no currentWorkoutplan 
				 * to be set
				 */
				if(w != null){
					wMapper.setCurrent(w.getId());
				}
				/**
				 * Refresh the Fragment to show changes. Decrease the currentListId count by 1 to ensure that
				 * no outOfIndex occurs
				 */
				Fragment fragment = getActivity().getFragmentManager().findFragmentByTag("ActionBarWorkoutPlanPickerFragment");
				decreaseCurrenListId(fragment);
				FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
				ft.detach(fragment);
				ft.attach(fragment);
				ft.commit();
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
	
	@SuppressWarnings("static-access")
	public static void decreaseCurrenListId(Fragment fragment){
		ActionBarWorkoutPlanPickerFragment actionBarWorkoutPlanPickerFragment = (ActionBarWorkoutPlanPickerFragment) fragment;
		int currentListId = actionBarWorkoutPlanPickerFragment.getCurrentListId();
		/**
		 * If the currentId is 0 then there is no need to decrease it further
		 */
		if(currentListId != 0) actionBarWorkoutPlanPickerFragment.setCurrentListId(currentListId-1);
	}
}
