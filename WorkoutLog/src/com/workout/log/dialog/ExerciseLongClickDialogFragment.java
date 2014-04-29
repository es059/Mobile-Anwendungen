package com.workout.log.dialog;


import com.example.workoutlog.R;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.listAdapter.ExerciseListAdapter;
import com.workout.log.listAdapter.ExerciseListWithoutSetsRepsAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.ArrayAdapter;


public class ExerciseLongClickDialogFragment extends DialogFragment{
private	ExerciseSelectionDialogListener rSDListener;
private	ExerciseMapper em;
private	int b;
private int duration;
private Toast toast;
private ExerciseListWithoutSetsRepsAdapter c;
private DialogFragment dialogFragment;
	
	
	public static ExerciseLongClickDialogFragment newInstance(int i, ExerciseListWithoutSetsRepsAdapter a) {
		ExerciseLongClickDialogFragment exerciseLongClickDialogFragment = new ExerciseLongClickDialogFragment(i, a);
		return exerciseLongClickDialogFragment;
	
		
	}
	
	public ExerciseLongClickDialogFragment(int i, ExerciseListWithoutSetsRepsAdapter a) {
		super();
		b = i;
		c = a;
		// TODO Auto-generated constructor stub
	}

	@Override 
	public Dialog onCreateDialog(Bundle savedInstanceState){
		
				
		toast = Toast.makeText(getActivity(), "Übung wurde erfolgreich gelöscht!", Toast.LENGTH_SHORT );
		em = new ExerciseMapper(getActivity());
		System.out.println(b);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Einstellungen").setItems(R.array.exerciseLongClickDialogArray, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					System.out.println("1");
					em.delete(b);
					toast.show();
					c.clear();
					c.addAll(em.getAll());
					c.notifyDataSetChanged();
					break;
				case 1:
					dialogFragment = ExerciseUpdateDialogFragment.newInstance(getActivity(), c, b);
					
					dialogFragment.show(getActivity().getFragmentManager(), "Open Exercise Settings on Long Click");
					System.out.println("2");
					break;
				case 2: 
					break;
				default:
					break;
				}
				rSDListener.onExerciseSelectionItemLongClick(ExerciseLongClickDialogFragment.this);
			}
		});
		
		return builder.create();
		
		
	}
	
	public interface ExerciseSelectionDialogListener{
		public void onExerciseSelectionItemLongClick(ExerciseLongClickDialogFragment dialog);
		}
	ExerciseSelectionDialogListener rSDListner;
	
	@Override 
	public void onAttach (Activity activity){
		super.onAttach(activity);
		try{
			rSDListener = (ExerciseSelectionDialogListener) activity;
		}catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement ExerciseLongClickDialogFragment");
		}
	}
}
