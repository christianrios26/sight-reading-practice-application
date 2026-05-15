package com.chrisapps2003.sightreadingpracticeapplication;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import com.christools2003.gamedev.GameFrame;

public class PracticeWindow extends GameFrame
{
	protected static final int WINDOW_WIDTH = 1344;
	protected static final int WINDOW_HEIGHT = 756;
	protected static final Color BACKGROUND_COLOR = new Color(0x2A2A2A);
	protected static final Color HIGHLIGHT = new Color(0xFFCC02);
	protected static final HashMap<String, Image> IMAGES = new HashMap<>();
	protected Font menuFont;
	protected PracticeLogic logic;
	protected MidiReceiver receiver;
	protected WindowKeyListener windowKeyListener;
	protected boolean showAccuracy;
	protected int screenNumber;
	private List<Runnable> screenDrawings; // Stores runnable objects pointing to methods that draw each screen
	

	public PracticeWindow(String title, int gameWidth, int gameHeight)
	{
		super(title, gameWidth, gameHeight);

		// Initialize the font and icon
		try
		{
			ClassLoader loader = getClass().getClassLoader(); // used for loading resources that will persist after an executable is made
			
			// Load the menu font
			menuFont = Font.createFont(Font.TRUETYPE_FONT, loader.getResourceAsStream("resources/califr.ttf")).deriveFont(Font.PLAIN, 0); // font size doesn't matter here
			
			// Image names (simply add the name of an image to this array to load it)
			String[] imageNames =
			{
				"clef", "time_signature", "whole_note_head", "half_note_head", "solid_note_head", "start_screen_art", "instruction_image",
				"quarter_rest", "eighth_flag", "eighth_flag_inverted", "eighth_rest", "sixteenth_flag", "sixteenth_flag_inverted", "sixteenth_rest",
				"sharp", "flat", "natural", "digital_piano", "keyboard_layout"
			};

			// Load images into image hashmap (a more scalable solution, so that additional fields do not have to be created for new images)
			for (String name : imageNames)
			{
				IMAGES.put(name, ImageIO.read(loader.getResourceAsStream("resources/" + name + ".png")));
			}
			
			// Set the icon
			setIcon(ImageIO.read(loader.getResourceAsStream("resources/icon.png")));
		}
		catch (FontFormatException | IOException e)
		{
			e.printStackTrace();
		}
		
		// Initialize fields
		logic = new PracticeLogic(this);
		receiver = new MidiReceiver(logic);
		windowKeyListener = new WindowKeyListener(logic);
		showAccuracy = false;
		screenNumber = 0;
		screenDrawings = Arrays.asList(new Runnable[] {() -> drawStartScreen(), () -> drawInstructionScreen(), () -> drawPracticeScreen()});
		
		// Prepare all connected MIDI devices for input detection (set the receiver for each one)
		MidiDevice.Info[] midiDevices = MidiSystem.getMidiDeviceInfo();
		for (MidiDevice.Info deviceInfo : midiDevices)
		{
			try
			{
				MidiDevice device = MidiSystem.getMidiDevice(deviceInfo);
				if (device.getMaxTransmitters() != 0)
				{
					device.open();
					device.getTransmitter().setReceiver(receiver);
				}
			}
			catch (MidiUnavailableException e)
			{
				e.printStackTrace();
			}
		}

		// Add listener to the panel and repaint the game
		GAME_PANEL.addKeyListener(windowKeyListener);
		repaintGame();
	}

	protected void drawStartScreen()
	{
		setColor(Color.WHITE);
		drawString("PractiKey: Piano Sight Reading Practice", 271, 80, 50, 1f, menuFont);
		Image startScreenArt = getImage("start_screen_art");
		int imageHeight = (int) ((double) WINDOW_WIDTH / startScreenArt.getWidth(null) * startScreenArt.getHeight(null));
		drawImage(startScreenArt, 0, WINDOW_HEIGHT / 2 - imageHeight / 2, WINDOW_WIDTH, imageHeight);
		setColor(HIGHLIGHT);
		drawString("(ENTER):", 546, 736, 26, 1f, menuFont);
		setColor(Color.WHITE);
		drawString("Go to next screen", 661, 736, 26, 1f, menuFont);
	}

