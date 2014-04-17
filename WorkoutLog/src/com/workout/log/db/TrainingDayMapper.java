package com.workout.log.db;

import java.io.IOException;
import java.util.ArrayList;
import com.workout.log.data.TrainingDay;
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
	
	// Abfrage für alle Trainingstage
	public ArrayList<TrainingDay> getAll() {
		ArrayList<TrainingDay> trainingdayList = new ArrayList<TrainingDay>();
		
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		
		sql = "SELECT * FROM TrainingDay";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				TrainingDay d = new TrainingDay();
				d.setID(Integer.parseInt(cursor.getString(0)));
				d.setName(cursor.getString(1));
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
}
