package main.states;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Objects;

import forces.Gravity;
import main.GameEngine;
import main.Resource;
import main.buttons.AddBodyButton;
import main.buttons.Button;
import main.buttons.ClampButton;
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

	public SolarSystemState(GameEngine engine) {
		super(engine); // Creates this state
		
		Body.showVectors = false; // Does not show the planets' vectors by default

		bodies = new ArrayList<Body>();
		removeBodies = new ArrayList<Body>();
		buttons = new ArrayList<Button>(); // Buttons are displayed on the canvas

		cursorMode = CursorMode.select; // Starts off in "select" mode
		setCursor(); // Will set the frame's cursor based on the current cursor mode

		// Adds the various buttons
		buttons.add(new PlayPauseButton(300, 20, 32, 32, paused)); // Plays & pauses
		buttons.add(new VectorButton(300, 60, 32, 32)); // Turn on and off vectors
		buttons.add(new ResetButton(300, 100, 32, 32)); // Resets the bodies to the starting positions and velocities
		buttons.add(new CursorModeButton(340, 20, 32, 32)); // Changes the cursor mode (needs improvements)
		buttons.add(new AddBodyButton(340, 60, 32, 32)); // Adds a black hole (later will add options)
		buttons.add(new ClampButton(340, 100, 32, 32));

		bodySetSolarSystem1(); // Adds set of Bodies
	}

	// A static method called from inside bodies to remove itself from the
	// simulation
	public static void removeBody(Body b) {
		removeBodies.add(b);
	}

	// Resets the state
	public static void reset() {
		bodies.clear(); // Clears the current bodies

		// Sets the camera's position back to 0, 0
		camera.setX(0);
		camera.setY(0);

		// creates new set of bodies
		bodySetSolarSystem1();

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
		// G = .1
		int width = engine.getWidth();
		int height = engine.getHeight();

		// Bodies to add
		Body Sun = new Body("Sun", width / 2, height / 2, 12, 1989);
		Body Mercury = new Body("Mercury", width / 2, height / 2 + 36 * 2, 3, .3285);
		Body Venus = new Body("Venus", width / 2 - 67 * 2, height / 2, 5, 4.867);
		Body Earth = new Body("Earth", width / 2, height / 2 - 93 * 2, 6, 5.972);
		Body Moon = new Body("Moon", width / 2, height / 2 - 93 * 2 - 12, 2, .07346);
		Body Mars = new Body("Mars", width / 2 + 142 * 2, height / 2, 4, .639);

		// Initial velocities
		Mercury.setVel(-1.5, 0);
		Venus.setVel(0, 1.05);
		Earth.setVel(.947, 0);
		Moon.setVel(.767, 0);
		Mars.setVel(0, .778);

		// Images used
		Sun.setImage(Resource.sun);
		Mercury.setImage(Resource.mercury);
		Venus.setImage(Resource.venus);
		Earth.setImage(Resource.earth);
		Moon.setImage(Resource.moon);
		Mars.setImage(Resource.mars);

		// For lighting (currently not implemented)
		// Sun.toggleEmitter();

		// Main body used for finding relative distance to
		Body.mainBody = Sun;

		// Adds the bodies created to the ArrayList
		bodies.add(Sun);
		bodies.add(Mercury);
		bodies.add(Venus);
		bodies.add(Earth);
		bodies.add(Moon);
		bodies.add(Mars);
	}

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
		g.setColor(Color.white);
		g.fillRect(10, 10, 250, numBodies * 25);

		// Text
		g.setColor(Color.black);
		g.setFont(Font.getFont(Font.MONOSPACED));
		for (int i = 0; i < numBodies; i++) {
			Body b = bodies.get(i);
			g.drawString(b.getStats() + "  " + String.format("%.1f", b.getDistTo(bodies.get(0))), 20, 25 * (i + 1));
		}

	}

	// Update method
	public void tick() {
		if (paused)
			return; // Does not go past this if paused

		Collider.checkCollisions(bodies); // Checks if bodies are touching

		if (!removeBodies.isEmpty())
			bodies.removeAll(removeBodies);

		// Changes the velocity of bodies based on other bodies positions
		Gravity.Gravitate(bodies);

		for (Body b : bodies) {
			b.tick();
			b.clamp(0, 0, engine.getWidth(), engine.getHeight());
		}

		if (Body.selectedBody != null) {
			// TODO Fix camera offset
			camera.setOffset((int)(Body.selectedBody.getX() - Body.selectedBody.getStartClickX()), 
					(int)(Body.selectedBody.getY() - Body.selectedBody.getStartClickY()));
			double desiredX = camera.getXOffset();
			double desiredY = camera.getYOffset();
			camera.setX((int) desiredX);
			camera.setY((int) desiredY);
		}
	}

	// Drawing method
	public void render(Graphics g) {
		// TODO Organize the use of Graphics and Graphics2D
		Graphics2D g2d = (Graphics2D) g;

		// Background
		g2d.drawImage(Resource.milkyWayBg, 0, 0, engine.getWidth(), engine.getHeight(), null);
		g.setColor(new Color(0, 0, 0, .85f));
		g.fillRect(0, 0, engine.getWidth(), engine.getHeight());

		// Border lines
		if (Body.clamp) {
			g.setColor(Color.white);
			g.drawRect(-camera.getX(), -camera.getY(), engine.getWidth(), engine.getHeight());
		}

		// GUI
		showStatPanel(g);
		drawButtons(g);

		// Bodies
		for (Body b : bodies) {
			b.draw(g, camera);
		}
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
		Body blackhole = new Body("Black Hole", 0, 0, 14, 100000);
		blackhole.setImage(Resource.blackhole);
		bodies.add(blackhole);
		Body.selectedBody = blackhole;
	}

	public static boolean isBodyVectors() {
		// TODO Clean this
		return Body.showVectors;
	}

	public static void clampSwitch() {
		// TODO Clean this
		Body.clamp = !Body.clamp;
	}

}
