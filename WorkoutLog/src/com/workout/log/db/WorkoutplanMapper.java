package com.workout.log.db;

import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;



import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.workout.log.data.Workoutplan;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class WorkoutplanMapper   {

	DataBaseHelper myDBHelper;
	String sql;
	
	
	public WorkoutplanMapper(Context context){
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
	
	/*
	 * Get Currently selected Workoutplan. Workoutplan is current if column 'Current' 
	 * has Value 1
	 * 
	 * @ return Workoutplan
	 */
	public Workoutplan getCurrent(){
		Workoutplan w = new Workoutplan();
		//Establish Database Conncetion
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT Workoutplan_Id, WorkoutplanName, Timestamp FROM Workoutplan WHERE Current = 1";
		
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			w.setID(Integer.parseInt(cursor.getString(0)));
			w.setName(cursor.getString(1));
			//w.setTimeStamp ((cursor.getString(2));
		}

		return w;
	}
	
	public void  add(Workoutplan w) {
		int id = 0;
		Date date;
		
		date = Calendar.getInstance().getTime();
		
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT MAX(Trainingsplan_id) FROM Trainingsplan";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			id = Integer.parseInt(cursor.getString(0));
		}
		sql = "INSERT INTO WorkoutPlan VALUES (" + id + ", " + w.getName() +", " + String.valueOf(date) + ")";
		db.execSQL(sql);
		// Insert ID into Data Object
		w.setID(id);
		w.setTimeStamp(date);
        db.close();
        // return contact list
	}
	
	public ArrayList<Workoutplan> getAll() {
		ArrayList<Workoutplan> workoutplanList = new ArrayList<Workoutplan>();
		
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		
		sql = "SELECT * FROM Trainingsplan";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				Workoutplan w = new Workoutplan();
				w.setID(Integer.parseInt(cursor.getString(0)));
				w.setName(cursor.getString(1));
			//	w.setTimeStamp(new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(cursor.getString(2)));
				workoutplanList.add(w);
			}while(cursor.moveToNext());
		}
		db.close();
		return workoutplanList;
	}
	
	public void delete(Workoutplan w){

	}
	
	public Workoutplan update(Workoutplan w){
		return w;
	}	
}
