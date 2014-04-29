package com.workout.log;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.example.workoutlog.R.id;
import com.example.workoutlog.R.layout;
import com.example.workoutlog.R.menu;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.TrainingDay;
import com.workout.log.data.Default;
import com.workout.log.data.MenueListe;
import com.workout.log.listAdapter.CustomDrawerAdapter;
import com.workout.log.listAdapter.DefaultAddListAdapter;
import com.workout.log.listAdapter.TrainingDayListAdapter;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.os.Build;

public class WorkoutplanAdd extends Activity implements OnItemClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workoutplan_add);
		
		//ListView defaultListView = (ListView) findViewById(R.id.workoutplan_add_list);
		ListView trainingDayListView = (ListView) findViewById(R.id.workoutplan_trainingDay_list);
		
		/*
		//Default ListAdapter mit Hinzufügen-Eintrag
		ArrayList<Default> defaultAddList = new ArrayList<Default>();
		Default defaultTrainingDay = new Default("Hinzufügen", "(füge einen neue Trainingstag hinzu)");
		defaultAddList.add(defaultTrainingDay);
		DefaultAddListAdapter adapter = new DefaultAddListAdapter(this,0,defaultAddList);
		defaultListView.setAdapter(adapter);
		defaultListView.setOnItemClickListener(this);
		*/
		
	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.exercise_overview_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Intent intent= null;
		switch (item.getItemId()){
			case R.id.menu_add:
				intent = new Intent();
				intent.setClass(this, TrainingDayAdd.class);
				startActivity(intent);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		openTrainingDayAdd();	
	}
	
	public void openTrainingDayAdd(){
		Intent intent = new Intent();
		intent.setClass(this, TrainingDayAdd.class);
		startActivity(intent);
	}
}
