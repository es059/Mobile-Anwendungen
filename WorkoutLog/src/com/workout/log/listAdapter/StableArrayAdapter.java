/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.workout.log.listAdapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.workout.log.bo.TrainingDay;
import com.workout.log.customLayout.ListViewTrainingDay;

public class StableArrayAdapter extends ArrayAdapter<TrainingDay> {

    final int INVALID_ID = -1;

    HashMap<TrainingDay, Integer> mIdMap = new HashMap<TrainingDay, Integer>();

    public StableArrayAdapter(Context context, int textViewResourceId, List<TrainingDay> objects) {
       super(context, textViewResourceId, objects);
       for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
      }
    }

   @Override

    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
       TrainingDay item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
    @Override
	 public View getView(int position, View convertView, ViewGroup parent) {
		 TrainingDay trainingDay = getItem(position);
		 ListViewTrainingDay listViewTrainingDay = null;
		 if(convertView != null){
			 listViewTrainingDay = (ListViewTrainingDay) convertView;
		 }
		 else{
			 listViewTrainingDay = new ListViewTrainingDay(getContext());
		 }
		 
		 listViewTrainingDay.setTrainingDay(trainingDay);;

		 return listViewTrainingDay;
	 }
    @Override
    public int getViewTypeCount() {
    	return 1;
    }

	@Override
	public int getItemViewType(int position) {
		return 0;
	}
}