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
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.dialog.TrainingDayAddDialogFragment;
import com.workout.log.dialog.WorkoutplanAddDialogFragment;
import com.workout.log.fragment.ActionBarTrainingDayPickerFragment;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Build;

public class ManageTrainingDays extends Activity implements OnItemClickListener, OnItemLongClickListener{

	private ArrayList<TrainingDay> trainingDayList;
	private TrainingDayListAdapter trainingDayListAdapter;
	private ListView trainingDayListView; 
	private TrainingDayMapper tdMapper;
	
	/**
	 * Variable of the DrawerMenu
	 */
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private CustomDrawerAdapter adapter;
    private MenueListe l = new MenueListe();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.training_day_add);
		
		/**
		 * Add the Drawer to the Activity
		 * 
		 * @author Remi Tessier
		 */
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                    GravityCompat.START);
        
        // Add Drawer Item to dataList
        adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item, l.getDataList());
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
         mDrawerLayout.setDrawerListener(mDrawerToggle);    
	}
	/**
	 * Implements the LiveSearch capability of the EditView and loads 
	 * the Training Days into the ListView
	 * 
	 * @author Eric Schmidt
	 */
	@Override
	protected void onResume(){
		super.onResume();
		
		tdMapper = new TrainingDayMapper(this);
		trainingDayListView = (ListView) findViewById(R.id.trainingDay_add_list);
		trainingDayList = new ArrayList<TrainingDay>();
		trainingDayList = tdMapper.getAllTrainingDay();
		EditText search = (EditText) findViewById(R.id.searchbar_text);
		trainingDayListAdapter = new TrainingDayListAdapter(this, R.layout.listview_training_day, trainingDayList);
		trainingDayListView.setAdapter(trainingDayListAdapter);
		trainingDayListView.setOnItemClickListener(this);
		trainingDayListView.setOnItemLongClickListener(this);

		search.addTextChangedListener(new TextWatcher(){    
	          public void afterTextChanged(Editable s)
	          {
	        	  trainingDayListAdapter.clear();
	        	  trainingDayList =   tdMapper.searchKeyString(String.valueOf(s));

	        	  trainingDayListAdapter = new TrainingDayListAdapter(getApplicationContext(),0,trainingDayList);
	        	  trainingDayListView.setAdapter(trainingDayListAdapter);
	          }

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
	  });
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.workoutplan_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (mDrawerToggle.onOptionsItemSelected(item)) {
	            return true;
	    }
		switch (item.getItemId()){
		case R.id.menu_add:

			DialogFragment dialogFragment = TrainingDayAddDialogFragment.newInstance(this);
			dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		TrainingDay td = (TrainingDay) arg0.getItemAtPosition(arg2);
		int trainingDayId = td.getId();
		String trainingDayName = td.getName();
		openExerciseOverview(trainingDayId, trainingDayName);

	}

	public void openExerciseOverview(int trainingDayId, String trainingDayName){
		Intent intent = new Intent();
		intent.putExtra("trainingDayId", trainingDayId);
		intent.putExtra("trainingDayName", trainingDayName);
		intent.setClass(this, TrainingDayExerciseOverview.class);
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}
	/**
	 * Following Block contains the code for the DrawerMenu
	 */
	public void SelectItem(int possition) { 
		Intent intent= null;
		switch(possition) {
			case 0:
				intent = new Intent();
				intent.setClass(this, ExerciseOverview.class);
				startActivity(intent);
				break;
			case 1: 
				intent = new Intent();
				intent.setClass(this, ManageWorkoutplan.class);
				startActivity(intent);
				break;
			case 2: 
				intent = new Intent();
				intent.setClass(this, ManageTrainingDays.class);
				startActivity(intent);
				break;
			case 3: 
				intent = new Intent();
				intent.setClass(this, ExerciseAdd.class);
				startActivity(intent);
				break;
			case 4: 
				intent = new Intent();
				intent.setClass(this, GraphActivity.class);
				startActivity(intent);
				break;
		}
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	      super.onConfigurationChanged(newConfig);
	      // Pass any configuration change to the drawer toggles
	      mDrawerToggle.onConfigurationChanged(newConfig);
	}
	private class DrawerItemClickListener implements
    ListView.OnItemClickListener {
			
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
		          long id) {
		    SelectItem(position);
		
		}
	}
}