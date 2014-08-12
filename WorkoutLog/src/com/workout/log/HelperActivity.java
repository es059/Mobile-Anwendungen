package com.workout.log;

import java.util.ArrayList;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.workoutlog.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.workout.log.ad.BannerFragment;
import com.workout.log.analytics.MyApplication;
import com.workout.log.analytics.MyApplication.TrackerName;
import com.workout.log.bo.Workoutplan;
import com.workout.log.data.MenuList;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.listAdapter.CustomDrawerAdapter;
import com.workout.log.navigation.OnBackPressedListener;
import com.workout.log.navigation.OnHomePressedListener;

public class HelperActivity extends FragmentActivity{
	protected OnBackPressedListener onBackPressedListener;
	protected OnHomePressedListener onHomePressedListener;
	
	private InterstitialAd interstitial;
	private float dpWidth;
	private Boolean doubleBackToExitPressedOnce = false;
	
	/**
	 * Variables for the Navigation Drawer
	 */
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private CustomDrawerAdapter adapter;
    private MenuList menuList;
    
    
    
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//Get an Analytics tracker to report app starts & uncaught exceptions etc.
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//Stop the analytics tracking
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_helper);

		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
		/**
		 * Tracker
		 */
		//Get a Tracker (should auto-report)
		((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
		
		
		/**
		 * If resolution is smaller than 4 inches than show intersitital ad
		 */
		Display display = getWindowManager().getDefaultDisplay();
	    DisplayMetrics outMetrics = new DisplayMetrics ();
	    display.getMetrics(outMetrics);

	    float density  = getResources().getDisplayMetrics().density;
	    dpWidth  = outMetrics.widthPixels / density;
        
	    if(dpWidth < 360){
	        // Create the interstitial.
	        interstitial = new InterstitialAd(this);
	        interstitial.setAdUnitId("ca-app-pub-8930461526777410/7867711089");
	
	        // Create an ad request. Check logcat output for the hashed device ID to
	        // get test ads on a physical device.
	        AdRequest adRequest = new AdRequest.Builder()
	        	.addKeyword("Gym")
	            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	            .addTestDevice(Secure.getString(this.getContentResolver(),Secure.ANDROID_ID))
	            .build();
	
	        // Begin loading your interstitial.
	        interstitial.loadAd(adRequest);
	        
	        // Set the AdListener.
	        interstitial.setAdListener(new AdListener() {
		          @Override
		          public void onAdLoaded() {
		              displayInterstitial();
		          }
	         });
	    }
        
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getActionBar().setHomeButtonEnabled(true);
		
        /**
         * Handles the Fragment calls which are to be done first
         */
		if (!firstTimeCheck()){
		    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	        transaction.replace(R.id.fragment_container, new ExerciseOverview(), "ExerciseOverview");
	        transaction.addToBackStack(null);
	        transaction.commit();
		}else{
			 FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		     transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		     transaction.replace(R.id.fragment_container, new ManageWorkoutplan(), "ManageWorkoutplan");
		     transaction.addToBackStack(null);
		     transaction.commit();
		}		
	}
	
	// Invoke displayInterstitial() when you are ready to display an interstitial.
	public void displayInterstitial() {
	  if (interstitial.isLoaded()) {
	    interstitial.show();
	  }
	}
	
	protected void onResume(){
		super.onResume();
		
		menuList = new MenuList(this);
		loadNavigationDrawer();
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
	    	if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
	    		/**
	    		 * Closes the application if the userer presses back twice
	    		 */
	    		 if (doubleBackToExitPressedOnce) {
	    		        this.finish();
	    		        return;
	    		    }
	    		    this.doubleBackToExitPressedOnce = true;
	    		    Toast.makeText(this, getResources().getString(R.string.HelperActivity_Close), Toast.LENGTH_SHORT).show();

	    		    new Handler().postDelayed(new Runnable() {

	    		        @Override
	    		        public void run() {
	    		            doubleBackToExitPressedOnce=false;                       
	    		        }
	    		    }, 2000);
	        } else {
	        	getSupportFragmentManager().popBackStack();
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
		   if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
		    		
		   }else {
			   getSupportFragmentManager().popBackStack();
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
        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
        
        RelativeLayout mDrawer = (RelativeLayout) findViewById(R.id.left_drawer);
        
        if (dpWidth >= 360){
	        /**
	         * Add AdFragment to the Navigation Drawer
	         */
	        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		    transaction.replace(R.id.ad_wrapper, new BannerFragment(), "AdFragment");
		    transaction.commit();
        }else{
        	final float scale = this.getResources().getDisplayMetrics().density;
        	int pixels = (int) (240 * scale + 0.5f);
        	
        	mDrawer.getLayoutParams().width = pixels;
        }
        
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                   GravityCompat.START);
        
        // Add Drawer Item to dataList
        adapter = new CustomDrawerAdapter(this, menuList.getDataList());
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
            	  /**
	  				* Tracker
	  				*/
	  			  // Get tracker.
	  		      Tracker t = ((MyApplication) getApplication()).getTracker(
	  		            TrackerName.APP_TRACKER);
	  		      // Build and send an Event.
	  		      t.send(new HitBuilders.EventBuilder()
	  		         .setCategory("ClickEvent")
	  		         .setAction("Open Drawer")
	  		         .setLabel("Open the NavigationDrawer")
	  		         .build());
            	  
            	  getActionBar().setTitle(mDrawerTitle);
            	  adapter.notifyDataSetChanged();
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
			case 1:
				transaction = getSupportFragmentManager().beginTransaction();
			    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			    transaction.replace(R.id.fragment_container, new ExerciseOverview(), "ExerciseOverview");
			    transaction.addToBackStack(null);
			    transaction.commit();
				break;
			case 2: 
				transaction = getSupportFragmentManager().beginTransaction();
			    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			    transaction.replace(R.id.fragment_container, new Graph(), "GraphActivity");
			    transaction.addToBackStack(null);
			    transaction.commit();
				break;
			case 3:
				transaction = getSupportFragmentManager().beginTransaction();
			    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			    transaction.replace(R.id.fragment_container, new ImportExport(), "ImportExport");
			    transaction.addToBackStack(null);
			    transaction.commit();
				break;
			case 5: 
				transaction = getSupportFragmentManager().beginTransaction();
			    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			    transaction.replace(R.id.fragment_container, new ManageWorkoutplan(), "ManageWorkoutplan");
			    transaction.addToBackStack(null);
			    transaction.commit();
				break;
			case 6: 
				transaction = getSupportFragmentManager().beginTransaction();
			    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			    transaction.replace(R.id.fragment_container, new ManageTrainingDays(), "ManageTrainingDays");
			    transaction.addToBackStack(null);
			    transaction.commit();
				break;
			case 7: 
				transaction = getSupportFragmentManager().beginTransaction();
			    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			    transaction.replace(R.id.fragment_container, new ExerciseAdd(), "ExerciseAdd");
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