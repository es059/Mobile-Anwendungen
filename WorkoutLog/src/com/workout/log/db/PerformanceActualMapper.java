package com.workout.log.db;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
	 * Get one PerformanceTarget by an Exercise id
	 * 
	 *  @param Exercise exercise
	 *  @param String timestamp in SimpleDateFormat dd.MM.yyyy
	 *  @return Exercise
	 *  @author Eric Schmidt
	 */
	public ArrayList<PerformanceActual> getPerformanceActualByExerciseId(Exercise exercise, String timestamp){
		PerformanceActual performanceActual = new PerformanceActual();
		ArrayList<PerformanceActual> performanceActualList = new ArrayList<PerformanceActual>();
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		sql = "SELECT * FROM PerformanceActual WHERE Exercise_Id = " + exercise.getId() + " AND TimestampActual = \'" + timestamp + "\'";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			performanceActual.setId(cursor.getInt(0));
			performanceActual.setRepetition(cursor.getInt(1));
			performanceActual.setSet(cursor.getInt(2));
			performanceActual.setWeight(cursor.getDouble(3));
			performanceActual.setTimestamp(new Date(cursor.getLong(4)));
			performanceActual.setExercise(exercise);
			performanceActualList.add(performanceActual);
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
		int id = 0;
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		
		sql = "SELECT MAX(PerformanceActual_Id) FROM PerformanceActual";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			id = Integer.parseInt(cursor.getString(0));
		}		
		sql = "REPLACE INTO PerformanceActual SET PerformaneActual_Id = " + id +
					", RepetitionActual = " + performanceActual.getRepetition() +
					", SetActual = "+ performanceActual.getSet() +
					", WeightActual = " + performanceActual.getWeight() +
					", TimestampActual = "+ sp.format(new Date()) +
					", Exercise_Id = '" + performanceActual.getExercise().getId() + "\'";
	
		db.execSQL(sql);
		performanceActual.setId(id);
		performanceActual.setTimestamp(new Date());
		db.close();
		return performanceActual;
		
	}
	
}
