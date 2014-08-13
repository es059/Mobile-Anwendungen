package com.workout.log.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.remic.workoutlog.R;

public class MenuList {
	private List<ListItem> dataList;
	
	public MenuList(Context context) {
		 dataList = new ArrayList<ListItem>();
		 dataList.add(new DrawerHeader(context.getResources().getString(R.string.MenuList_Functions)));
		 
		 DrawerItem diary = new DrawerItem(context.getResources().getString(R.string.MenuList_Diary));
		 diary.setImgResID(R.drawable.task);
		 dataList.add(diary);
		 
		 DrawerItem statistic = new DrawerItem(context.getResources().getString(R.string.MenuList_Statistic));
		 statistic.setImgResID(R.drawable.statistic);
		 dataList.add(statistic);
		 
		 DrawerItem importExport = new DrawerItem(context.getResources().getString(R.string.MenuList_Import_Export));
		 importExport.setImgResID(R.drawable.import_export);
		 dataList.add(importExport);
		 
		 dataList.add(new DrawerHeader(context.getResources().getString(R.string.MenuList_Manage)));
		 
		 DrawerItem workoutplan = new DrawerItem(context.getResources().getString(R.string.MenuList_Workoutplans));
		 workoutplan.setImgResID(R.drawable.calendar130);
		 dataList.add(workoutplan);
		 
		 DrawerItem trainingDay = new DrawerItem(context.getResources().getString(R.string.MenuList_Trainingdays));
		 trainingDay.setImgResID(R.drawable.calendar);
		 dataList.add(trainingDay);
		 
		 DrawerItem exercise = new DrawerItem(context.getResources().getString(R.string.MenuList_Exercises));
		 exercise.setImgResID(R.drawable.exercise);
		 dataList.add(exercise);
		 
	}
	public List<ListItem> getDataList() {
		return dataList;
	}
	public void setDataList(List<ListItem> dataList) {
		this.dataList = dataList;
	}
	
	
}
