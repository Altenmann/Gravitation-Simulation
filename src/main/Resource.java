package main;

// Imports
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public final class Resource {
//--------------------------------------------------------------------------------
// Resource Variables
//--------------------------------------------------------------------------------
	private static BufferedImage sheet;
	
	// TODO: Organize images
	public static BufferedImage playButton, pauseButton, vectorOff, vectorOn, ball, shadow;
	public static BufferedImage sun, mercury, venus, earth, moon, mars, blackhole;
	public static BufferedImage whiteCircleArrow, redCircleArrow, addButton;
	public static BufferedImage cyanSelector, blackCircle;
	public static BufferedImage cursorSelector, cursorHand;
	
	public static BufferedImage milkyWayBg;
//--------------------------------------------------------------------------------
// Initialization 
//--------------------------------------------------------------------------------
	public static void initalize() {
		sheet = loadImage("/textures/sheet.png");
		
		playButton = crop(0, 0, 32, 32);
		pauseButton = crop(32, 0, 32, 32);
		ball = crop(0, 32, 64, 64);
		shadow = crop(0, 96, 64, 64);
		
		sun = crop(64, 96, 64, 64);
		mercury = crop(128, 32, 64, 64);
		venus = crop(192, 32, 64, 64);
		earth = crop(64, 32, 64, 64);
		moon = crop(128, 96, 64, 64);
		mars = crop(192, 96, 64, 64);
		
		vectorOff = crop(64, 0, 32, 32);
		vectorOn = crop(96, 0, 32, 32);
		whiteCircleArrow = crop(128, 0, 32, 32);
		redCircleArrow = crop(160, 0, 32, 32);
		
		addButton = crop(256, 0, 32, 32);
		
		cursorSelector = crop(192, 0, 32, 32);
		cursorHand = crop(224, 0, 32, 32);
		
		cyanSelector = crop(0, 160, 64, 64);
		blackCircle = crop(64, 160, 64, 64);
		
		blackhole = crop(128, 160, 64, 64);
		
		milkyWayBg = loadImage("/textures/milky-way.jpg");
	}
	
//--------------------------------------------------------------------------------
// Helper Functions 
//--------------------------------------------------------------------------------
	private static BufferedImage loadImage(String path) {
		try {
			return ImageIO.read(Resource.class.getResource(path));
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return null;
	}
	
	private static BufferedImage crop(int x, int y, int width, int height) {
		return sheet.getSubimage(x, y, width, height);
	}
}