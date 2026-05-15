package com.chrisapps2003.sightreadingpracticeapplication;

import java.awt.Color;
import java.util.ArrayList;

public class Staff
{
	protected static final int LENGTH = 1248; // length of staff
    protected static final int SPACING = 16; // how far apart the lines are 
    protected static final int OBJECT_DRAWING_SPACE = LENGTH - 132;
    protected int x;  
    protected int y;
    protected PracticeLogic logic;
    private ArrayList<Measure> measures;
    private int staffObjectCount;
    private int noteCount;
    private ArrayList<ArrayList<Measure>> systems; // a system is a row of sheet music
    
    public Staff(int x, int y, ArrayList<Measure> measures, PracticeLogic logic)
    {   
    	this.x = x;
        this.y = y;
        this.logic = logic;
        setMeasures(measures);
    }
    
 // Gets a staff object within the entire staff (0-indexed)
    public StaffObject getStaffObject(int i)
    {
    	int objIndex = 0;
    	for (Measure measure : measures)
    	{
    		for (int j = 0; j < measure.getStaffObjectCount(); j++)
    		{
    			StaffObject obj = measure.getStaffObject(j);
    			if (objIndex == i)
    			{
    				return obj;
    			}
    			objIndex++;
    		}
    	}
    	return null;
    }
    
    // Gets a note within the entire staff (0-indexed)
    public Note getNote(int i)
    {
    	int noteIndex = 0;
    	for (Measure measure : measures)
    	{
    		for (int j = 0; j < measure.getStaffObjectCount(); j++)
    		{
    			StaffObject obj = measure.getStaffObject(j);
    			if (obj instanceof Note && noteIndex == i)
    			{
    				return (Note) obj;
    			}
    			else if (obj instanceof Note)
    			{
    				noteIndex++;
    			}
    		}
    	}
    	return null;
    }
    
    // Sets the list of measures this staff contains and determines the list of systems (rows) and the measures they contain
    public void setMeasures(ArrayList<Measure> measures)
    {
    	// Set measures and calculate staff object and note counts for the entire staff
    	this.measures = measures;
    	staffObjectCount = 0;
        noteCount = 0;
        for (Measure measure : measures)
        {
        	staffObjectCount += measure.getStaffObjectCount();
        	noteCount += measure.getNoteCount();
        }
        
        // Initialize the systems array with a shallow copy of the measures array
        systems = new ArrayList<>();
        systems.add(new ArrayList<>(measures));
        
        // Creates more systems as needed to fit all measures on the sheet without overlapping drawings
        for (int i = 0; i < systems.size(); i++)
        {
        	ArrayList<Measure> system = systems.get(i);
        	int systemMinimumWidth = getSystemTotalStaffObjectWidth(system) + 4 * (getSystemStaffObjectCount(system) - 1) + (system.contains(measures.getLast()) ? 12 : 2);
        	while (systemMinimumWidth > OBJECT_DRAWING_SPACE && system.size() > 1)
        	{
        		if (systems.size() <= i + 1)
        		{
        			systems.add(new ArrayList<>());
        		}
        		systems.get(i + 1).addFirst(system.removeLast());
            	systemMinimumWidth = getSystemTotalStaffObjectWidth(system) + 4 * (getSystemStaffObjectCount(system) - 1) + (system.contains(measures.getLast()) ? 12 : 2);
        	}
        }
    }
    
    // Gets the number of notes across the entire staff
    public int getNoteCount()
    {
    	return noteCount;
    }
    
    // Gets the number of staff objects (notes + rests) across the entire staff
    public int getStaffObjectCount()
    {
    	return staffObjectCount;
    }
    
    // Gets the number of measures across the entire staff
    public int getMeasureCount()
    {
    	return measures.size();
    }
    
    public void resetNoteColors() 
    {
    	for (int i = 0; i < noteCount; i++)
    	{
    		getNote(i).color = Color.BLACK;
    	}
    }

    public int getSystemCount()
    {
    	return systems.size();
    }
    
    // Draw the systems that make up the entire staff
    protected void draw()
    {
    	if (systems.size() == 1)
    	{
    		drawSystem(x, y + 111, measures, true, true);
    	}
    	else
    	{
    		for (int i = 0; i < systems.size(); i++)
    		{
    			ArrayList<Measure> system = systems.get(i);
    			drawSystem(x, y + 222 * i, system, i == 0, i == systems.size() - 1);
    		}
    	}
    }
    
