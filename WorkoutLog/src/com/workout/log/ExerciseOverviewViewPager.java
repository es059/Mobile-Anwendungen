package com.workout.log;

import java.util.ArrayList;

import com.remic.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.MuscleGroup;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;
import com.workout.log.data.ListItem;
import com.workout.log.data.MuscleGroupSectionItem;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.MuscleGroupMapper;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;
import com.workout.log.listAdapter.OverviewAdapter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ExerciseOverviewViewPager extends Fragment {
	
	ViewPager viewPager;
	SectionPagerAdapter pagerAdapter;
	int trainingDayId;
	ArrayList<TrainingDay> trainingDayList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	
		View view = inflater.inflate(R.layout.exercise_overview_viewpager, container, false);
		
		pagerAdapter = new SectionPagerAdapter(getChildFragmentManager());
		viewPager = (ViewPager) view.findViewById(R.id.pager);
		viewPager.setOffscreenPageLimit(trainingDayList.size());
		new BackGroundTask().execute();
		
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		final Bundle transferExtras = getArguments();
		if (transferExtras != null) {
			trainingDayId = transferExtras.getInt("TrainingDayId");
			int position = pagerAdapter.findItem(trainingDayId);
		//	viewPager.setCurrentItem(position);
			pagerAdapter.notifyDataSetChanged();
		}
	}

	public class SectionPagerAdapter extends FragmentPagerAdapter {
		
		//ArrayList<TrainingDay> trainingDayList;
		ArrayList<ExerciseOverview> exerciseOverviewList;

		public SectionPagerAdapter(FragmentManager fm) {
			super(fm);
			getAllTrainingDay();
			new CreateAllFragmentsAsync().execute();
		}

		@Override
		public Fragment getItem(int arg0) {
			return exerciseOverviewList.get(arg0);
		}

		@Override
		public int getCount() {
			return trainingDayList.size();
		}
		
		@Override
	    public CharSequence getPageTitle(int position) {
	        
	        return trainingDayList.get(position).getName();
	    }
		
		private void getAllTrainingDay(){
			//Select Current Workoutplan
			WorkoutplanMapper wMapper = new WorkoutplanMapper(getActivity());
			Workoutplan w = wMapper.getCurrent();
			//Select all TrainingDays from currrent Workoutplan
			TrainingDayMapper tMapper = new TrainingDayMapper(getActivity());
			trainingDayList = tMapper.getAllTrainingDaysFromWorkoutplan(w.getId());
		}
		
		private int findItem(int trainingDayId) {
			int position = 0;
			for(int i = 0; i < trainingDayList.size(); i++) {
				if(trainingDayId == trainingDayList.get(i).getId()) {
					position = i;
				}
			}
			return position;
		}
		public ArrayList<ExerciseOverview> createAllFragments(ArrayList<TrainingDay>trainingDayList) {
			exerciseOverviewList = new ArrayList<ExerciseOverview>();
			for(int i = 0; i < trainingDayList.size(); i++) {
				ExerciseOverview exerciseOverview = new ExerciseOverview();
				Bundle data = new Bundle();
				data.putInt("TrainingDayId", trainingDayList.get(i).getId());
				exerciseOverview.setArguments(data);
				exerciseOverviewList.add(exerciseOverview);

			}
			return exerciseOverviewList;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
		 public class CreateAllFragmentsAsync extends AsyncTask<Void, Void, Void> {
		 	   
			    @Override
			    protected void onPreExecute() {
			        super.onPreExecute();
			    
				}
			    
			    @Override
			    protected Void doInBackground(Void... params) {
			    	createAllFragments(trainingDayList);
					return null;
			    }

			    @Override
			    protected void onPostExecute(Void result) {
			        super.onPostExecute(result);
			
			}
		}
	}
	
	public class BackGroundTask extends AsyncTask<Void, Void, Void> {
	   
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	    
		}
	    
	    @Override
	    protected Void doInBackground(Void... params) {
	    	 if(pagerAdapter != null) {
		        	viewPager.setAdapter(pagerAdapter);
		        }
			return null;
	    }

	    @Override
	    protected void onPostExecute(Void result) {
	        super.onPostExecute(result);
	
	}
}
}
