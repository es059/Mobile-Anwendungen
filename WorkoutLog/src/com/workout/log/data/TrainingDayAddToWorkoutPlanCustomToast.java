package com.workout.log.data;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.TextView;

import com.remic.workoutlog.R;

public class TrainingDayAddToWorkoutPlanCustomToast {
    private View mBarView;
    private TextView mMessageView;
    private Button mBarViewButton;
    private ViewPropertyAnimator mBarAnimator;
    private Handler mHideHandler = new Handler();

    private ShortCutListener mShortCutListener;
    private String mUndoMessage;

    public interface ShortCutListener {
        void onShortCut();
    }

    public TrainingDayAddToWorkoutPlanCustomToast(View undoBarView, ShortCutListener shortCutListener) {
        mBarView = undoBarView;
        mBarAnimator = mBarView.animate();
        mShortCutListener = shortCutListener;

        mBarView.bringToFront();
        
        mMessageView = (TextView) mBarView.findViewById(R.id.undobar_message);
        mBarViewButton= (Button) mBarView.findViewById(R.id.undobar_button);
        mBarViewButton.setText("");
        mBarViewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideUndoBar(false);
                        mShortCutListener.onShortCut();
                    }
                });

        hideUndoBar(true);
    }

   
	public void showUndoBar(boolean immediate, String message, boolean hideAfterTime) {
        mUndoMessage = message;
        mMessageView.setText(mUndoMessage);

        mHideHandler.removeCallbacks(mHideRunnable);
        if (hideAfterTime) mHideHandler.postDelayed(mHideRunnable,
                mBarView.getResources().getInteger(R.integer.undobar_hide_delay));

        mBarView.setVisibility(View.VISIBLE);
        if (immediate) {
            mBarView.setAlpha(1);
        } else {
          //  mBarAnimator.cancel();
            mBarAnimator
                    .alpha(1)
                    .setDuration(
                            mBarView.getResources()
                                    .getInteger(android.R.integer.config_shortAnimTime))
                    .setListener(null);
        }
    }
   
	public void hideUndoBar(boolean immediate) {
        mHideHandler.removeCallbacks(mHideRunnable);
        if (immediate) {
            mBarView.setVisibility(View.GONE);
            mBarView.setAlpha(0);
            mUndoMessage = null;
            

        } else {
        //    mBarAnimator.cancel();
            mBarAnimator
                    .alpha(0)
                    .setDuration(mBarView.getResources()
                            .getInteger(android.R.integer.config_shortAnimTime))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mBarView.setVisibility(View.GONE);
                            mUndoMessage = null;
                            
                        }
                    });
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence("undo_message", mUndoMessage);
       
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mUndoMessage = savedInstanceState.getString("undo_message");
        }
    }

    private Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hideUndoBar(false);
        }
    };
    
    public View getUndoBar(){
    	return mBarView;
    }
    
    public View getButton(){
    	return mBarViewButton;
    }
}


