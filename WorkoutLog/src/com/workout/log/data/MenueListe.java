package com.workout.log.data;
import java.util.ArrayList;
import java.util.List;

import com.example.workoutlog.R;
import com.workout.log.data.*;
public class MenueListe {

	public List<DrawerItem> dataList;
	public MenueListe() {
		 dataList = new ArrayList<DrawerItem>();
		 dataList.add(new DrawerItem("Überblick", R.drawable.ic_action_email));
		 dataList.add(new DrawerItem("Trainingspläne", R.drawable.ic_action_good));
		 dataList.add(new DrawerItem("Trainingtage", R.drawable.ic_action_gamepad));
		 dataList.add(new DrawerItem("Übungen", R.drawable.ic_action_labels));
		 dataList.add(new DrawerItem("Statisitik", R.drawable.ic_action_search));
		 dataList.add(new DrawerItem("About", R.drawable.ic_action_cloud));
	}
	public List<DrawerItem> getDataList() {
		return dataList;
	}
	public void setDataList(List<DrawerItem> dataList) {
		this.dataList = dataList;
	}
	
	
}
