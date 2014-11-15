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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

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
	
	private ShowcaseView showcaseView = null;
	
	private Context context;
	private TrainingDayAddToWorkoutplan trainingDayAddToWorkoutplan = null;
	
	public static TrainingDayAddToWorkoutplanDialogFragment newInstance(Context a, int trainingDayId, int workoutPlanId) {
		TrainingDayAddToWorkoutplanDialogFragment trainingDayAddToWorkoutplan = new TrainingDayAddToWorkoutplanDialogFragment(a, trainingDayId, workoutPlanId );
		return trainingDayAddToWorkoutplan;
	}

	public TrainingDayAddToWorkoutplanDialogFragment(){
		
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
				showHelperOverlay();
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
    public void showHelperOverlay(){
    	if (showcaseView == null){	
    		ViewTarget target = new ViewTarget(trainingDayAddToWorkoutplan.getCustomToast().getButton());
    		
    		showcaseView = new ShowcaseView.Builder(getActivity())
	    	.setTarget(target)
		    .setContentTitle(getString(R.string.fifthShowcaseViewTitle))
		    .setContentText(getString(R.string.fifthShowcaseViewContext))
		    .setStyle(R.style.CustomShowcaseTheme)
		    .singleShot(99)
		    .build();
 
   		
    		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
    		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
    		layoutParams.setMargins(30, 0, 0, 30);
    		showcaseView.setButtonPosition(layoutParams);
    		showcaseView.getButton().setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					trainingDayAddToWorkoutplan.getCustomToast().hideUndoBar(true);
					showcaseView.hide();
				}
    		});
    		
    	}else{
    		showcaseView.refreshDrawableState();
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
    	return showcaseView;
    }

}