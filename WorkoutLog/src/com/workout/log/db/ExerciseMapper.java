package com.workout.log.db;

import java.io.IOException;
import java.util.ArrayList;

import com.workout.log.bo.Exercise;
import com.workout.log.bo.Workoutplan;

import android.content.Context;
import android.database.CursorJoiner.Result;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.SQLException;
import android.text.Editable;
import android.util.Log;

public class ExerciseMapper {
	
	private DataBaseHelper myDBHelper;
	private String sql;
	
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
	
	/**
	 * Get all Exercises from one TrainingDay
	 * 
	 * @param int trainingDayId
	 * @return ArrayList<Exercise>
	 * @author Eric Schmidt & Florian Belssing
	 */	
	public ArrayList<Exercise> getExerciseByTrainingDay(int trainingDayId){
		ArrayList<Exercise> exerciseList = new ArrayList<Exercise>();
		
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		
		sql = "SELECT Exercise_Id FROM TrainingDayHasExercise WHERE TrainingDay_Id = "
				+ trainingDayId;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				Exercise exercise = getExerciseById(Integer.parseInt(cursor.getString(0)));
				exerciseList.add(exercise);
			}while(cursor.moveToNext());
		}
		db.close();
		return exerciseList;
	}
	/**
	 * Select all Exercises of one Musclegroup using a ArrayList of Exercises of one TrainingDay
	 * 
	 * @param ArrayList<Exercise>
	 * @param MuscleGroupId
	 * @author Eric Schmidt
	 */
	public ArrayList<Exercise> getExerciseByMuscleGroup(ArrayList<Exercise> exercises, int muscleGroupId){
		ArrayList<Exercise> exerciseList = new ArrayList<Exercise>();
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		
		for (Exercise e : exercises){
			sql = "SELECT Exercise_Id, ExerciseName FROM Exercise WHERE Exercise_Id = " + e.getId() + " AND MuscleGroup_Id = " + muscleGroupId;
			Cursor cursor = db.rawQuery(sql, null);
				if (cursor.moveToFirst()){
					Exercise exercise = new Exercise();
				    exercise.setID(Integer.parseInt(cursor.getString(0)));
				    exercise.setName(cursor.getString(1));
				    exerciseList.add(exercise);
				}
		}
		
		return exerciseList;
	}
	/**
	 * Get one Exercise by an id
	 * 
	 *  @param int id
	 *  @return Exercise
	 *  @author Eric Schmidt & Florian Blessing
	 */
	 public Exercise getExerciseById(int exerciseId){
		    Exercise exercise = new Exercise();
		    SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		    sql = "SELECT Exercise_Id, ExerciseName FROM Exercise WHERE Exercise_Id = " + exerciseId;
		    Cursor cursor = db.rawQuery(sql, null);
		    if (cursor.moveToFirst()){
			    exercise.setID(Integer.parseInt(cursor.getString(0)));
			    exercise.setName(cursor.getString(1));
		    }
		    db.close();
		    return exercise;
	}
}
