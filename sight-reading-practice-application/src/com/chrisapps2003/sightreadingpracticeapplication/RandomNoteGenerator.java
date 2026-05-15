package com.chrisapps2003.sightreadingpracticeapplication;

import java.util.ArrayList;
import java.util.Random;

public class RandomNoteGenerator
{
	protected static final int MIN_NUMERIC_PITCH = 31;
	protected static final int MAX_NUMERIC_PITCH = 67;
	protected static final double LEAP_PROBABILITY = 0.2;
	protected static final double UNISON_PROBABILITY = 0.1;
	protected static final double REST_PROBABILITY = 0.1;
	protected PracticeLogic logic;
	private Random rand;
	private RhythmPattern[] patterns;
	private Note[][] keys;
	
	public RandomNoteGenerator(PracticeLogic logic)
	{
		this.logic = logic;
		rand = new Random();
		
		patterns = new RhythmPattern[]
		{
			new RhythmPattern(new Duration[] { Duration.QUARTER }, 0.25),
			new RhythmPattern(new Duration[] { Duration.EIGHTH, Duration.EIGHTH }, 0.25),
			new RhythmPattern(new Duration[] { Duration.SIXTEENTH, Duration.SIXTEENTH, Duration.EIGHTH }, 0.12),
			new RhythmPattern(new Duration[] { Duration.EIGHTH, Duration.SIXTEENTH, Duration.SIXTEENTH}, 0.12),
			new RhythmPattern(new Duration[] { Duration.SIXTEENTH, Duration.SIXTEENTH, Duration.SIXTEENTH, Duration.SIXTEENTH }, 0.12),
			new RhythmPattern(new Duration[] { Duration.HALF}, 0.09),
			new RhythmPattern(new Duration[] { Duration.WHOLE}, 0.05)
		};
		
		keys = new Note[][]
		{
			new Note[] { new Note('C', 'n', 4, null, logic), new Note('D', 'n', 4, null, logic), new Note('E', 'n', 4, null, logic), new Note('F', 'n', 4, null, logic), new Note('G', 'n', 4, null, logic), new Note('A', 'n', 4, null, logic), new Note('B', 'n', 4, null, logic) },
			new Note[] { new Note('G', 'n', 4, null, logic), new Note('A', 'n', 4, null, logic), new Note('B', 'n', 4, null, logic), new Note('C', 'n', 5, null, logic), new Note('D', 'n', 5, null, logic), new Note('E', 'n', 5, null, logic), new Note('F', '#', 5, null, logic) },
			new Note[] { new Note('D', 'n', 4, null, logic), new Note('E', 'n', 4, null, logic), new Note('F', '#', 4, null, logic), new Note('G', 'n', 4, null, logic), new Note('A', 'n', 4, null, logic), new Note('B', 'n', 4, null, logic), new Note('C', '#', 5, null, logic) },
			new Note[] { new Note('A', 'n', 4, null, logic), new Note('B', 'n', 4, null, logic), new Note('C', '#', 5, null, logic), new Note('D', 'n', 5, null, logic), new Note('E', 'n', 5, null, logic), new Note('F', '#', 5, null, logic), new Note('G', '#', 5, null, logic) },
			new Note[] { new Note('E', 'n', 4, null, logic), new Note('F', '#', 4, null, logic), new Note('G', '#', 4, null, logic), new Note('A', 'n', 4, null, logic), new Note('B', 'n', 4, null, logic), new Note('C', '#', 5, null, logic), new Note('D', '#', 5, null, logic) },
			new Note[] { new Note('B', 'n', 4, null, logic), new Note('C', '#', 5, null, logic), new Note('D', '#', 5, null, logic), new Note('E', 'n', 5, null, logic), new Note('F', '#', 5, null, logic), new Note('G', '#', 5, null, logic), new Note('A', '#', 5, null, logic) },
			new Note[] { new Note('F', '#', 4, null, logic), new Note('G', '#', 4, null, logic), new Note('A', '#', 4, null, logic), new Note('B', 'n', 4, null, logic), new Note('C', '#', 5, null, logic), new Note('D', '#', 5, null, logic), new Note('E', '#', 5, null, logic) },
			new Note[] { new Note('G', 'b', 4, null, logic), new Note('A', 'b', 4, null, logic), new Note('B', 'b', 4, null, logic), new Note('C', 'b', 5, null, logic), new Note('D', 'b', 5, null, logic), new Note('E', 'b', 5, null, logic), new Note('F', 'n', 5, null, logic) },
			new Note[] { new Note('D', 'b', 4, null, logic), new Note('E', 'b', 4, null, logic), new Note('F', 'n', 4, null, logic), new Note('G', 'b', 4, null, logic), new Note('A', 'b', 4, null, logic), new Note('B', 'b', 4, null, logic), new Note('C', 'n', 5, null, logic) },
			new Note[] { new Note('A', 'b', 4, null, logic), new Note('B', 'b', 4, null, logic), new Note('C', 'n', 5, null, logic), new Note('D', 'b', 5, null, logic), new Note('E', 'b', 5, null, logic), new Note('F', 'n', 5, null, logic), new Note('G', 'n', 5, null, logic) },
			new Note[] { new Note('E', 'b', 4, null, logic), new Note('F', 'n', 4, null, logic), new Note('G', 'n', 4, null, logic), new Note('A', 'b', 4, null, logic), new Note('B', 'b', 4, null, logic), new Note('C', 'n', 5, null, logic), new Note('D', 'n', 5, null, logic) },
			new Note[] { new Note('B', 'b', 4, null, logic), new Note('C', 'n', 5, null, logic), new Note('D', 'n', 5, null, logic), new Note('E', 'b', 5, null, logic), new Note('F', 'n', 5, null, logic), new Note('G', 'n', 5, null, logic), new Note('A', 'n', 5, null, logic) },
			new Note[] { new Note('F', 'n', 4, null, logic), new Note('G', 'n', 4, null, logic), new Note('A', 'n', 4, null, logic), new Note('B', 'b', 4, null, logic), new Note('C', 'n', 5, null, logic), new Note('D', 'n', 5, null, logic), new Note('E', 'n', 5, null, logic) }
		};
	}
	
	
	public ArrayList<Measure> getRandomMeasures(int measureCount)
	{
		ArrayList<Measure> measures = new ArrayList<>();
		ArrayList<ArrayList<StaffObject>> beats = getRandomBeats(12);
		
		int beatIndex = 0;
		
		for (int i = 0; i < measureCount; i++) // for every measure
		{
			Measure measure = new Measure(4, Duration.QUARTER, logic);
			
			for (int j = 0; j < measure.beatCount; j++) // up to the number of beats this measure should have
			{
				ArrayList<StaffObject> beat = beats.get(beatIndex);
				for (StaffObject obj : beat) // for every staff object in the next random beat
				{
					if (obj instanceof Note)
					{
						Note note = (Note) obj;
						measure.addNote(note.pitch, note.accidental, note.octave, note.duration);
					}
					else // it must be a rest
					{
						measure.addRest(obj.duration);
					}
				}
				beatIndex++;
			}
			
			measures.add(measure);
		}
		
		return measures;
	}
	
