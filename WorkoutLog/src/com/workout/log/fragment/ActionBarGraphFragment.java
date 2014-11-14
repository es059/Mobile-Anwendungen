package com.workout.log.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.RectangleTarget;
import com.github.amlcurran.showcaseview.targets.Target;
import com.remic.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.listAdapter.ExerciseSpinnerListAdapter;

/**
 * Fragment class to handel the Spinner view in the GraphActivity
 * 
 * @author Eric Schmidt
 *
 */
public class ActionBarGraphFragment  extends Fragment implements OnItemSelectedListener{
	
	private Spinner exerciseSpinner;
	private LineGraphFragment lineGraphFragment;
	
	private ShowcaseView showcaseView1 = null;
	private ShowcaseView showcaseView2 = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.actionbar_graph_fragment, container,false);
		return view;
	}

	@Override
	public void onResume(){
		super.onResume();
		exerciseSpinner = (Spinner) getView().findViewById(R.id.graph_exercise);
		lineGraphFragment = (LineGraphFragment) getActivity().getSupportFragmentManager().findFragmentByTag("LineGraphFragment");
		
		ExerciseMapper eMapper = new ExerciseMapper(getActivity());
		ArrayList<Exercise> exerciseList = eMapper.getAllExercise();
		
		ExerciseSpinnerListAdapter adapter = new ExerciseSpinnerListAdapter(getActivity(),0, exerciseList);
		exerciseSpinner.setAdapter(adapter);
		exerciseSpinner.setOnItemSelectedListener(this);
		
		showHelperOverlay1();
	}
	
	/**
     * ShowcaseView which points to the first entry of the listView
     */
    public void showHelperOverlay1(){
    	if (showcaseView1 == null){
    		RectangleTarget target = new RectangleTarget(exerciseSpinner);
	    	
	    	showcaseView1 = new ShowcaseView.Builder(getActivity())
	    	.setTarget(target)
		    .setContentTitle(getString(R.string.eigthShowcaseViewTitle))
	    	.setContentText(getString(R.string.eigthShowcaseViewContext))
		    .setStyle(R.style.CustomShowcaseTheme)
		    .singleShot(50)
		    .build();
	    	
    	}else{
    		showcaseView1.refreshDrawableState();
    	}
    }
    
	/**
     * ShowcaseView which points to the first entry of the listView
     */
    public void showHelperOverlay2(){
    	if (showcaseView2 == null){	    	
    		showcaseView2 = new ShowcaseView.Builder(getActivity())
	    	.setTarget(Target.NONE)
		    .setContentTitle(getString(R.string.ninthShowcaseViewTitle))
	    	.setContentText(getString(R.string.ninthShowcaseViewContext))
		    .setStyle(R.style.CustomShowcaseTheme)
		    .singleShot(51)
		    .build();
	    	
    	}else{
    		showcaseView2.refreshDrawableState();
    	}
    }
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		if (showcaseView1.isShown())showcaseView1.hide();
		
		if (lineGraphFragment == null){
			lineGraphFragment = (LineGraphFragment) getActivity().getSupportFragmentManager().findFragmentByTag("LineGraphFragment");
		}
		lineGraphFragment.updateGraph((Exercise)parent.getItemAtPosition(pos));
		showHelperOverlay2();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}