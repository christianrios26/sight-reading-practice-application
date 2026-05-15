package com.chrisapps2003.sightreadingpracticeapplication;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class MidiReceiver implements Receiver
{
	protected PracticeLogic logic;

	protected MidiReceiver(PracticeLogic logic)
	{
		this.logic = logic;
	}

	@Override
	public void send(MidiMessage message, long timeStamp) 
	{
		if (message instanceof ShortMessage)
		{
			ShortMessage sm = (ShortMessage) message;
			int command = sm.getCommand(), data1 = sm.getData1(); //data2 = sm.getData2();
			if (command == ShortMessage.NOTE_ON || command == ShortMessage.NOTE_OFF)
			{
				int numericPitch = data1 - 21;
				if (numericPitch >= 31 && numericPitch <= 67)
				{
					if (command == ShortMessage.NOTE_ON)
					{
						logic.keyPressed(logic.NOTE_KEY_CODES[numericPitch - 31], false);
					}
					else
					{
						logic.keyReleased(logic.NOTE_KEY_CODES[numericPitch - 31]);
					}
				}
			}
		}
	}

	@Override
	public void close() {}
}