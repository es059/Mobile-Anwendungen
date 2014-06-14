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
	
	private DataBaseHelper myDBHelper;
	private String sql;
	
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
		
	/**
	 * Add a TrainingDay to the database
	 * 
	 * @param trainingDayName
	 */
	public void add(TrainingDay d){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		int id = 1;
		sql = "SELECT MAX(TrainingDay_Id) FROM TrainingDay";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			if (!cursor.isNull(0)){
				id = Integer.parseInt(cursor.getString(0));
				id++;
			}
		}	
		sql = "INSERT INTO TrainingDay (TrainingDay_Id, TrainingDayName) VALUES (" + id + ", '"+ d.getName() + "')";
		db.execSQL(sql);
		db.close();
	}
	
	/**
	 * Get all TrainingDays from one Workoutplan using the workoutplanId
	 * 
	 * @param int workoutplanId
	 * @return ArrayList<TrainingDay>
	 * @author Eric Schmidt & Florian Blessing
	 */
	public ArrayList<TrainingDay> getAll(int workoutplanId) {
		ArrayList<TrainingDay> trainingdayList = new ArrayList<TrainingDay>();
		
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		
		sql = "SELECT TrainingDay_Id, WorkoutplanHasTrainingDay_Id FROM WorkoutplanHasTrainingDay WHERE Workoutplan_Id = " + workoutplanId;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				TrainingDay d = getTrainingDayById(Integer.parseInt(cursor.getString(0)));
				d.setTrainingDayHasWorkoutplanId(cursor.getInt(1));
				trainingdayList.add(d);
			}while(cursor.moveToNext());
		}
		db.close();
		return trainingdayList;
	}
	// L�schen von Trainingstagen
	public void delete(TrainingDay d){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "DELETE FROM TrainingDay WHERE TrainingDay_Id = " + d.getId();
		db.execSQL(sql);
		db.close();	
	}
	// Trainingstag updaten , z.B nach einer �nderung von einem Trainingstag
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
		    d.setId(Integer.parseInt(cursor.getString(0)));
		    d.setName(cursor.getString(1));
	    }
	    db.close();
	    return d;

	}
	
	public ArrayList<TrainingDay> getAllTrainingDay() {
		ArrayList<TrainingDay> trainingdayList = new ArrayList<TrainingDay>();
		
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		
		sql = "SELECT * FROM TrainingDay";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				TrainingDay d = new TrainingDay();
				d.setId(Integer.parseInt(cursor.getString(0)));
				d.setName(cursor.getString(1));
				trainingdayList.add(d);
			}while(cursor.moveToNext());
		}
		db.close();
		return trainingdayList;
	}
	
	public void ExerciseAddToTrainingDay(int trainingsDayId, int exerciseId, int eTargetSetCount,  int eTargetRepCount) {
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "INSERT INTO TrainingDayHasExercise (TrainingDay_Id, Exercise_Id) VALUES (" + trainingsDayId +","+exerciseId+")";
		db.execSQL(sql);
		/**
		 * Idee: Weiteres Attribut in TrainingDayHasExercise : PerformanceTarget Id um jede �bung einzeln anzusprechen
		 */
		sql= "INSERT INTO PerformanceTarget (TrainingDay_Id, Exercise_Id, RepetitionTarget, SetTarget) VALUES  (" + trainingsDayId +","+exerciseId+", " + eTargetRepCount + ","+eTargetSetCount+")";
		db.execSQL(sql);
		db.close();
	}
	
	public void exerciseDeleteFromTrainingDay(int trainingDayId, Exercise e) {
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql ="DELETE FROM TrainingDayHasExercise WHERE TrainingDay_Id = " + trainingDayId + " AND Exercise_Id = " + e.getId();
		db.execSQL(sql);
		db.close();
	}
	
	/**
	 * Delete a trainingDay from one Workoutplan
	 * 
	 * @param trainingDayId
	 * @param workoutplanId
	 * @param primarykey
	 */
	public void deleteTrainingDayFromWorkoutplan(int trainingDayId, int workoutplanId, int primarykey) {
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "DELETE FROM WorkoutplanHasTrainingDay WHERE TrainingDay_Id=" + trainingDayId + " AND Workoutplan_Id=" + workoutplanId + " AND WorkoutplanHasTrainingDay_Id=" + primarykey +"";
		db.execSQL(sql);
		db.close();
	}
	
	/**
	 * Delete a trainingDay from all workoutplans
	 * 
	 * @param trainingDayId
	 * @author Eric Schmidt
	 */
	public void deleteTrainingDayFromAllWorkoutplan(int trainingDayId){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "DELETE FROM WorkoutplanHasTrainingDay WHERE TrainingDay_Id=" + trainingDayId;
		db.execSQL(sql);
		db.close();
	}
	
	public ArrayList<TrainingDay> searchKeyString(String key){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
	    ArrayList<TrainingDay> trainingDayList = new ArrayList<TrainingDay>();

	     String selectQuery =  "SELECT * FROM TrainingDay WHERE TrainingDayName LIKE '%" + key + "%'";
	        Cursor cursor = db.rawQuery(selectQuery, null);
	        if (cursor.moveToFirst()) {
	            do {
	              TrainingDay trainingDay = new TrainingDay();
	              trainingDay.setId(Integer.parseInt(cursor.getString(0)));
	              trainingDay.setName(cursor.getString(1));
	              trainingDayList.add(trainingDay);
	            	
	            } while (cursor.moveToNext());
	        }
	   
	        cursor.close();
	        

	    return trainingDayList;
	}
}

