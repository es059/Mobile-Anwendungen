package com.workout.log.data;


import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.workout.log.bo.Exercise;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Cap;
import android.view.View;


/**
 * Creates a Line Graph with the data provided by the local database
 *
 * @author Eric Schmidt
 */
public class LineGraph {
	
	private int[] repetitions;
	private Date[] date;
	private double[] weight;
	
	/**
	 * Initializes the variable repetitions with the data from the table PerformanceActuale
	 * 
	 * @author Eric Schmidt
	 */
	private void fillRepetitions(){
		
	}
	
	/**
	 * Initializes the variable weight with the data from the table PerformanceActuale
	 * 
	 * @author Eric Schmidt
	 */
	private void fillWeight(){
		
	}
	
	/**
	 * Initializes the variable weight with the data from the table PerformanceActuale
	 * 
	 * @author Eric Schmidt
	 */
	private void fillDate(){
		
	}
	
	/**
	 * Create a LineGraph using the variable weight and repetitions based on one exercise
	 * 
	 * @author Eric Schmidt
	 */
	public View getView(Context context, Exercise exercise){
		
		//Call the Methodes to fill the Variables
		fillDate();
		fillWeight();
		fillRepetitions();
		
		/**
		 * Creates the renderer which holds all the other renderers and modifies the appearance of the Graph
		 */
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer(); 
		//Specify the Graph itself
		mRenderer.setMargins(new int[]{0,0,0,0});
		mRenderer.setAxisTitleTextSize(20);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
		mRenderer.setBackgroundColor(Color.TRANSPARENT);
		//Show Grid
		mRenderer.setShowGridY(true);
		mRenderer.setShowGridX(true);
		//Legend and Axis
		mRenderer.setShowAxes(true);
		mRenderer.setLegendTextSize(20);
		mRenderer.setLabelsTextSize(30);
		mRenderer.setFitLegend(true);
		//Scrolling
		mRenderer.setPanEnabled(true, false);
		
		/**
		 * Creates a dataset which holds all of the series
		 */
		//Create the Dataset
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset(); //Holds multipleSeries
		//Create the Series
		TimeSeries timeSeriesRepetitions = new TimeSeries("");
		TimeSeries timeSeriesWeight = new TimeSeries("");
		
		dataset.addSeries(timeSeriesRepetitions);
		dataset.addSeries(timeSeriesWeight);
		
		/**
		 * Creates the series renderer and modifies the appearance of each series
		 */
		//Repetition Rendere
		XYSeriesRenderer RepetitionsRenderer = new XYSeriesRenderer();
		RepetitionsRenderer.setLineWidth(2);
		RepetitionsRenderer.setFillPoints(true);
		RepetitionsRenderer.setColor(Color.CYAN);
		RepetitionsRenderer.setShowLegendItem(false);
		//Weight Rendere
		XYSeriesRenderer WeightRenderer = new XYSeriesRenderer();
		WeightRenderer.setColor(Color.GRAY);
		WeightRenderer.setPointStyle(PointStyle.SQUARE);
		WeightRenderer.setLineWidth(5);
		WeightRenderer.setFillPoints(true);
		RepetitionsRenderer.setShowLegendItem(false);
		
		//Adds the series Renderer to the multipleRenderer
		mRenderer.addSeriesRenderer(RepetitionsRenderer);
		mRenderer.addSeriesRenderer(WeightRenderer);
		
		/**
		 * Fills the X and Y Axis with the given Data. If Date equals 0 than abort
		 */
		if (date != null){
			for (int i = 0; i < date.length; i++){
				timeSeriesRepetitions.add(date[i], repetitions[i]);
			}
			for (int i = 0; i < date.length; i++){
				timeSeriesRepetitions.add(date[i], weight[i]);
			}
		}
		//Create View
		View view = ChartFactory.getLineChartView(context, 
				dataset, mRenderer);
		
		return view;
	}
}