    // Draws a single row (system) of sheet music
    // TODO: correctly draw beamed eighth and sixteenth notes within beats
    private void drawSystem(int x, int y, ArrayList<Measure> system, boolean timeSignatureIsVisible, boolean usesEndBarline)
    {
    	// Draw white background
    	logic.setColor(Color.WHITE);
    	logic.fillRect(x, y, LENGTH, 222);
    	
    	int staffX = x;
    	int staffY = y + 78;
    	
    	// Draw the staff lines
		logic.setColor(Color.BLACK);
		for (int i = 0; i < 5; i++)
		{
			logic.fillRect(staffX, staffY + i * SPACING, LENGTH, 2);
		}
		
		// Draw the clef and time signature
		logic.drawSolidColorImage(PracticeWindow.getImage("clef"), Color.BLACK, staffX + 9, staffY - 25);
		if (timeSignatureIsVisible)
		{
			logic.drawSolidColorImage(PracticeWindow.getImage("time_signature"), Color.BLACK, staffX + 69, staffY);
		}		
		
		// Loop draws staff objects in all measures in the system
		int objPixelGap = 2 * (OBJECT_DRAWING_SPACE - getSystemTotalStaffObjectWidth(system) - (usesEndBarline ? 12 : 2)) / (2 * getSystemStaffObjectCount(system) - 1);
		int objIndex = 0; // keeps track of the system staff object index of the objects at the start of each measure
		for (Measure measure : system)
		{
			for (int i = objIndex; i < objIndex + measure.getStaffObjectCount(); i++) // loops through staff objects in each measure (done this way so previous object can be accessed between measures)
			{
				// Get the previous object in this system (used for x positioning)
				StaffObject prevObj = null;
				if (i > 0)
				{
					prevObj = getSystemStaffObject(system, i - 1);
				}
				
				// Get the current object in this system and set its x position
				StaffObject obj = getSystemStaffObject(system, i);
				obj.x = prevObj != null ? prevObj.x + prevObj.width + objPixelGap : staffX + 132;
				
				if (obj instanceof Note)
				{
					// Set note's y position
					Note note = (Note) obj;
					note.y = staffY + (note.octave > 0 ? 30 - (note.pitch - 'G' + 11) % 7 - (note.octave - 1) * 7 : 'G' - note.pitch + 26) * SPACING / 2;
					
					// Draw ledger lines if they exist
					if (note.numericPitch <= 39 || (note.numericPitch == 40 && note.accidental == '#')) // if note is below the staff (C4 or less)
					{
						int ledgerLineX = note.x - 5 + (note.accidentalImage != null && note.getAccidentalIsVisible() ? note.accidentalImage.getWidth(null) + 4 : 0);
						int ledgerLineY = note.y + (note.y - staffY) % SPACING;
						for (int j = 0; ledgerLineY >= staffY + 5 * SPACING; j++)
						{
							logic.setColor(Color.BLACK);
							logic.fillRect(ledgerLineX, ledgerLineY, 33, 2);
							ledgerLineY = note.y + (note.y - staffY) % SPACING - j * SPACING;
						}
					}
					if (note.numericPitch >= 60 || (note.numericPitch == 59 && note.accidental == 'b')) // if note is above the staff (A5 or more)
					{
						int ledgerLineX = note.x - 5 + (note.accidentalImage != null && note.getAccidentalIsVisible() ? note.accidentalImage.getWidth(null) + 4 : 0);
						int ledgerLineY = note.y + SPACING / 2 + Math.abs((staffY - note.y) % SPACING - SPACING / 2);
						for (int j = 0; ledgerLineY <= staffY - SPACING; j++)
						{
							logic.setColor(Color.BLACK);
							logic.fillRect(ledgerLineX, ledgerLineY, 33, 2);
							ledgerLineY = note.y + SPACING / 2 + Math.abs((staffY - note.y) % SPACING - SPACING / 2) + j * SPACING;
						}
					}
				}
				else // it must be a rest
				{
					// Set the rest's y position
					obj.y = staffY;
					obj.y += obj.duration == Duration.WHOLE ? 16 : 0; 
					obj.y += obj.duration == Duration.HALF ? 24 : 0; 
					obj.y += obj.duration == Duration.QUARTER ? 6 : 0; 
					obj.y += obj.duration == Duration.EIGHTH || obj.duration == Duration.SIXTEENTH ? 18 : 0; 
				}
				obj.draw(); // draw the note or rest
				
				if (i == objIndex + measure.getStaffObjectCount() - 1) // if this is the last iteration of the inner loop
				{
					if (!measure.equals(system.getLast()))
					{
						// Draw a bar line
						logic.setColor(Color.BLACK);
						logic.fillRect(obj.x + (obj.width - 1) + objPixelGap / 2, staffY, 2, 66);
						objIndex += measure.getStaffObjectCount();
						break; // ensure the inner loop ends here
					}
					// Draw final bar line
					logic.setColor(Color.BLACK);
					if (usesEndBarline)
					{
						logic.fillRect(staffX + LENGTH - 12, staffY, 2, 66);
						logic.fillRect(staffX + LENGTH - 6, staffY, 6, 66);
					}
					else
					{
						logic.fillRect(staffX + LENGTH - 2, staffY, 2, 66);
					}
				}
			}
		}
    }
    
    // Gets a staff object (note or rest) within a system (0-indexed)
    private StaffObject getSystemStaffObject(ArrayList<Measure> system, int i)
    {
    	int objCount = 0;
    	for (Measure measure : system)
    	{
    		if (objCount + measure.getStaffObjectCount() > i)
    		{
    			return measure.getStaffObject(i - objCount);
    		}
    		objCount += measure.getStaffObjectCount();
    	}
    	return null;
    }
    
    // Gets the number of staff objects (notes + rests) within a system
    private int getSystemStaffObjectCount(ArrayList<Measure> system)
    {
    	int systemStaffObjectCount = 0;
    	
    	for (Measure measure : system)
    	{
    		systemStaffObjectCount += measure.getStaffObjectCount();
    	}
    	
    	return systemStaffObjectCount;
    }
    
    // Gets the sum of the widths of all staff objects in the system in pixels
    private int getSystemTotalStaffObjectWidth(ArrayList<Measure> system)
    {
    	int minWidth = 0;
    	
    	for (Measure measure : system)
    	{
    		minWidth += measure.getTotalStaffObjectWidth();
    	}
    	
    	return minWidth;
    }
}