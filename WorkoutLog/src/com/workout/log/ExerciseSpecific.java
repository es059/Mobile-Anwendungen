package com.workout.log;

import java.io.File;
import java.util.ArrayList;

import com.example.workoutlog.R;
import com.example.workoutlog.R.id;
import com.example.workoutlog.R.layout;
import com.example.workoutlog.R.menu;
import com.workout.log.data.Exercise;
import com.workout.log.data.MenueListe;
import com.workout.log.data.Set;
import com.workout.log.listAdapter.CustomDrawerAdapter;
import com.workout.log.listAdapter.SetListAdapter;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.os.Build;

public class ExerciseSpecific extends Activity {

	
	    
	    
	ListView exerciseView;
	Exercise exercise;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exercise_specific);
		
		exerciseView = (ListView) findViewById(R.id.exerciseSpecificList);
		//Dummy Daten
		ArrayList<Set> setList = new ArrayList<Set>();
		Set set1 = new Set("1");
		setList.add(set1);
		Set set2 = new Set("2");
		setList.add(set2);
		Set set3 = new Set("3");
		setList.add(set3);
		
		SetListAdapter adapter = new SetListAdapter(this,0,setList);
		exerciseView.setAdapter(adapter);
		
		
		//Übergabe der Exercise ID ---> Späterern Zugriff auf FindById der Mapperklasse Exercise
		final Bundle intentExtras = getIntent().getExtras();
		if (intentExtras != null){
			String id = null;
			String name= null;		
			try{
				id = intentExtras.getString("ExerciseID");
				getActionBar().setTitle(intentExtras.getString("ExerciseName"));
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
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
		int id = item.getItemId();
	
		return super.onOptionsItemSelected(item);
	}

	}


