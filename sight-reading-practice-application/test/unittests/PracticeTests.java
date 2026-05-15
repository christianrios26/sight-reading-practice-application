package unittests;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import org.junit.jupiter.api.Test;

import com.chrisapps2003.sightreadingpracticeapplication.Duration;
import com.chrisapps2003.sightreadingpracticeapplication.Measure;
import com.chrisapps2003.sightreadingpracticeapplication.Note;
import com.chrisapps2003.sightreadingpracticeapplication.PracticeLogic;
import com.chrisapps2003.sightreadingpracticeapplication.PracticeWindow;
import com.chrisapps2003.sightreadingpracticeapplication.Staff;
import com.chrisapps2003.sightreadingpracticeapplication.StaffObject;

public class PracticeTests
{
	private static PracticeWindow window;
	
	public PracticeTests()
	{
		// Test window created to load images
		window = window == null ? new PracticeWindow("Test", 1344, 756) : window;
	}
	
	@Test
    void testAccuraciesCalculatedCorrectly()
    {        
        window.setScreenNumber(2); //screenNumber must be at 2 for calculations
        
        Measure measure = new Measure(4, Duration.QUARTER, window.getLogic());
        measure.addNote('C', 'n', 4, Duration.QUARTER);
        measure.addNote('C', 'n', 6, Duration.QUARTER);
        ArrayList<Measure> measures = new ArrayList<>();
        measures.add(measure);
        window.getStaff().setMeasures(measures);

        int correctKey = window.getLogic().getPitchKeyCode(window.getStaff().getNote(0));
        
        window.getLogic().keyPressed(correctKey, false); //checks for correct input
		
        window.getLogic().keyPressed(KeyEvent.VK_Z, false); //checks for wrong input
        assertEquals(50, window.getLogic().getPitchAccuracy()); // should have 50% accuracy 
        assertEquals(100, window.getLogic().getRhythmAccuracy()); // should have 100% accuracy
    }

    @Test
    void testCtrlRResetsAttempts()
    {        	
        window.setScreenNumber(2); //screenNumber must be at 2 for calculations
        window.getLogic().setAttemptCount(7); //number of attempts the user has 

        window.getLogic().keyPressed(KeyEvent.VK_R, true); //checks for the correct command to reset the counter

        assertEquals(0, window.getLogic().getAttemptCount()); //counter will be 0 if correct command was made
    }
    
    @Test
	void testGetTimeUnits()
	{
		assertEquals(16, Duration.WHOLE.getTimeUnits());
		assertEquals(8, Duration.HALF.getTimeUnits());
		assertEquals(4, Duration.QUARTER.getTimeUnits());
		assertEquals(2, Duration.EIGHTH.getTimeUnits());
		assertEquals(1, Duration.SIXTEENTH.getTimeUnits());
	}
    
    @Test
    void testGoToNextScreen()
    {
    	window.setScreenNumber(0);
    	window.getLogic().keyPressed(KeyEvent.VK_ENTER, false);
    	assertEquals(1, window.getScreenNumber());	
    }
    
    @Test
    void testGoToPreviousScreen()
    {
    	window.setScreenNumber(1);
    	window.getLogic().keyPressed(KeyEvent.VK_BACK_SPACE, false);
    	assertEquals(0, window.getScreenNumber());	
    }

    // Test Numeric Pitch -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @Test
    void testNumericPitch()
    {
    	assertEquals(39, PracticeLogic.getNumericPitch('C', 'n', 4)); // C4 = 39
    }
    
	@Test
	void testNumericPitchSharp()
	{
		assertEquals(40, PracticeLogic.getNumericPitch('C', '#', 4));
	}

	@Test
	void testNumericPitchFlat()
	{
		assertEquals(38, PracticeLogic.getNumericPitch('C', 'b', 4));
	}
	
	@Test
	void testNumericPitchMinimum() // boundary value
	{
		assertEquals(0, PracticeLogic.getNumericPitch('A', 'n', 0));
	}
	
	@Test
	void testNumericPitchMaximum() // boundary value
	{
		assertEquals(87, PracticeLogic.getNumericPitch('C', 'n', 8));
	}
	
	@Test
	void testNumericPitchMaximumMinusOne() // boundary value
	{
		assertEquals(86, PracticeLogic.getNumericPitch('B', 'n', 7));
	}
	
	@Test
	void testNumericPitchMaximumPlusOne() // boundary value
	{
		assertEquals(-1, PracticeLogic.getNumericPitch('C', '#', 8));
	}
	
	@Test
	void testNumericPitchMinimumPlusOne() // boundary value
	{
		assertEquals(1, PracticeLogic.getNumericPitch('B', 'b', 0));
	}
	
	@Test
	void testNumericPitchMinimumMinusOne() // boundary value
	{
		assertEquals(-1, PracticeLogic.getNumericPitch('A', 'b', 0));
	}

