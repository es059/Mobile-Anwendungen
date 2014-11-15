package com.workout.log.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.workout.log.bo.MuscleGroup;

/**
 * Mapper Class of BusinessObject MuscleGroup
 * 
 * @author Eric Schmidt
 */
public class MuscleGroupMapper {
	private DataBaseHelper myDBHelper;
	private String sql;
	
	public MuscleGroupMapper(Context context){
		myDBHelper = DataBaseHelper.getInstance(context);
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
		cursor.close();
		
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
		cursor.close();
		
		return muscleGroup;
	}	
}
