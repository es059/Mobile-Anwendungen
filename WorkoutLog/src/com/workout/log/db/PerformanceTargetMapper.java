package com.workout.log.db;

import java.io.IOException;

import android.content.Context;
import android.database.SQLException;

/*
 * Mapper Class of BusinessObject PerformanceTarget
 * 
 * @author Eric Schmidt
 */

public class PerformanceTargetMapper {
	private DataBaseHelper myDBHelper;
	private String sql;
	
	public PerformanceTargetMapper(Context context){
		myDBHelper = new DataBaseHelper(context);
		try {	 
	       	myDBHelper.createDataBase();
	 	} catch (IOException ioe) {
	 		throw new Error("Unable to create database");
	 	}
	 	try {
	 		myDBHelper.openDataBase();
	 	}catch(SQLException sqle){
	 		throw sqle;
	 	}
	}
	
	
}
