package com.workout.log.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.remic.workoutlog.R;
import com.workout.log.db.WorkoutplanMapper;

@SuppressLint("ValidFragment")
public class TrainingDayAddToWorkoutplanDialogFragment extends DialogFragment {
	
private int workoutPlanId;
private int trainingDayId;
private static WorkoutplanMapper wpMapper;
private ShowcaseView fifthShowcaseView = null;

	public static TrainingDayAddToWorkoutplanDialogFragment newInstance(Context a, int trainingDayId, int workoutPlanId) {
		TrainingDayAddToWorkoutplanDialogFragment trainingDayAddToWorkoutplan = new TrainingDayAddToWorkoutplanDialogFragment(a, trainingDayId, workoutPlanId );
		return trainingDayAddToWorkoutplan;
	}

	public TrainingDayAddToWorkoutplanDialogFragment(Context c, int a, int b) {
		super();
		wpMapper = new WorkoutplanMapper(c);
		workoutPlanId = b;
		trainingDayId = a;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(getResources().getString(R.string.TrainingDayAddToWorkoutplan));
	
		alert.setPositiveButton(getResources().getString(R.string.Save), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				/**
				 * Save the trainingday to the current workoutplan
				 */
				wpMapper.addTrainingDayToWorkoutplan(trainingDayId, workoutPlanId);
						
		        /**
		         * Show a Message that the trainingday was added to the workoutplan
		         */
		        Toast.makeText(getActivity(), getResources().getString(R.string.TrainingDayAddToWorkoutplanDialogFragment_AddSuccess), Toast.LENGTH_SHORT ).show();
		        
		        showFifthHelperOverlay();
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
	
	/**
     * ShowcaseView which points to the first entry of the listView
     */
    public void showFifthHelperOverlay(){
    	if (fifthShowcaseView == null){	
    		//ViewTarget target = new ViewTarget(trainingDayListView.getChildAt(0));
    		
    		fifthShowcaseView = new ShowcaseView.Builder(getActivity())
	    	.setTarget(Target.NONE)
		    .setContentTitle("Populate the training days with exercises")
		    .setContentText("Use the short cut or go back and use the navigation and select 'Training Days' to populate" +
		    		" the training day with exercises.")
		    .setStyle(R.style.CustomShowcaseTheme)
		    //.singleShot(45)
		    .build();
    		fifthShowcaseView.setButtonText("Manage Training Day");
    		fifthShowcaseView.refreshDrawableState();
    	}else{
    		fifthShowcaseView.refreshDrawableState();
    	}
    }
}