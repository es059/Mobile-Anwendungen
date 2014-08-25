package com.workout.log.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.remic.workoutlog.R;
public class SpecificCounterFragment extends Fragment{
	
	private ImageButton increase = null;
	private ImageButton decrease = null;
	private ImageButton counterAction = null;
	private TextView timeView = null;
	
	private MyCountDownTimer countDownTimer = null;
	
	private int timeCount = 0;
	private final int interval = 1 * 1000;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.count_down_fragment, container,false);
		
		return view;
	}

	@Override
	public void onResume(){
		super.onResume();
		
		increase = (ImageButton) getView().findViewById(R.id.increase_time);
		decrease = (ImageButton) getView().findViewById(R.id.decrease_time);
		counterAction = (ImageButton) getView().findViewById(R.id.action_timer);
		timeView = (TextView) getView().findViewById(R.id.counter_timer);
		
		counterAction.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_timer));
		
		if (readSharedPrefs() != -1) {
			timeCount = readSharedPrefs();
			timeView.setText(String.valueOf(readSharedPrefs()));
		}
		
		increase.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				timeCount = Integer.parseInt(timeView.getText().toString());
				timeCount++;
				timeView.setText(String.valueOf(timeCount));		
				writeSharedPrefs();
			}
		});
		
		decrease.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				timeCount = Integer.parseInt(timeView.getText().toString());
				timeCount--;
				timeView.setText(String.valueOf(timeCount));	
				writeSharedPrefs();
			}
		});
		
		counterAction.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				countDownTimer = new MyCountDownTimer(timeCount * 1000, interval);
				countDownTimer.start();
				counterAction.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause_timer));
			}
		});
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
	
	 public class MyCountDownTimer extends CountDownTimer {
		 
		private Vibrator v = null;
		private long startTime = 0;
		
		public MyCountDownTimer(long startTime, long interval) {
		    super(startTime, interval);
		    this.startTime = startTime;
		    v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		}
		
		@Override
		public void onFinish() {
			timeView.setText(String.valueOf(startTime / 1000));			
			v.vibrate(2000);			
			counterAction.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_timer));
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			timeView.setText(String.valueOf(millisUntilFinished / 1000));
		} 
	 }
}
