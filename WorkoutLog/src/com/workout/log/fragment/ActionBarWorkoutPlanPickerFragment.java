package com.workout.log.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.remic.workoutlog.R;
import com.workout.log.ExerciseOverview;
import com.workout.log.ManageWorkoutplan;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.dialog.WorkoutplanAddDialogFragment;
import com.workout.log.dialog.WorkoutplanUpdateDialogFragment;

public class ActionBarWorkoutPlanPickerFragment extends Fragment implements OnClickListener  {
	private ImageButton previousButton;
	private ImageButton nextButton;
	private TextView workoutplanTextView;
	private TextView workoutplanDateView;
	
	private ArrayList<Workoutplan> workoutplanList;
	private ArrayList<TrainingDay> trainingDayList;
	
	private WorkoutplanMapper wpMapper;
	private TrainingDayMapper tdMapper;
	private ManageWorkoutplan manageWorkoutplan; 
	
	private static int currentListId = 0;
	private ShowcaseView showcaseView = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		/**
		 * Reference ExerciseOverview Layout and set ListView 
		 */
		View view = inflater.inflate(R.layout.actionbar_workout_plan_picker_fragment, container,false);
		
		wpMapper = new WorkoutplanMapper(getActivity());
		tdMapper = new TrainingDayMapper(getActivity());
		
