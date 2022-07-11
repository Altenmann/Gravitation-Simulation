package main.states;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Objects;

import forces.Gravity;
import gui.Panel;
import main.GameEngine;
import main.Resource;
import main.buttons.AddBodyButton;
import main.buttons.Button;
import main.buttons.CursorModeButton;
import main.buttons.PlayPauseButton;
import main.buttons.ResetButton;
import main.buttons.VectorButton;
import main.gfx.Camera;
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
	// Removes all bodies from the @bodies array list on start of tick
	private static ArrayList<Body> removeBodies;

	// Buttons for GUI
	private static ArrayList<Button> buttons;

	// Used for displaying individual planet properties
	private static Panel bodyPanel;

	// Changes the cursor mode to interact with things differently
	private static CursorMode cursorMode;

	// Used if planets should update their positions or not
	private static boolean paused;
	// If true will show the planets velocities and acceleration
	private static boolean vectors;

	// A camera used to follow the planets around
	private static Camera camera;

	public SolarSystemState(GameEngine engine) {
		super(engine); // Creates a state

		paused = true; // Starts the simulation paused
		vectors = false; // Shows no vectors by default
		Body.showVectors = vectors; // Body vector value (could change to 1 variable)

		bodies = new ArrayList<Body>();
		removeBodies = new ArrayList<Body>();
		buttons = new ArrayList<Button>(); // Buttons are displayed on the canvas

		cursorMode = CursorMode.select; // Starts off in "select" mode
		setCursor(); // Will set the frame's cursor based on the current cursor mode

		// Adds the various buttons
		buttons.add(new PlayPauseButton(300, 20, 32, 32, paused)); // Plays & pauses
		buttons.add(new VectorButton(300, 60, 32, 32, vectors)); // Turn on and off vectors
		buttons.add(new ResetButton(300, 100, 32, 32)); // Resets the bodies to the starting positions and velocities
		buttons.add(new CursorModeButton(340, 20, 32, 32)); // Changes the cursor mode (needs improvements)
		buttons.add(new AddBodyButton(340, 60, 32, 32)); // Adds a black hole (later will add options)

		bodySetSolarSystem1(); // Adds set of Bodies

		// Sets the camera to 0, 0 (meaningless atm)
		camera = new Camera(0, 0);
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

	// Switches the vectors and updates the Body class to match
	public static void vectorSwitch() {
		vectors = !vectors;
		Body.showVectors = vectors;
	}

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

	// A body panel used to display information on selected planets
	public static void createBodyPanel(Body b) {
		bodyPanel = new Panel(25, 400, 150, 150);
	}

	// Deletes the panel (hiding might be better and reusing)
	// Currently unused
	public static void removePanel() {
		bodyPanel = null;
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
		case select:
			setCursorMode(CursorMode.hand);
			break;
		case hand:
			setCursorMode(CursorMode.select);
			break;
		default:
			setCursorMode(CursorMode.select);
		}

		setCursor();
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
			b.clamp(0, 0, engine.getWidth(), engine.getHeight() - 34);
		}

		if (Body.selectedBody != null) {
			double desiredX = Body.selectedBody.getX() - engine.getWidth() / 2;
			double desiredY = Body.selectedBody.getY() - engine.getHeight() / 2;
			camera.setX((int) desiredX);
			camera.setY((int) desiredY);
		}
	}

	// Drawing method
	public void render(Graphics g) {

		// Background
		g.setColor(Color.black);
		g.fillRect(0, 0, engine.getWidth(), engine.getHeight());

		// Body panel
		if (bodyPanel != null) {
			bodyPanel.draw(g);
		}

		// Border lines
		if (Body.clamp) {
			g.setColor(Color.white);
			g.drawRect(-camera.getX(), -camera.getY(), engine.getWidth(), engine.getHeight() - 34);
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

		if (Panel.selectedPanel != null) {
			Panel.selectedPanel.onDrag(x, y);
			return;
		}

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

		if (Panel.selectedPanel != null)
			Panel.selectedPanel = null;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		for (Body b : bodies) {
			if (b.checkBounds(x, y, camera)) {
				b.onPress(camera);
				return;
			}
		}

		if (bodyPanel != null && bodyPanel.checkBounds(x, y)) {
			Panel.selectedPanel = bodyPanel;
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

		if (bodyPanel != null && bodyPanel.checkBounds(e.getX(), e.getY())) {
			changeCursor(Cursor.MOVE_CURSOR);
		} else {
			setCursor(); // Fix this
		}
	}

	// Changes the frames cursor using engine's changeCursor method
	public static void changeCursor(int cursor) {
		engine.changeCursor(Cursor.getPredefinedCursor(cursor));
	}

	// Adds a black hole to the simulation
	public static void addBlackHole() {
		// TODO Auto-generated method stub
		Body blackhole = new Body("Black Hole", 0, 0, 14, 100000);
		blackhole.setImage(Resource.blackhole);
		bodies.add(blackhole);
		Body.selectedBody = blackhole;
	}

}
