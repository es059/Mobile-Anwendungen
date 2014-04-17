package com.workout.log;

import java.util.ArrayList;

import com.example.workoutlog.R;
import com.example.workoutlog.R.id;
import com.example.workoutlog.R.layout;
import com.example.workoutlog.R.menu;
import com.workout.log.data.Default;
import com.workout.log.data.Exercise;
import com.workout.log.data.MenueListe;
import com.workout.log.data.Workoutplan;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.listAdapter.CustomDrawerAdapter;
import com.workout.log.listAdapter.DefaultAddListAdapter;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.os.Build;

public class ExerciseAdd extends Activity{
	// Attribute für Menü 1
		private DrawerLayout mDrawerLayout;
	    private ListView mDrawerList;
	    private ActionBarDrawerToggle mDrawerToggle;

	    private CharSequence mDrawerTitle;
	    private CharSequence mTitle;
	    CustomDrawerAdapter adapter1;
	    MenueListe l = new MenueListe();
	    EditText search;
	    
	    ExerciseMapper em = new ExerciseMapper(this);
	
    WorkoutplanMapper m = new WorkoutplanMapper(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exercise_add);
		
		search = (EditText) findViewById(R.id.trainingDay_subject);
		
		ListView exerciseListView = (ListView) findViewById(R.id.add_exerciseList);
		
		//Default ListAdapter mit Hinzufügen-Eintrag
		ArrayList<Default> defaultAddList = new ArrayList<Default>();
		Default defaultExercise = new Default("Hinzufügen", "(füge eine neue Übung hinzu)");
		defaultAddList.add(defaultExercise);
		DefaultAddListAdapter adapter = new DefaultAddListAdapter(this,0,defaultAddList);
		
		ArrayAdapter a = new ArrayAdapter(this, android.R.layout.simple_list_item_1, m.getAll());
		exerciseListView.setAdapter(a);
		
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
        
        // Implementierung Text Change Listener für Live Suche
        
        search.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        		
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
	private class DrawerItemClickListener implements
    ListView.OnItemClickListener {
		
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
	          long id) {
	    SelectItem(position);
	
	}
}
	public void SelectItem(int possition) { 
		Intent intent= null;
		switch(possition) {
		case 0:
			intent = new Intent();
			intent.setClass(this, WorkoutplanSelect.class);
			startActivity(intent);
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


