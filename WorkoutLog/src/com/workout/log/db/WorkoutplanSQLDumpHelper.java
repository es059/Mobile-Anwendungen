package com.workout.log.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.os.Environment;

import com.remic.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceTarget;
import com.workout.log.bo.TrainingDay;
import com.workout.log.bo.Workoutplan;

public class WorkoutplanSQLDumpHelper {
	
    private WorkoutplanMapper workoutplanMapper;
    private ExerciseMapper exerciseMapper;
    private TrainingDayMapper trainingDayMapper;
    private MuscleGroupMapper muscleGroupMapper;
	
	private Workoutplan workoutplan = null;
	private String fileName = "";
	private Context context;
	
	public WorkoutplanSQLDumpHelper(Context context){
		this.context = context;
	    this.workoutplanMapper = new WorkoutplanMapper(context);
	    this.exerciseMapper = new ExerciseMapper(context);
	    this.trainingDayMapper = new TrainingDayMapper(context);
	    this.muscleGroupMapper = new MuscleGroupMapper(context);
	}
	
	public File createSQLDump(Workoutplan workoutplan){
		this.workoutplan = workoutplan;
		
		fileName = context.getString(R.string.WorkoutplanDatabaseName) + "_" + workoutplan.getName() + ".Log";
		
		File sqlDump = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
		try {
			sqlDump.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/**
		 * Create String SQL Dump
		 */
		String sqlDumpString = createSQLString();
		try {
			FileWriter writer = new FileWriter(sqlDump);
			writer.write(sqlDumpString);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sqlDump;
	}
	
	private String createSQLString(){	
		StringBuffer completeSQLDump = new StringBuffer();
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		String sql = "";
	      
		workoutplan = workoutplanMapper.addAdditionalInformation(workoutplan);
		/**
		 * Workoutplan
		 */
		sql = "Workoutplan," + workoutplan.getName() + "," + sp.format(workoutplan.getTimeStamp()) + ";";
		completeSQLDump.append(sql);
		/**
		 * WorkoutplanHasTrainingDay
		 */
		for (Integer trainingdayId : workoutplan.getTrainingDayIdList()){
			/**
			 * TrainingDay
			 */
			TrainingDay trainingday = trainingDayMapper.getTrainingDayById(trainingdayId);
			sql = "TrainingDay," + trainingday.getName() + ";";
			completeSQLDump.append(sql);
	
			/**
			 * TrainingDayhasExercise
			 */
			int exerciseOrder = 1;
			for (Exercise exercise : exerciseMapper.getExerciseByTrainingDay(trainingdayId)){    		 
				exercise = exerciseMapper.addAdditionalInfo(exercise);
				/**
				 * PerformanceTarget and Exercise
				 */
				for (PerformanceTarget performanceTarget : exercise.getPerformanceTargetList()){
					if (performanceTarget.getTrainingDayId() == trainingdayId){
						sql = "Exercise," + exercise.getMuscleGroup().getId() + "," +  exercise.getName() + "," +
								performanceTarget.getSet() + "," + performanceTarget.getRepetition() + ";"; 
						completeSQLDump.append(sql);
					}
				}
			}
		}
		return completeSQLDump.toString();
	}

	public void importSQLDump(File sqlDump){
		StringBuffer sqlDumpString = new StringBuffer();
		try {
		    BufferedReader br = new BufferedReader(new FileReader(sqlDump));
		    String line;

		    while ((line = br.readLine()) != null) {
		    	sqlDumpString.append(line);
		    }
		}
		catch (IOException e) {
		    //You'll need to add proper error handling here
		}
		
		String[] splitSQLDump = sqlDumpString.toString().split(";");
		SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
		
		Workoutplan workoutplan = new Workoutplan();
		TrainingDay trainingDay = new TrainingDay();
		boolean finished = false;
		
		for (int i = 0; i < splitSQLDump.length; i++){
			String[] splitAttributes = splitSQLDump[i].split(",");
			finished = false;
			for (int j = 0; j < splitAttributes.length; j++){
				if (!finished){
					switch(splitAttributes[j].toString()){
						case "Workoutplan":
							workoutplan.setName(splitAttributes[j + 1]);
							try {
								workoutplan.setTimeStamp(sp.parse(splitAttributes[j + 2]));
							} catch (ParseException e) {
								e.printStackTrace();
							}
							workoutplan = workoutplanMapper.add(workoutplan);
							
							finished = true;
							break;
						case "TrainingDay":
							trainingDay = new TrainingDay();
							trainingDay.setName(splitAttributes[j + 1]);
							
							trainingDay = trainingDayMapper.add(trainingDay);
							workoutplanMapper.addTrainingDayToWorkoutplan(trainingDay.getId(), workoutplan.getId());
							
							finished = true;
							break;
						case "Exercise":
							Exercise exercise = new Exercise();
							exercise.setMuscleGroup(muscleGroupMapper.getMuscleGroupById
									(Integer.parseInt(splitAttributes[j + 1].trim())));
							exercise.setName(splitAttributes[j + 2]);
							
							exercise = exerciseMapper.add(exercise);
							trainingDayMapper.addExerciseToTrainingDayAndPerformanceTarget(trainingDay.getId(), exercise.getId(), Integer.parseInt(splitAttributes[j + 3].trim()), Integer.parseInt(splitAttributes[j + 4].trim()));	
							
							finished = true;
							break;
					}
				}
			}
		}
		

	}
}
