package com.chrisapps2003.sightreadingpracticeapplication;

// Enum represents durations and the number of time units they occupy in a measure
public enum Duration
{
	WHOLE(16),
	HALF(8),
	QUARTER(4),
	EIGHTH(2),
	SIXTEENTH(1);
	
	private int timeUnits;
	
	private Duration(int timeUnits)
	{
		this.timeUnits = timeUnits;
	}
	
	public int getTimeUnits()
	{
		return timeUnits;
	}
}