package com.workout.log.db;

import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;



import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.workout.log.bo.Workoutplan;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.widget.Toast;

public class WorkoutplanMapper   {

	DataBaseHelper myDBHelper;
	String sql;
	Context context;
	
	
	public WorkoutplanMapper(Context context){
		this.context = context;
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
	
	/**
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
			w.setId(Integer.parseInt(cursor.getString(0)));
			w.setName(cursor.getString(1));
			w.setTimeStamp(new Date(cursor.getLong(2)));
		}

		return w;
	}
	
	public void setCurrent(int workoutplanId){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "UPDATE Workoutplan SET Current = NUll";
		db.execSQL(sql);
		sql = "Update Workoutplan SET Current = 1 WHERE Workoutplan_Id = " + workoutplanId;
		db.execSQL(sql);
		Toast toast = Toast.makeText(context, "Dieser Trainingsplan ist nun aktiv!", Toast.LENGTH_SHORT );
		toast.show();
		db.close();
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
		w.setId(id);
		w.setTimeStamp(date);
        
        // return contact list
	}
	/**
	 * Get all Workoutplans in the Database
	 * 
	 * @return ArrayList<Workoutplan>
	 * @author Eric Schmidt & Florian Blessing
	 */
	public ArrayList<Workoutplan> getAll() {
		ArrayList<Workoutplan> workoutplanList = new ArrayList<Workoutplan>();
		
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT Workoutplan_Id, WorkoutplanName, Timestamp FROM Workoutplan";

		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				Workoutplan w = new Workoutplan();
				w.setId(Integer.parseInt(cursor.getString(0)));
				w.setName(cursor.getString(1));
			    w.setTimeStamp(new Date(cursor.getLong(2)));
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

	public void addWP(Editable name) {
		
		Date date;
		date = Calendar.getInstance().getTime();
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "INSERT INTO Workoutplan (WorkoutplanName, Timestamp) VALUES ('" + String.valueOf(name) + "', '"+ String.valueOf(date) + "')";
		db.execSQL(sql);
		db.close();
		
	}
	
	public void addTrainingDayToWorkoutplan(int trainingDayId, int workoutplanId) {
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "INSERT INTO WorkoutplanHasTrainingDay (Workoutplan_Id, TrainingDay_Id) VALUES (" + workoutplanId + ", " + trainingDayId + ")";
		db.execSQL(sql);
		db.close();
	}
	
}
