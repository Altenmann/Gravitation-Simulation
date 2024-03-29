package main.states;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Objects;

import forces.Gravity;
import main.GameEngine;
import main.Resource;
import main.buttons.AddBodyButton;
import main.buttons.BodyButton;
import main.buttons.Button;
import main.buttons.CursorModeButton;
import main.buttons.PlayPauseButton;
import main.buttons.ResetButton;
import main.buttons.VectorButton;
import objects.Body;
import objects.Collider;


/**
 * Creates a rough gravitational simulation of our solar system
 * 
 * Needs organization
 * 
 * @author Kevin
 *
 */
public class SolarSystemState extends State {

	// Stores all celestial bodies
	private static ArrayList<Body> bodies;
	// Removes all bodies from the bodies array list on start of tick
	private static ArrayList<Body> removeBodies;

	// Buttons for GUI
	private static ArrayList<Button> buttons;

	// Changes the cursor mode to interact with things differently
	private static CursorMode cursorMode;
	
	private static double timeIncrement = 10;

	public SolarSystemState(GameEngine engine) {
		super(engine); // Creates this state
		
		Body.showVectors = false; // Does not show the planets' vectors by default

		bodies = new ArrayList<Body>();
		removeBodies = new ArrayList<Body>();
		buttons = new ArrayList<Button>(); // Buttons are displayed on the canvas

		cursorMode = CursorMode.select; // Starts off in "select" mode
		setCursor(); // Will set the frame's cursor based on the current cursor mode

		// Adds the various buttons
		buttons.add(new PlayPauseButton(800, 20, 32, 32, paused)); // Plays & pauses
		buttons.add(new VectorButton(800, 60, 32, 32)); // Turn on and off vectors
		buttons.add(new ResetButton(800, 100, 32, 32)); // Resets the bodies to the starting positions and velocities
		buttons.add(new CursorModeButton(840, 20, 32, 32)); // Changes the cursor mode (needs improvements)
		buttons.add(new AddBodyButton(840, 60, 32, 32)); // Adds a black hole (later will add options)

		bodySetSolarSystem1(); // Adds set of Bodies
	}

	// A static method called from inside bodies to remove itself from the
	// simulation
	public static void removeBody(Body b) {
		removeBodies.add(b);
	}

	// Resets the state
	public static void reset() {
		// Reset the bodies back to original parameters
		for(Body b : bodies) b.reset();

		// Changes the cursor back to select
		setCursorMode(CursorMode.select);
	}

	// Switches the pause
	public static void pauseSwitch() {
		paused = !paused;
	}

	// Switches the vectors display
	public static void vectorSwitch() {
		Body.showVectors = !Body.showVectors;
	}
	
	/*
	 * TODO Calculate proper simulated distances and masses
	 */
	// Adding all the bodies and their properties
	private static void bodySetSolarSystem1() {
		/* 
		 * G = .1
		 * Bodies to add
		 */// Body               Name       X  Y (m)             Diameter(m)    Mass (kg)
		Body Sun =		new Body("Sun", 	0, 0		   			,   1.39E+9, 	1.989E+30);
		
		// Main body used for finding relative distance to
		Body.mainBody = Sun;
		
		Body Mercury = 	new Body("Mercury", 0, -46E+9				,   4879E+3, 	   .33E+24);
		Body Venus = 	new Body("Venus", 	0, 107.5E+9				,  12104E+3,      4.8E+24);
		Body Earth = 	new Body("Earth", 	0, 147.1E+9				,  12756E+3,      5.97E+24);
		Body Moon = 	new Body("Moon", 	0, 147.1E+9 + .363E+9	,   3475E+3,       .073E+24);
		Body Mars = 	new Body("Mars", 	0, 206.7E+9				,   6792E+3, 	   .642E+24);
		Body Jupiter =  new Body("Jupiter", 0, 740.6E+9 			, 142984E+3,   1898E+24);
		Body Saturn = 	new Body("Saturn",  0, 1357.6E+9			, 120536E+3,    568E+24);
		Body Uranus = 	new Body("Uranus",  0, 2732.7E+9			,  51118E+3,     86.8E+24);
		Body Neptune = 	new Body("Neptune", 0, 4471.1E+9			,  49528E+3, 	102.0E+24);

		// Initial velocities (m/s)
		Mercury.setVel(	-59E+3, 0);
		Venus.setVel  (	35.2E+3, 0);
		Earth.setVel  (	30.29E+3, 0);
		Moon.setVel	  (	30.29E+3 + 1082, 0);
		Mars.setVel	  (	24.07E+3, 0);
		Jupiter.setVel( 13.72E+3, 0);
		Saturn.setVel ( 10.18E+3, 0);
		Uranus.setVel (  7.11E+3, 0);
		Neptune.setVel(  5.5E+3, 0);

		// Images used
		Sun.setImage(Resource.sun);
		Mercury.setImage(Resource.mercury);
		Venus.setImage(Resource.venus);
		Earth.setImage(Resource.earth);
		Moon.setImage(Resource.moon);
		Mars.setImage(Resource.mars);
		Jupiter.setImage(Resource.jupiter);
		Saturn.setImage(Resource.saturn);
		Uranus.setImage(Resource.uranus);
		Neptune.setImage(Resource.neptune);
		
		Saturn.setRingBuffer(.67); // Quick fix for saturn

		// Adds the bodies created to the ArrayList
		bodies.add(Sun);
		bodies.add(Mercury);
		bodies.add(Venus);
		bodies.add(Earth);
		bodies.add(Moon);
		bodies.add(Mars);
		bodies.add(Jupiter);
		bodies.add(Saturn);
		bodies.add(Uranus);
		bodies.add(Neptune);
		
		// Body Buttons
		for(int i=0; i<bodies.size(); i++) {
			buttons.add(new BodyButton(5, 16 + 24 * i, 16, 16, bodies.get(i)));
		}
	}

