package com.workout.log;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.example.workoutlog.R.id;
import com.example.workoutlog.R.layout;
import com.example.workoutlog.R.menu;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.dialog.TrainingDayAddToWorkoutplanDialogFragment;
import com.workout.log.fragment.ActionBarWorkoutPlanPickerFragment;
import com.workout.log.listAdapter.SwipeDismissListViewTouchListener;
import com.workout.log.listAdapter.TrainingDayListAdapter;
import com.workout.log.listAdapter.WorkoutplanListAdapter;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Build;

public class ManageWorkoutplan extends Activity implements OnItemClickListener {
	
	private ImageButton pre;
	private ImageButton next;
	private TextView trainingDay;
	private ListView trainingDayListView;
	private DynamicListView listView;
	private ArrayList<Workoutplan> workoutplanList;
	private ArrayList<TrainingDay> trainingDayList;
	
	private TrainingDayListAdapter trainingDayAdapter; 
	private StableArrayAdapter adapter12;
	private WorkoutplanMapper wpMapper;
	private TrainingDayMapper tdMapper;
	private int workoutplanId =1;
	Bundle intent;
	int tdId = 0;
	
	private ActionBarWorkoutPlanPickerFragment actionBarWorkoutPlanPickerFragment;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_workoutplan);
		
		
		if( intent != null) { getIntent().getExtras();
		workoutplanId = intent.getInt("WorkoutplanId");
		actionBarWorkoutPlanPickerFragment.settdIdByWorkoutplanId(workoutplanId);}
		
		
		
		workoutplanList = new ArrayList<Workoutplan>();
		trainingDayList = new ArrayList<TrainingDay>();
	

		wpMapper = new WorkoutplanMapper(this);
		 tdMapper = new TrainingDayMapper(this);
		workoutplanList = wpMapper.getAll();
		for(int i = 0; i < workoutplanList.size(); i++) {
			if(workoutplanList.get(i).getId() == workoutplanId) {
				tdId = i;
			}}
		trainingDayList = tdMapper.getAll(workoutplanList.get(tdId).getId());

		
	//	trainingDayAdapter = new TrainingDayListAdapter(this, R.id.TrainingDayList, trainingDayList);
	//	trainingDayListView.setAdapter(trainingDayAdapter);
		
		adapter12 = new StableArrayAdapter(this, R.layout.listview_training_day, trainingDayList);
		listView = (DynamicListView) findViewById(R.id.TrainingDayList);
		listView.setCheeseList(trainingDayList);
       listView.setAdapter(adapter12);
       listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); 
	
       // Create a ListView-specific touch listener. ListViews are given special treatment because
       // by default they handle touches for their list items... i.e. they're in charge of drawing
       // the pressed state (the list selector), handling list item clicks, etc.
       SwipeDismissListViewTouchListener touchListener =
               new SwipeDismissListViewTouchListener(
               		listView,
                       new SwipeDismissListViewTouchListener.DismissCallbacks() {
                           @Override
                           public boolean canDismiss(int position) {
                               return true;
                           }

                           @Override
                           public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                               for (int position : reverseSortedPositions) {
                               	TrainingDay td = (TrainingDay) listView.getItemAtPosition(position);
                           		int i = td.getID();
                           		int primarykey = td.getTrainingDayHasWorkoutplanId();
                           		tdMapper.deleteTrainingDayFromWorkoutplan(i, workoutplanId, primarykey);
                             
                           	
                           		trainingDayList.remove(position);
                           		adapter12 = new StableArrayAdapter(getApplicationContext(), R.layout.listview_training_day, trainingDayList);
                           		((DynamicListView) listView).setCheeseList(trainingDayList);
                           		listView.setAdapter(adapter12);
                           		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); 
                               	
                                   
                                   
                               }
                           //    adapter12.notifyDataSetChanged();
                           }
                       });
       listView.setOnTouchListener(touchListener);
       // Setting this scroll listener is required to ensure that during ListView scrolling,
       // we don't look for swipes.
       listView.setOnScrollListener(touchListener.makeScrollListener());
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
		switch (item.getItemId()){
		case R.id.menu_add:
			Intent intent = new Intent();
			intent.putExtra("WorkoutplanId", workoutplanId);
			intent.setClass(this, TrainingDayAddToWorkoutplan.class);
			startActivity(intent);
			break; 
			}
		return super.onOptionsItemSelected(item);
	}

	//

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		
		
	}
	public void addtoList(ArrayList<TrainingDay> trainingdayList1) {
		
		
		adapter12 = new StableArrayAdapter(this, R.layout.listview_training_day, trainingdayList1);
		listView.setCheeseList(trainingdayList1);
	       listView.setAdapter(adapter12);
	       listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); 
	}

	public void setWorkoutplanId(int id) {
	 workoutplanId = id;
		}
	
	
	
}
