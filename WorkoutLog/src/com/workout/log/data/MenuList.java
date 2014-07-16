package com.workout.log.data;

import java.util.ArrayList;
import java.util.List;

public class MenuList {
	private List<DrawerItem> dataList;
	
	public MenuList() {
		 dataList = new ArrayList<DrawerItem>();
		 dataList.add(new DrawerItem("Überblick"));
		 dataList.add(new DrawerItem("Trainingspläne"));
		 dataList.add(new DrawerItem("Trainingtage"));
		 dataList.add(new DrawerItem("Übungen"));
		 dataList.add(new DrawerItem("Statisitik"));
		 dataList.add(new DrawerItem("Import/Export"));
	}
	public List<DrawerItem> getDataList() {
		return dataList;
	}
	public void setDataList(List<DrawerItem> dataList) {
		this.dataList = dataList;
	}
	
	
}
