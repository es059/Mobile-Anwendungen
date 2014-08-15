package com.workout.log.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.workout.log.bo.Workoutplan;

@SuppressLint("SimpleDateFormat") 
public class WorkoutplanMapper   {

	private DataBaseHelper myDBHelper;
	private String sql;

	public WorkoutplanMapper(Context context){
		myDBHelper = DataBaseHelper.getInstance(context);
	}
	
	/**
	 * Get one Workoutplan by an id
	 * 
	 *  @param int id
	 *  @return Exercise
	 *  @author Eric Schmidt & Florian Blessing
	 */
	 public Workoutplan getWorkoutPlanById(int workoutplanId){
	    Workoutplan workoutplan = new Workoutplan();
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		
	    SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
	    sql = "SELECT * FROM Workoutplan WHERE Workoutplan_Id = " + workoutplanId;
	    Cursor cursor = db.rawQuery(sql, null);
	    if (cursor.moveToFirst()){
	    	workoutplan.setId(Integer.parseInt(cursor.getString(1)));
	    	workoutplan.setName(cursor.getString(2));
	    	try {
	    		workoutplan.setTimeStamp(sp.parse(cursor.getString(3)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
	    }
	    cursor.close();
	    
	    return workoutplan;
	}
	
	/**
	 * Get Currently selected Workoutplan. Workoutplan is current if column 'Current' 
	 * has Value 1
	 * 
	 * @ return Workoutplan
	 */
	public Workoutplan getCurrent(){
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		Workoutplan w = new Workoutplan();
		//Establish Database Conncetion
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT Workoutplan_Id, WorkoutplanName, Timestamp FROM Workoutplan WHERE Current = 1";
		
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			w.setId(Integer.parseInt(cursor.getString(0)));
			w.setName(cursor.getString(1));
			try {
	    		w.setTimeStamp(sp.parse(cursor.getString(2)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		cursor.close();
		
		return w;
	}
	
	/**
	 * Set the current Workoutplan
	 * 
	 * @param workoutplanId
	 */
	public void setCurrent(int workoutplanId){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "UPDATE Workoutplan SET Current = NUll";
		db.execSQL(sql);
		sql = "Update Workoutplan SET Current = 1 WHERE Workoutplan_Id = " + workoutplanId;
		db.execSQL(sql);
		
	}
	
	/**
	 * Add a new Workoutplan to the database
	 * 
	 * @param w the new Workoutplan
	 */
	public Workoutplan add(Workoutplan w) {
		int id = 1;
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT MAX(WorkoutPlan_Id) FROM WorkoutPlan";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			if(cursor.getString(0) != null)	id = Integer.parseInt(cursor.getString(0)) + 1;
		}
		sql = "INSERT INTO WorkoutPlan VALUES (1, " + id + ", '" + w.getName() +
				"', '" + sp.format(new Date()) + "')";
		db.execSQL(sql);

		w.setId(id);
		w.setTimeStamp(new Date());
		
		cursor.close();
		
		return w;
	}
	
	/**
	 * Get all Workoutplans in the Database
	 * 
	 * @return ArrayList<Workoutplan>
	 * @author Eric Schmidt & Florian Blessing
	 */
	public ArrayList<Workoutplan> getAll() {
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		ArrayList<Workoutplan> workoutplanList = new ArrayList<Workoutplan>();
		
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "SELECT Workoutplan_Id, WorkoutplanName, Timestamp FROM Workoutplan";

		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				Workoutplan w = new Workoutplan();
				w.setId(Integer.parseInt(cursor.getString(0)));
				w.setName(cursor.getString(1));
				try {
		    		w.setTimeStamp(sp.parse(cursor.getString(2)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				workoutplanList.add(w);
			}while(cursor.moveToNext());
		}
		cursor.close();
		
		return workoutplanList;
	}
	
	/**
	 * Delete a WorkoutPlan 
	 * 
	 * @param w the workoutplan to be deleted
	 * @author Eric Schmidt
	 */
	public void delete(Workoutplan w){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "DELETE FROM Workoutplan WHERE Workoutplan_Id =" + w.getId();
		db.execSQL(sql);
		
	}
	
	/**
	 * Delete all references from the table WorkoutplanHasTrainingDay 
	 * 
	 * @param w the referenced workoutplan
	 * @author Eric Schmidt
	 */
	public void deleteWorkoutPlanFromTrainingDay(Workoutplan w){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "DELETE FROM WorkoutplanHasTrainingDay WHERE Workoutplan_Id =" + w.getId();
		db.execSQL(sql);
		
	}
	
	/**
	 * Fill the TrainingDayIdList from the Workoutplan Object. This is only
	 * needed if you want to undo a delete
	 * 
	 * @author Eric Schmidt
	 */
	public Workoutplan addAdditionalInformation(Workoutplan workoutplan){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		ArrayList<Integer> trainingDayIdList = new ArrayList<Integer>();
		sql = "SELECT TrainingDay_Id FROM WorkoutplanHasTrainingDay WHERE Workoutplan_Id =" + workoutplan.getId();
		
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do{
				trainingDayIdList.add(cursor.getInt(0));
			}while(cursor.moveToNext());
		}
		workoutplan.setTrainingDayIdList(trainingDayIdList);
		cursor.close();
		
		return workoutplan;
	}
	
	/**
	 * Update a WorkoutPlan with the given Information
	 * 
	 * @param w the updated WorkoutPlan
	 * @author Eric Schmidt
	 */
	public void update(Workoutplan w){
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "UPDATE Workoutplan SET WorkoutplanName='" + w.getName() +  "' WHERE Workoutplan_Id=" + w.getId() + "";
		db.execSQL(sql);	
	}
		
	/**
	 * Add a trainingDay to a Workoutplan
	 * 
	 * @param trainingDayId the id of the TrainingDay 
	 * @param workoutplanId the id of the Workoutplan
	 * @author Eric Schmidt
	 */
	public void addTrainingDayToWorkoutplan(int trainingDayId, int workoutplanId) {
		SQLiteDatabase db = myDBHelper.getWritableDatabase();
		sql = "INSERT INTO WorkoutplanHasTrainingDay (Workoutplan_Id, TrainingDay_Id) VALUES (" + workoutplanId + ", " + trainingDayId + ")";
		db.execSQL(sql);	
	}
	
}