	// Test Measure Methods -----------------------------------------------------------------------------------------------------------------------------------------------------------------------
	@Test
	void testAddNote()
	{
		Measure measure = new Measure(4, Duration.QUARTER, null);
		boolean isAdded = measure.addNote('C', 'n', 4, Duration.QUARTER);
		assertTrue(isAdded);
	}
	
	@Test
	void testAddNoteFailure()
	{
		Measure measure = new Measure(4, Duration.QUARTER, null);
		measure.addNote('C', 'n', 4, Duration.WHOLE);
		measure.addNote('C', 'n', 4, Duration.WHOLE);
		measure.addNote('C', 'n', 4, Duration.WHOLE);
		boolean isAdded = measure.addNote('C', 'n', 4, Duration.QUARTER);
		assertFalse(isAdded);
	}

	@Test
	void testAddRest()
	{
		Measure measure = new Measure(4, Duration.QUARTER, null);
		boolean isAdded = measure.addRest(Duration.QUARTER);
		assertTrue(isAdded);
	}
	
	@Test
	void testAddRestFailure()
	{
		Measure measure = new Measure(4, Duration.QUARTER, null);
		measure.addRest(Duration.WHOLE);
		measure.addRest(Duration.WHOLE);
		measure.addRest(Duration.WHOLE);
		boolean isAdded = measure.addRest(Duration.QUARTER);
		assertFalse(isAdded);
	}

	@Test
	void testGetStaffObject()
	{
		Measure measure = new Measure(4, Duration.QUARTER, null);

		measure.addNote('C', 'n', 4, Duration.QUARTER);
		measure.addRest(Duration.QUARTER);

		StaffObject firstObj = measure.getStaffObject(0);
		StaffObject secondObj = measure.getStaffObject(1);
		StaffObject thirdObj = measure.getStaffObject(2);

		assertNotNull(firstObj);
		assertNotNull(secondObj);
		assertNull(thirdObj);
	}

	@Test
	void testCountFunctions()
	{
		Measure measure = new Measure(4, Duration.QUARTER, null);

		measure.addNote('C', 'n', 4, Duration.QUARTER);
		measure.addRest(Duration.QUARTER);
		measure.addNote('D', 'n', 4, Duration.QUARTER);

		assertEquals(3, measure.getStaffObjectCount());
		assertEquals(2, measure.getNoteCount());
	}

	@Test
	void testGetTotalStaffObjectWidth()
	{
		Measure measure = new Measure(4, Duration.QUARTER, null);

		measure.addNote('C', 'n', 4, Duration.QUARTER);
		measure.addRest(Duration.QUARTER);

		int noteWidth = 23; 
		int restWidth = 16; 


		assertEquals((noteWidth + restWidth), measure.getTotalStaffObjectWidth());
	}

	// White-box tests
	@Test
	void testClearMeasure()
	{
		Measure measure = new Measure(4, Duration.QUARTER, null);
		measure.addNote('C', 'n', 4, Duration.QUARTER);
		measure.addNote('C', 'n', 4, Duration.QUARTER);
		measure.clear();
		assertEquals(0, measure.getNoteCount());
	}
	
	@Test
	void testGetMeasureTimeUnits()
	{
		Measure measure = new Measure(4, Duration.QUARTER, null);
		assertEquals(16, measure.getTimeUnits());
	}
	
	@Test
	void testGetStartTime()
	{
		Measure measure = new Measure(4, Duration.QUARTER, null);
		measure.addNote('C', 'n', 4, Duration.QUARTER);
		measure.addNote('C', 'n', 4, Duration.QUARTER);
		assertEquals(4, measure.getStartTime(measure.getStaffObject(1)));
	}
	
	@Test
	void testMeasureIsFull()
	{
		Measure measure = new Measure(4, Duration.QUARTER, null);
		measure.addNote('C', 'n', 4, Duration.QUARTER);
		measure.addNote('C', 'n', 4, Duration.QUARTER);
		measure.addNote('C', 'n', 4, Duration.QUARTER);
		measure.addNote('C', 'n', 4, Duration.QUARTER);
		assertTrue(measure.isFull());
	}
	// Test Staff Methods -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	@Test
	void testGetMeasureCount()
	{
		ArrayList<Measure> measures = new ArrayList<>();
		measures.add(new Measure(4, Duration.QUARTER, null));
		measures.add(new Measure(4, Duration.QUARTER, null));

		Staff staff = new Staff(0, 0, measures, null);

		assertEquals(2, staff.getMeasureCount());
	}

	@Test 
	void testGetNote()
	{
		Measure measure1 = new Measure(4, Duration.QUARTER, null);

		measure1.addNote('C', 'n', 4, Duration.QUARTER);
		measure1.addRest(Duration.QUARTER);

		ArrayList<Measure> measures = new ArrayList<>();
		measures.add(measure1);

		Staff staff = new Staff(0, 0, measures, null);

		Note firstNote = staff.getNote(0);

		assertNotNull(firstNote);
	}
	
