package com.workout.log.db;

import java.io.IOException;
import java.util.ArrayList;

import com.workout.log.bo.Exercise;
import com.workout.log.bo.TrainingDay;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class TrainingDayMapper {
	
	DataBaseHelper myDBHelper;
	String sql;
	
	public TrainingDayMapper (Context context){
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
		
	// Hinzufügen von Trainingstagen
	public void add(TrainingDay d){
		int id = 0;
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		db.close();
	}
	
	/*
	 * Get all TrainingDays from one Workoutplan using the 
	 * 
	 * @param int workoutplanId
	 * @return ArrayList<TrainingDay>
	 * @author Eric Schmidt & Florian Blessing
	 */
	public ArrayList<TrainingDay> getAll(int workoutplanId) {
		ArrayList<TrainingDay> trainingdayList = new ArrayList<TrainingDay>();
		
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		
		sql = "SELECT TrainingDay_Id FROM WorkoutplanHasTrainingDay WHERE Workoutplan_Id = " + workoutplanId;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				TrainingDay d = getTrainingDayById(Integer.parseInt(cursor.getString(0)));
				trainingdayList.add(d);
			}while(cursor.moveToNext());
		}
		db.close();
		return trainingdayList;
	}
	// Löschen von Trainingstagen
	public void delete(TrainingDay d){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "DELETE * FROM TrainingDay WHERE *";
		db.close();
		
	}
	// Trainingstag updaten , z.B nach einer Änderung von einem Trainingstag
	public TrainingDay update(TrainingDay d){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "UPDATE TrainingDay SET * WHERE *";
		db.close();
		return d;
	}
	public TrainingDay getTrainingDayById(int id){
		TrainingDay d = new TrainingDay();
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
	    sql = "SELECT TrainingDay_Id, TrainingDayName FROM TrainingDay WHERE TrainingDay_Id = " + id;
	    Cursor cursor = db.rawQuery(sql, null);
	    if (cursor.moveToFirst()){
		    d.setID(Integer.parseInt(cursor.getString(0)));
		    d.setName(cursor.getString(1));
	    }
	    db.close();
	    return d;

	}
}

