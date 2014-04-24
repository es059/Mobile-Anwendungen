package com.workout.log;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.example.workoutlog.R.id;
import com.example.workoutlog.R.layout;
import com.example.workoutlog.R.menu;
import com.workout.log.bo.Exercise;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.listAdapter.ExerciseListAdapter;

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
import android.widget.ListView;
import android.widget.TextView;
import android.os.Build;

public class TrainingDayExerciseOverview extends Activity {
	private ListView exerciseListView;
	private ArrayList<Exercise> exerciseList;
	private ExerciseListAdapter exerciseListAdapter;
	private ExerciseMapper eMapper;
	private int trainingDayId;
	private String trainingDayName;
	private Bundle intent;
	private TextView TvTrainingDayName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.training_day_exercise_overview);
		// Übergabe der Trainingtags-ID über das Intent 
		intent = getIntent().getExtras();
		trainingDayId = intent.getInt("trainingDayId");
		trainingDayName = intent.getString("trainingDayName");
		// Textview initialisierung 
		TvTrainingDayName = (TextView) findViewById(R.id.aktuellerTrainingsplan);
		// TextView den aktuellen TrainingsTag hinzufügen
		TvTrainingDayName.setText(trainingDayName);
		
		eMapper = new ExerciseMapper(this);
		exerciseListView = (ListView) findViewById(R.id.ExerciseListView);
		exerciseList = new ArrayList<Exercise>();
		exerciseList = eMapper.getAllExercise(trainingDayId);
		exerciseListAdapter = new ExerciseListAdapter(this, R.layout.listview_exercise, exerciseList);
		exerciseListView.setAdapter(exerciseListAdapter);
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
		else if( id == R.id.menu_add){
			openExerciseAddtoTrainingDay(trainingDayId);
		}
		return super.onOptionsItemSelected(item);
	}

	private void openExerciseAddtoTrainingDay(int trainingDayId2) {
		Intent intent = new Intent();
		intent.putExtra("trainingDayId", trainingDayId);
		intent.setClass(getApplicationContext(), ExerciseAddToTrainingDay.class);
		startActivity(intent);
		
		
	}

	
	}