	private ArrayList<ArrayList<StaffObject>> getRandomBeats(int beatCount)
	{
		// beats of a measure indexed at 0 (i.e. beats of first measure are 0, 1, 2, and 3)
		ArrayList<ArrayList<StaffObject>> beats = new ArrayList<>();
		Note[] keySignature = keys[rand.nextInt(keys.length)]; // choose a random key
		
		Note prevNote = null;
		StaffObject currStaffObject = null;
		for (int beatIndex = 0; beatIndex < beatCount; beatIndex++)
		{
			ArrayList<StaffObject> beat = new ArrayList<>();
			
			if (beatIndex % 4 == 0) // is the first beat of a measure
			{
				setPatternProbabilities(new double[] {0.25, 0.25, 0.12, 0.12, 0.12, 0.09, 0.05});
			}
			else if (beatIndex % 2 == 1) // is beat 1 or 3 of a measure
			{
				setPatternProbabilities(new double[] {0.2864, 0.2864, 0.1424, 0.1424, 0.1424, 0, 0});
			}
			else if (beatIndex % 4 == 2) // is beat 2 of a measure
			{
				setPatternProbabilities(new double[] {0.2632, 0.2632, 0.1263, 0.1263, 0.1263, 0.0947, 0});
			}
			
			Duration[] rhythms = getRandomPattern().rhythms;
			for (Duration duration : rhythms)
			{
				currStaffObject = getNextRandomStaffObject(prevNote, keySignature, duration);
				beat.add(currStaffObject);
				prevNote = currStaffObject instanceof Note ? (Note) currStaffObject : prevNote;
			}
			
			beats.add(beat);
		}
		
		return beats;
	}
	
