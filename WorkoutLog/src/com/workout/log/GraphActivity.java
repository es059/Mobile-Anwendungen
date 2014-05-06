package com.workout.log;

import com.example.workoutlog.R;
import com.example.workoutlog.R.layout;
import com.workout.log.data.MenueListe;
import com.workout.log.graph.LineGraph;
import com.workout.log.listAdapter.CustomDrawerAdapter;

import android.app.Activity;
import android.app.ActionBar;
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
import android.widget.ListView;
import android.os.Build;

public class GraphActivity extends Activity {

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
		setContentView(R.layout.activity_graph);
		
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
	    }
		return super.onOptionsItemSelected(item);
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
