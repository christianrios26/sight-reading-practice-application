package com.chrisapps2003.sightreadingpracticeapplication;

import java.util.Arrays;

public class Measure
{	
	protected int beatCount;
	protected int timeUnits;
	protected int remainingTimeUnits;
	protected int staffObjectCount;
	protected int noteCount;
	protected Duration durationPerBeat;
	protected PracticeLogic logic;
	private StaffObject[] staffObjects;
	
	public Measure(int beatCount, Duration durationPerBeat, PracticeLogic logic) // essentially takes in the time signature the measure is in
	{
		this.beatCount = beatCount;
		timeUnits = durationPerBeat.getTimeUnits() * beatCount;
		remainingTimeUnits = timeUnits;
		staffObjectCount = 0;
		noteCount = 0;
		this.durationPerBeat = durationPerBeat;
		staffObjects = new StaffObject[timeUnits];
		this.logic = logic;
	}
	
	// Adds a note to the measure
	public boolean addNote(char pitch, char accidental, int octave, Duration duration)
	{
		Note newNote = new Note(pitch, accidental, octave, duration, logic);
		if (!addStaffObject(newNote))
		{
			return false;
		}
		
		// Logic for setting accidental visibility in the case the note was added to the measure
		for (int i = staffObjectCount - 2; i >= 0; i--)
		{
			StaffObject prevObj = getStaffObject(i);
			if (prevObj instanceof Note)
			{
				Note prevNote = (Note) prevObj;
				if (prevNote.pitch == newNote.pitch && prevNote.octave == newNote.octave)
				{
					newNote.accidentalIsVisible(prevNote.accidental != newNote.accidental);
					break;
				}
			}
		}
		return true;
	}
	
	// Adds a rest to the measure
	public boolean addRest(Duration duration)
	{
		return addStaffObject(new Rest(duration, logic));
	}
	
	// Gets a staff object (note or rest) in the measure (0-indexed)
	public StaffObject getStaffObject(int i)
	{
		int objIndex = 0;
		for (int j = 0; j < staffObjects.length; j++)
		{
			StaffObject obj = staffObjects[j];
			if (obj != null && objIndex == i)
			{
				return obj;
			}
			else if (obj != null)
			{
				objIndex++;
			}
		}
    	return null;
	}
	
	// Gets the number of staff objects (notes + rests) in the measure
	public int getStaffObjectCount()
	{
		return staffObjectCount;
	}
	
	// Gets the number of notes in the measure
	public int getNoteCount()
	{
		return noteCount;
	}
	
	// The following methods are intended to be used for note beaming logic later
	public int getTimeUnits()
	{
		return timeUnits;
	}
	
	public void clear()
	{
		staffObjects = new StaffObject[timeUnits];
		staffObjectCount = 0;
		noteCount = 0;
	}
	
	public int getStartTime(StaffObject obj)
	{
		return Arrays.asList(staffObjects).indexOf(obj);
	}
	// ------------------------------------------------------------------------------
	
	public boolean isFull()
	{
		return remainingTimeUnits == 0;
	}
	
	// Gets the sum of the widths of all staff objects in the measure in pixels
	public int getTotalStaffObjectWidth()
	{
		int minWidth = 0;
		for (StaffObject obj : staffObjects)
		{
			if (obj != null)
			{
				minWidth += obj.width;
			}
		}
		return minWidth;
	}
	
	// Adds a note or rest to the measure
	private boolean addStaffObject(StaffObject obj)
	{
		if (remainingTimeUnits - obj.duration.getTimeUnits() < 0)
		{
			return false;
		}
		staffObjects[timeUnits - remainingTimeUnits] = obj;
		remainingTimeUnits -= obj.duration.getTimeUnits();
		staffObjectCount++;
		noteCount = obj instanceof Note ? noteCount + 1 : noteCount;
		return true;
	}
}