package main;
//--------------------------------------------------------------------------------
// Java Game Engine
// GameEngine.java
// -- By : Kevin Swearingin
//--------------------------------------------------------------------------------

//--------------------------------------------------------------------------------
// Imports
//--------------------------------------------------------------------------------
import java.awt.Graphics;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import main.states.State;

//--------------------------------------------------------------------------------
// Game Engine Class
//--------------------------------------------------------------------------------
public class GameEngine implements Runnable, KeyListener,
	MouseMotionListener, MouseWheelListener, MouseListener {
//--------------------------------------------------------------------------------
// Variables
//--------------------------------------------------------------------------------
	// --- Window ---
	private JFrame jframe;
	private Canvas canvas;
	private int width, height;
	
	// --- Thread ---
	private Thread thread;
	private boolean running = false;
	// Frames per second | Seconds between updates
	private int FPS = 50;
	private int MILLIS_PER_UPDATE = 1000 / FPS;
	
	// --- Input ---
	// - Keyboard -
	public boolean[] keyInput = new boolean[256];
	// - Mouse -
	private int mouseX, mouseY;
	
	// --- Graphics ---
	private Graphics g;
	private BufferStrategy bs;
	
//--------------------------------------------------------------------------------
// Constructor(s)
//--------------------------------------------------------------------------------
	public GameEngine() {
		Resource.initalize();
		createWindow();
		start();
	}

//--------------------------------------------------------------------------------
// Loop
//--------------------------------------------------------------------------------
	public void tick() {
		State.currentState.tick();
	}
	
	public void render() {
		if(jframe == null || canvas == null)
			return;
		bs = canvas.getBufferStrategy();
		if(bs==null) {
			canvas.createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();
		g.clearRect(0, 0, width, height);
		
		// Draw here
		State.currentState.render(g);
		
		g.dispose();
		bs.show();
	}
	
	public void update() {
		if(State.currentState == null) return;
		tick();
		render();
	}
	
//--------------------------------------------------------------------------------
// Input
//--------------------------------------------------------------------------------
	//----------------
	// Keyboard
	//----------------
	@Override
	public void keyPressed(KeyEvent e) { keyInput[e.getKeyCode()] = true; }
	
	@Override
	public void keyReleased(KeyEvent e) { keyInput[e.getKeyCode()] = false; }
	
	@Override
	public void keyTyped(KeyEvent e) {}
	
	//----------------
	// Mouse
	//----------------
	@Override
	public void mouseClicked(MouseEvent e) {
		State.currentState.mouseClick(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		State.currentState.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		State.currentState.mouseReleased(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		State.currentState.mouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		State.currentState.mouseMoved(e);
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		State.currentState.mouseWheelMoved(e);
	}

//--------------------------------------------------------------------------------
// Main Thread (Main Loop)
//--------------------------------------------------------------------------------
	@Override
    public void run() {
        long nextTime = System.currentTimeMillis() + MILLIS_PER_UPDATE;
        int sleepTimer;

        running = true;
        while (running) {
            update();

            sleepTimer = (int) (nextTime - System.currentTimeMillis());
            nextTime += MILLIS_PER_UPDATE;
            if(sleepTimer > 0)
                try { Thread.sleep(sleepTimer); } catch (Exception e) {}
            // If sleepTimer < 0 the updates are behind
        }
    } 
	
	// Starts the main thread
	public synchronized void start() {
		if(running) return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	// Ends the main thread
	public synchronized void stop() {
		if(!running) return;
		try {
			thread.join();
			running=false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
//--------------------------------------------------------------------------------
// Window
//--------------------------------------------------------------------------------
	private void createWindow() {
		// --- JFrame ---
		jframe = new JFrame("Gravitation Simulation");
				
		// Sets the jframe full screen
		jframe.setExtendedState(JFrame.MAXIMIZED_BOTH);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
		// --- Canvas --- 
		canvas = new Canvas();
		// Allows the canvas to match the jframe size (fullscreen)
		canvas.setPreferredSize(jframe.getSize());
		canvas.setFocusable(false);
				
		// - Adding JFrame elements -
		jframe.add(canvas);
		jframe.pack();
				
		// --- Input ---
		jframe.addKeyListener(this);
		jframe.addMouseListener(this);
		jframe.addMouseMotionListener(this);
		jframe.addMouseWheelListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addMouseWheelListener(this);
				
		jframe.setVisible(true);
				
		// --- Global width and height
		width = jframe.getSize().width;
		height = jframe.getSize().height;
	}
	
	public void changeCursor(Cursor c) {
		jframe.setCursor(c);
	}
	
//--------------------------------------------------------------------------------
// Getters
//--------------------------------------------------------------------------------
	// --- Window ---
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	
	// --- Input ---
	// - Keyboard -
	public boolean getKeyInput(int keyEvent) { return keyInput[keyEvent]; }
	
	// - Mouse -
	public int getMouseX() { return mouseX; }
	public int getMouseY() { return mouseY; }
	
	public void addComponent(Component c) {
		jframe.add(c);
	}
}