	private StaffObject getNextRandomStaffObject(Note prevNote, Note[] keySignature, Duration duration)
	{
		// Check for returning a rest first -------------------------------------------------------------------------------------------------------------------------------------------------------
		if (rand.nextDouble() <= REST_PROBABILITY)
		{
			return new Rest(duration, logic);
		}
		
		// Generate the first note if prevNote is null --------------------------------------------------------------------------------------------------------------------------------------------
		if (prevNote == null)
		{
			Note randomNoteTemplate = keySignature[rand.nextInt(keySignature.length)]; // choose a random first note
			return new Note(randomNoteTemplate.pitch, randomNoteTemplate.accidental, randomNoteTemplate.octave, duration, logic); // return a copy with the given note duration
		}
		
		// If previous note is not null, then generate the next note based on it ------------------------------------------------------------------------------------------------------------------
		// Search for note in key signature array with the same pitch as the previous note
		int noteIndex = 0;
		for (int i = 0; i < keySignature.length; i++)
		{
			if (prevNote.pitch == keySignature[i].pitch)
			{
				noteIndex = i;
				break;
			}
		}
		
		Note randomNote;
		int randomNoteNumericPitch;
		do 
		{
			int randomShift;
			if (rand.nextDouble() > LEAP_PROBABILITY)
			{
				randomShift = rand.nextDouble() > UNISON_PROBABILITY ? 1 : 0; // do a step or unison
			}
			else
			{
				randomShift = rand.nextInt(7) + 1; // do a leap
			}
			
			randomShift *= 1 - 2 * rand.nextInt(2); // random shift is multiplied by either 1 or -1 with a 50% chance
			int randomNoteIndex = noteIndex + randomShift;
			int randomNoteOctave = prevNote.octave;
			
			// Modify random note index if necessary
			randomNoteIndex = randomNoteIndex > 6 ? randomNoteIndex - 7 : randomNoteIndex;
			randomNoteIndex = randomNoteIndex < 0 ? randomNoteIndex + 7 : randomNoteIndex;
			
			// Modify the octave if necessary
			if (randomShift > 0)
			{
				for (int i = 1; i <= randomShift; i++)
				{
					if (keySignature[(noteIndex + i) % 7].pitch == 'C')
					{
						randomNoteOctave++;
						break;
					}
				}
			}
			else if (randomShift < 0)
			{
				for (int i = 1; i <= -randomShift; i++)
				{
					if (keySignature[noteIndex - i < 0 ? noteIndex - i + 7 : noteIndex - i].pitch == 'B')
					{
						randomNoteOctave--;
						break;
					}
				}
			}
			
			randomNote = new Note(keySignature[randomNoteIndex].pitch, keySignature[randomNoteIndex].accidental, randomNoteOctave, duration, logic);
			randomNoteNumericPitch = PracticeLogic.getNumericPitch(randomNote.pitch, randomNote.accidental, randomNote.octave);
		} while (randomNoteNumericPitch < MIN_NUMERIC_PITCH || randomNoteNumericPitch > MAX_NUMERIC_PITCH);
		
		return randomNote;
	}
	
	private void setPatternProbabilities(double[] probabilities)
	{
		if (probabilities.length != patterns.length)
		{
			return;
		}
		for (int i = 0; i < probabilities.length; i++)
		{
			patterns[i].probability = probabilities[i];
		}
	}
	
	private RhythmPattern getRandomPattern()
	{
		double randomDouble = rand.nextDouble();
		double low = 0;
		double high = patterns[0].probability;
		for (int i = 0; i < patterns.length; i++)
		{
			if (randomDouble > low && randomDouble <= high)
			{
				return patterns[i];
			}
			low += patterns[i].probability;
			high += patterns[i + 1].probability; // in theory, should never cause an out of bounds exception
		}
		return null; // in theory, should never be returned
	}
	
	private class RhythmPattern
	{
		private Duration[] rhythms;
		private double probability;
		
		private RhythmPattern(Duration[] rhythms, double probability)
		{
			this.rhythms = rhythms;
			this.probability = probability;
		}
	}
}