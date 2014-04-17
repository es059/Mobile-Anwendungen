package com.workout.log.db;

import java.io.IOException;
import java.util.ArrayList;

import com.workout.log.data.Exercise;
import com.workout.log.data.Workoutplan;

import android.content.Context;
import android.database.CursorJoiner.Result;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

public class ExerciseMapper {
	
	DataBaseHelper myDBHelper;
	String sql;
	
	public ExerciseMapper(Context context){
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
	public void add(Exercise e){
		int id = 0;
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT MAX(Exercise_id) FROM Exercise";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			id = Integer.parseInt(cursor.getString(0));
		}
		sql = "INSERT INTO Exercise (Exercise_Id) VALUES (id)";
		db.execSQL(sql);
		e.setID(id);
		db.close();
			
		
		
		
	}
	public void delete(Exercise e){	
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "DELETE FROM Exercise WHERE *";
		db.close();
	}
	
	public Exercise update(Exercise e){
		
	return e;
	}
	
	
	public String searchKeyString(String key){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
	    StringBuilder rtn = new StringBuilder();
	   // Log.d("searchKeyString");

	        // Alles Anfragen auswählen
	        String selectQuery = "SELECT  * FROM Exercise WHERE KEY_KEY=?";

	        Cursor cursor = db.rawQuery(selectQuery,  new String[] {key});
	        // you can change it to
	        // db.rawQuery("SELECT * FROM "+table+" WHERE KEY_KEY LIKE ?", new String[] {key+"%"});
	        // if you want to get everything starting with that key value

	        // looping through all rows and adding to list
	        if (cursor.moveToFirst()) {
	            do {
	               // Log.d("searchKeyString","searching");

	                rtn.append(",").append(cursor.getString(2));
	            } while (cursor.moveToNext());
	        }
	        cursor.close();
	        //Log.d("searchKeyString","finish search");

	    return rtn.toString();
	}
	
	
	public ArrayList<Exercise> getAllExercise(){
		ArrayList<Exercise> exerciseList = new ArrayList<Exercise>();
		
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		
		sql = "SELECT * FROM Exercise";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				Exercise ex = new Exercise();
				ex.setID(Integer.parseInt(cursor.getString(0)));
				ex.setName(cursor.getString(1));
				exerciseList.add(ex);
			}while(cursor.moveToNext());
		}
		db.close();
		return exerciseList;
		}
		
	 public Exercise getExerciseById(int id){
		    SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		    sql = "SELECT Exercise_Id FROM Exercise WHERE Exercise_Id = " + id;
		    Cursor cursor = db.rawQuery(sql, null);
		    Exercise ex = new Exercise();
		    ex.setID(Integer.parseInt(cursor.getString(0)));
		    db.close();
		    return ex;
	}
}
