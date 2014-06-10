package com.workout.log;

import java.util.ArrayList;








import com.example.workoutlog.R;
import com.workout.log.listAdapter.*;
import com.workout.log.bo.Exercise;
import com.workout.log.data.MenueListe;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.dialog.ExerciseAddDialogFragment;
import com.workout.log.dialog.ExerciseLongClickDialogFragment;
import com.workout.log.dialog.ExerciseLongClickDialogFragment.ExerciseSelectionDialogListener;
import com.workout.log.listAdapter.CustomDrawerAdapter;
import com.workout.log.listAdapter.ExerciseListAdapter;
import com.workout.log.listAdapter.ExerciseListWithoutSetsRepsAdapter;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
	    private ExerciseListWithoutSetsRepsAdapter a;
	    private ListView exerciseListView;
	    private DialogFragment dialogFragment;
	    private Toast toast;
	    private ArrayList<String> abc;
	 //   private DynamicListView dlv = new DynamicListView(this);
	//  int i;
	    
	  private  ExerciseMapper em = new ExerciseMapper(this);
	
	 private  WorkoutplanMapper m = new WorkoutplanMapper(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exercise_add);
		
		 
	abc = new ArrayList<String>();
	abc = em.getAllbyString();
	List = new ArrayList<Exercise>();
	List = em.getAll();
	/*	StableArrayAdapter adapter12 = new StableArrayAdapter(this, R.layout.listview_exercise_without_repssets, List);
	        DynamicListView listView = (DynamicListView) findViewById(R.id.listview);

	       listView.setCheeseList(List);
	      listView.setAdapter(adapter12);
	       listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); 
		*/
		final Toast toast = Toast.makeText(this, "Übung wurde gelöscht!", Toast.LENGTH_SHORT );
		
		search = (EditText) findViewById(R.id.trainingDay_subject);
		
		exerciseListView = (ListView) findViewById(R.id.add_exerciseList);
		
		a = new ExerciseListWithoutSetsRepsAdapter(this , R.layout.listview_exercise_without_repssets, List);
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

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
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
        
        
        // Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                		exerciseListView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                	Exercise e = (Exercise) exerciseListView.getItemAtPosition(position);
                            		int i = e.getId();
                                	em.delete(i);
                                	a.remove(a.getItem(position));
                                	toast.show();
                                    
                                    
                                }
                                a.notifyDataSetChanged();
                            }
                        });
        exerciseListView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        exerciseListView.setOnScrollListener(touchListener.makeScrollListener());

      
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
		int i = e.getId();
		showDialogLongClickFragment(i, a);
		
		
		
		return false;
	}
public void listviewactual() {
	 a.clear();
	 a.addAll(em.getAll());
	 a.notifyDataSetChanged();
	 exerciseListView.invalidateViews();
}
	private void showDialogLongClickFragment(int i, ExerciseListWithoutSetsRepsAdapter a) {
		
		dialogFragment = ExerciseLongClickDialogFragment.newInstance(i, a);
		dialogFragment.show(this.getFragmentManager(), "Open Exercise Settings on Long Click");
		
	}

}


