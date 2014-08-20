package com.workout.log.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.remic.workoutlog.R;
import com.workout.log.TrainingDayAddToWorkoutplan;
import com.workout.log.db.WorkoutplanMapper;

@SuppressLint("ValidFragment")
public class TrainingDayAddToWorkoutplanDialogFragment extends DialogFragment {
	
	private int workoutPlanId;
	private int trainingDayId;
	private static WorkoutplanMapper wpMapper;
	private ShowcaseView fifthShowcaseView = null;
	private Context context;
	private TrainingDayAddToWorkoutplan trainingDayAddToWorkoutplan = null;
	
	public static TrainingDayAddToWorkoutplanDialogFragment newInstance(Context a, int trainingDayId, int workoutPlanId) {
		TrainingDayAddToWorkoutplanDialogFragment trainingDayAddToWorkoutplan = new TrainingDayAddToWorkoutplanDialogFragment(a, trainingDayId, workoutPlanId );
		return trainingDayAddToWorkoutplan;
	}

	public TrainingDayAddToWorkoutplanDialogFragment(Context context, int trainingDayId, int workoutplanId) {
		super();
		this.context = context;
		wpMapper = new WorkoutplanMapper(context);
		this.workoutPlanId = workoutplanId;
		this.trainingDayId = trainingDayId;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(getResources().getString(R.string.TrainingDayAddToWorkoutplan));
		trainingDayAddToWorkoutplan = (TrainingDayAddToWorkoutplan) ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag("TrainingDayAddToWorkoutplan");
		
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
		        //Toast.makeText(getActivity(), getResources().getString(R.string.TrainingDayAddToWorkoutplanDialogFragment_AddSuccess), Toast.LENGTH_SHORT ).show();
		        
		        /**
		         * Show CustomToast
		         */
				if (isFirstTime()){
					writeSharedPrefs();
					trainingDayAddToWorkoutplan.getCustomToast().showUndoBar(false, getString(R.string.TrainingDayAddDialogFragment_CustomToast), false);
				}else{
					trainingDayAddToWorkoutplan.getCustomToast().showUndoBar(false, getString(R.string.TrainingDayAddDialogFragment_CustomToast), true);
				}
				 /**
		         * Show ShowcaseView
		         */
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
    		ViewTarget target = new ViewTarget(trainingDayAddToWorkoutplan.getCustomToast().getButton());
    		
    		fifthShowcaseView = new ShowcaseView.Builder(getActivity())
	    	.setTarget(target)
		    .setContentTitle(getString(R.string.fifthShowcaseViewTitle))
		    .setContentText(getString(R.string.fifthShowcaseViewContext))
		    .setStyle(R.style.CustomShowcaseTheme)
		    //.singleShot(45)
		    .build();
    		
    		fifthShowcaseView.hideButton();
    		fifthShowcaseView.refreshDrawableState();
   		
    		/* 	RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) fifthShowcaseView.getButton().getLayoutParams();
    		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
    		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
    		fifthShowcaseView.setButtonPosition(layoutParams);*/
    		
    	}else{
    		fifthShowcaseView.refreshDrawableState();
    	}
    }
    
    /**
     * Write in SharedPrefs
     */
    private void writeSharedPrefs(){
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("Toast", 45);
        editor.commit();
    }
    
    /**
     * Read in SharedPres
     */
    private boolean isFirstTime(){
    	SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
    	int retrievedValue = sharedPref.getInt("Toast", -1);
    	if (retrievedValue == -1) return true;
    	return false;
    }
    
    public ShowcaseView getShowcaseView(){
    	return fifthShowcaseView;
    }

}