package main.states;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import main.GameEngine;
import main.gfx.Camera;

public abstract class State {
	
	// State to be ticked and rendered
	public static State currentState;
	
	protected static GameEngine engine;
	
	protected static boolean resetState = false;
	
	protected static Camera camera;
	protected static boolean paused;
	
	protected double deltaTime;

	public State(GameEngine engine) {
		State.engine = engine;
		
		camera = new Camera(-engine.getWidth()/2, -engine.getHeight()/2);
		camera.setOffset(0, 0);
		camera.setScreenBounds(engine.getWidth(), engine.getHeight());
		deltaTime = 0;
		paused = true;
	}
	
	// Sub-class methods
	public abstract void tick();
	public abstract void render(Graphics g);
	
	public abstract void mouseClick(MouseEvent e);
	public abstract void mouseDragged(MouseEvent e);
	public abstract void mouseReleased(MouseEvent e);
	public abstract void mousePressed(MouseEvent e);
	public abstract void mouseMoved(MouseEvent e);
	
	public abstract void mouseWheelMoved(MouseWheelEvent e);
}
