package main.gfx;


/**
 * Camera used to display all objects in a different
 * location based on the camera's current position
 * 
 * TODO Needs updated for easier and a more understandable use
 * 
 * @author Kevin
 *
 */
public class Camera {
	
	// TODO Fix choppy camera
	private int x, y, xoffset, yoffset;
	private double zoom;
	
	private int width, height;
	
	public Camera(int x, int y) {
		this.x = x;
		this.y = y;
		zoom = .00001;
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	public int getXOffset() { return xoffset; }
	public int getYOffset() { return yoffset; }
	public double getZoom() { return zoom; }

	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setOffset(int xoffset, int yoffset) {
		this.xoffset = xoffset;
		this.yoffset = yoffset;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	public void setLocation(double x2, double y2) {
		x = (int) x2;
		y = (int) y2;
	}
	
	public void setScreenBounds(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
