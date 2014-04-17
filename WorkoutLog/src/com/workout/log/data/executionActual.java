package com.workout.log.data;

import java.util.Date;

public class executionActual {
	
	private int executionActualId;
	private int repsActual;
	private int setActual;
	private double weight;
	private Date executionTimestamp;
	private int exerciseId;
	public int getExecutionActualId() {
		return executionActualId;
	}
	public void setExecutionActualId(int executionActualId) {
		this.executionActualId = executionActualId;
	}
	public int getRepsActual() {
		return repsActual;
	}
	public void setRepsActual(int repsActual) {
		this.repsActual = repsActual;
	}
	public int getSetActual() {
		return setActual;
	}
	public void setSetActual(int setActual) {
		this.setActual = setActual;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public Date getExecutionTimestamp() {
		return executionTimestamp;
	}
	public void setExecutionTimestamp(Date executionTimestamp) {
		this.executionTimestamp = executionTimestamp;
	}
	public int getExerciseId() {
		return exerciseId;
	}
	public void setExerciseId(int exerciseId) {
		this.exerciseId = exerciseId;
	}

}
