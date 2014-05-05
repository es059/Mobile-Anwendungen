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
	private int mgID;
	
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
	
	public void add(String a, String b){

		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT MuscleGroup_Id FROM MuscleGroup WHERE MuscleGroupName='" + b + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			mgID = Integer.parseInt(cursor.getString(0));
		}
		String sql1 = "INSERT INTO Exercise (ExerciseName, MuscleGroup_Id ) VALUES ('" +a +"', " + mgID + ")";
		db.execSQL(sql1);
		db.close();
	}


	public void add(Exercise e){
		int id = 0;
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT MAX(Exercise_id) FROM Exercise";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			id = Integer.parseInt(cursor.getString(1));
		}
		sql = "INSERT INTO Exercise (Exercise_Id) VALUES (id)";
		db.execSQL(sql);
		e.setID(id);
		db.close();
	}
	
	public void delete(int e){	
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "DELETE FROM Exercise WHERE Exercise_Id =" + e + "";
		db.execSQL(sql);
		db.close();
	}
	
	public ArrayList<Exercise> getAll() {
		ArrayList<Exercise> exerciseList = new ArrayList<Exercise>();
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT * FROM Exercise";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()) {
            do {
              Exercise e = new Exercise();
              e.setID(Integer.parseInt(cursor.getString(1)));
              e.setName(cursor.getString(2));
              exerciseList.add(e);
            	
            } while (cursor.moveToNext());
        }
   
        cursor.close();
        

    return exerciseList;
		
	}
	public ArrayList<String> getAllbyString() {
		ArrayList<String> exerciseList = new ArrayList<String>();
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT * FROM Exercise";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()) {
            do {
            	String e = "";
              
              e= cursor.getString(2);
              exerciseList.add(e);
            	
            } while (cursor.moveToNext());
        }
   
        cursor.close();
        

    return exerciseList;
		
	}
	
	public void update(int ID, String bezeichnung){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		String sql = "UPDATE Exercise SET ExerciseName='" + bezeichnung +  "' WHERE Exercise_Id=" + ID + "";
		db.execSQL(sql);
		db.close();
		
	
	}
	
	/**
	 * Get all Exercises 
	 * 
	 * @return ArrayList<Exercise>
	 * @author Eric Schmidt
	 */
	public ArrayList<Exercise> getAllExercise(){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
	    ArrayList<Exercise> exerciseList = new ArrayList<Exercise>();
	    
	    sql= "SELECT Exercise_Id, ExerciseName FROM Exercise";
	    Cursor cursor = db.rawQuery(sql, null);
	    if ( cursor.moveToFirst()){
	    	do{
	            Exercise e = new Exercise();
	            e.setID(cursor.getInt(0));
	            e.setName(cursor.getString(1));
	            exerciseList.add(e);
	    	}while(cursor.moveToNext());
	    }
	    db.close();
	    return exerciseList;
	}
	
	public ArrayList<Exercise> searchKeyString(String key){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
	    ArrayList<Exercise> exerciseList = new ArrayList<Exercise>();
	   // Log.d("searchKeyString");

	        // Alles Anfragen auswählen
	     //   String selectQuery = "SELECT  * FROM Uebung WHERE Bezeichung="+"'+ key +'";
	     String selectQuery =  "SELECT * FROM Exercise WHERE ExerciseName LIKE '%" + key + "%'";
	        Cursor cursor = db.rawQuery(selectQuery, null);
	        // you can change it to
	        // db.rawQuery("SELECT * FROM "+table+" WHERE KEY_KEY LIKE ?", new String[] {key+"%"});
	        // if you want to get everything starting with that key value

	        // looping through all rows and adding to list
	        if (cursor.moveToFirst()) {
	            do {
	              Exercise e = new Exercise();
	              e.setID(Integer.parseInt(cursor.getString(1)));
	              e.setName(cursor.getString(2));
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
		
		sql = "SELECT Exercise_Id,TrainingstagHatUebungId  FROM TrainingDayHasExercise WHERE TrainingDay_Id = "
				+ trainingDayId;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				Exercise exercise = getExerciseById(Integer.parseInt(cursor.getString(0)));
				exercise.setTrainingDayHasExerciseId(cursor.getInt(1));
				exerciseList.add(exercise);
				System.out.println(exercise.getId() + exercise.getName() + exercise.getTrainingDayHasExerciseId());
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
