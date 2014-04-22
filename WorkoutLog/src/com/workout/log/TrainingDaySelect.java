package com.workout.log;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.example.workoutlog.R.id;
import com.example.workoutlog.R.layout;
import com.example.workoutlog.R.menu;
import com.workout.log.data.*;
import com.workout.log.dialog.ExerciseLongClickDialogFragment;
import com.workout.log.dialog.ExerciseLongClickDialogFragment.ExerciseSelectionDialogListener;
import com.workout.log.listAdapter.CustomDrawerAdapter;
import com.workout.log.listAdapter.DefaultAddListAdapter;
import com.workout.log.listAdapter.TrainingDayListAdapter;

import android.app.Activity;
import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.os.Build;

public class TrainingDaySelect extends Activity implements OnItemClickListener, OnItemLongClickListener, ExerciseSelectionDialogListener {

	// Attribute für Menü
		private DrawerLayout mDrawerLayout;
	    private ListView mDrawerList;
	    private ActionBarDrawerToggle mDrawerToggle;

	    private CharSequence mDrawerTitle;
	    private CharSequence mTitle;
	    CustomDrawerAdapter adapter1;
	    MenueListe l = new MenueListe();
	    
	ListView trainingsDayList;
	ArrayList<TrainingDay> list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.training_day_select);
	
	trainingsDayList = (ListView) findViewById(R.id.trainingDay_select_list);
	list = new ArrayList<TrainingDay>();
	TrainingDayListAdapter adapter = new TrainingDayListAdapter(this, 0, list);
	TrainingDay d = new TrainingDay("test");
	TrainingDay d1 = new TrainingDay("test");
	list.add(d);
	list.add(d1);
	trainingsDayList.setAdapter(adapter);
	trainingsDayList.setOnItemClickListener(this);
	trainingsDayList.setOnItemLongClickListener(this);
	
	
	// Initializing
    
    mTitle = mDrawerTitle = getTitle();
    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    mDrawerList = (ListView) findViewById(R.id.left_drawer);

    mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
    
 // Add Drawer Item to dataList
   
	
    adapter1 = new CustomDrawerAdapter(this, R.layout.custom_drawer_item, l.getDataList());

    mDrawerList.setAdapter(adapter);
    
    mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    
    getActionBar().setDisplayHomeAsUpEnabled(true);
    getActionBar().setHomeButtonEnabled(true);

    mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open,
                R.string.drawer_close) {
          public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to
                                                          // onPrepareOptionsMenu()
          }

          public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to
                                                          // onPrepareOptionsMenu()
          }
    };
    mDrawerLayout.setDrawerListener(mDrawerToggle);      
	
	

	
	
	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.training_day_select, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
      }
		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_training_day_select, container, false);
			return rootView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		TrainingDay td;
		td = (TrainingDay) trainingsDayList.getItemAtPosition(position);
		openExerciseOverview(td);
		
		
	}

	private void openExerciseOverview(TrainingDay td) {
		Intent intent = new Intent();
		intent.setClass(this, ExerciseOverview.class);
		intent.putExtra("mID", td.getID());
		intent.putExtra("mName", td.getName());
		startActivity(intent);
		
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		this.showDialogLongClickFragment();
		return true;
	}
	public void showDialogLongClickFragment(){
		DialogFragment dialogFragment = ExerciseLongClickDialogFragment.newInstance();
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
	}
	@Override
	public void onExerciseSelectionItemLongClick(
			ExerciseLongClickDialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}

	private class DrawerItemClickListener implements
    ListView.OnItemClickListener {
		
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
	          long id) {
	    SelectItem(position);
	
	}
}
	public void SelectItem(int possition) { 
		
		switch(possition) {
		case 0:
			Intent intent= null;
			intent = new Intent();
			intent.setClass(this, ExerciseOverview.class);
			startActivity(intent);
			break;
		case 1: 
			Intent intent1= null;
			intent1 = new Intent();
			intent1.setClass(this, WorkoutplanSelect.class);
			startActivity(intent1);
			break;
		case 2: 
			break;
		case 3: 
			Intent intent2= null;
			intent2 = new Intent();
			intent2.setClass(this, ExerciseAdd.class);
			startActivity(intent2);
			break;
		}
		
		/**
		 * TODOO
		 * 
		 */
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	      super.onConfigurationChanged(newConfig);
	      // Pass any configuration change to the drawer toggles
	      mDrawerToggle.onConfigurationChanged(newConfig);
	}
}
