package com.workout.log;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.remic.workoutlog.R;
import com.workout.log.SwipeToDelete.UndoBarController;
import com.workout.log.SwipeToDelete.UndoBarController.UndoListener;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.MuscleGroup;
import com.workout.log.data.Default;
import com.workout.log.data.ListItem;
import com.workout.log.data.MuscleGroupSectionItem;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.MuscleGroupMapper;
import com.workout.log.dialog.ExerciseAddDialogFragment;
import com.workout.log.dialog.ExerciseUpdateDialogFragment;
import com.workout.log.fragment.ExerciseSearchBarFragment;
import com.workout.log.listAdapter.DefaultAddListAdapter;
import com.workout.log.listAdapter.ExerciseListWithoutSetsRepsAdapter;

public class SelectExerciseForQuickWorkout extends Fragment implements OnItemLongClickListener, UndoListener{
	private ListView exerciseListView = null;;
	private ExerciseListWithoutSetsRepsAdapter listAdapter = null;
	
	private ExerciseMapper eMapper = null;;
	private ArrayList<Exercise> exerciseList = null;
	private UndoBarController mUndoBarController = null;
	private ExerciseSpecific exerciseSpecific = null;
	private ShowcaseView showcaseView = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.exercise_add, container, false);
		
        /**
		 * Add the searchBar fragment to the current fragment
		 */
	    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.add_searchBar, ExerciseSearchBarFragment.newInstance(this), "ActionBarSearchBarFragment");
        transaction.commit();

		setHasOptionsMenu(true);
		return view;
	}
	
	/**
	 * Load the methods to populate the ListView
	 */
	@Override
	public void onResume(){
		super.onResume();
		
		/**
		 * If more than one xml layout file uses the Layout for the UndoBar than 
		 * use this line to ensure that the reference is always correct
		 */
		eMapper = new ExerciseMapper(getActivity());
		
		mUndoBarController = null;
		
		
		/**
		 * Hide UndoController
		 */
	    if (mUndoBarController==null) mUndoBarController = new UndoBarController(getView().findViewById(R.id.undobar), this);
	    mUndoBarController.getUndoBar().setVisibility(View.GONE);
		
		exerciseListView = (ListView) getView().findViewById(R.id.add_exerciseList);
		exerciseListView.setOnItemLongClickListener(this);
		
		updateListView(eMapper.getAllExercise(), false, null);
			    
		showHelperOverlay();
	}
	
	private void showHelperOverlay(){
    	showcaseView = new ShowcaseView.Builder(getActivity())
    	.setTarget(Target.NONE)
	    .setContentTitle(getString(R.string.quickWorkoutTitle))
    	.setContentText(getString(R.string.qickWorkoutContext))
	    .setStyle(R.style.CustomShowcaseTheme)
	    .singleShot(55)
	    .build();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.workoutplan_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.menu_add) {
			this.showDialogAddFragment(null);
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Method which opens a DialogFragment to create a new exercise
	 */
	public void showDialogAddFragment(String exerciseStringName){
		ExerciseAddDialogFragment dialogFragment = ExerciseAddDialogFragment.newInstance(getActivity(), listAdapter,
				exerciseStringName);
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		Exercise e = (Exercise) arg0.getItemAtPosition(arg2);
		int currentExerciseId = e.getId();
		showDialogLongClickFragment(currentExerciseId);
		
		return true;
	}
	
	/**
	 * Method which opens a DialogFragment to update the selected item
	 * 
	 * @param i is the ID of the Exercise 
	 * @param a is the Adapter
	 */
	private void showDialogLongClickFragment(int currentExerciseId) {	
		ExerciseUpdateDialogFragment dialogFragment = ExerciseUpdateDialogFragment.newInstance(this,currentExerciseId);
		dialogFragment.show(getActivity().getFragmentManager(), "Open Exercise Update Dialog on Long Click");
	}

	/**
	 * Update the ListView with a given Exercise ArrayList
	 * 
	 * @param List the updated ArrayList
	 * @author Eric Schmidt
	 */
	@SuppressWarnings("unchecked")
	public void updateListView(ArrayList<Exercise> list, boolean searchMode, final String exerciseStringName){
		exerciseList = list;
		if (exerciseList.size() == 0){
			Default d = new Default();
			if (exerciseStringName == null || exerciseStringName.equals("")){
				d.setTitel(getString(R.string.noExercise));
			}else{
				d.setTitel(exerciseStringName + " " + getResources().getString(R.string.NotFound));
				d.setHint(getResources().getString(R.string.Add));
			}
			ArrayList<Default> ld = new ArrayList<Default>();
			ld.add(d);
			DefaultAddListAdapter l = new DefaultAddListAdapter(getActivity(), 0, ld);
			exerciseListView.setAdapter(l);
			exerciseListView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
					showDialogAddFragment(exerciseStringName);
				}	
			});
			
		}else{
			exerciseListView.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Exercise e = (Exercise) arg0.getItemAtPosition(arg2);
					openExerciseSpecific(e, -1);
					
				}
				
			});
			new BackGroundTask(exerciseListView, searchMode).execute(list);
		}
	}
	
	/**
	 * Handels the Database queries in an Async Task
	 * 
	 * @author Eric Schmidt
	 */
	public class BackGroundTask extends AsyncTask<ArrayList<Exercise>, Void, ExerciseListWithoutSetsRepsAdapter> {
	    /**
	     * Variables for the UpdateListView method
	     */
		private ArrayList<Exercise> eListMuscleGroup;
		private ArrayList<MuscleGroup> mList = null;
		private MuscleGroupMapper mMapper = null;
		
		private ListView exerciseListView;
		private boolean searchMode;

		public BackGroundTask (ListView exerciseListView, boolean searchMode){	
			this.searchMode = searchMode;
			this.exerciseListView = exerciseListView;
		}

		
		private String getMuscleGroupNameById(MuscleGroup muscleGroup){
			
			String[] muscleGroupArray = getResources().getStringArray(R.array.MuscleGroup);
			String muscleGroupName = "";
			
			switch (muscleGroup.getId()){
		    	case 1:
		    		muscleGroupName = muscleGroupArray[0]; //Back
		    		break;
		    	case 2:
		    		muscleGroupName =  muscleGroupArray[1]; //Abs
		    		break;
		    	case 3:
		    		muscleGroupName =  muscleGroupArray[3]; //Chest
		    		break;
		    	case 4:
		    		muscleGroupName =  muscleGroupArray[4]; //Legs
		    		break;
		    	case 5:
		    		muscleGroupName =  muscleGroupArray[6]; //Biceps
		    		break;
		    	case 6:
		    		muscleGroupName =  muscleGroupArray[5]; //Triceps
		    		break;
		    	case 8:
		    		muscleGroupName =  muscleGroupArray[2]; //Shoulder
		    		break;
		    	case 7:
		    		//muscleGroupName =  muscleGroupArray[7]; --> Cardio
		    		break;
			}
			return muscleGroupName;	
		}
		
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        getActivity().setProgressBarIndeterminateVisibility(true);
	    }

	    @Override
	    protected ExerciseListWithoutSetsRepsAdapter doInBackground(ArrayList<Exercise>... params) {
	    	/**
			 * Build a ArrayList containing the muscleGroup and exercises
			 */
	    	
			mMapper = new MuscleGroupMapper(getActivity());
			//Select all MuscleGroups
			mList = mMapper.getAll();
			ArrayList<ListItem> listComplete = new ArrayList<ListItem>();
			for (MuscleGroup m : mList){
				eListMuscleGroup = eMapper.getExerciseByMuscleGroup(params[0], m.getId());
				if (!eListMuscleGroup.isEmpty()){
					listComplete.add(new MuscleGroupSectionItem(getMuscleGroupNameById(m)));
					listComplete.addAll(eListMuscleGroup);
				}
			}
			listAdapter = new ExerciseListWithoutSetsRepsAdapter(getActivity(), listComplete);
			return listAdapter;	
	    }

	    @Override
	    protected void onPostExecute(ExerciseListWithoutSetsRepsAdapter result) {
	        super.onPostExecute(result);

	        if (result != null && searchMode == false){
	          	/**
	    		 * Enable animation of the ListView Items
	    		 */
	        	SwingBottomInAnimationAdapter swingButtonInAnimationAdapter = new SwingBottomInAnimationAdapter(result);
	        	swingButtonInAnimationAdapter.setAbsListView(exerciseListView);
	        	
	        	exerciseListView.setAdapter(swingButtonInAnimationAdapter);
	        }else if (result != null){
	        	exerciseListView.setAdapter(result);
	        }else exerciseListView.invalidateViews();
	        
	        getActivity().setProgressBarIndeterminateVisibility(false);
	    }
	}
	
	/**
	 * Method to open the ExerciseSpecific Activity with the selected Exercise and the current TrainingDay
	 * 
	 * @param exercise
	 * @author Eric Schmidt
	 */
	private void openExerciseSpecific (Exercise exercise, int i){
		if(getActivity().getSupportFragmentManager().findFragmentByTag("ExerciseSpecific") == null) {
		exerciseSpecific = new ExerciseSpecific();
		}
		else {
			exerciseSpecific = (ExerciseSpecific) getActivity().getSupportFragmentManager().findFragmentByTag("ExerciseSpecific");
		}
		Bundle data = new Bundle();
        data.putInt("ExerciseID",exercise.getId());
        data.putString("ExerciseName",exercise.getName());
        data.putInt("TrainingDayId",i);
        
        exerciseSpecific.setArguments(data);
        
	    FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.fragment_container, exerciseSpecific , "ExerciseSpecific");
        transaction.addToBackStack(null);
        transaction.commit();
	}

	@Override
	public void onUndo(Parcelable token) {
		// TODO Auto-generated method stub
		
	}


}
	
	
