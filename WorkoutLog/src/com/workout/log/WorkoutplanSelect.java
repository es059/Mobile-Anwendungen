package com.workout.log;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.workout.log.data.Workoutplan;
import com.workout.log.dialog.ExerciseLongClickDialogFragment;
import com.workout.log.dialog.ExerciseLongClickDialogFragment.ExerciseSelectionDialogListener;
import com.workout.log.listAdapter.WorkoutplanListAdapter;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class WorkoutplanSelect extends Activity  implements OnItemLongClickListener, OnItemClickListener, ExerciseSelectionDialogListener{
private ListView workoutplanListView;
private View lastView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workoutplan_select);
		
		workoutplanListView = (ListView) findViewById(R.id.workoutplanList);
		
		//Dummy Daten mit Hinzufügen-Eintrag
		ArrayList<Workoutplan> workoutplanList = new ArrayList<Workoutplan>();
		Workoutplan p1 = new Workoutplan();
		p1.setName("Trainingsplan 1");
		workoutplanList.add(p1);
		Workoutplan p2 = new Workoutplan();
		p2.setName("Trainingsplan 1");
		workoutplanList.add(p2);
		WorkoutplanListAdapter workoutplanAdapter = new WorkoutplanListAdapter(this,0,workoutplanList);
		
		workoutplanListView.setAdapter(workoutplanAdapter);
		workoutplanListView.setOnItemClickListener(this);
		workoutplanListView.setOnItemLongClickListener(this);
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
	          long id) {
	       if(lastView==null){
	    	   view.setBackgroundResource(R.drawable.actionbar_orange);

	            lastView=view;
	            }
	            else{
	            lastView.setBackgroundColor(Color.WHITE); 
	            view.setBackgroundResource(R.drawable.actionbar_orange);

	            lastView=view;
	            }
	}
	
	public void showDialogLongClickFragment(){
		DialogFragment dialogFragment = ExerciseLongClickDialogFragment.newInstance();
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		this.showDialogLongClickFragment();
		return true;
	}

	@Override
	public void onExerciseSelectionItemLongClick(
			ExerciseLongClickDialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}
}
