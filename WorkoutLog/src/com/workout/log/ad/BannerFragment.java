package com.workout.log.ad;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.remic.workoutlog.R;

/**
 * A simple {@link Activity} that embeds an AdView.
 */
public class BannerFragment extends Fragment {
  /** The view to show the ad. */
  private AdView adView;

  /* Your ad unit id. Replace with your actual ad unit id. */
  private static final String AD_UNIT_ID = "ca-app-pub-8930461526777410/4833519485";

  /** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.banner_fragment, container, false);
	
	    // Create an ad.
	    adView = new AdView(getActivity());
	    adView.setAdSize(AdSize.BANNER);
	    adView.setAdUnitId(AD_UNIT_ID);
	
	    // Add the AdView to the view hierarchy. The view will have no size
	    // until the ad is loaded.
	    LinearLayout layout = (LinearLayout) view.findViewById(R.id.ad_wrapper);
	    layout.addView(adView);
	
	    // Create an ad request. Check logcat output for the hashed device ID to
	    // get test ads on a physical device.
	    AdRequest adRequest = new AdRequest.Builder()
	    	.addKeyword("Gym")
	    	.addKeyword("Fitness")
	    	.addKeyword("Muscle")
	    	.addKeyword("Health")
	    	.addKeyword("Bodybuilding")
	    	.addKeyword("Gym")
	        .build();
		 
	    // Start loading the ad in the background.
	    adView.loadAd(adRequest);
	    
	    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    if (adView != null) {
      adView.resume();
    }
  }

  @Override
  public void onPause() {
    if (adView != null) {
      adView.pause();
    }
    super.onPause();
  }

  /** Called before the activity is destroyed. */
  @Override
  public void onDestroy() {
    // Destroy the AdView.
    if (adView != null) {
      adView.destroy();
    }
    super.onDestroy();
  }
}