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
import com.workout.log.dialog.ExerciseAddDialogFragment;
import com.workout.log.dialog.ExerciseLongClickDialogFragment;
import com.workout.log.dialog.ExerciseLongClickDialogFragment.ExerciseSelectionDialogListener;
import com.workout.log.listAdapter.CustomDrawerAdapter;
import com.workout.log.listAdapter.DefaultAddListAdapter;
import com.workout.log.listAdapter.ExerciseListAdapter;

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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.os.Build;

public class ExerciseAdd extends Activity implements ExerciseSelectionDialogListener, OnItemLongClickListener {
	// Attribute für Menü 1
		private DrawerLayout mDrawerLayout;
	    private ListView mDrawerListe;
	    private ActionBarDrawerToggle mDrawerToggle;

	    private CharSequence mDrawerTitle;
	    private CharSequence mTitle;
	    private CustomDrawerAdapter adapter;
	    private MenueListe l = new MenueListe();
	    private EditText search;
	    private ArrayList<Exercise> List;
	    private ExerciseListAdapter a;
	    private ListView exerciseListView;
	    private DialogFragment dialogFragment;
	//  int i;
	    
	  private  ExerciseMapper em = new ExerciseMapper(this);
	
	 private  WorkoutplanMapper m = new WorkoutplanMapper(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exercise_add);
		
		search = (EditText) findViewById(R.id.trainingDay_subject);
		
		exerciseListView = (ListView) findViewById(R.id.add_exerciseList);
		List = new ArrayList<Exercise>();
		List = em.getAll();
		a = new ExerciseListAdapter(this , R.layout.listview_exercise, List);
		exerciseListView.setAdapter(a);
		exerciseListView.setOnItemLongClickListener(this);
		search.addTextChangedListener(new TextWatcher() 
		  {
	          
	          public void beforeTextChanged(CharSequence s, int start, int count, int after)
	          {
	                    
	          }
	 
	          public void onTextChanged(CharSequence s, int start, int before, int count)
	          {
	        	 
	          }

	          public void afterTextChanged(Editable s)
	          {
	              
	        	  a.clear();
	        	  List =   em.searchKeyString(String.valueOf(s));
	        	  
	        	  
	        	  a.addAll(List);
	        	  a.notifyDataSetChanged();
	        	  exerciseListView.invalidateViews();
	        	  System.out.println(s);
	          }
	  });

		
	
	//	ArrayList<Exercise> uebungList = new ArrayList<Exercise>();
	//	ExerciseListAdapter ExerciseAdapter = new ExerciseListAdapter(null, 0, uebungList);
	//	exerciseListView.setAdapter(ExerciseAdapter);
		
		// Initializing
	       
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListe= (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                    GravityCompat.START);
        
		
		// Add Drawer Item to dataList
       
		
        adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item, l.getDataList());
        mDrawerListe.setAdapter(adapter);
        mDrawerListe.setOnItemClickListener(new DrawerItemClickListener());
        
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
		
		switch (item.getItemId()){
			case R.id.menu_add:
				this.showDialogAddFragment();
				break; 
				}
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
	public void showDialogAddFragment(){
		DialogFragment dialogFragment = ExerciseAddDialogFragment.newInstance(this, a);
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
	}

	@Override
	public void onExerciseSelectionItemLongClick(
			ExerciseLongClickDialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		
		Exercise e = (Exercise) arg0.getItemAtPosition(arg2);
		int i = e.getID();
		showDialogLongClickFragment(i, a);
		
		
		
		return false;
	}
public void listviewactual() {
	 a.clear();
	 a.addAll(em.getAll());
	 a.notifyDataSetChanged();
	 exerciseListView.invalidateViews();
}
	private void showDialogLongClickFragment(int i, ExerciseListAdapter a) {
		
		dialogFragment = ExerciseLongClickDialogFragment.newInstance(i, a);
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
		
	}

}


