package com.workout.log.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.remic.workoutlog.R;
import com.workout.log.data.CountDownBroadcastService;

public class SpecificCounterFragment extends Fragment{
	
	private ImageButton increase = null;
	private ImageButton decrease = null;
	private ImageButton counterAction = null;
	private TextView timeView = null;

	private Vibrator v = null;
	
	private int timeCount = 0;
	private static boolean isRunning =false;
	private static boolean isPaused = false;
	
	private Drawable play = null;
	private Drawable stop = null;
	
	private BroadcastReceiver  br = new BroadcastReceiver(){
	    @Override
	    public void onReceive(Context context, Intent intent) {            
	        updateTextView(intent); // or whatever method used to update your GUI fields
	    }

		private void updateTextView(Intent intent) {
		    if (intent.getExtras() != null) {
		        long secondsUntilFinished = intent.getLongExtra("countdown", -1);
		        if (secondsUntilFinished != -1) {
		        	timeView.setText(String.valueOf(secondsUntilFinished));
		        }else{
		        	timeView.setText(String.valueOf(timeCount));			
					v.vibrate(2000);		
					
					counterAction.setImageDrawable(play);
					
					increase.setEnabled(true);
					decrease.setEnabled(true);
					
					isRunning = false;
		        }
		    }
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.count_down_fragment, container,false);
		
		v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		play = getResources().getDrawable(R.drawable.play_timer);
		stop = getResources().getDrawable(R.drawable.stop_timer);
		return view;
	}

	@Override
	public void onResume(){
		super.onResume();
		
		getActivity().registerReceiver(br, new IntentFilter(CountDownBroadcastService.COUNTDOWN_BR));
		
		increase = (ImageButton) getView().findViewById(R.id.increase_time);
		decrease = (ImageButton) getView().findViewById(R.id.decrease_time);
		counterAction = (ImageButton) getView().findViewById(R.id.action_timer);
		timeView = (TextView) getView().findViewById(R.id.counter_timer);
		
		if (!isRunning){
			counterAction.setImageDrawable(play);
			if (readSharedPrefs() != -1) {
				timeCount = readSharedPrefs();
				timeView.setText(String.valueOf(readSharedPrefs()));
			}
		}else{
			counterAction.setImageDrawable(stop);
		}
		
		increase.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				timeCount = Integer.parseInt(timeView.getText().toString());
				timeCount += 5;
				timeView.setText(String.valueOf(timeCount));		
				writeSharedPrefs();
			}
		});
		
		decrease.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				timeCount = Integer.parseInt(timeView.getText().toString());
				if(timeCount - 5 > 0) {
					timeCount -= 5;
				}else{
					timeCount = 0;
				}
				timeView.setText(String.valueOf(timeCount));	
				writeSharedPrefs();
			}
		});
		
		counterAction.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if (!isRunning){						
					Intent startBroadcast = new Intent(getActivity(), CountDownBroadcastService.class);
					startBroadcast.putExtra("Time", timeCount);
					getActivity().startService(startBroadcast);
					
					counterAction.setImageDrawable(stop);
					
					increase.setEnabled(false);
					decrease.setEnabled(false);
					
					isRunning = true;
				}else{
					getActivity().stopService(new Intent(getActivity(), CountDownBroadcastService.class));
					
					timeView.setText(String.valueOf(timeCount));					
					counterAction.setImageDrawable(play);
					
					increase.setEnabled(true);
					decrease.setEnabled(true);
					
					isRunning = false;
					isPaused = false;
				}
			}
		});
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    isPaused = true;
	    //getActivity().unregisterReceiver(br);
	}

	@Override
	public void onStop() {
	    try {
	    	isPaused = true;
	    	//getActivity().unregisterReceiver(br);
	    } catch (Exception e) {
	        // Receiver was probably already stopped in onPause()
	    }
	    super.onStop();
	}
	
	@Override
	public void onDestroy() {        
		if (!isPaused) getActivity().stopService(new Intent(getActivity(), CountDownBroadcastService.class));
	    super.onDestroy();
	}
	
	/**
     * Write in SharedPrefs
     */
    private void writeSharedPrefs(){
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("counterTime", timeCount);
        editor.commit();
    }
    
    /**
     * Read in SharedPres
     */
    private int readSharedPrefs(){
    	SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
    	int retrievedValue = sharedPref.getInt("counterTime", -1);
    	return retrievedValue;
    }
}
