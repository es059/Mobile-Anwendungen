package com.workout.log.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceTarget;

/**
 * Mapper Class of BusinessObject PerformanceTarget
 * 
 * @author Eric Schmidt
 */

public class PerformanceTargetMapper {
	private DataBaseHelper myDBHelper;
	private String sql;
	
	public PerformanceTargetMapper(Context context){
		myDBHelper = DataBaseHelper.getInstance(context);
	}
	/**
	 * Get one PerformanceTarget by an Exercise id
	 * 
	 *  @param Exercise exercise
	 *  @return Exercise
	 *  @author Eric Schmidt
	 */
	public PerformanceTarget getPerformanceTargetByExerciseId(Exercise exercise, int trainingDayId){
		PerformanceTarget performanceTarget = new PerformanceTarget();
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		sql = "SELECT RepetitionTarget, SetTarget, PerformanceTarget_Id FROM PerformanceTarget WHERE Exercise_Id = " + exercise.getId() + 
				" AND TrainingDay_Id = " + trainingDayId;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			performanceTarget.setRepetition(Integer.parseInt(cursor.getString(0)));
			performanceTarget.setSet(Integer.parseInt(cursor.getString(1)));
			performanceTarget.setId(Integer.parseInt(cursor.getString(2)));
			performanceTarget.setExercise(exercise);
		}
		
		cursor.close();
		return performanceTarget;
	}
	
	public void updatePerformanceTarget(PerformanceTarget pt){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "UPDATE PerformanceTarget SET SetTarget = " + pt.getSet() + ", RepetitionTarget = " + pt.getRepetition()
				+ " WHERE PerformanceTarget_Id = " + pt.getId();
		db.execSQL(sql);
		
	}
	
	public void deletePerformanceTarget(int trainingDayId, int exerciseId) {
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "DELETE FROM PerformanceTarget WHERE TrainingDay_Id=" + trainingDayId + " AND Exercise_Id=" + exerciseId + "";
		db.execSQL(sql);
		
	}
	
	/**
	 * Delete all Entries in PerformanceTarget where the given exercise occurs
	 * 
	 * @param e the exercise to be deleted
	 * @author Eric Schmidt
	 */
	public void deleteExerciseFromPerfromanceTarget(Exercise e){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "DELETE FROM PerformanceTarget WHERE Exercise_Id =" + e.getId() + "";
		db.execSQL(sql);
		
	}
	
	/**
	 * Delete a TrainingDay from all performance targets
	 * 
	 * @param trainingDayId
	 * @author Eric Schmidt
	 */
	public void deleteTrainingDayFromAllPerformanceTarget(int trainingDayId){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "DELETE FROM PerformanceTarget WHERE TrainingDay_Id=" + trainingDayId;
		db.execSQL(sql);
		
	}
	
}
