package com.workout.log.db;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceActual;
import com.workout.log.bo.PerformanceTarget;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Mapper Class of BusinessObject PerformanceTarget
 * 
 * @author Eric Schmidt
 */
public class PerformanceActualMapper {
	private DataBaseHelper myDBHelper;
	private String sql;
	
	public PerformanceActualMapper(Context context){
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
	 * Delete one PerformanceActual Dataset from the database
	 * 
	 * @param PerfromanceActualId
	 * @author Eric Schmidt  
	 */
	public void deletePerformanceActualById(int performanceActualId){
		if (performanceActualId != 0){
			SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
			sql = "DELETE FROM PerformanceActual WHERE PerformanceActual_Id =" + performanceActualId;
			db.execSQL(sql);
			db.close();
		}
	}
	
	/**
	 * Get one PerformanceTarget by an Exercise id
	 * 
	 *  @param Exercise exercise
	 *  @param String timestamp in SimpleDateFormat dd.MM.yyyy
	 *  @return Exercise
	 *  @author Eric Schmidt
	 */
	public ArrayList<PerformanceActual> getPerformanceActualByExerciseId(Exercise exercise, String timestamp){
		ArrayList<PerformanceActual> performanceActualList = new ArrayList<PerformanceActual>();
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		sql = "SELECT * FROM PerformanceActual WHERE Exercise_Id = " + exercise.getId() 
				+ " AND TimestampActual = \'" + timestamp + "\' ORDER BY SetActual";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do {
				PerformanceActual performanceActual = new PerformanceActual();
				performanceActual.setId(cursor.getInt(0));
				performanceActual.setRepetition(cursor.getInt(1));
				performanceActual.setSet(cursor.getInt(2));
				performanceActual.setWeight(cursor.getDouble(3));
				performanceActual.setTimestamp(new Date(cursor.getLong(4)));
				performanceActual.setExercise(exercise);
				performanceActualList.add(performanceActual);
			} while (cursor.moveToNext());
		}
		db.close();
		return performanceActualList;
	}
	
	/**
	 * Get a ArrayList of PerformanceActual Objects from 
	 * the last workout of one exercise
	 * 
	 * @param Date the current Date of the Workout
	 * @param Exercise the currently selected Exercise
	 * @return ArrayList<PerformanceActual> a List of PerformanceActual Objects from the last Workout
	 * @author Eric Schmidt
	 */
	public ArrayList<PerformanceActual> getLastPerformanceActual(Calendar currentDate, Exercise currentExercise){
		ArrayList<PerformanceActual> performanceActualList = new ArrayList<PerformanceActual>();
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		Cursor cursor;
		int dayCount=0;
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		/**
		 * Search for the latest date. If the latest date is older than 100 Days than
		 * it will be ignored
		 */
		do{
			dayCount++;
			currentDate.add(Calendar.DATE, -1);
			sql = "SELECT * FROM PerformanceActual WHERE Exercise_Id = " + currentExercise.getId()
						+ " AND TimestampActual = '" + sp.format(currentDate.getTime()) + "' ORDER BY SetActual";
			cursor = db.rawQuery(sql,null);
		} while (!cursor.moveToFirst() && dayCount != 100);
		cursor = db.rawQuery(sql,null);
		if (cursor.moveToFirst()){
			do{
				PerformanceActual performanceActual = new PerformanceActual();
				performanceActual.setId(cursor.getInt(0));
				performanceActual.setRepetition(cursor.getInt(1));
				performanceActual.setSet(cursor.getInt(2));
				performanceActual.setWeight(cursor.getDouble(3));
				performanceActual.setTimestamp(new Date(cursor.getLong(4)));
				performanceActual.setExercise(currentExercise);
				performanceActualList.add(performanceActual);
			}while(cursor.moveToNext());
		}
		db.close();
		return performanceActualList;
		
	}
	
	/**
	 * Get a ArrayList of PerformanceActual Objects from 
	 * the last workout of one exercise
	 * 
	 * @param Date the current Date of the Workout
	 * @param Exercise the currently selected Exercise
	 * @return ArrayList<PerformanceActual> a List of PerformanceActual Objects from the last Workout
	 * @author Eric Schmidt
	 */
	public ArrayList<PerformanceActual> getNextPerformanceActual(Calendar currentDate, Exercise currentExercise){
		ArrayList<PerformanceActual> performanceActualList = new ArrayList<PerformanceActual>();
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		Cursor cursor;
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		/**
		 * Search for the latest date
		 */
		do{
		currentDate.add(Calendar.DATE, +1);
		sql = "SELECT * FROM PerformanceActual WHERE Exercise_Id = " + currentExercise.getId()
					+ " AND TimestampActual = '" + sp.format(currentDate.getTime()) + "' ORDER BY SetActual";
		cursor = db.rawQuery(sql,null);
		} while (!cursor.moveToFirst());
		cursor = db.rawQuery(sql,null);
		if (cursor.moveToFirst()){
			do{
				PerformanceActual performanceActual = new PerformanceActual();
				performanceActual.setId(cursor.getInt(0));
				performanceActual.setRepetition(cursor.getInt(1));
				performanceActual.setSet(cursor.getInt(2));
				performanceActual.setWeight(cursor.getDouble(3));
				performanceActual.setTimestamp(new Date(cursor.getLong(4)));
				performanceActual.setExercise(currentExercise);
				performanceActualList.add(performanceActual);
			}while(cursor.moveToNext());
		}
		db.close();
		return performanceActualList;
		
	}
	
	/**
	 * Save or Update one PerformanceActual Object into the Database
	 * 
	 *  @param Exercise exercise
	 *  @return Exercise
	 *  @author Eric Schmidt
	 */
	public PerformanceActual savePerformanceActual(PerformanceActual performanceActual){
		int id = 1;
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		if (performanceActual.getId() == 0){
			sql = "SELECT MAX(PerformanceActual_Id) FROM PerformanceActual";
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst()){
				if (!cursor.isNull(0)){
					id = Integer.parseInt(cursor.getString(0));
					id++;
				}
			}	
		}else{
			id = performanceActual.getId();
		}
		sql= "INSERT OR REPLACE INTO PerformanceActual "
				+ "(PerformanceActual_Id, RepetitionActual, SetActual, "
				+ "WeightActual, TimestampActual, Exercise_Id) "
				+ "VALUES (" + id + "," + performanceActual.getRepetition() 
				+ "," + performanceActual.getSet()
				+ "," + performanceActual.getWeight() + ",'" + sp.format(new Date())
				+ "'," + performanceActual.getExercise().getId() + ")";
				
		db.execSQL(sql);
		performanceActual.setId(id);
		performanceActual.setTimestamp(new Date());
		db.close();
		return performanceActual;
		
	}
	
}
