package com.example.smartspacesproject1;

import java.util.*;

public class averageAndFindAnomalies
{
	public static final int WINDOW_SIZE = 5;
	public static final Double TRESHHOLD = 2.0;

	/**
	   * Gets the moving average of a vector of data
	   * @param windowSize 
	   *	the size of the windows to average
	   * @param data
	   *	a Vector of Doubles, the data to be averaged
	   * @return
	   * 	a Vector of Doubles containing averaged data
	   */
	private static Vector<Double> movingAverage(int windowSize, Vector<Double> data)
	{
		//create a new vector for the averaged data
		Vector<Double> newData = new Vector<Double>();

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
	private static boolean checkForAnomalies(Double threshhold, Vector<Double> data)
	{
		//for each value in the data
		for(int i = 0; i < data.size(); i++)
		{
			//if the data is above the threshhold or below the negative threshhold
			if(data.get(i) > threshhold || data.get(i) < -1 * threshhold)
			{
				return true;
			}
		}
		//if no data values were found to be anomalies
		return false;
	}

	public static void main(String args[])
	{
		//initialise variables
		Double[] dataArray = {0.0, -1.0, -0.1, -0.2, 0.0, 10.0, -0.4, -0.1, 0.3, -0.5, -0.1};
		Vector<Double> data = new Vector<Double>(Arrays.asList(dataArray));
		Vector<Double> averagedData = new Vector<Double>();
		boolean anomaly;

		//get moving average of data
		averagedData = movingAverage(WINDOW_SIZE, data);
		//check for anomalies
		anomaly = checkForAnomalies(TRESHHOLD, averagedData);

		//print results (original data, averaged data, if anomalies were found)
		System.out.println("Original data: ");
		System.out.println(data+"\n");
		System.out.println("Averaged data: ");
		System.out.println(averagedData+"\n");
		System.out.println("Anomaly found? "+anomaly);
	}
}