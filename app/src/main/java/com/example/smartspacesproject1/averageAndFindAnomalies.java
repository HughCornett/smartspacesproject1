package com.example.smartspacesproject1;

import java.util.*;

public class averageAndFindAnomalies
{
	public static final Double GRAVITY = 9.81;

	/**
	   * Gets the moving average of a vector of data
	   * @param windowSize 
	   *	the size of the windows to average
	   * @param data
	   *	a Vector of Doubles, the data to be averaged
	   * @return
	   * 	a Vector of Doubles containing averaged data
	   */
	public static Vector<Double> movingAverage(int windowSize, Vector<Double> data)
	{
		//create a new vector for the averaged data
		Vector<Double> newData = new Vector<>();

		//for every windowSize value (if windowSize is 5, for every 5th value)
		for(int i = 0; i <= data.size() - windowSize; i += windowSize)
		{
			//add the next windowSize values
			Double sum = 0.0;
			for(int j = 0; j < windowSize; j++)
			{
				sum += data.get(i+j);
			}
			//get the average of these values
			Double average = sum / windowSize;
			//add to the new data
			newData.add(average);
		}	
		return newData;
	}

	/**
	   * Checks a Vector of filtered accelerometer data for
	   * anomalies above the given threshhold value
	   * @param threshhold 
	   *	a data value outside this threshhold is an anomaly
	   * @param data
	   *	a Vector of Doubles, the data to be analysed for anomalies
	   * @return
	   * 	true if an anomaly is found
	   *	false otherwise
	   */
	public static boolean checkForAnomalies(Double threshhold, Vector<Double> data)
	{
		//for each value in the data
		for(int i = 0; i < data.size(); i++)
		{
			//if the data is above the threshhold or below the negative threshhold (taking gravity into account)
			if(data.get(i) > GRAVITY + threshhold || data.get(i) < GRAVITY + (-1 *  threshhold))
			{
				return true;
			}
		}
		//if no data values were found to be anomalies
		return false;
	}
}