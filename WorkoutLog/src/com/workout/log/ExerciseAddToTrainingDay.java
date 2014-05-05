package com.workout.log;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.example.workoutlog.R.id;
import com.example.workoutlog.R.layout;
import com.example.workoutlog.R.menu;
import com.workout.log.bo.Exercise;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.dialog.ExerciseAddDialogFragment;
import com.workout.log.dialog.ExerciseClickDialogFragment;
import com.workout.log.dialog.ExerciseLongClickDialogFragment;
import com.workout.log.dialog.ExerciseLongClickDialogFragment.ExerciseSelectionDialogListener;
import com.workout.log.listAdapter.ExerciseListAdapter;
import com.workout.log.listAdapter.ExerciseListWithoutSetsRepsAdapter;
import com.workout.log.listAdapter.SwipeDismissListViewTouchListener;

import android.app.Activity;
import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.os.Build;

public class ExerciseAddToTrainingDay extends Activity implements ExerciseSelectionDialogListener, OnItemLongClickListener, OnItemClickListener {
	private EditText search;
    private ArrayList<Exercise> List;
    private ExerciseListWithoutSetsRepsAdapter a;
    private ListView exerciseListView;
    private DialogFragment dialogFragment;
    private  ExerciseMapper em = new ExerciseMapper(this);
	private  WorkoutplanMapper m = new WorkoutplanMapper(this);
	private int trainingDayId;
	private int exerciseId;
	private Bundle intent;
	private Toast toast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exercise_add);
		
		final Toast toast = Toast.makeText(this, "Übung wurde gelöscht!", Toast.LENGTH_SHORT );
		// Übergabe der Trainingtags-ID über das Intent 
		intent = getIntent().getExtras();
		trainingDayId = intent.getInt("trainingDayId");
		
		search = (EditText) findViewById(R.id.trainingDay_subject);
		
		exerciseListView = (ListView) findViewById(R.id.add_exerciseList);
		List = new ArrayList<Exercise>();
		List = em.getAll();
		a = new ExerciseListWithoutSetsRepsAdapter(this , R.layout.listview_exercise_without_repssets, List);
		exerciseListView.setAdapter(a);
		exerciseListView.setOnItemLongClickListener(this);
		exerciseListView.setOnItemClickListener(this);
		search.addTextChangedListener(new TextWatcher() 
		  {
	          
	          public void beforeTextChanged(CharSequence s, int start, int count, int after)
	          {
	                    
	          }
	 
	          public void onTextChanged(CharSequence s, int start, int before, int count)
	          {
	        	 
	          }

	          public void afterTextChanged(Editable s)
	          {
	              
	        	  a.clear();
	        	  List =   em.searchKeyString(String.valueOf(s));
	        	  
	        	  
	        	  a.addAll(List);
	        	  a.notifyDataSetChanged();
	        	  exerciseListView.invalidateViews();
	        	  System.out.println(s);
	          }
	  });
		
		/**
		 * Implementierung der Swipe to dissmiss-funktion 
		 */
		 // Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                		exerciseListView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                	Exercise e = (Exercise) exerciseListView.getItemAtPosition(position);
                            		int i = e.getId();
                                	em.delete(i);
                                	a.remove(a.getItem(position));
                                	toast.show();
                                    
                                    
                                }
                                a.notifyDataSetChanged();
                            }
                        });
        exerciseListView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        exerciseListView.setOnScrollListener(touchListener.makeScrollListener());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.workoutplan_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_add) {
			this.showDialogAddFragment();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showDialogAddFragment(){
		DialogFragment dialogFragment = ExerciseAddDialogFragment.newInstance(this, a);
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
	}

	@Override
	public void onExerciseSelectionItemLongClick(
			ExerciseLongClickDialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		
		Exercise e = (Exercise) arg0.getItemAtPosition(arg2);
		int i = e.getId();
		showDialogLongClickFragment(i, a);
		
		
		
		return false;
	}
	public void listviewactual() {
		 a.clear();
		 a.addAll(em.getAll());
		 a.notifyDataSetChanged();
		 exerciseListView.invalidateViews();
}
	private void showDialogLongClickFragment(int i, ExerciseListWithoutSetsRepsAdapter a) {
		
		dialogFragment = ExerciseLongClickDialogFragment.newInstance(i, a);
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Exercise e = (Exercise) arg0.getItemAtPosition(arg2);
		exerciseId = e.getId();
		showDialogClickFragment(this, trainingDayId, exerciseId);
		
	}
	private void showDialogClickFragment(Context a, int  trainingDayId, int exerciseId) {
		
		dialogFragment = ExerciseClickDialogFragment.newInstance(a, trainingDayId, exerciseId);
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
		
	}


}
