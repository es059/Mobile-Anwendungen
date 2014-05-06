package com.workout.log.db;

import java.io.IOException;
import java.util.ArrayList;

import com.workout.log.bo.Exercise;
import com.workout.log.bo.TrainingDay;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;

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
	public void add(Editable d){
		
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "INSERT INTO TrainingDay (TrainingDayName) VALUES ('"+ d + "')";
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
	public void ExerciseAddToTrainingDay(int trainingsDayId, int exerciseId, Editable ETW, Editable ETS) {
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		String sql = "INSERT INTO TrainingDayHasExercise (TrainingDay_Id, Exercise_Id) VALUES (" + trainingsDayId +","+exerciseId+")";
		String sql2 = "INSERT INTO PerformanceTarget (TrainingDay_Id, Exercise_Id, RepetitionTarget, SetTarget) VALUES  (" + trainingsDayId +","+exerciseId+", " + ETW + ","+ETS+")";
		db.execSQL(sql);
		db.execSQL(sql2);
		db.close();
	}
	public void exerciseDeleteFromTrainingDay(int trainingDayHasExerciseId) {
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql ="DELETE FROM TrainingDayHasExercise WHERE TrainingstagHatUebungId = " +trainingDayHasExerciseId + "";
		db.execSQL(sql);
		db.close();
	}
	
	public void deleteTrainingDayFromWorkoutplan(int trainingDayId, int workoutplanId, int primarykey) {
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "DELETE FROM WorkoutplanHasTrainingDay WHERE TrainingDay_Id=" + trainingDayId + " AND Workoutplan_Id=" + workoutplanId + " AND WorkoutplanHasTrainingDay_Id=" + primarykey +"";
		db.execSQL(sql);
		db.close();
	}
	
}

