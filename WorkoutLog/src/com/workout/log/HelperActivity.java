package com.workout.log;



import com.example.workoutlog.R;
import com.workout.log.data.MenueListe;
import com.workout.log.listAdapter.CustomDrawerAdapter;
import com.workout.log.navigation.OnBackPressedListener;
import com.workout.log.navigation.OnHomePressedListener;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class HelperActivity extends Activity  {
	private static final String PREF_FIRST_LAUNCH = "first";
	protected OnBackPressedListener onBackPressedListener;
	protected OnHomePressedListener onHomePressedListener;
	
	/**
	 * Variables for the Navigation Drawer
	 */
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private CustomDrawerAdapter adapter;
    private MenueListe l = new MenueListe();
    
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_helper);
		
		/**
		 * Navigation Menu implementation
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
        
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getActionBar().setHomeButtonEnabled(true);

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
		
        
        /**
         * Handels the Fragment calls which are to be done first
         */
		if (!firstTimeCheck()){
		    FragmentTransaction transaction = getFragmentManager().beginTransaction();
	        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	        transaction.replace(R.id.fragment_container, new ExerciseAdd(), "ExerciseAdd");
	        transaction.addToBackStack(null);
	        transaction.commit();
		}else{
			//startActivity(new Intent(this, WorkoutplanSelect.class));
		    //finish();
			 FragmentTransaction transaction = getFragmentManager().beginTransaction();
		        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		        transaction.replace(R.id.fragment_container, new ExerciseAdd(), "ExerciseAdd");
		        transaction.addToBackStack(null);
		        transaction.commit();
		}
	}
	/**
	 * Check Shared Preferences if the user had opened the Application before
	 * 
	 * @return false if not launched for the first time
	 * @author Eric Schmidt
	 */
	private boolean firstTimeCheck(){
		return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREF_FIRST_LAUNCH, true);
	}
	
	public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
	    this.onBackPressedListener = onBackPressedListener;
	}

	public void setOnHomePressedListener(OnHomePressedListener onHomePressedListener) {
	    this.onHomePressedListener = onHomePressedListener;
	}
	
	@Override
	public void onBackPressed() {
	    if (onBackPressedListener != null)
	        onBackPressedListener.doBack();
	}
	
	@Override
	public Intent getParentActivityIntent() {
		if (onBackPressedListener != null)
	        onHomePressedListener.doHome();
		return null;
	}
	

	/**
	 * Methods to handle the Navigation Drawer
	 * 
	 *
	 */
	public void SelectItem(int possition) { 
		FragmentTransaction transaction;
		switch(possition) {
			case 0:
				transaction = getFragmentManager().beginTransaction();
			    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			    transaction.replace(R.id.fragment_container, new ExerciseOverview(), "ExerciseOverview");
			    transaction.addToBackStack(null);
			    transaction.commit();
				break;
			case 1: 
				transaction = getFragmentManager().beginTransaction();
			    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			    transaction.replace(R.id.fragment_container, new ManageWorkoutplan(), "ManageWorkoutplan");
			    transaction.addToBackStack(null);
			    transaction.commit();
				break;
			case 2: 
				transaction = getFragmentManager().beginTransaction();
			    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			    transaction.replace(R.id.fragment_container, new ManageTrainingDays(), "ManageTrainingDays");
			    transaction.addToBackStack(null);
			    transaction.commit();
				break;
			case 3: 
				transaction = getFragmentManager().beginTransaction();
			    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			    transaction.replace(R.id.fragment_container, new ExerciseAdd(), "ExerciseAdd");
			    transaction.addToBackStack(null);
			    transaction.commit();
				break;
			case 4: 
				transaction = getFragmentManager().beginTransaction();
			    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			    transaction.replace(R.id.fragment_container, new GraphActivity(), "GraphActivity");
			    transaction.addToBackStack(null);
			    transaction.commit();
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (mDrawerToggle.onOptionsItemSelected(item)) {
	    	return true;
	    }
		return super.onOptionsItemSelected(item);
	}
	
}
