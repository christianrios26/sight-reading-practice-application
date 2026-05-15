package com.chrisapps2003.sightreadingpracticeapplication;

import java.awt.Color;
import java.awt.Image;

public class Rest extends StaffObject
{
	protected Image restImage;
	protected PracticeLogic logic;
	
	public Rest(Duration duration, PracticeLogic logic)
	{
		super(duration);
		restImage = null;
		this.logic = logic;
		
		// Determine the width of the rest
		width = 22; // width for a whole or half rest
		restImage = duration == Duration.QUARTER ? PracticeWindow.getImage("quarter_rest") : restImage;
		restImage = duration == Duration.EIGHTH ? PracticeWindow.getImage("eighth_rest") : restImage;
		restImage = duration == Duration.SIXTEENTH ? PracticeWindow.getImage("sixteenth_rest") : restImage;
		width = restImage != null ? restImage.getWidth(null) : width;
	}

	// Draws the rest
	@Override
	protected void draw()
	{
		if (restImage != null)
		{
			logic.drawSolidColorImage(restImage, Color.BLACK, x, y);
		}
		else // must be a whole or half rest
		{
			logic.setColor(Color.BLACK);
			logic.fillRect(x, y, 22, 10);
		}
	}
}