	// TODO Fix cursor modes
	// Gets the current cursorMode
	public static CursorMode getCursorMode() {
		return cursorMode;
	}

	// Sets the current cursor mode enumeration
	public static void setCursorMode(CursorMode cm) {
		cursorMode = cm;
	}

	// Sets the cursor based on the enumeration
	public static void setCursor() {
		switch (cursorMode) {
		case hand:
			changeCursor(Cursor.HAND_CURSOR);
			break;
		case select:
			changeCursor(Cursor.DEFAULT_CURSOR);
			break;
		default:
			changeCursor(Cursor.DEFAULT_CURSOR);
		}
	}

	// changes the cursor mode to another (used inside Body)
	public static void switchCursorMode() {
		switch (cursorMode) {
		case select: // Switches to hand from select
			setCursorMode(CursorMode.hand);
			break;
		case hand: // Switches to select from hand
			setCursorMode(CursorMode.select);
			break;
		default: // Defaults to select
			setCursorMode(CursorMode.select);
		}
		setCursor(); // Applies changes
	}

	// Renders the buttons
	private void drawButtons(Graphics g) {
		for (Button b : buttons) {
			b.draw(g);
		}
	}

	// Used for displaying information about the bodies
	private void showStatPanel(Graphics g) {
		int numBodies = bodies.size();

		// Back panel of stats
		//g.setColor(Color.white);
		//g.fillRect(10, 10, 400, numBodies * 25);

		// Text
		g.setColor(Color.white);
		g.setFont(Font.getFont(Font.MONOSPACED));
		for (int i = 0; i < numBodies; i++) {
			Body b = bodies.get(i);
			g.drawString(b.getStats(), 20, 25 * (i + 1));
		}

	}

	// Update method
	public void tick() {
		if (paused)
			return; // Does not go past this if paused
		
		// checking for collisions 
		Collider.checkCollisions(bodies); // Checks if bodies are touching

		if (!removeBodies.isEmpty())
			bodies.removeAll(removeBodies);

		// Changes the velocity of bodies based on other bodies positions
		Gravity.Gravitate(bodies);

		for (Body b : bodies) {
			b.tick();
		}

		if (Body.selectedBody != null) {
			// TODO Fix camera offset and zoom
			camera.setOffset((int)(Body.selectedBody.getX()*camera.getZoom() - Body.selectedBody.getStartClickX()), 
					(int)(Body.selectedBody.getY()*camera.getZoom() - Body.selectedBody.getStartClickY()));
			double desiredX = camera.getXOffset();
			double desiredY = camera.getYOffset();
			camera.setX((int) desiredX);
			camera.setY((int) desiredY);
		}
		
		deltaTime += timeIncrement;
	}

	// Drawing method
	public void render(Graphics g) {
		// TODO Organize the use of Graphics and Graphics2D
		Graphics2D g2d = (Graphics2D) g;

		// Background
		g2d.drawImage(Resource.milkyWayBg, 0, 0, engine.getWidth(), engine.getHeight(), null);
		g.setColor(new Color(0, 0, 0, .85f));
		g.fillRect(0, 0, engine.getWidth(), engine.getHeight());
		
		// Bodies
		for (Body b : bodies) {
			b.draw(g, camera);
		}

		// GUI
		showStatPanel(g);
		drawButtons(g);

		
	}

	// Sends the clicks to the target GUI
	@Override
	public void mouseClick(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		for (Button b : buttons) {
			if (b.checkClick(x, y)) {
				b.onClick();
				return;
			}
		}

		for (Body b : bodies) {
			if (b.checkBounds(x, y, camera)) {
				b.onClick();
				return;
			}
		}

		Body.clearBodies();

		if (cursorMode == CursorMode.hand)
			switchCursorMode();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		if (Objects.nonNull(Body.selectedBody)) {
			Body.selectedBody.onDrag(x, y, camera);
		} else 
		if (Objects.nonNull(Body.heldBody)) {
			Body.heldBody.onDrag(x, y, camera);
			return;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (Objects.nonNull(Body.heldBody)) {
			Body.heldBody.releaseHeldBody(e.getX(), e.getY(), paused);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		for (Body b : bodies) {
			if (b.checkBounds(x, y, camera)) {
				b.onPress(x, y, camera);
				return;
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		for (Button b : buttons) {
			if (b.checkClick(e.getX(), e.getY())) {
				b.onHover();
				return;
			} else
				b.offHover();
		}
	}

	// Changes the frames cursor using engine's changeCursor method
	public static void changeCursor(int cursor) {
		engine.changeCursor(Cursor.getPredefinedCursor(cursor));
	}

	// Adds a black hole to the simulation
	public static void addBlackHole() {
		// TODO Black holes
		Body blackhole = new Body("Black Hole", 0, 0, 14e6, 1e31);
		blackhole.setImage(Resource.blackhole);
		bodies.add(blackhole);
		Body.selectedBody = blackhole;
	}

	public static boolean isBodyVectors() {
		// TODO Clean this
		return Body.showVectors;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		camera.setZoom(camera.getZoom() * Math.pow(1.05, -e.getPreciseWheelRotation()) );
	}

	public static void center(Body b) {
		camera.setLocation(b.getX() * camera.getZoom() - engine.getWidth() / 2, b.getY() * camera.getZoom() - engine.getHeight() / 2);
		b.center(engine.getWidth(), engine.getHeight());
		//camera.setZoom(.00001);
	}
	
	public static double getTimeIncrement() {
		return timeIncrement;
	}

}
