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
import android.text.Editable;
import android.util.Log;

public class ExerciseMapper {
	
	DataBaseHelper myDBHelper;
	String sql;
	int id =  10;
	String bezeichung = "Kreuzheben";
	
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
	public void add(String a){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "INSERT INTO Uebung (Bezeichung) VALUES ('" +a +"')";
		db.execSQL(sql);
		db.close();

		
	}
	public void delete(int e){	
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "DELETE FROM Uebung WHERE UebungId =" + e + "";
		db.execSQL(sql);
		db.close();
	}
	
	public ArrayList<Exercise> getAll() {
		ArrayList<Exercise> exerciseList = new ArrayList<Exercise>();
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT * FROM Uebung";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()) {
            do {
              Exercise e = new Exercise();
              e.setID(Integer.parseInt(cursor.getString(0)));
              e.setName(cursor.getString(1));
              exerciseList.add(e);
            	
            } while (cursor.moveToNext());
        }
   
        cursor.close();
        

    return exerciseList;
		
	}
	
	public void update(int ID, String bezeichnung){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		String sql = "UPDATE Uebung SET Bezeichung='" + bezeichnung +  "' WHERE UebungId=" + ID + "";
		db.execSQL(sql);
		db.close();
		
	
	}
	
	
	public ArrayList<Exercise> searchKeyString(String key){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
	    ArrayList<Exercise> exerciseList = new ArrayList<Exercise>();
	   // Log.d("searchKeyString");

	        // Alles Anfragen auswählen
	     //   String selectQuery = "SELECT  * FROM Uebung WHERE Bezeichung="+"'+ key +'";
	     String selectQuery =  "SELECT * FROM Uebung WHERE Bezeichung LIKE '%" + key + "%'";
	        Cursor cursor = db.rawQuery(selectQuery, null);
	        // you can change it to
	        // db.rawQuery("SELECT * FROM "+table+" WHERE KEY_KEY LIKE ?", new String[] {key+"%"});
	        // if you want to get everything starting with that key value

	        // looping through all rows and adding to list
	        if (cursor.moveToFirst()) {
	            do {
	              Exercise e = new Exercise();
	              e.setID(Integer.parseInt(cursor.getString(0)));
	              e.setName(cursor.getString(1));
	              exerciseList.add(e);
	            	
	            } while (cursor.moveToNext());
	        }
	   
	        cursor.close();
	        

	    return exerciseList;
	}
	
	
	public ArrayList<Exercise> findExerciseByName(){
		ArrayList<Exercise> exerciseList = new ArrayList<Exercise>();
		
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		
		sql = "SELECT * FROM Exercise";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				Exercise ex = new Exercise();
				ex.setID(Integer.parseInt(cursor.getString(0)));
				ex.setName(cursor.getString(1));
			//	w.setTimeStamp(new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(cursor.getString(2)));
				exerciseList.add(ex);
			}while(cursor.moveToNext());
		}
		db.close();
		return exerciseList;
	
	
	
	}
}
