package com.workout.log.db;

import java.io.IOException;
import java.util.ArrayList;
import com.workout.log.bo.MuscleGroup;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
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
		ArrayList<MuscleGroup> muscleGroupList = new ArrayList<MuscleGroup>();
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		
		sql = "SELECT * FROM MuscleGroup"; 
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				MuscleGroup muscleGroup = new MuscleGroup();
				muscleGroup.setId(Integer.parseInt(cursor.getString(0)));
				muscleGroup.setName(cursor.getString(1));
				muscleGroupList.add(muscleGroup);
			}while(cursor.moveToNext());
		}
		db.close();
		return muscleGroupList;	
	}
	
	public MuscleGroup getMuscleGroupById(int MuscleGroupId){
		MuscleGroup muscleGroup = null;
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT * FROM MuscleGroup WHERE MuscleGroup_Id = " + MuscleGroupId;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			muscleGroup = new MuscleGroup();
			muscleGroup.setId(Integer.parseInt(cursor.getString(0)));
			muscleGroup.setName(cursor.getString(1));
		}
		db.close();
		return muscleGroup;
	}	
}
