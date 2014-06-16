package com.workout.log.graph;


import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;

import com.workout.log.bo.Exercise;
import com.workout.log.bo.PerformanceActual;
import com.workout.log.db.PerformanceActualMapper;


/**
 * Creates a Line Graph with the data provided by the local database
 *
 * @author Eric Schmidt
 */
public class LineGraph{
	
	private ArrayList<Double> repetitions = new ArrayList<Double>();
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
			repetitions.add((double) item.getRepetition());
			weight.add(item.getWeight());
			date.add(item.getTimestamp());
		}
	}

	  public View getView(Context context,Exercise exericse) {
		View mChart = null;
		getPerformanceActualData(context, exericse);
		if (!date.isEmpty()){  
			XYSeries weightSeries =  new XYSeries("Gewicht",0);
			XYSeries repetitionsSeries =  new XYSeries("Wiederholung",1);
	
			XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	
			XYSeriesRenderer weightRenderer = new XYSeriesRenderer();
			weightRenderer.setColor(Color.parseColor("#FF9900"));
			weightRenderer.setDisplayChartValues(true);
			weightRenderer.setChartValuesTextAlign(Align.CENTER);
			weightRenderer.setChartValuesTextSize(30);
			weightRenderer.setPointStyle(PointStyle.CIRCLE);
			weightRenderer.setLineWidth(5);
			weightRenderer.setFillPoints(true);
			weightRenderer.setShowLegendItem(true);
			
	
		    XYSeriesRenderer repetitionsRenderer = new XYSeriesRenderer();
		    repetitionsRenderer.setColor(Color.GRAY);
		    repetitionsRenderer.setDisplayChartValues(true);
		    repetitionsRenderer.setChartValuesTextAlign(Align.CENTER);
		    repetitionsRenderer.setChartValuesTextSize(30);
		    repetitionsRenderer.setPointStyle(PointStyle.CIRCLE);
		    repetitionsRenderer.setLineWidth(5);
		    repetitionsRenderer.setFillPoints(true);
		    repetitionsRenderer.setShowLegendItem(true);
	
	
	
		    XYMultipleSeriesRenderer  mRenderer = new XYMultipleSeriesRenderer(2);
		    mRenderer.setXLabels(0);
	
		    for(int i=0; i<weight.size();i++){
		    	weightSeries.add(i, weight.get(i));
		    	repetitionsSeries.add(i, repetitions.get(i));
		        Date dat = date.get(i);
		        Format formatter = null;
		        if (i%2 == 0) { 
		        	formatter = new SimpleDateFormat("dd.MM.yy");
		        }else{
		        	formatter = new SimpleDateFormat("\n\n\n\n\n\n dd.MM.yy");
		    	}
		        String date = formatter.format(dat);
		        mRenderer.addXTextLabel(i, date);
		    }
		    dataset.addSeries(0, weightSeries);
		    dataset.addSeries(1, repetitionsSeries);
		    //Specify the Graph itself
		    //(int) mRenderer.getLabelsTextSize() + 25
		  	mRenderer.setMargins(new int[]{0,0,100,0});
		  	mRenderer.setAxisTitleTextSize(20);
		  	mRenderer.setApplyBackgroundColor(true);
		  	mRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
		  	mRenderer.setBackgroundColor(Color.TRANSPARENT);
		  	//Show Grid
		  	mRenderer.setShowGridY(true);
		  	mRenderer.setShowGridX(true);
		  	//Legend and Axis
		  	mRenderer.setShowAxes(true);
		  	//mRenderer.setXAxisMin(date.get(0).getTime());
		  	mRenderer.setYLabels(0);
		  	mRenderer.setXLabels(0);
			mRenderer.setAntialiasing(true);
			mRenderer.setAxisTitleTextSize(50);
			mRenderer.setYLabelsAlign(Align.RIGHT);
			mRenderer.setLegendTextSize(30);
			mRenderer.setLabelsTextSize(30);
			mRenderer.setFitLegend(true);
			//Scrolling
			double maxDate = date.get(date.size()-1).getTime() * (81300000*1);
			double minDate = date.get(0).getTime();
			
			mRenderer.setPanEnabled(true, false);
			//double[] limits = new double[] {minDate,maxDate,0,0};
			//mRenderer.setPanLimits(limits);
			//Diverse
		  	mRenderer.setShowGrid(true);
		  	mRenderer.setXLabelsAlign(Align.CENTER);
		  	mRenderer.setYLabelsAlign(Align.RIGHT,0);
		    mRenderer.addSeriesRenderer(weightRenderer);
		    mRenderer.addSeriesRenderer(repetitionsRenderer);
		    mRenderer.setApplyBackgroundColor(true);
		    mRenderer.setBackgroundColor(Color.argb(0x00, 0x01, 0x01, 0x01));
		    mRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
		    mRenderer.setYLabelsColor(0, Color.BLACK);
		    mRenderer.setXLabelsColor(Color.BLACK);
		    mRenderer.setXAxisMin(-0.5,0);
		    mRenderer.setXAxisMin(-0.5,1);
		    //mRenderer.setXAxisMax(weightSeries.getMaxX()+3);
		    mRenderer.setYAxisMin(weightSeries.getMinY()-5,0);
		    mRenderer.setYAxisMax(weightSeries.getMaxY()+5,0);
		    mRenderer.setYAxisMin(repetitionsSeries.getMinY()-5,1);
		    mRenderer.setYAxisMax(repetitionsSeries.getMaxY()+8,1);
		    mRenderer.setYAxisAlign(Align.RIGHT, 0);   
		    
		    mChart = ChartFactory.getLineChartView(context, dataset, mRenderer);
		}
		return mChart;
	}
	
}
