package com.workout.log.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.workout.log.bo.Exercise;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;

public class TrainingDayMapper {
	
	private DataBaseHelper myDBHelper;
	private String sql;
	
	private Context context = null;
	private static WorkoutplanMapper wMapper= null;
	private static PerformanceTargetMapper pMapper = null;
	private static ExerciseMapper eMapper = null;
		
	public TrainingDayMapper (Context context){
		myDBHelper = DataBaseHelper.getInstance(context);
		
		this.context = context;
		if (wMapper == null) wMapper= new WorkoutplanMapper(context);
	}
		
	/**
	 * Add a TrainingDay to the database
	 * 
	 * @param trainingDayName
	 */
	public void add(TrainingDay trainingDay){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		int id = 1;
		if (trainingDay.getId() == 0){
			sql = "SELECT MAX(TrainingDay_Id) FROM TrainingDay";
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst()){
				if (!cursor.isNull(0)){
					id = Integer.parseInt(cursor.getString(0));
					id++;
				}
			}	
		}else{
			id = trainingDay.getId();
		}
		sql = "INSERT INTO TrainingDay (TrainingDay_Id, TrainingDayName) VALUES (" + id + ", '"+ trainingDay.getName() + "')";
		db.execSQL(sql);	
	}
	
	public ArrayList<Workoutplan> getWorkoutplansFromTrainingDays(int trainingDayId){
		ArrayList<Workoutplan> workoutplanList = new  ArrayList<Workoutplan>();
		
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT Workoutplan_Id FROM WorkoutplanHasTrainingDay WHERE TrainingDay_Id = " + String.valueOf(trainingDayId);
		
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				workoutplanList.add(wMapper.getWorkoutPlanById(cursor.getInt(0)));
			}while(cursor.moveToNext());
		}
		cursor.close();
		return workoutplanList;
	}
	
	public ArrayList<Exercise> getExercisesFromTrainingDay(int trainingDayId){
		ArrayList<Exercise> workoutplanList = new  ArrayList<Exercise>();
		if (eMapper == null) eMapper = new ExerciseMapper(context);
		
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT Exercise_Id FROM TrainingDayHasExercise  WHERE TrainingDay_Id = " + trainingDayId;
		
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				workoutplanList.add(eMapper.getExerciseById(cursor.getInt(0)));
			}while(cursor.moveToNext());
		}
		cursor.close();
		return workoutplanList;
	}
	
	/**
	 * Get all TrainingDays from one Workoutplan using the workoutplanId
	 * 
	 * @param int workoutplanId
	 * @return ArrayList<TrainingDay>
	 * @author Eric Schmidt & Florian Blessing
	 */
	public ArrayList<TrainingDay> getAllTrainingDaysFromWorkoutplan(int workoutplanId) {
		ArrayList<TrainingDay> trainingdayList = new ArrayList<TrainingDay>();
		
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		
		sql = "SELECT TrainingDay_Id, WorkoutplanHasTrainingDay_Id FROM WorkoutplanHasTrainingDay WHERE Workoutplan_Id = " + workoutplanId;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				TrainingDay trainingDay = getTrainingDayById(cursor.getInt(0));
				trainingdayList.add(trainingDay);
			}while(cursor.moveToNext());
		}
		
		cursor.close();
		return trainingdayList;
	}
	
	/**
	 * Löschen von Trainingstagen
	 * @param d
	 */
	public void delete(TrainingDay d){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "DELETE FROM TrainingDay WHERE TrainingDay_Id = " + d.getId();
		db.execSQL(sql);
			
	}
	
	/**
	 * Trainingstag updaten , z.B nach einer Änderung von einem Trainingstag
	 * @param t
	 * @return
	 */
	public TrainingDay update(TrainingDay t){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "UPDATE TrainingDay SET TrainingDayName='" + t.getName() +  "' WHERE TrainingDay_Id=" + t.getId();
		db.execSQL(sql);
		
		return t;
	}
	
	public TrainingDay getTrainingDayById(int id){
		TrainingDay trainingDay = new TrainingDay();
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		if (pMapper == null) pMapper = new PerformanceTargetMapper(context);
		
	    sql = "SELECT TrainingDay_Id, TrainingDayName FROM TrainingDay WHERE TrainingDay_Id = " + id;
	    Cursor cursor = db.rawQuery(sql, null);
	    if (cursor.moveToFirst()){
		    trainingDay.setId(Integer.parseInt(cursor.getString(0)));
		    trainingDay.setWorkoutplanList(getWorkoutplansFromTrainingDays(trainingDay.getId()));
		    trainingDay.setName(cursor.getString(1));
		    trainingDay.setExerciseList(getExercisesFromTrainingDay(trainingDay.getId()));
		    trainingDay.setPerformanceTargetList(pMapper.getPerformanceTargetByTrainingDay(trainingDay));
	    }
	    cursor.close();
	    return trainingDay;
	}
	
	public ArrayList<TrainingDay> getAllTrainingDay() {
		ArrayList<TrainingDay> trainingdayList = new ArrayList<TrainingDay>();
		
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		
		sql = "SELECT * FROM TrainingDay";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				trainingdayList.add(getTrainingDayById(cursor.getInt(0)));
			}while(cursor.moveToNext());
		}
		
		cursor.close();
		return trainingdayList;
	}
	
	public ArrayList<Integer> getTrainingDayIdsByExercise(int exerciseId){
		ArrayList<Integer> trainingdayList = new ArrayList<Integer>();
		
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT TrainingDay_Id FROM TrainingDayHasExercise WHERE Exercise_Id =" + exerciseId;
		
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				trainingdayList.add(cursor.getInt(0));
			}while(cursor.moveToNext());
		}
		
		cursor.close();
		return trainingdayList;
	}
	
	
	public void updateTrainingDayHasExercise(Exercise e, int trainingDayId, int OrderNumber){
		int trainingDayHasExerciseId = -1;
		
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT TrainingstagHasExercise_Id FROM TrainingDayHasExercise WHERE Exercise_Id =" + e.getId() + 
				" AND TrainingDay_Id = " + trainingDayId;
		
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			trainingDayHasExerciseId = cursor.getInt(0);
		}
		
		sql = "UPDATE TrainingDayHasExercise SET ExerciseOrder=" + OrderNumber +
				" WHERE TrainingstagHasExercise_Id = " + trainingDayHasExerciseId;
		db.execSQL(sql);
		cursor.close();
		
	}
	
	/**
	 * Add a Exercise to a trainingDay and performanceTarget
	 * 
	 * @param trainingsDayId
	 * @param exerciseId
	 * @param eTargetSetCount
	 * @param eTargetRepCount
	 */
	public void addExerciseToTrainingDayAndPerformanceTarget(int trainingsDayId, int exerciseId, int eTargetSetCount,  int eTargetRepCount) {
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "INSERT INTO TrainingDayHasExercise (TrainingDay_Id, Exercise_Id) VALUES (" + trainingsDayId +","+exerciseId+")";
		db.execSQL(sql);
		/**
		 * Idee: Weiteres Attribut in TrainingDayHasExercise : PerformanceTarget Id um jede Übung einzeln anzusprechen
		 */
		sql= "INSERT INTO PerformanceTarget (TrainingDay_Id, Exercise_Id, RepetitionTarget, SetTarget) VALUES  (" + trainingsDayId +","+exerciseId+", " + eTargetRepCount + ","+eTargetSetCount+")";
		db.execSQL(sql);
		
	}
	
	/**
	 * Add a Exercise to a trainingDay
	 * 
	 * @param trainingsDayId
	 * @param exerciseId
	 * @param eTargetSetCount
	 * @param eTargetRepCount
	 */
	public void addExerciseToTrainingDay(int trainingsDayId, int exerciseId) {
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "INSERT INTO TrainingDayHasExercise (TrainingDay_Id, Exercise_Id) VALUES (" + trainingsDayId +","+exerciseId+")";
		db.execSQL(sql);

	}
	
	/**
	 * Check if a Exercise was already added to a trainingDay
	 * 
	 * @param trainingDayId
	 * @param e
	 */
	public boolean checkIfExist(int trainingDayId, int exerciseId){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		boolean exist = false;
		
		sql = "Select * FROM TrainingDayHasExercise WHERE TrainingDay_Id = " + trainingDayId +" AND Exercise_Id = "+ exerciseId;
		Cursor cursor = db.rawQuery(sql, null);
	    if (cursor.moveToFirst()) {
	    	do {
		              exist = true;	
		              return exist;
		        } while (cursor.moveToNext());
	    }
	    cursor.close();
	    
	    return exist;
	}
	
	public void deleteExerciseFromTrainingDay(int trainingDayId, Exercise e) {
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql ="DELETE FROM TrainingDayHasExercise WHERE TrainingDay_Id = " + trainingDayId + " AND Exercise_Id = " + e.getId();
		db.execSQL(sql);	
	}
	
	/**
	 * Delete a trainingDay from one Workoutplan
	 * 
	 * @param trainingDayId
	 * @param workoutplanId
	 * @param primarykey
	 */
	public void deleteTrainingDayFromWorkoutplan(TrainingDay trainingDay, int workoutplanId) {
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "DELETE FROM WorkoutplanHasTrainingDay WHERE TrainingDay_Id=" + trainingDay.getId() 
				+ " AND Workoutplan_Id=" + workoutplanId;
		db.execSQL(sql);
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

