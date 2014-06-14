package com.workout.log.db;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceActual;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Mapper Class of BusinessObject PerformanceTarget
 * 
 * @author Eric Schmidt
 */
@SuppressLint("SimpleDateFormat") 
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
	 * Delete all Entries in PerformanceActual where the given exercise occurs
	 * 
	 * @param e the exercise to be deleted
	 * @author Eric Schmidt
	 */
	public void deleteExerciseFromPerfromanceActual(Exercise e){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "DELETE FROM PerformanceActual WHERE Exercise_Id =" + e.getId() + "";
		db.execSQL(sql);
		db.close();
	}
	
	/**
	 * Select all the Dates which are present in the database
	 * 
	 * @param exercise
	 * @return
	 */
	public ArrayList<String> getAllDates(Exercise exercise){
		ArrayList<String> date = new ArrayList<String>();
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		sql = "SELECT DISTINCT TimestampActual FROM PerformanceActual WHERE Exercise_Id ="
				+ exercise.getId();
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				date.add(cursor.getString(0));
			}while(cursor.moveToNext());
		}
		db.close();
		return date;
	}
	/**
	 *  Get all the Max Weight of one Exercise per day
	 * 
	 *  @param Exercise exercise
	 *  @param String timestamp in SimpleDateFormat dd.MM.yyyy
	 *  @return Exercise
	 *  @author Eric Schmidt
	 */
	public ArrayList<PerformanceActual> getAllPerformanceActual(Exercise exercise){
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		Cursor cursor;
		ArrayList<String> strings= getAllDates(exercise);
		ArrayList<PerformanceActual> performanceActualList = new ArrayList<PerformanceActual>();
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		for(String item : strings){
			sql = "SELECT MAX(RepetitionActual), MAX(WeightActual) FROM PerformanceActual WHERE"
					+ " TimestampActual = '" + item + "' AND"
					+ " Exercise_Id = " + exercise.getId();
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst()){
				PerformanceActual performanceActual = new PerformanceActual();
				performanceActual.setRepetition(cursor.getInt(0));
				performanceActual.setWeight(cursor.getDouble(1));
				try {
					performanceActual.setTimestamp(sp.parse(item));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				performanceActualList.add(performanceActual);
			}
		}
	
		db.close();
		return performanceActualList;
	}
	
	/**
	 * Get the number of Training Days of one exercise
	 * 
	 * @param int ExerciseId
	 * @return int Number of TrainingDays
	 * @author Eric Schmidt
	 */
	public int getTrainingDaysbyExercise(int exerciseId){
		int trainingDays = 0;
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		sql = "SELECT COUNT(DISTINCT TimestampActual) FROM PerformanceActual WHERE Exercise_Id =" 
				+ exerciseId;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			trainingDays = cursor.getInt(0);
		}
		db.close();
		return trainingDays;
	}
	
	
	/**
	 * Get one PerformanceTarget by an Exercise id
	 * 
	 *  @param Exercise exercise
	 *  @param String timestamp in SimpleDateFormat dd.MM.yyyy
	 *  @return Exercise
	 *  @author Eric Schmidt
	 */
	public ArrayList<PerformanceActual> getCurrentPerformanceActual(Exercise exercise, String timestamp){
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
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
				try {
					performanceActual.setTimestamp(sp.parse(cursor.getString(4)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
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
		Calendar c = currentDate;
		int dayCount=0;
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		/**
		 * Search for the latest date. If the latest date is older than 100 Days than
		 * it will be ignored
		 */
		do{
			dayCount++;
			c.add(Calendar.DATE, -1);
			sql = "SELECT * FROM PerformanceActual WHERE Exercise_Id = " + currentExercise.getId()
						+ " AND TimestampActual = '" + sp.format(c.getTime()) + "' ORDER BY SetActual";
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
				try {
					performanceActual.setTimestamp(sp.parse(cursor.getString(4)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				performanceActual.setExercise(currentExercise);
				performanceActualList.add(performanceActual);
			}while(cursor.moveToNext());
		}else if (dayCount == 100){
			c.add(Calendar.DATE, 100);
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
		Calendar c = currentDate;
		int dayCount=0;
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		/**
		 * Search for the newest date. If the newest date is older than 100 Days than
		 * the standard PerformanceActual will be shown
		 */
		do{
			dayCount++;
			c.add(Calendar.DATE, 1);
			sql = "SELECT * FROM PerformanceActual WHERE Exercise_Id = " + currentExercise.getId()
						+ " AND TimestampActual = '" + sp.format(c.getTime()) + "' ORDER BY SetActual";
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
				try {
					performanceActual.setTimestamp(sp.parse(cursor.getString(4)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
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
