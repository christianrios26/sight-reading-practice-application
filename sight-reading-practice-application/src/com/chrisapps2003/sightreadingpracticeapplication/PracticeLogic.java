package com.chrisapps2003.sightreadingpracticeapplication;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PracticeLogic
{
	protected static final Color CORRECT_COLOR =  new Color(0xAAAAAA);
	protected static final Color INCORRECT_COLOR =  new Color(0xFA9392);
	protected final HashMap<Integer, Boolean> KEY_CODES_MAP;
	protected final Integer[] NOTE_KEY_CODES;
	
	protected PracticeWindow window;
	protected Staff staff;
	protected int attemptCount;
	protected double pitchAccuracy;
	protected double rhythmAccuracy;
	protected double totalAccuracy;
	protected double averagePitchAccuracy;
	protected double averageRhythmAccuracy;
	protected double averageTotalAccuracy;
	
	private RandomNoteGenerator randomNoteGenerator;
	private HashMap<Duration, Long> absoluteDurations;
	private int notesPlayed;
	private int correctNotes;
	private int correctRhythms;
	private int currStaffObjIndex;
	private double totalPitchAccuracy;
	private double totalRhythmAccuracy;
	private double totalTotalAccuracy;
	private long notePlayedTime;
	private long notePlayedInterval;
	private boolean attemptCompleted;
	
	public PracticeLogic(PracticeWindow window)
	{
		KEY_CODES_MAP = new HashMap<>();
		NOTE_KEY_CODES = new Integer[]
		{
			KeyEvent.VK_Z, KeyEvent.VK_X, KeyEvent.VK_D, KeyEvent.VK_C, KeyEvent.VK_F,
			KeyEvent.VK_V, KeyEvent.VK_G, KeyEvent.VK_B, KeyEvent.VK_N, KeyEvent.VK_J,
			KeyEvent.VK_M, KeyEvent.VK_K, KeyEvent.VK_COMMA, KeyEvent.VK_PERIOD, KeyEvent.VK_SEMICOLON,
			KeyEvent.VK_SLASH, KeyEvent.VK_QUOTE, KeyEvent.VK_Q, KeyEvent.VK_2, KeyEvent.VK_W,
			KeyEvent.VK_E, KeyEvent.VK_4, KeyEvent.VK_R, KeyEvent.VK_5, KeyEvent.VK_T,
			KeyEvent.VK_Y, KeyEvent.VK_7, KeyEvent.VK_U, KeyEvent.VK_8, KeyEvent.VK_I,
			KeyEvent.VK_9, KeyEvent.VK_O, KeyEvent.VK_P, KeyEvent.VK_MINUS, KeyEvent.VK_OPEN_BRACKET,
			KeyEvent.VK_EQUALS, KeyEvent.VK_CLOSE_BRACKET
		};
		for (int code : NOTE_KEY_CODES)
		{
			KEY_CODES_MAP.put(code, false);
		}
		KEY_CODES_MAP.put(KeyEvent.VK_ENTER, false);
		KEY_CODES_MAP.put(KeyEvent.VK_BACK_SPACE, false);
		
		this.window = window;
		attemptCount = 0;
		pitchAccuracy = 0;
		rhythmAccuracy = 0;
		totalAccuracy = 0;
		averagePitchAccuracy = 0;
		averageRhythmAccuracy = 0;
		averageTotalAccuracy = 0;
		
		randomNoteGenerator = new RandomNoteGenerator(this);
		absoluteDurations = new HashMap<>();
		notesPlayed = 0;
		correctNotes = 0;
		correctRhythms = 0;
		currStaffObjIndex = 0;
		totalPitchAccuracy = 0;
		totalRhythmAccuracy = 0;
		totalTotalAccuracy = 0;
		notePlayedTime = System.currentTimeMillis();
		notePlayedInterval = 0;
		attemptCompleted = false;
		
		staff = new Staff(48, 159, randomNoteGenerator.getRandomMeasures(3), this);
		while (staff.getSystemCount() > 2)
		{
			staff.setMeasures(randomNoteGenerator.getRandomMeasures(3));
		}
	}
	
	public void keyPressed(int keyCode, boolean isControlDown)
	{
		Boolean keyIsBeingHeld = KEY_CODES_MAP.get(keyCode);

		if (keyIsBeingHeld != null && !keyIsBeingHeld)
		{
			switch (keyCode)
			{
			case KeyEvent.VK_ENTER:
				if (window.screenNumber < 2)
				{
					window.screenNumber++;
					window.repaintGame();
				}
				else
				{
					if (attemptCompleted)
					{
						startNewAttempt();
					}
					else
					{
						resetCurrentAttempt();
					}
				}
				break;
			case KeyEvent.VK_R: // Ctrl + R check
				if (isControlDown && window.screenNumber == 2)
				{
					resetAttemptCount();
				}
				break;
			case KeyEvent.VK_BACK_SPACE:
				if (window.screenNumber > 0)
				{
					window.screenNumber--;
					window.repaintGame();
				}
			}
			
			// Condition for checking for note inputs (excludes case with Ctrl + R input)
			if (Arrays.asList(NOTE_KEY_CODES).contains(keyCode) && !(keyCode == KeyEvent.VK_R && isControlDown) && window.screenNumber == 2 && notesPlayed < staff.getNoteCount())
			{
				notesPlayed++;
				Note note = staff.getNote(notesPlayed - 1);
				StaffObject obj;
				
				// Rhythm accuracy check
				if (notesPlayed == 1)
				{
					correctRhythms++;
					notePlayedTime = System.currentTimeMillis();
					while (!note.equals(staff.getStaffObject(currStaffObjIndex)))
					{
						currStaffObjIndex++;
					}
				}
				else if (notesPlayed == 2)
				{
					correctRhythms++;
					Note firstNote = staff.getNote(notesPlayed - 2);
					long temp = System.currentTimeMillis();
					notePlayedInterval = temp - notePlayedTime;
					double intervalDivision = 0;
					while (!note.equals(staff.getStaffObject(currStaffObjIndex)))
					{
						obj = staff.getStaffObject(currStaffObjIndex);
						intervalDivision += (double) firstNote.duration.getTimeUnits() / obj.duration.getTimeUnits();
						currStaffObjIndex++;
					}
					notePlayedInterval /= intervalDivision;
					notePlayedTime = temp;
					populateAbsoluteDurations(notePlayedInterval, firstNote.duration);
				}
				else
				{
					long temp = System.currentTimeMillis();
					notePlayedInterval = temp - notePlayedTime;
					long totalDuration = 0;
					while (!note.equals(staff.getStaffObject(currStaffObjIndex)))
					{
						obj = staff.getStaffObject(currStaffObjIndex);
						totalDuration += absoluteDurations.get(obj.duration);
						currStaffObjIndex++;
					}
					notePlayedTime = temp;
					if (Math.abs(totalDuration - notePlayedInterval) < 50)
					{
						correctRhythms++;
					}
				}
				
				// Pitch accuracy check
				boolean correctPitch = keyCode == getPitchKeyCode(note);
				if (correctPitch)
				{
					correctNotes++;
					note.color = CORRECT_COLOR;
				}
				else
				{
					note.color = INCORRECT_COLOR;
				}
				if (notesPlayed >= staff.getNoteCount())
				{
					completeAttempt();
					
				}
				window.repaintGame();
			}
		}

		KEY_CODES_MAP.put(keyCode, true); // this key has been pressed, so put true in the map
	}
	
	public void keyReleased(int keyCode)
	{
		KEY_CODES_MAP.put(keyCode, false); // this key has been released, so put false in the map
	}
	
	public int getAttemptCount()
	{
		return attemptCount;
	}
	
	public void setAttemptCount(int attempts)
	{
		this.attemptCount = attempts;
	}
	
	public double getPitchAccuracy()
	{
		return pitchAccuracy;
	}
	
	public double getRhythmAccuracy()
	{
		return rhythmAccuracy;
	}

	// Gets the key code that a note correspond with based on its pitch. Returns -1 if the pitch is not in the array.
	public int getPitchKeyCode(Note note)
	{
		int pitchKeyCodeIndex = note.numericPitch - 31;
		if (pitchKeyCodeIndex < 0 || pitchKeyCodeIndex > NOTE_KEY_CODES.length - 1)
		{
			return -1;
		}
		return NOTE_KEY_CODES[note.numericPitch - 31];
	}
	
	// Gets the distance in half steps between two notes in a single octave (assuming the octave starts on C, used for calculating numeric pitch)
    public static int getSingleOctaveDistance(char pitch1, char accidental1, char pitch2, char accidental2)
    {
    	List<Character> pitchList = Arrays.asList(new Character[] {'C', ' ', 'D', ' ', 'E', 'F', ' ', 'G', ' ', 'A', ' ', 'B'});
    	return Math.abs(Math.abs(pitchList.indexOf(pitch2) + getPitchShift(accidental2)) - Math.abs(pitchList.indexOf(pitch1) + getPitchShift(accidental1)));
    }
    
    // Gets the amount to shift the numeric pitch based on the note's accidental
    public static int getPitchShift(char accidental)
    {
    	int pitchShift = 0;
        pitchShift = accidental == '#' ? 1 : pitchShift;
    	pitchShift = accidental == 'b' ? -1 : pitchShift;
    	return pitchShift;
    }
    
    // Gets the 0-indexed pitch of a note (range: 0-87). Returns -1 if the note is out of range
    public static int getNumericPitch(char pitch, char accidental, int octave)
    {
    	int numericPitch = getSingleOctaveDistance('C', 'n', pitch, 'n') + (octave > 0 ? 3 + (octave - 1) * 12 : -9) + getPitchShift(accidental);
    	numericPitch = numericPitch < 0 || numericPitch > 87 ? -1 : numericPitch;
    	return numericPitch;
    }
	
	public void setColor(Color color)
	{
		window.setColor(color);
	}
	
	public void fillRect(int x, int y, int width, int height)
	{
		window.fillRect(x, y, width, height);
	}
	
	public void drawSolidColorImage(Image image, Color color, int x, int y)
	{
		window.drawSolidColorImage(image, color, x, y);
	}
	
	private void completeAttempt()
	{
		attemptCount++;
		pitchAccuracy = ((double) correctNotes / notesPlayed) * 100;
		totalPitchAccuracy += pitchAccuracy;
		averagePitchAccuracy = totalPitchAccuracy / attemptCount;
		correctNotes = 0;
		
		rhythmAccuracy = ((double) correctRhythms / notesPlayed) * 100;
		totalRhythmAccuracy += rhythmAccuracy;
		averageRhythmAccuracy = totalRhythmAccuracy / attemptCount;
		correctRhythms = 0;
		
		totalAccuracy = (pitchAccuracy + rhythmAccuracy) / 2;
		totalTotalAccuracy += totalAccuracy;
		averageTotalAccuracy = totalTotalAccuracy / attemptCount;
		window.showAccuracy = true;
		attemptCompleted = true;
	}

	private void resetCurrentAttempt()
	{
		notesPlayed = 0;
		correctNotes = 0;
		correctRhythms = 0;
		currStaffObjIndex = 0;
		staff.resetNoteColors();
		window.repaintGame();
	}
	
	private void startNewAttempt()
	{
		attemptCompleted = false;
		window.showAccuracy = false;
		staff.setMeasures(randomNoteGenerator.getRandomMeasures(3));
		while (staff.getSystemCount() > 2)
		{
			staff.setMeasures(randomNoteGenerator.getRandomMeasures(3));
		}
		resetCurrentAttempt();
	}

	private void resetAttemptCount()
	{
		attemptCount = 0;
		averagePitchAccuracy = 0;
		totalPitchAccuracy = 0;
		pitchAccuracy = 0;
		averageRhythmAccuracy = 0;
		totalRhythmAccuracy = 0;
		rhythmAccuracy = 0;
		averageTotalAccuracy = 0;
		totalTotalAccuracy = 0;
		totalAccuracy = 0;
		window.repaintGame();
	}
	
	private void populateAbsoluteDurations(long timeInterval, Duration duration)
	{
		Duration[] durations = {Duration.WHOLE, Duration.HALF, Duration.QUARTER, Duration.EIGHTH, Duration.SIXTEENTH};
		for (Duration dur : durations)
		{
			double scale = (double) dur.getTimeUnits() / duration.getTimeUnits();
			absoluteDurations.put(dur, (long) (scale * timeInterval));
		}
	}
}