	// White-box test
	@Test
	void testTwoSystems()
	{
		Measure measure1 = new Measure(4, Duration.QUARTER, window.getLogic());
		Measure measure2 = new Measure(4, Duration.QUARTER, window.getLogic());
		Measure measure3 = new Measure(4, Duration.QUARTER, window.getLogic());
		
		for (int i = 0; i < 48; i++)
		{
			if (i / 16 == 0)
			{
				measure1.addNote('C', 'n', 4, Duration.SIXTEENTH);
			}
			if (i / 16 == 1)
			{
				measure2.addNote('C', 'n', 4, Duration.SIXTEENTH);
			}
			if (i / 16 == 2)
			{
				measure3.addNote('C', 'n', 4, Duration.SIXTEENTH);
			}
		}
		
		ArrayList<Measure> measures = new ArrayList<>();
		measures.add(measure1);
		measures.add(measure2);
		measures.add(measure3);
		
		window.getStaff().setMeasures(measures); // creates multiple systems to run more code
	}
	
	// Test Logic Methods -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	@Test
	void testPitchShiftSharp()
	{
		assertEquals(1, PracticeLogic.getPitchShift('#'));
	}
	
	@Test
	void testPitchShiftFlat()
	{
		assertEquals(-1, PracticeLogic.getPitchShift('b'));
	}
	
	@Test
	void testSingleOctaveDistance()
	{
		assertEquals(7, PracticeLogic.getSingleOctaveDistance('C', 'n', 'G', 'n'));
	}
	
	@Test
	void testSingleOctaveDistanceMininmum() // boundary value
	{
		assertEquals(0, PracticeLogic.getSingleOctaveDistance('C', 'n', 'C', 'n'));
	}
	
	@Test
	void testSingleOctaveDistanceMaximum() // boundary value
	{
		assertEquals(11, PracticeLogic.getSingleOctaveDistance('C', 'n', 'B', 'n'));
	}
	
	@Test
	void testSingleOctaveDistanceHalfStepAbove() // boundary value
	{
		assertEquals(1, PracticeLogic.getSingleOctaveDistance('C', 'n', 'C', '#'));
	}
	
	@Test
	void testSingleOctaveDistanceHalfStepBelow() // boundary value
	{
		assertEquals(1, PracticeLogic.getSingleOctaveDistance('C', 'n', 'C', 'b'));
	}
	
	@Test
	void testGetPitchKeyCode()
	{
		assertEquals(KeyEvent.VK_C, window.getLogic().getPitchKeyCode(new Note('G', 'n', 3, Duration.QUARTER, null)));
	}
	
	@Test
	void testGetPitchKeyCodeBottomBoundary() // boundary value
	{
		assertEquals(KeyEvent.VK_Z, window.getLogic().getPitchKeyCode(new Note('E', 'n', 3, Duration.QUARTER, null)));
	}
	
	@Test
	void testGetPitchKeyCodeBottomBoundaryPlusOne()
	{
		assertEquals(KeyEvent.VK_X, window.getLogic().getPitchKeyCode(new Note('E', '#', 3, Duration.QUARTER, null)));
	}
	
	@Test
	void testGetPitchKeyCodeBottomBoundaryMinusOne()
	{
		assertEquals(-1, window.getLogic().getPitchKeyCode(new Note('E', 'b', 3, Duration.QUARTER, null)));
	}
	
	@Test
	void testGetPitchKeyCodeTopBoundary() // boundary value
	{
		assertEquals(KeyEvent.VK_CLOSE_BRACKET, window.getLogic().getPitchKeyCode(new Note('E', 'n', 6, Duration.QUARTER, null)));
	}
	
	@Test
	void testGetPitchKeyCodeTopBoundaryMinusOne()
	{
		assertEquals(KeyEvent.VK_EQUALS, window.getLogic().getPitchKeyCode(new Note('E', 'b', 6, Duration.QUARTER, null)));
	}
	
	@Test
	void testGetPitchKeyCodeTopBoundaryPlusOne()
	{
		assertEquals(-1, window.getLogic().getPitchKeyCode(new Note('E', '#', 6, Duration.QUARTER, null)));
	}
	
	// White-box test for running Midi Receiver code
	@Test
	void testMidiReceiver()
	{
		ShortMessage msg1 = new ShortMessage();
		ShortMessage msg2 = new ShortMessage();
		try
		{
			msg1.setMessage(ShortMessage.NOTE_ON, 0, 60, 64);
			msg1.setMessage(ShortMessage.NOTE_OFF, 0, 60, 64);
		}
		catch (InvalidMidiDataException e)
		{
			e.printStackTrace();
		}
		
		window.getReceiver().send(msg1, -1);
		window.getReceiver().send(msg2, -1);
	}
}
