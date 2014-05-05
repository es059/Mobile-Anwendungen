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

package com.workout.log;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;

import com.workout.log.bo.Exercise;
import com.workout.log.customLayout.ListViewExercise;
import com.workout.log.customLayout.ListViewExerciseWithoutSetsReps;

public class StableArrayAdapter extends ArrayAdapter<Exercise> {

    final int INVALID_ID = -1;

    HashMap<Exercise, Integer> mIdMap = new HashMap<Exercise, Integer>();

    public StableArrayAdapter(Context context, int textViewResourceId, List<Exercise> objects) {
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
       Exercise item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
    @Override
	 public View getView(int position, View convertView, ViewGroup parent) {
		 Exercise exercise = getItem(position);
		 ListViewExerciseWithoutSetsReps listViewExercise = null;
		 if(convertView != null){
			 listViewExercise = (ListViewExerciseWithoutSetsReps) convertView;
		 }
		 else{
			 listViewExercise = new ListViewExerciseWithoutSetsReps(getContext());
		 }
		 
		 listViewExercise.setExercise(exercise);

		 return listViewExercise;
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
