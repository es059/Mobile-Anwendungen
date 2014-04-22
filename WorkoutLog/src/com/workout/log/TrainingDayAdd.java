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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.os.Build;

public class TrainingDayAdd extends Activity implements OnItemClickListener{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.training_day_add);
		
		/*
		ListView exerciseListView = (ListView) findViewById(R.id.trainingDay_add_list);
		
		//Default ListAdapter mit Hinzufügen-Eintrag
		ArrayList<Default> defaultAddList = new ArrayList<Default>();
		Default defaultExercise = new Default("Hinzufügen", "(füge eine neue Übung hinzu)");
		defaultAddList.add(defaultExercise);
		DefaultAddListAdapter adapter = new DefaultAddListAdapter(this,0,defaultAddList);
		exerciseListView.setAdapter(adapter);
		
		exerciseListView.setOnItemClickListener(this);
		*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		openExerciseAdd();
		
	}
	
	public void openExerciseAdd(){
		Intent intent = new Intent();
		intent.setClass(this, ExerciseAdd.class);
		startActivity(intent);
	}

}
