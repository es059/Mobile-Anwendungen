package com.workout.log.navigation;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

public class BaseNavigationListener implements OnBackPressedListener, OnHomePressedListener{
	private final FragmentActivity activity;

    public BaseNavigationListener(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    public void doBack() {
        activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

	@Override
	public Intent doHome() {
		activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		return null;
		
	}
}
