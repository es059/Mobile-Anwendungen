package com.workout.log.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.workout.log.bo.Exercise;


public class ExerciseMapper {
	private DataBaseHelper myDBHelper;
	private String sql;
	private Context context;
	private int muscleGroupID;
	
	private MuscleGroupMapper mMapper = null;
	
	public ExerciseMapper(Context context){
		myDBHelper = DataBaseHelper.getInstance(context);
	    mMapper = new MuscleGroupMapper(context);
		this.context = context;
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
	
	public ArrayList<Exercise> getAll() {
		ArrayList<Exercise> exerciseList = new ArrayList<Exercise>();
		MuscleGroupMapper mMapper = new MuscleGroupMapper(context);
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT * FROM Exercise";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()) {
            do {
              Exercise e = new Exercise();
              e.setMuscleGroup(mMapper.getMuscleGroupById(cursor.getInt(0)));
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
		
		sql = "SELECT Exercise_Id,TrainingstagHasExercise_Id FROM TrainingDayHasExercise WHERE TrainingDay_Id = "
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
					Exercise exercise = new Exercise();
				    exercise.setID(Integer.parseInt(cursor.getString(0)));
				    exercise.setName(cursor.getString(1));
				    exerciseList.add(exercise);
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
}
