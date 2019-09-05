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
	   * anomalies above the given threshold value
     *
     * OBSOLETE - REPLACED BY checkForAnomalies()
     *
	   * @param threshold 
	   *	a data value outside this threshold is an anomaly
	   * @param data
	   *	a Vector of Doubles, the data to be analysed for anomalies
	   * @return
	   * 	true if an anomaly is found
	   *	false otherwise
	   */
	public static boolean checkForAnomalies(Double threshold, Vector<Double> data)
	{
		//for each value in the data
		for(int i = 0; i < data.size(); i++)
		{
			//if the data is above the threshold or below the negative threshold (taking gravity into account)
			if(data.get(i) > GRAVITY + threshold || data.get(i) < GRAVITY + (-1 *  threshold))
			{
				return true;
			}
		}
		//if no data values were found to be anomalies
		return false;
	}

	/**
	* Returns the type of anomaly found in the data as an int
	 * @param smallthreshold
	 * 		a data value outside smallthreshold and inside largethreshold is a small anomaly
	 * @param largethreshold
	 * 	 * 	a data value outside largethreshold is a large anomaly
	 * @param data
	 * 		a Vector of Doubles, the data to be analysed for anomalies
	 * @return
	 *		0: No anomaly
	 *		1: Small anomaly
	 *		2: Large anomaly
	 */
	public static anomalyTypePosition getAnomalyType(Double smallthreshold, Double largethreshold, Vector<Double> data)
	{
		//CHECK FOR POTHOLES/BUMPS
        int maxAnomalyFound = 0;
		//for each value in the data
		for(int i = 0; i < data.size(); i++)
		{
			//if the data is outside the small threshold (taking gravity into account)
			if(data.get(i) > GRAVITY + smallthreshold || data.get(i) < GRAVITY - smallthreshold)
			{
			    //if the data is also outside the large threshold (taking gravity into account)
                if (data.get(i) > GRAVITY + largethreshold || data.get(i) < GRAVITY - largethreshold)
                {
                    //a large anomaly was found
                    return new anomalyTypePosition(2, i);
                }
                //a small anomaly was found
                maxAnomalyFound = 1;
            }
		}
		//if no data values were found to be anomalies
		return new anomalyTypePosition(maxAnomalyFound, -1);
	}
}