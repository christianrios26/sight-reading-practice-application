package com.chrisapps2003.sightreadingpracticeapplication;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class WindowKeyListener implements KeyListener
{
	protected PracticeLogic logic;

	public WindowKeyListener(PracticeLogic logic)
	{
		this.logic = logic;
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		logic.keyPressed(e.getKeyCode(), e.isControlDown());
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		logic.keyReleased(e.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent e) {}
}