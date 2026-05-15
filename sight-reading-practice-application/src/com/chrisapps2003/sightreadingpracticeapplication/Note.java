package com.chrisapps2003.sightreadingpracticeapplication;

import java.awt.Color;
import java.awt.Image;

public class Note extends StaffObject
{
	protected char pitch; // takes values 'A', 'B', 'C', 'D', 'E', 'F', and 'G'
	protected char accidental; // takes values '#', 'b', and 'n'
    protected int octave;
	protected PracticeLogic logic;
    protected Color color;
    protected int numericPitch; // the 0-indexed pitch of the note (88 piano keys, so pitches are indexed from 0-87)
    protected boolean isInverted;
    protected Image headImage;
    protected Image accidentalImage;
    private boolean accidentalIsVisible;
    
    public Note(char pitch, char accidental, int octave, Duration duration, PracticeLogic logic)
    {	
    	super(duration);
    	this.pitch = pitch;
        this.accidental = accidental;
        this.octave = octave;
        color = Color.BLACK;
        this.logic = logic;
        
        // Determine other fields from constructor parameters
        numericPitch = PracticeLogic.getNumericPitch(pitch, accidental, octave);
        isInverted = numericPitch >= 50; // B4 corresponds with a numeric pitch of 50
        
        // Determine the note head image and accidental image
    	headImage = PracticeWindow.getImage("solid_note_head");
    	headImage = duration == Duration.WHOLE ?  PracticeWindow.getImage("whole_note_head") : headImage;
    	headImage = duration == Duration.HALF ?  PracticeWindow.getImage("half_note_head") : headImage;
    	accidentalImage = null;
    	accidentalImage = accidental == '#' ? PracticeWindow.getImage("sharp") : accidentalImage;
		accidentalImage = accidental == 'b' ? PracticeWindow.getImage("flat") : accidentalImage;
		accidentalImage = accidental == 'n' ? PracticeWindow.getImage("natural") : accidentalImage;
    	
		accidentalIsVisible(accidental != 'n');
    }
    
    // Sets whether the accidental for this note is visible and recalculates the note's width
    protected void accidentalIsVisible(boolean accidentalIsVisible)
    {
    	this.accidentalIsVisible = accidentalIsVisible;
    	
    	// Recalculate the note's width
    	width = headImage.getWidth(null);
    	if (!isInverted)
    	{
    		width = duration == Duration.EIGHTH ? width + PracticeWindow.getImage("eighth_flag").getWidth(null) - 3 : width;
    		width = duration == Duration.SIXTEENTH ? width + PracticeWindow.getImage("sixteenth_flag").getWidth(null) - 3 : width;
    	}
    	width += accidentalImage != null && accidentalIsVisible ? accidentalImage.getWidth(null) + 4 : 0;
    }
    
    protected boolean getAccidentalIsVisible()
    {
    	return accidentalIsVisible;
    }
    
    // Draws the note
    @Override
    protected void draw()
    {
    	// Draw the accidental (if it exists and is visible)
    	int xOffset = 0;
    	int yOffset = 0;
    	if (accidentalImage != null && accidentalIsVisible)
    	{
    		xOffset = accidentalImage.getWidth(null) + 4;
    		int accidentalYOffset = -13; // assumes it's a sharp or natural
    		accidentalYOffset = accidental == 'b' ? -21 : accidentalYOffset;
    		logic.drawSolidColorImage(accidentalImage, color, x, y + accidentalYOffset);		
    	}
    	
    	// Draw the note head
    	logic.drawSolidColorImage(headImage, color, x + xOffset, y);
    	
    	// Draw the stem and flag (if they exist)
    	if (duration != Duration.WHOLE)
    	{
    		xOffset = !isInverted ? xOffset + 20 : xOffset;
        	yOffset = !isInverted ? -48 : 11;
        	logic.setColor(color);
        	logic.fillRect(x + xOffset, y + yOffset, 2, !isInverted ? 53 : 55);
        	
        	// Draw the flag
        	if (duration == Duration.EIGHTH || duration == Duration.SIXTEENTH)
        	{
        		yOffset = isInverted ? yOffset - 1 : yOffset;
        		Image flagImage = PracticeWindow.getImage((duration == Duration.EIGHTH ? "eighth" : "sixteenth") + "_flag" + (isInverted ? "_inverted" : ""));
        		logic.drawSolidColorImage(flagImage, color, x + xOffset, y + yOffset);
        	}
    	}
    }
}