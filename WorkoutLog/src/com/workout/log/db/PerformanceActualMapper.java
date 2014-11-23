package com.workout.log.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceActual;
import com.workout.log.data.StatisticListElement;

/**
 * Mapper Class of BusinessObject PerformanceTarget
 * 
 * @author Eric Schmidt
 */
@SuppressLint("SimpleDateFormat") 
public class PerformanceActualMapper {
	private DataBaseHelper myDBHelper;
	private String sql;
	private Context context;
	
	public PerformanceActualMapper(Context context){
		myDBHelper = DataBaseHelper.getInstance(context);
		this.context = context;
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
		cursor.close();
		
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
			sql = "SELECT RepetitionActual, WeightActual, SetActual, PerformanceActual_Id FROM PerformanceActual WHERE"
					+ " TimestampActual = '" + item + "' AND"
					+ " Exercise_Id = " + exercise.getId()
					+ " ORDER BY WeightActual DESC, RepetitionActual DESC";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst()){
				do{
					PerformanceActual performanceActual = new PerformanceActual();
					performanceActual.setExercise(exercise);
					if (cursor.getString(0) != null) performanceActual.setRepetition(cursor.getInt(0));
					if (cursor.getString(1) != null) performanceActual.setWeight(cursor.getDouble(1));
					performanceActual.setSet(cursor.getInt(2));
					performanceActual.setId(cursor.getInt(3));
					try {
						performanceActual.setTimestamp(sp.parse(item));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					performanceActualList.add(performanceActual);
				}while(cursor.moveToNext());
			}
			cursor.close();
		}
		return performanceActualList;
	}
	
	
	/**
	 * Searches the performanceActual Item with the Exercise and the Timestamp. This only works if the exercise is
	 * from the Cardio MuscleGroup since there is only one entry per day.
	 */
	public PerformanceActual getPerformanceActualByExerciseAndDate(Exercise exercise, Date timestamp){
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		PerformanceActual performanceActual = null;
		
		sql = "SELECT PerformanceActual_Id FROM PerformanceActual WHERE Exercise_Id =" + exercise.getId() + " AND TimestampActual = '" + sp.format(timestamp) + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			performanceActual = this.getPerformanceActualById(cursor.getInt(0));
		}
		
		return performanceActual;
	}
	
	/**
	 * Get Performance Actual by a Id
	 */
	
	public PerformanceActual getPerformanceActualById (int performanceActualId){
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		PerformanceActual performanceActual = new PerformanceActual();
		ExerciseMapper eMapper = new ExerciseMapper(context);
		
		sql = "SELECT RepetitionActual, SetActual, WeightActual, TimestampActual, Exercise_Id  FROM PerformanceActual WHERE PerformanceActual_Id = " + performanceActualId;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			performanceActual.setId(performanceActualId);
			performanceActual.setRepetition(cursor.getInt(0));
			performanceActual.setSet(cursor.getInt(1));
			performanceActual.setWeight(cursor.getDouble(2));
			try {
				performanceActual.setTimestamp(sp.parse(cursor.getString(3)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			performanceActual.setExercise(eMapper.getExerciseById(cursor.getInt(4)));
		}
		return performanceActual;	
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
		cursor.close();
		
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
				if (cursor.getString(1) != null) performanceActual.setRepetition(cursor.getInt(1));
				performanceActual.setSet(cursor.getInt(2));
				if (cursor.getString(3) != null) performanceActual.setWeight(cursor.getDouble(3));
				try {
					performanceActual.setTimestamp(sp.parse(cursor.getString(4)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				performanceActual.setExercise(exercise);
				performanceActualList.add(performanceActual);
			} while (cursor.moveToNext());
		}
		cursor.close();
		
		return performanceActualList;
	}
	
	public ArrayList<StatisticListElement> getAllStatisticElements(int exercise_Id, ArrayList<String> dates) {
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		ExerciseMapper eMapper = new ExerciseMapper(context);
		ArrayList<StatisticListElement> items = new ArrayList<StatisticListElement>();
		
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		
		for(int i = 0; i < dates.size(); i++) {
			int count = 0;
			sql = "SELECT * FROM PerformanceActual WHERE Exercise_Id = " + exercise_Id 
					+ " AND TimestampActual = \'" + dates.get(i) + "\' ";
			StatisticListElement object = new StatisticListElement();
			ArrayList<PerformanceActual> paList = new ArrayList<PerformanceActual>();
			object.setTimestamp(dates.get(i));
			object.setPerformanceActualList(paList);
			items.add(i, object);
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst()){
				do {
					PerformanceActual performanceActual = new PerformanceActual();
					performanceActual.setId(cursor.getInt(0));
					if (cursor.getString(1) != null) performanceActual.setRepetition(cursor.getInt(1));
					performanceActual.setSet(cursor.getInt(2));
					if (cursor.getString(3) != null) performanceActual.setWeight(cursor.getDouble(3));
					try {
						performanceActual.setTimestamp(sp.parse(cursor.getString(4)));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					performanceActual.setExercise(eMapper.getExerciseById(exercise_Id));
					items.get(i).getPerformanceActualList().add(count,performanceActual);
					count++;
				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		return items;
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
	public ArrayList<PerformanceActual> getPreviousPerformanceActual(Calendar currentDate, Exercise currentExercise){
		int i = 0;
		Date dateItem =new Date();
		ArrayList<Date> dateList = new ArrayList<Date>();
		boolean noMatch = false;
		
		ArrayList<PerformanceActual> performanceActualList = new ArrayList<PerformanceActual>();
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		Cursor cursor;
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		
		/**
		 * Create a List of dates where this exercise was exercised. The List starts with the oldest date
		 */
		sql = "SELECT DISTINCT TimestampActual FROM PerformanceActual WHERE Exercise_ID = " + currentExercise.getId();
		cursor = db.rawQuery(sql,null);
		if (cursor.moveToFirst()){
			do {
				try {
					if (!cursor.getString(0).equals(sp.format(currentDate.getTime()))){
						dateList.add(sp.parse(cursor.getString(0)));
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			} while (cursor.moveToNext());
		}
		
		/**
		 * Reverse ArrayList so that the newest date is the first entry
		 */
		Collections.reverse(dateList);
		
		/**
		 * Get the first date which is before the currentDate. Since the List is in order the first match
		 * will always be the right one
		 */
		do{
			if (i >= dateList.size()){
				noMatch = true;
			}else{
				dateItem = dateList.get(i);
			}
			i++;
		}while (noMatch == false && !dateItem.before(currentDate.getTime()));
		
		if (noMatch != true){
			sql = "SELECT * FROM PerformanceActual WHERE Exercise_Id = " + currentExercise.getId()
					+ " AND TimestampActual = '" + sp.format(dateItem.getTime()) + "' ORDER BY SetActual";
			cursor = db.rawQuery(sql,null);
			if (cursor.moveToFirst()){
				do{
					PerformanceActual performanceActual = new PerformanceActual();
					performanceActual.setId(cursor.getInt(0));
					if (cursor.getString(1) != null) performanceActual.setRepetition(cursor.getInt(1));
					performanceActual.setSet(cursor.getInt(2));
					if (cursor.getString(3) != null) performanceActual.setWeight(cursor.getDouble(3));
					try {
						performanceActual.setTimestamp(sp.parse(cursor.getString(4)));
					} catch (ParseException e) { 
						e.printStackTrace();
					}
					performanceActual.setExercise(currentExercise);
					performanceActualList.add(performanceActual);
				}while(cursor.moveToNext());
			}
			
			cursor.close();
		}
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
		int i = 0;
		boolean noMatch = false;
		Date dateItem =new Date();
		ArrayList<Date> dateList = new ArrayList<Date>();
		
		ArrayList<PerformanceActual> performanceActualList = new ArrayList<PerformanceActual>();
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		Cursor cursor;
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
	
		/**
		 * Create a List of dates where this exercise was exercised. The List starts with the oldest date
		 */
		sql = "SELECT DISTINCT TimestampActual FROM PerformanceActual WHERE Exercise_ID = " + currentExercise.getId();
		cursor = db.rawQuery(sql,null);
		if (cursor.moveToFirst()){
			do {
				try {
					dateList.add(sp.parse(cursor.getString(0)));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			} while (cursor.moveToNext());
		}
		
		Collections.sort(dateList);
		
		
		/**
		 * Get the first date which is after the currentDate. Since the List is in order the first match
		 * will always be the right one
		 */
		do{
			if (i >= dateList.size()){
				noMatch = true;
			}else{
				dateItem = dateList.get(i);
			}
			i++;
		}while (noMatch == false && !dateItem.after(currentDate.getTime()));
		
		if (noMatch != true){
			sql = "SELECT * FROM PerformanceActual WHERE Exercise_Id = " + currentExercise.getId()
					+ " AND TimestampActual = '" + sp.format(dateItem.getTime()) + "' ORDER BY SetActual";
			cursor = db.rawQuery(sql,null);
			
			if (cursor.moveToFirst()){
				do{
					PerformanceActual performanceActual = new PerformanceActual();
					performanceActual.setId(cursor.getInt(0));
					if (cursor.getString(1) != null) performanceActual.setRepetition(cursor.getInt(1));
					performanceActual.setSet(cursor.getInt(2));
					if (cursor.getString(3) != null) performanceActual.setWeight(cursor.getDouble(3));
					try {
						performanceActual.setTimestamp(sp.parse(cursor.getString(4)));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					performanceActual.setExercise(currentExercise);
					performanceActualList.add(performanceActual);
				}while(cursor.moveToNext());
			}
			cursor.close();
		}
		return performanceActualList;
		
	}
	
	/**
	 * Save or Update one PerformanceActual Object into the Database
	 * 
	 *  @param Exercise exercise
	 *  @return Exercise
	 *  @author Eric Schmidt
	 */
	public void addPerformanceActual(PerformanceActual performanceActual, Date date){
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
			cursor.close();
		}else{
			id = performanceActual.getId();
		}
		sql= "INSERT OR REPLACE INTO PerformanceActual "
				+ "(PerformanceActual_Id, RepetitionActual, SetActual, "
				+ "WeightActual, TimestampActual, Exercise_Id) "
				+ "VALUES (" + id + "," +((performanceActual.getRepetition() == -1) ? null :  performanceActual.getRepetition()) 
				+ "," + performanceActual.getSet()
				+ "," + ((performanceActual.getWeight() == -1) ? null : performanceActual.getWeight())
				+ ",'" + sp.format(date)
				+ "'," + performanceActual.getExercise().getId() + ")";
				
		db.execSQL(sql);
		db.close();		
	}
	
	/**
	 * Get one PerformanceTarget by an Exercise id
	 * 
	 *  @param Exercise exercise
	 *  @param String timestamp in SimpleDateFormat dd.MM.yyyy
	 *  @return Exercise
	 *  @author Eric Schmidt
	 */
	public ArrayList<PerformanceActual> getCurrentPerformanceActual(int exercise_Id){
		ExerciseMapper eMapper = new ExerciseMapper(context);
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		ArrayList<PerformanceActual> performanceActualList = new ArrayList<PerformanceActual>();
		SQLiteDatabase db = this.myDBHelper.getReadableDatabase();
		sql = "SELECT * FROM PerformanceActual WHERE Exercise_Id = " + exercise_Id 
				+ "";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()){
			do {
				PerformanceActual performanceActual = new PerformanceActual();
				performanceActual.setId(cursor.getInt(0));
				if (cursor.getString(1) != null) performanceActual.setRepetition(cursor.getInt(1));
				performanceActual.setSet(cursor.getInt(2));
				if (cursor.getString(3) != null) performanceActual.setWeight(cursor.getDouble(3));
				try {
					performanceActual.setTimestamp(sp.parse(cursor.getString(4)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				performanceActual.setExercise(eMapper.getExerciseById(exercise_Id));
				performanceActualList.add(performanceActual);
			} while (cursor.moveToNext());
		}
		cursor.close();
		
		return performanceActualList;
	}
	
}
