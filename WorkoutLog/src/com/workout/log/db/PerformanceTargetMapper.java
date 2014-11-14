package com.workout.log.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.bo.TrainingDay;

/**
 * Mapper Class of BusinessObject PerformanceTarget
 * 
 * @author Eric Schmidt
 */

public class PerformanceTargetMapper {
	private DataBaseHelper myDBHelper;
	private String sql;
	
	private static ExerciseMapper eMapper = null;
	private static TrainingDayMapper tMapper = null;
	private Context context = null;
	
	public PerformanceTargetMapper(Context context){
		myDBHelper = DataBaseHelper.getInstance(context);
		
		this.context = context;
		if (tMapper == null) tMapper = new TrainingDayMapper(context);
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
	
	/**
	 * Get all PerformanceTarget by an Exercise id
	 * 
	 *  @param Exercise exercise
	 *  @return Exercise
	 *  @author Eric Schmidt
	 */
	public ArrayList<PerformanceTarget> getAllPerformanceTargetByExercise(Exercise exercise){
		ArrayList<PerformanceTarget> performanceTargetList = new ArrayList<PerformanceTarget>();
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		sql = "SELECT RepetitionTarget, SetTarget, PerformanceTarget_Id, TrainingDay_Id FROM PerformanceTarget WHERE Exercise_Id = " + exercise.getId();
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				PerformanceTarget performanceTarget = new PerformanceTarget();
				
				performanceTarget.setRepetition(Integer.parseInt(cursor.getString(0)));
				performanceTarget.setSet(cursor.getInt(1));
				performanceTarget.setId(cursor.getInt(2));
				performanceTarget.setTrainingDayId(cursor.getInt(3));
				performanceTarget.setExercise(exercise);
				
				performanceTargetList.add(performanceTarget);
			}while(cursor.moveToNext());

		}
		
		cursor.close();
		return performanceTargetList;
	}
	
	/**
	 * Get all PerformanceTarget by a Training Day
	 * 
	 *  @param Exercise exercise
	 *  @return Exercise
	 *  @author Eric Schmidt
	 */
	public ArrayList<PerformanceTarget> getPerformanceTargetByTrainingDay(TrainingDay trainingDay){
		ArrayList<PerformanceTarget> performanceTargetList = new ArrayList<PerformanceTarget>();
		if (eMapper == null) eMapper = new ExerciseMapper(context);
		
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		sql = "SELECT * FROM PerformanceTarget WHERE TrainingDay_Id = " + trainingDay.getId();
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				PerformanceTarget performanceTarget = new PerformanceTarget();
				
				performanceTarget.setTrainingDayId(trainingDay.getId());
				performanceTarget.setId(cursor.getInt(1));
				performanceTarget.setRepetition(cursor.getInt(2));
				performanceTarget.setSet(cursor.getInt(3));
				performanceTarget.setExercise(eMapper.getExerciseById(cursor.getInt(4)));
				
				performanceTargetList.add(performanceTarget);
			}while(cursor.moveToNext());
		}
		
		cursor.close();
		return performanceTargetList;
	}
	
	/**
	 * Use this method if the performanceTarget already exists in the database
	 * @param pt
	 */
	public void updatePerformanceTarget(PerformanceTarget pt){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "UPDATE PerformanceTarget SET SetTarget = " + pt.getSet() + ", RepetitionTarget = " + pt.getRepetition()
				+ " WHERE PerformanceTarget_Id = " + pt.getId();
		db.execSQL(sql);
	}
	
	/**
	 * Insert a new performanceTarget into the database
	 * @param pt
	 */
	public void addPerformanceTarget(PerformanceTarget pt){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql= "INSERT INTO PerformanceTarget (TrainingDay_Id, Exercise_Id, RepetitionTarget, SetTarget) VALUES  (" + pt.getTrainingDayId() +","+ pt.getExercise().getId() 
				+", " + pt.getRepetition() + ","+ pt.getSet()+")";
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