		return view;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onResume(){
		super.onResume();
		
		manageWorkoutplan  = (ManageWorkoutplan) getActivity().getSupportFragmentManager().findFragmentByTag("ManageWorkoutplan");
		
		if (manageWorkoutplan == null) {
			FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
			transaction.remove(this);
			transaction.commit();	
		}else{
			previousButton = (ImageButton) getView().findViewById(R.id.Previous);
			nextButton = (ImageButton) getView().findViewById(R.id.Next);
			
			workoutplanTextView = (TextView) getView().findViewById(R.id.workoutplanPicker);
			workoutplanDateView = (TextView) getView().findViewById(R.id.workoutplanPickerDate);
	
			workoutplanList = new ArrayList<Workoutplan>();
			trainingDayList = new ArrayList<TrainingDay>();
			
			manageWorkoutplan.updateListView(null);
			
			workoutplanList = wpMapper.getAll();
			if (workoutplanList.size() != 0){
				workoutplanTextView.setHint("");
				workoutplanDateView.setVisibility(View.VISIBLE);
				
				setCurrentIdByWorkoutplanId(wpMapper.getCurrent().getId());
				trainingDayList = tdMapper.getAllTrainingDaysFromWorkoutplan(workoutplanList.get(currentListId).getId());
				
				workoutplanTextView.setText(workoutplanList.get(currentListId).getName());
				
				SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
				workoutplanDateView.setText(sp.format(workoutplanList.get(currentListId).getTimeStamp()));
				
				manageWorkoutplan.setWorkoutplanId(workoutplanList.get(currentListId).getId());
				
				/**
				 * ClickListener handles the Update/delete function of the workoutplan
				 */
				RelativeLayout workoutplanInformation = (RelativeLayout) getView().findViewById(R.id.workoutplan_Information);
				workoutplanInformation.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						WorkoutplanUpdateDialogFragment dialogFragment = WorkoutplanUpdateDialogFragment.newInstance(getActivity(), workoutplanList.get(currentListId).getId());
						dialogFragment.show(getActivity().getSupportFragmentManager(), "Open Exercise Settings on Long Click");			
					}	
				});
				
			}else{
				nextButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_add));
				workoutplanTextView.setHint(getString(R.string.noWorkoutplan));
				workoutplanDateView.setVisibility(View.GONE);
			}
			
			previousButton.setOnClickListener(this);
			nextButton.setOnClickListener(this);
			
			/**
			 * Ensure that the previousButton is invisible, the add Button is showing if 
			 * the currentId is the last index in the workoutplanList and that the 
			 * current trainingDayList is added to the ListView
			 */
			if(currentListId == 0)previousButton.setVisibility(View.INVISIBLE); 
			if(workoutplanList.size() <= currentListId + 1){
				/**
				 * Show the ShowcaseView
				 */
				showHelperOverlay();
				nextButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_add));
			}
		
			manageWorkoutplan.updateListView(trainingDayList);
			
			
			/**
			 * Refresh the ShareIntent
			 */
			manageWorkoutplan.createCurrentSqlDump();
		}
	}
		
    /**
     * ShowcaseView which points to the + Symbol 
     */
    private void showHelperOverlay(){
    	if (showcaseView == null){    		
	    	ViewTarget target = new ViewTarget(R.id.Next, this);
	    	
			showcaseView = new ShowcaseView.Builder(getActivity())
		    .setTarget(target)
		    .setContentTitle(getString(R.string.firstShowcaseViewTitle))
		    .setContentText(getString(R.string.firstShowcaseViewContext))
		    .setStyle(R.style.CustomShowcaseTheme)
		    .singleShot(42)
		    .build();	
    	}else{
    		showcaseView.refreshDrawableState();
    	}
    }
    
	public ShowcaseView getFirstShowcaseView() {
		return showcaseView;
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.Next) {
			/**
			 * Hide the ShowcaseView if visible
			 */
			if(showcaseView != null && showcaseView.isShown()) showcaseView.hide();
			
			if(workoutplanList.size() <= currentListId +1) {
				WorkoutplanAddDialogFragment dialogFragment = WorkoutplanAddDialogFragment.newInstance(getActivity(), workoutplanList);
				dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
				if(workoutplanList.size()>1) previousButton.setVisibility(View.VISIBLE);	
			}
			else {	
				if(currentListId == 0) {
					previousButton.setVisibility(View.VISIBLE);
				}
				workoutplanTextView.setText(workoutplanList.get(currentListId +1).getName());
				SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
				workoutplanDateView.setText(sp.format(workoutplanList.get(currentListId +1).getTimeStamp()));
				
				manageWorkoutplan.updateListView(tdMapper.getAllTrainingDaysFromWorkoutplan(workoutplanList.get(currentListId +1).getId()));
				manageWorkoutplan.setWorkoutplanId(workoutplanList.get(currentListId +1).getId());
				
				wpMapper.setCurrent(workoutplanList.get(currentListId +1).getId());
				((ExerciseOverview) getActivity().getSupportFragmentManager().findFragmentByTag("ExerciseOverview")).setTrainingDay(-1);
				
				Toast.makeText(getActivity(), workoutplanList.get(currentListId +1).getName() + " ist nun aktiv!", Toast.LENGTH_SHORT ).show();
				currentListId += 1;
				if(workoutplanList.size() <= currentListId +1) {
					nextButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_add));
				}
				
				/**
				 * Refresh the ShareIntent
				 */
				manageWorkoutplan.createCurrentSqlDump();
			}
		} else if (id == R.id.Previous) {
			if(workoutplanList.size() <= currentListId +1) {
				nextButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_navigation_next_item));
			}
			workoutplanTextView.setText(workoutplanList.get(currentListId -1).getName());
			SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
			workoutplanDateView.setText(sp.format(workoutplanList.get(currentListId -1).getTimeStamp()));
			manageWorkoutplan.updateListView(tdMapper.getAllTrainingDaysFromWorkoutplan(workoutplanList.get(currentListId -1).getId()));
			manageWorkoutplan.setWorkoutplanId(workoutplanList.get(currentListId - 1).getId());
			
			wpMapper.setCurrent(workoutplanList.get(currentListId -1).getId());
			((ExerciseOverview) getActivity().getSupportFragmentManager().findFragmentByTag("ExerciseOverview")).setTrainingDay(-1);
			
			Toast.makeText(getActivity(), workoutplanList.get(currentListId -1).getName() + " ist nun aktiv!", Toast.LENGTH_SHORT ).show();
			currentListId -= 1;
			if(currentListId == 0) {
				previousButton.setVisibility(View.INVISIBLE);
			}
			
			/**
			 * Refresh the ShareIntent
			 */
			manageWorkoutplan.createCurrentSqlDump();
		}	
	}
	
	public void setCurrentIdByWorkoutplanId(int workoutplanId) {
	  for(int i = 0; i <= workoutplanList.size() - 1; i++) {
		if(workoutplanList.get(i).getId() == workoutplanId) {
			currentListId = i;
		}
	  }
	}

	public static void setCurrentListId(int currentListId) {
		ActionBarWorkoutPlanPickerFragment.currentListId = currentListId;
	}	
	
	public static int getCurrentListId() {
		return currentListId;
	}
	
	/**
	 * Returns the current Workoutplan
	 * 
	 * @return the current Workoutplan
	 * @author Eric Schmidt
	 */
	public Workoutplan getCurrentWorkoutplan(){
		workoutplanList = wpMapper.getAll();
		if (workoutplanList != null && workoutplanList.size() != 0){
			return workoutplanList.get(currentListId);
		}
		return null;
	}
	
	/**
	 * Returns the previous workoutplan to set the current workoutplan
	 * if the one used now is deleted
	 * 
	 * @return
	 */
	public Workoutplan getPreviousWorkoutplan(){
		if (workoutplanList.size() > 1){
			if (currentListId != 0) return workoutplanList.get(currentListId -1);
			else return workoutplanList.get(currentListId +1);
		}
		return null;
	}
}
