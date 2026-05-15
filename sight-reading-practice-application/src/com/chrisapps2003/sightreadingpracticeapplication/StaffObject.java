package com.chrisapps2003.sightreadingpracticeapplication;

// Class represents either a note or a rest
public abstract class StaffObject
{
	protected int width;
	protected int x;
	protected int y;
	protected Duration duration;
	
	public StaffObject(Duration duration)
	{
		width = 0;
		x = 0;
		y = 0;
		this.duration = duration;
	}
	
	protected abstract void draw();
}