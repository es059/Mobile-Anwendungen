package com.workout.log;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.Workoutplan;
import com.workout.log.data.MenueListe;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.dialog.ExerciseLongClickDialogFragment;
import com.workout.log.dialog.ExerciseLongClickDialogFragment.ExerciseSelectionDialogListener;
import com.workout.log.listAdapter.CustomDrawerAdapter;
import com.workout.log.listAdapter.WorkoutplanListAdapter;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class WorkoutplanSelect extends Activity  implements OnItemLongClickListener, OnItemClickListener, ExerciseSelectionDialogListener{
	private ListView workoutplanListView;
	private View lastView;
	private ArrayList<Workoutplan> wList;
	private WorkoutplanMapper wMapper;
	private static final String PREF_FIRST_LAUNCH = "first";
	//Attribute für Menü
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	CustomDrawerAdapter adapter1;
	MenueListe l = new MenueListe();
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workoutplan_select);
				
		// Initializing 
	    mTitle = mDrawerTitle = getTitle();
	    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	    mDrawerList = (ListView) findViewById(R.id.left_drawer);

	    mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
	                GravityCompat.START);
	    
	    // Add Drawer Item to dataList
        adapter1 = new CustomDrawerAdapter(this, R.layout.custom_drawer_item, l.getDataList());
        mDrawerList.setAdapter(adapter1);
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
	/**
	 * Initialize the ListView content
	 * 
	 * @author Eric Schmidt
	 */
	@Override
	protected void onResume(){
		super.onResume();
		int index = 0;
		boolean found = false;
		workoutplanListView = (ListView) findViewById(R.id.workoutplanList);
		//Select the current Workoutplan
		wMapper = new WorkoutplanMapper(this);
		wList = wMapper.getAll();
		Workoutplan current = wMapper.getCurrent();
		
		WorkoutplanListAdapter adapter = new WorkoutplanListAdapter(this,0,wList);
		workoutplanListView.setAdapter(adapter);
		
		workoutplanListView.setOnItemClickListener(this);
		workoutplanListView.setOnItemLongClickListener(this);
		/*
		//Set the Background Resource of the Current Workoutplan
		for (Workoutplan w : wList){
			if (current.equals(w)){
				//Select Current Workoutplan
				selectItem(index, workoutplanListView);
				found = true;
			}
			index++;
		}
		if (found = false){
			//If there is no Current Workoutplan then select the first Entry as Standard Value
			selectItem(1, workoutplanListView);
		}
		*/
		storeSharedPref(false);
	}
	/**
	 * Select an Item in a specific ListView
	 * 
	 * @param position
	 * @param listView
	 */
	public void selectItem(int position, ListView listView){
		//listView.performItemClick(listView.getAdapter().getView(position, null, null), position, listView.getAdapter().getItemId(position));
		this.onItemClick(listView, listView.getAdapter().getView(position, null, null), position, position);
	}
	
	
	/**
	 * Storing in Shared Preferences if the Application is starts it for the first time
	 * 
	 * @param false if not first time
	 */
	protected void storeSharedPref(Boolean firstTime){
		SharedPreferences isLogged = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = isLogged.edit();
		editor.putBoolean(PREF_FIRST_LAUNCH, firstTime);
		editor.commit();
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
		if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
      }
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
	          long id) {
		
		//Alternative Lösung
	    Workoutplan w = (Workoutplan) workoutplanListView.getItemAtPosition(position);
	    wMapper.setCurrent(w.getId());
	    
	    Intent intent = new Intent();
	    intent.setClass(this, TrainingDaySelect.class);
	    intent.putExtra("WorkoutplanId", w.getId());
	    intent.putExtra("WorkoutplanName", w.getName());
	    startActivity(intent);
	    
		/*
	       if(lastView==null){
	    	    view.setBackgroundResource(R.drawable.actionbar_orange);
	    	    Workoutplan w = (Workoutplan) workoutplanListView.getItemAtPosition(position);
	    	    wMapper.setCurrent(w.getId());
	            lastView=view;
	            }
	            else{
	            lastView.setBackgroundColor(Color.WHITE); 
	            view.setBackgroundResource(R.drawable.actionbar_orange);
	            wMapper.setCurrent(wList.get(position).getId());
	            lastView=view;
	            }
	         */
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
	private class DrawerItemClickListener implements
    ListView.OnItemClickListener {
		
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
	          long id) {
	    SelectItem(position);
	
	}
}
}
