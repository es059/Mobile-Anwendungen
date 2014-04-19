package com.workout.log.db;

import java.io.IOException;
import java.util.ArrayList;

import com.workout.log.bo.MuscleGroup;

import android.content.Context;
import android.database.SQLException;

/*
 * Mapper Class of BusinessObject MuscleGroup
 * 
 * @author Eric Schmidt
 */
public class MuscleGroupMapper {
	private DataBaseHelper myDBHelper;
	private String sql;
	
	public MuscleGroupMapper(Context context){
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
	
	public ArrayList<MuscleGroup> getAll(){
		return null;
		
	}
	
	public MuscleGroup getMuscleGroupById(int Id){
		return null;
		
	}
	
}
