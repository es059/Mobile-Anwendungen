package com.workout.log.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.workoutlog.R;
import com.workout.log.SwipeToDelete.UndoBarController;
import com.workout.log.SwipeToDelete.UndoItem;
import com.workout.log.bo.Workoutplan;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.fragment.ActionBarWorkoutPlanPickerFragment;

@SuppressLint("ValidFragment")
public class WorkoutplanUpdateDialogFragment extends DialogFragment{
	private static WorkoutplanMapper wMapper;
	private int workoutPlanId;
	private UndoBarController mUndoBarController = null;
	
	private Fragment wokoutplanPicker = null;
	private FragmentTransaction ft = null;
	private FragmentManager fm = null;
	private View manageWorkoutplanView = null;
	
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
		fm = getActivity().getSupportFragmentManager();
		wokoutplanPicker = fm.findFragmentByTag("ActionBarWorkoutPlanPickerFragment");
		manageWorkoutplanView = fm.findFragmentByTag("ManageWorkoutplan").getView();
		
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialogfragment_workoutplan_edit, null);
		
		alert.setTitle("Trainingsplan ändern");
		// Set an TextView view to view the InformationText
		TextView informationText = (TextView) view.findViewById(R.id.TextView_Information);
		informationText.setText("Bitte geben Sie den neuen Namen des Trainingsplans ein:");
		
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
			/**
			 *  Get String from textfield
			 */
			String value = String.valueOf(workoutplanName.getText());
			if(!value.isEmpty()){
				w.setName(value);
				/**
				 * Update Workoutplan
				 */
				wMapper.update(w);
				Toast.makeText(getActivity(), "Trainingstag wurde erfolgreich geändert!", Toast.LENGTH_SHORT ).show();

				updateFragment();			
			}else{
				Toast.makeText(getActivity(), "Bitte einen Namen angeben", Toast.LENGTH_SHORT ).show();
			}
		  }
		});

		alert.setNeutralButton("Löschen", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				/**
				 * Add Additional attributes to the workoutplan object to ensure that the
				 * undo method is working
				 */
				Workoutplan workoutplan = w;
				workoutplan = wMapper.addAdditionalInformation(workoutplan);
				
				wMapper.delete(workoutplan);
				wMapper.deleteWorkoutPlanFromTrainingDay(workoutplan);
				
				Workoutplan[] items= new Workoutplan[1];;
				items[0]=workoutplan;

				if (mUndoBarController==null) mUndoBarController = new UndoBarController(manageWorkoutplanView.findViewById(R.id.undobar), new UndoBarController.UndoListener(){
					/**
					 * Handels the click on the Undo Button. Revive the Data that was deleted in the
					 * onDismiss function
					 * 
					 * @author Eric Schmidt
					 */
					@Override
					public void onUndo(Parcelable token) {
						/**
						 * Restore items in lists (use reverseSortedOrder)
						 */
						if (token != null) {
							/**
							 * Retrieve data from token
							 */
							UndoItem itemRetrieve = (UndoItem) token;
							Workoutplan[] items = (Workoutplan[]) itemRetrieve.items;

							if (items != null) {
								for (int i = 0; i >= 0; i--) {
									Workoutplan item = items[i];
									
									wMapper.add(item);
									for (Integer trainingDayId : item.getTrainingDayIdList()){
										wMapper.addTrainingDayToWorkoutplan(trainingDayId, item.getId());
									}
								}
							}
							updateFragment();
						}
					}
				});
				
				Workoutplan w = ((ActionBarWorkoutPlanPickerFragment) getActivity().getSupportFragmentManager().
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
				decreaseCurrenListId(wokoutplanPicker);
				updateFragment();
				
				/**
				 * Show the Undo Layout
				 */
				UndoItem itemUndo=new UndoItem(items,null);
				String messageUndoBar = "Item gelöscht";
        		 
		        mUndoBarController.showUndoBar(false,messageUndoBar,itemUndo);
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
	
	/**
	 * Refresh the Fragment to show changes
	 */
	public void updateFragment(){
		ft = fm.beginTransaction();
		
		ft.detach(wokoutplanPicker);
		ft.attach(wokoutplanPicker);
		ft.commit();
	}
}
