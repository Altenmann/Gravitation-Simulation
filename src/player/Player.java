package player;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

import main.GameEngine;
import main.Resource;

public class Player {
	
	private GameEngine engine;
	
	private double x, y, xVel, yVel;

	public Player(GameEngine engine) {
		x = 0;
		y = 0;
		
		this.engine = engine;
	}
	
	public void update() {
		x += xVel;
		y += yVel;
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform at = new AffineTransform();
		
		at.translate(engine.getWidth()/2, engine.getHeight()/2);
		at.scale(.2, .2);
		
		g2d.drawImage(Resource.ball, at, null);
	}
	
	public void Controller() {
		if(engine.getKeyInput(KeyEvent.VK_W)) this.yVel -= .05;
		if(engine.getKeyInput(KeyEvent.VK_S)) this.yVel += .05; 
		if(engine.getKeyInput(KeyEvent.VK_A)) this.xVel -= .05;
		if(engine.getKeyInput(KeyEvent.VK_D)) this.xVel += .05;
	}
	
	public double getX() { return x; }
	public double getY() { return y; }

}
