package main.states;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import main.GameEngine;
import main.Resource;
import player.Player;

public class GameState extends State {
	
	private Player player;

	public GameState(GameEngine engine) {
		super(engine);
		
		player = new Player(engine);
	}
	
	public void cameraFollowPlayer() {
		camera.setX((int)player.getX());
		camera.setY((int)player.getY());
	}

	@Override
	public void tick() {
		// Player controls
		player.Controller();
		// Player update
		player.update();
		
		cameraFollowPlayer();
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		// Draws stars background
		g2d.drawImage(Resource.milkyWayBg, 0, 0, engine.getWidth(), engine.getHeight(), null);
		
		g.setColor(new Color(0, 0, 0, .85f));
		g.fillRect(0, 0, engine.getWidth(), engine.getHeight());
		
		// Shows player
		player.render(g);
	}

	@Override
	public void mouseClick(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {}

}
