package com.workout.log.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.workout.log.bo.Exercise;

public class ExerciseMapper {
	private DataBaseHelper myDBHelper;
	private String sql;
	private int muscleGroupID;
	
	private static MuscleGroupMapper mMapper = null;
	private static TrainingDayMapper tMapper = null;
	private static PerformanceActualMapper paMapper = null;
	private static PerformanceTargetMapper ptMapper = null;
	
	public ExerciseMapper(Context context){
		myDBHelper = DataBaseHelper.getInstance(context);
	    if (mMapper == null) mMapper = new MuscleGroupMapper(context);
	    if (tMapper == null) tMapper = new TrainingDayMapper(context);
	    if (paMapper == null) paMapper = new PerformanceActualMapper(context);
	    if (ptMapper == null) ptMapper= new PerformanceTargetMapper(context);
	}
	
	public void add(String exerciseName, String muscleGroup){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT MuscleGroup_Id FROM MuscleGroup WHERE MuscleGroupName='" + muscleGroup + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			muscleGroupID = Integer.parseInt(cursor.getString(0));
		}
		sql = "INSERT INTO Exercise (ExerciseName, MuscleGroup_Id ) VALUES ('" + exerciseName +"', " + muscleGroupID + ")";
		db.execSQL(sql);
		cursor.close();
	}
	
	public void add(Exercise exercise){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT MuscleGroup_Id FROM MuscleGroup WHERE MuscleGroupName='" + exercise.getMuscleGroup().getName() + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			muscleGroupID = Integer.parseInt(cursor.getString(0));
		}
		sql = "INSERT INTO Exercise (Exercise_Id, ExerciseName, MuscleGroup_Id ) VALUES ("+ exercise.getId() + ", '" + exercise.getName() +"', " + muscleGroupID + ")";
		db.execSQL(sql);
		cursor.close();
		
	}
	
	public void delete(Exercise e){	
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "DELETE FROM Exercise WHERE Exercise_Id =" + e.getId() + "";
		db.execSQL(sql);
		
	}
	
	/**
	 * This method will delete one Exercise from all trainingdays by using the table
	 * TrainingDayHasExercise.
	 * 
	 * @param e the Exercise to be deleted
	 * @author Eric Schmidt
	 */
	public void deleteExerciseFromAllTrainingDays(Exercise e){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "DELETE FROM TrainingDayHasExercise WHERE Exercise_Id =" + e.getId() + "";
		db.execSQL(sql);
		
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
	
	/**
	 * Update the exercise with the given Information
	 * 
	 * @param exerciseId
	 * @param bezeichnung
	 * @param muscleGroup
	 * 
	 * @author Eric Schmidt
	 */
	public void update(int exerciseId, String bezeichnung, String muscleGroup){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT MuscleGroup_Id FROM MuscleGroup WHERE MuscleGroupName='" + muscleGroup + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			muscleGroupID = Integer.parseInt(cursor.getString(0));
		}
		sql = "UPDATE Exercise SET ExerciseName='" + bezeichnung +  "', MuscleGroup_Id=" + muscleGroupID + 
				" WHERE Exercise_Id=" + exerciseId + "";
		db.execSQL(sql);
		
		
		cursor.close();
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
	    
	    sql= "SELECT Exercise_Id FROM Exercise";
	    Cursor cursor = db.rawQuery(sql, null);
	    if ( cursor.moveToFirst()){
	    	do{
	    		exerciseList.add(getExerciseById(cursor.getInt(0)));
	    	}while(cursor.moveToNext());
	    }
	    
	    cursor.close();
	    return exerciseList;
	}
	
	public ArrayList<Exercise> searchKeyString(String key){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
	    ArrayList<Exercise> exerciseList = new ArrayList<Exercise>();
	    String selectQuery =  "SELECT * FROM Exercise WHERE ExerciseName LIKE '%" + key + "%'";
	    Cursor cursor = db.rawQuery(selectQuery, null);
	    
        if (cursor.moveToFirst()) {
            do {
            	exerciseList.add(getExerciseById(cursor.getInt(1)));	
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
		
		sql = "SELECT Exercise_Id,TrainingstagHasExercise_Id, ExerciseOrder FROM TrainingDayHasExercise WHERE TrainingDay_Id = "
				+ trainingDayId + " ORDER BY ExerciseOrder";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				Exercise e = getExerciseById(cursor.getInt(0));
				e.setOrderNumber(cursor.getInt(2));
				exerciseList.add(e);
			}while(cursor.moveToNext());
		}
		cursor.close();
		
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
					exerciseList.add(getExerciseById(cursor.getInt(0)));
				}
			cursor.close();
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
		    sql = "SELECT * FROM Exercise WHERE Exercise_Id = " + exerciseId;
		    Cursor cursor = db.rawQuery(sql, null);
		    if (cursor.moveToFirst()){
		    	exercise.setMuscleGroup(mMapper.getMuscleGroupById(cursor.getInt(0)));
		    	exercise.setID(Integer.parseInt(cursor.getString(1)));
		    	exercise.setName(cursor.getString(2));
		    }
		    cursor.close();
		    
		    return exercise;
	}
	 
	/**
	 * To get better performance call this method only if you need 
	 * the trainingDays, performanceActualList and PerforamnceTargetList of a Exercise.
	 * This is only needed if you want to undo a exercise 
	 */
	 public Exercise addAdditionalInfo(Exercise exercise){
		 exercise.setTrainingDayIdList(tMapper.getTrainingDayIdsByExercise(exercise.getId()));
	     exercise.setPerformanceActualList(paMapper.getAllPerformanceActual(exercise));
	     exercise.setPerformanceTargetList(ptMapper.getAllPerformanceTargetByExercise(exercise));
	     
	     return exercise;
	 }
}