	protected void drawInstructionScreen()
	{
		setColor(Color.WHITE);
		drawString("Instructions", 542, 80, 50, 1f, menuFont);
		drawImage(getImage("digital_piano"), 54, 126);
		drawString("OR", 622, 268, 72, 1f, menuFont);
		drawImage(getImage("keyboard_layout"), 776, 126);
		drawString("Notes are intended to be entered using a digital piano instrument connected to the computer", 138, 429, 26, 1f, menuFont);
		drawString("through a USB port. Alternatively, notes can be entered using the displayed keyboard input scheme.", 138, 459, 26, 1f, menuFont);
		drawString("Note inputs are accepted in the range E3-E6.", 138, 489, 26, 1f, menuFont);
		drawString("The next screen is the practice screen. When ready, press ENTER to advance to the practice screen,", 138, 579, 26, 1f, menuFont);
		drawString("and start an attempt by inputting notes according to the generated sheet music. ", 138, 609, 26, 1f, menuFont);
		setColor(HIGHLIGHT);
		drawString("(ENTER):", 266, 736, 26, 1f, menuFont);
		drawString("(BACKSPACE):", 646, 736, 26, 1f, menuFont);
		setColor(Color.WHITE);
		drawString("Go to next screen", 381, 736, 26, 1f, menuFont);
		drawString("Go to previous screen", 820, 736, 26, 1f, menuFont);
	}
	
	protected void drawPracticeScreen()
	{
		setColor(Color.WHITE);
		drawString("Practice", 568, 80, 50, 1f, menuFont);
		logic.staff.draw();
		setColor(HIGHLIGHT);
		drawString("(ENTER):", 60, 736, 26, 1f, menuFont);
		drawString("(CTRL + R):", 430, 736, 26, 1f, menuFont);
		drawString("(BACKSPACE):", 870, 736, 26, 1f, menuFont);
		setColor(Color.WHITE);
		drawString("Start new attempt", 175, 736, 26, 1f, menuFont);
		drawString("Reset attempt counter", 567, 736, 26, 1f, menuFont);
		drawString("Go to previous screen", 1044, 736, 26, 1f, menuFont);
		drawString("Attempt Counter: " + logic.attemptCount, 546, 140, 26, 1f, menuFont);
		if (showAccuracy)
		{
			drawString("Accuracy Scores:", 560, 640, 26, 1f, menuFont);
			drawString(String.format("Pitch: %.2f%% (%.2f%% average)", logic.pitchAccuracy, logic.averagePitchAccuracy), 70, 690, 26, 1f, menuFont);
			drawString(String.format("Rhythm: %.2f%% (%.2f%% average)", logic.rhythmAccuracy, logic.averageRhythmAccuracy), 470, 690, 26, 1f, menuFont);
			drawString(String.format("Total: %.2f%% (%.2f%% average)", logic.totalAccuracy, logic.averageTotalAccuracy), 900, 690, 26, 1f, menuFont);
		}
	}

	@Override
	protected void drawGame()
	{
		// Draw the background
		setColor(BACKGROUND_COLOR);
		fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		// Draw specific screen contents
		if (screenDrawings != null)
		{
			screenDrawings.get(screenNumber).run();	
		}
	}
	
	public static void main(String[] args)
	{
		@SuppressWarnings("unused")
		PracticeWindow window = new PracticeWindow("PractiKey: Piano Sight Reading Practice", WINDOW_WIDTH, WINDOW_HEIGHT);
	}
	
	public static Image getImage(String name)
	{
		return IMAGES.get(name);
	}
	
	public Staff getStaff()
	{
		return logic.staff;
	}
	
	public void setScreenNumber(int screenNumber)
	{
		this.screenNumber = screenNumber;
	}
	
	public int getScreenNumber()
	{
		return screenNumber;
	}
	
	public PracticeLogic getLogic()
	{
		return logic;
	}
	
	public MidiReceiver getReceiver()
	{
		return receiver;
	}
}