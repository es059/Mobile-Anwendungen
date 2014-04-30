package com.workout.log.data;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.example.workoutlog.R;
import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceActual;
import com.workout.log.db.PerformanceActualMapper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.view.View;


/**
 * Creates a Line Graph with the data provided by the local database
 *
 * @author Eric Schmidt
 */
public class LineGraph {
	
	private ArrayList<Integer> repetitions = new ArrayList<Integer>();
	private ArrayList<Date> date = new ArrayList<Date>();
	private ArrayList<Double> weight = new ArrayList<Double>();
	
	/**
	 * Initializes the variables weight, date and weight with the data from the table PerformanceActuale
	 * 
	 * @author Eric Schmidt
	 */
	private void getPerformanceActualData(Context context, Exercise exercise){
		PerformanceActualMapper paMapper = new PerformanceActualMapper(context);
		ArrayList<PerformanceActual> performanceActualList = paMapper.getAllPerformanceActual(exercise);
		
		for (PerformanceActual item : performanceActualList){
			repetitions.add(item.getRepetition());
			weight.add(item.getWeight());
			date.add(item.getTimestamp());
		}
	}
	
	/**
	 * Create a LineGraph using the variable weight and repetitions based on one exercise
	 * 
	 * @author Eric Schmidt
	 */
	public View getView(Context context, Exercise exercise){
		
		//Call the Methodes to fill the Variables
		getPerformanceActualData(context, exercise);
		
		/**
		 * Creates the renderer which holds all the other renderers and modifies the appearance of the Graph
		 */
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();  
			
		//Specify the Graph itself
		mRenderer.setMargins(new int[]{50,80,100,0});
		mRenderer.setAxisTitleTextSize(20);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
		mRenderer.setBackgroundColor(Color.TRANSPARENT);
		//Show Grid
		mRenderer.setShowGridY(true);
		mRenderer.setShowGridX(true);
		//Legend and Axis
		mRenderer.setShowAxes(true);
		mRenderer.setXAxisMin(date.get(0).getTime());
		mRenderer.setYLabels(0);
		mRenderer.setXLabels(3);
		mRenderer.setAntialiasing(true);
		mRenderer.setYTitle("Wiederholungen / Gewicht");
		mRenderer.setXTitle("Datum");
		mRenderer.setAxisTitleTextSize(50);
		mRenderer.setYLabelsAlign(Align.RIGHT);
		mRenderer.setLegendTextSize(30);
		mRenderer.setLabelsTextSize(30);
		mRenderer.setFitLegend(true);
		//Scrolling
		double maxDate = date.get(date.size()-1).getTime() * (81300000*1);
		double minDate = date.get(0).getTime();

		mRenderer.setPanEnabled(true, false);
		double[] limits = new double[] {minDate,maxDate,0,0};
		mRenderer.setPanLimits(limits);

		
		/**
		 * Creates a dataset which holds all of the series
		 */
		//Create the Dataset
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset(); //Holds multipleSeries
		//Create the Series
		TimeSeries timeSeriesRepetitions = new TimeSeries("Wiederholungen");
		TimeSeries timeSeriesWeight = new TimeSeries("Gewicht");
		
		dataset.addSeries(timeSeriesRepetitions);
		dataset.addSeries(timeSeriesWeight);
		
		/**
		 * Creates the series renderer and modifies the appearance of each series
		 */
		//Repetition Rendere
		XYSeriesRenderer RepetitionsRenderer = new XYSeriesRenderer();
		RepetitionsRenderer.setColor(Color.parseColor("#FF9900"));
		RepetitionsRenderer.setDisplayChartValues(true);
		RepetitionsRenderer.setChartValuesTextAlign(Align.CENTER);
		RepetitionsRenderer.setChartValuesTextSize(30);
		RepetitionsRenderer.setPointStyle(PointStyle.POINT);
		RepetitionsRenderer.setLineWidth(5);
		RepetitionsRenderer.setFillPoints(true);
		RepetitionsRenderer.setShowLegendItem(true);
		//Weight Rendere
		XYSeriesRenderer WeightRenderer = new XYSeriesRenderer();
		WeightRenderer.setColor(Color.GRAY);
		WeightRenderer.setDisplayChartValues(true);
		WeightRenderer.setChartValuesTextAlign(Align.CENTER);
		WeightRenderer.setChartValuesTextSize(30);
		WeightRenderer.setPointStyle(PointStyle.SQUARE);
		WeightRenderer.setLineWidth(5);
		WeightRenderer.setFillPoints(true);
		WeightRenderer.setShowLegendItem(true);
		
		//Adds the series Renderer to the multipleRenderer
		mRenderer.addSeriesRenderer(RepetitionsRenderer);
		mRenderer.addSeriesRenderer(WeightRenderer);
		
		/**
		 * Fills the X and Y Axis with the given Data. If Date equals 0 than abort
		 */
		if (date != null){
			for (int i = 0; i < date.size(); i++){
				//timeSeriesRepetitions.add(date.get(i), repetitions.get(i));
				timeSeriesRepetitions.add(date.get(i), repetitions.get(i));
				timeSeriesWeight.add(date.get(i), weight.get(i));
			}
		}
		//Create View
		View view = ChartFactory.getTimeChartView(context, dataset, mRenderer,"dd.MM.yyyy");
		return view;
	}
}
