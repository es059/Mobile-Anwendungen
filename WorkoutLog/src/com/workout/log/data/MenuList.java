package com.workout.log.data;

import java.util.ArrayList;
import java.util.List;

import com.example.workoutlog.R;

public class MenuList {
	private List<ListItem> dataList;
	
	public MenuList() {
		 dataList = new ArrayList<ListItem>();
		 dataList.add(new DrawerHeader("Funktionen"));
		 
		 DrawerItem diary = new DrawerItem("Tagebuch");
		 diary.setImgResID(R.drawable.task);
		 dataList.add(diary);
		 
		 DrawerItem statistic = new DrawerItem("Statisitik");
		 statistic.setImgResID(R.drawable.statistic);
		 dataList.add(statistic);
		 
		 DrawerItem importExport = new DrawerItem("Import/Export");
		 importExport.setImgResID(R.drawable.import_export);
		 dataList.add(importExport);
		 
		 dataList.add(new DrawerHeader("Verwaltung"));
		 
		 DrawerItem workoutplan = new DrawerItem("Trainingspläne");
		 workoutplan.setImgResID(R.drawable.calendar130);
		 dataList.add(workoutplan);
		 
		 DrawerItem trainingDay = new DrawerItem("Trainingstage");
		 trainingDay.setImgResID(R.drawable.calendar);
		 dataList.add(trainingDay);
		 
		 DrawerItem exercise = new DrawerItem("Übungen");
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
