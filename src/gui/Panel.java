package gui;

import java.awt.Color;
import java.awt.Graphics;

public class Panel {
	
	public static Panel selectedPanel;
	
	private static int x, y;
	private int width, height;
	
	public Panel(int x, int y, int width, int height) {
		Panel.x = x;
		Panel.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.white);
		g.fillRoundRect(x, y, width, height, 3, 3);
	}
	
	public boolean checkBounds(int x, int y) {
		return (x >= Panel.x && x <= Panel.x + width && y >= Panel.y && y <= Panel.y + height);
	}
	
	public void onHover() {
		
	}
	
	public void onClick() {
		
	}
	
	public void onDrag(int x, int y) {
		Panel.x = x;
		Panel.y = y;
	}
}
