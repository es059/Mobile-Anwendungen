package com.workout.log;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.workoutlog.R;
import com.workout.log.bo.Workoutplan;
import com.workout.log.data.MenuList;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.listAdapter.CustomDrawerAdapter;
import com.workout.log.navigation.OnBackPressedListener;
import com.workout.log.navigation.OnHomePressedListener;

public class HelperActivity extends Activity{
	protected OnBackPressedListener onBackPressedListener;
	protected OnHomePressedListener onHomePressedListener;
	
	Boolean doubleBackToExitPressedOnce = false;
	
	/**
	 * Variables for the Navigation Drawer
	 */
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private CustomDrawerAdapter adapter;
    private MenuList menuList = new MenuList();
    
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_helper);

		loadNavigationDrawer();
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getActionBar().setHomeButtonEnabled(true);
		
        /**
         * Handles the Fragment calls which are to be done first
         */
		if (!firstTimeCheck()){
		    FragmentTransaction transaction = getFragmentManager().beginTransaction();
	        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	        transaction.replace(R.id.fragment_container, new ExerciseOverview(), "ExerciseOverview");
	        transaction.addToBackStack(null);
	        transaction.commit();
		}else{
			 FragmentTransaction transaction = getFragmentManager().beginTransaction();
		     transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		     transaction.replace(R.id.fragment_container, new ManageWorkoutplan(), "ManageWorkoutplan");
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
		WorkoutplanMapper wMapper = new WorkoutplanMapper(this);
		ArrayList<Workoutplan> wList = wMapper.getAll();
		if (wList.isEmpty())return true;
		return false;
	}
	
	/**
	 * Handles the behavior of the back buttons
	 * 
	 * @param onBackPressedListener
	 */
	public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
	    this.onBackPressedListener = onBackPressedListener;
	}

	public void setOnHomePressedListener(OnHomePressedListener onHomePressedListener) {
	    this.onHomePressedListener = onHomePressedListener;
	}
	
	@Override
	public void onBackPressed() {
	    if (onBackPressedListener != null){
	        onBackPressedListener.doBack();
	    }else{
	    	if (getFragmentManager().getBackStackEntryCount() == 1 || getFragmentManager().findFragmentByTag("ExerciseOverview").isVisible()) {
	    		/**
	    		 * Closes the application if the userer presses back twice
	    		 */
	    		 if (doubleBackToExitPressedOnce) {
	    		        this.finish();
	    		        return;
	    		    }
	    		    this.doubleBackToExitPressedOnce = true;
	    		    Toast.makeText(this, "Erneut drücken um zu Beenden", Toast.LENGTH_SHORT).show();

	    		    new Handler().postDelayed(new Runnable() {

	    		        @Override
	    		        public void run() {
	    		            doubleBackToExitPressedOnce=false;                       
	    		        }
	    		    }, 2000);
	    		
	        } else {
	            getFragmentManager().popBackStack();
	        	/**
				 * Set the visibility of the NavigationDrawer to Visible
				 */
			   setNavigationDrawerVisibility(true);
	        }
	    }
	}
	
	@Override
	public Intent getParentActivityIntent() {
		if (onBackPressedListener != null){
	        onHomePressedListener.doHome();
		}else{
		   if (getFragmentManager().getBackStackEntryCount() == 1) {
		    		
		   }else {
			   getFragmentManager().popBackStack();
				/**
				 * Set the visibility of the NavigationDrawer to Visible
				 */
			   setNavigationDrawerVisibility(true);
		   }
        }
		return null;
		 
	}
	

	
	/**
	 * Navigation Menu implementation
	 * 
	 * @author Eric Schmidt
	 */
	private void loadNavigationDrawer(){
	    mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                   GravityCompat.START);
        
        // Add Drawer Item to dataList
        adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item, menuList.getDataList());
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                    R.drawable.ic_drawer, R.string.drawer_open,
                    R.string.drawer_close) {
              @Override
			public void onDrawerClosed(View view) {
            	  getActionBar().setTitle(mTitle);
            	  invalidateOptionsMenu(); 
              }
              @Override
			public void onDrawerOpened(View drawerView) {
            	  getActionBar().setTitle(mDrawerTitle);
            	  invalidateOptionsMenu();
              }
        };
       
        mDrawerLayout.setDrawerListener(mDrawerToggle);   	
	}
	
	/**
	 * Set the visiblity of the NavigationDrawer
	 * 
	 * @param visible true if visible - false if not
	 * @author Eric Schmidt
	 */
	public void setNavigationDrawerVisibility(Boolean visible){
		if (visible) mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		if (!visible)mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}
	
	/**
	 * Decide which Fragment is to be called if the corresponding menu item is clicked 
	 * 
	 * @param int the position of the menu item
	 */
	private void SelectItem(int possition) { 
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
			    transaction.replace(R.id.fragment_container, new Graph(), "GraphActivity");
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
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {	
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
		          long id) {
			mDrawerLayout.closeDrawers();
		    SelectItem(position);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int lockMode = mDrawerLayout.getDrawerLockMode(Gravity.LEFT);
	    if (lockMode == DrawerLayout.LOCK_MODE_UNLOCKED &&
	    		mDrawerToggle.onOptionsItemSelected(item)) {
	    	return true;
	    }
		return super.onOptionsItemSelected(item);
	}
}
