package main.buttons;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public abstract class Button {
	
	private int x, y, width, height;
	protected BufferedImage image;
	
	public Button(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void draw(Graphics g) {
		g.drawImage(image, x, y, width, height, null);
	}
	
	public boolean checkClick(int cx, int cy) {
		return (cx > x && cx < x+width && cy > y && cy < y+height);
	}
	
	public abstract void onClick();
	public abstract void onHover();
	public abstract void offHover();
}
