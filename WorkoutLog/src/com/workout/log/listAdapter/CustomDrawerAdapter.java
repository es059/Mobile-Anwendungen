package com.workout.log.listAdapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.workoutlog.R;
import com.workout.log.data.DrawerHeader;
import com.workout.log.data.DrawerItem;
import com.workout.log.data.ListItem;
import com.workout.log.db.ExerciseMapper;
import com.workout.log.db.TrainingDayMapper;
import com.workout.log.db.WorkoutplanMapper;
 
public class CustomDrawerAdapter extends ArrayAdapter<ListItem> {
      private List<ListItem> drawerItemList;
      private LayoutInflater layoutInflater;
      private Context context = null;
      private boolean upperPart = true;
 
      public CustomDrawerAdapter(Context context, List<ListItem> listItems) {
            super(context, 0, listItems);
            this.drawerItemList = listItems;
            this.context = context;
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      }
 
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
 
            final ListItem item = drawerItemList.get(position);
            if (item != null){
    			if (item.isSection()){
    				DrawerHeader drawerHeader = (DrawerHeader) item;
    				
    				if (drawerHeader.getTitle() == "Verwaltung") upperPart = false;
    				view = layoutInflater.inflate(R.layout.custom_drawer_header, null);
    				
    				if (upperPart){
    					view.setBackgroundColor(Color.parseColor("#3a3a3a"));
    				}else{
    					view.setBackgroundColor(Color.parseColor("#232323"));
    				}
    				view.setOnClickListener(null); 
    				view.setOnLongClickListener(null);
    				view.setLongClickable(false);
    				
    				final TextView sectionView = (TextView) view.findViewById(R.id.drawer_header_text);
    				sectionView.setTextColor(Color.parseColor("#fe9901"));
    				sectionView.setText(drawerHeader.getTitle());
    			}else{
    				DrawerItem drawerItem = (DrawerItem) item;
    				view = layoutInflater.inflate(R.layout.custom_drawer_item, null);
    				
    				if (upperPart){
    					view.setBackgroundColor(Color.parseColor("#3a3a3a"));
    				}else{
    					view.setBackgroundColor(Color.parseColor("#232323"));
    				}
    				
    				if (drawerItem.getItemName() == "Import/Export") view.setPadding(0, 0, 0, 100);
    				
    				final TextView 	itemName = (TextView) view.findViewById(R.id.drawer_itemName);
    				final ImageView	itemIcon = (ImageView) view.findViewById(R.id.drawer_icon);
    				final TextView 	itemInformation = (TextView) view.findViewById(R.id.drawer_information);
    				
    				itemInformation.setTextColor(Color.WHITE);
    				itemName.setTextColor(Color.WHITE);
    				
    				switch (drawerItem.getItemName()){
	    				case "Trainingspläne":
	    					WorkoutplanMapper wMapper = new WorkoutplanMapper(context);
	    					itemInformation.setText(String.valueOf(wMapper.getAll().size()));
	    					break;
	    				case "Trainingstage":
	    					TrainingDayMapper tMapper = new TrainingDayMapper(context);
	    					itemInformation.setText(String.valueOf(tMapper.getAllTrainingDay().size()));
	    					break;
	    				case "Übungen":
	    					ExerciseMapper eMapper = new ExerciseMapper(context);
	    					itemInformation.setText(String.valueOf(eMapper.getAllExercise().size()));
	    					break;
    				}
    				
    				itemName.setText(drawerItem.getItemName());
    				if (drawerItem.getImgResID() != -1){
    					itemIcon.setImageDrawable(view.getResources().getDrawable(drawerItem.getImgResID()));
    				}
    			}
    		}
            return view;
      }
}
