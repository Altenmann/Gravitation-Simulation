package objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import main.Resource;
import main.gfx.Camera;
import main.states.CursorMode;
import main.states.SolarSystemState;
import math.Maths;


/**
 * @author admin
 * 
 * @param mainBody - the point of reference for calculations
 * @param heldBody
 * @param selectedBody
 * @param clamp 
 *
 */
public class Body implements Collider {
	
	public static Body mainBody;
	public static Body heldBody;
	public static Body selectedBody;
	
	public static boolean showVectors = false;
	
	private String name;
	
	// TODO Make a vectors and/or physics body class
	private double x, y, mass, radius, xVel, yVel, xAcc, yAcc;
	private int startingX, startingY, endX, endY;
	
	private int startClickX, startClickY;
	
	public boolean stationary = false;
	private boolean release = false;
	
	private BufferedImage image;
	
	private int imageWidth, imageHeight;
	
	private double ringBuffer;
	
	private double startVarX, startVarY, startVarXVel, startVarYVel;
	
	private double maxDist, maxVel, minDist, minVel;
	
	private double velocity;
	
	public Body(String name, double x, double y, double diameter, double mass) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.mass = mass;
		this.radius = diameter/2;
		
		startVarX = x;
		startVarY = y;
		
		xVel = 0;
		yVel = 0;
		
		if(Body.mainBody != null) {
			maxDist = getDistTo(Body.mainBody);
			minDist = maxDist;
		} else {
			maxDist = 0;
			minDist = 1e100;
		}
		
		minVel = 0;
		maxVel = 0;
		
		ringBuffer = 1f;
		
		// Defaults the image to a white circle
		image = Resource.ball;
	}
	
	// Checks if a given x and y position and camera location if its inside the boundaries of the circle
	public boolean checkBounds(int x, int y, Camera camera) {
		double zoom = camera.getZoom();
		return (x >= Math.max( this.x*zoom - radius*zoom - camera.getX() , -2 ) && x <= this.x*zoom + radius*zoom - camera.getX() && 
				y <= this.y*zoom + radius*zoom - camera.getY() && y >= this.y*zoom - radius*zoom - camera.getY());
	}
	
	// What to do when a body is clicked
	public void onClick() {
		if(SolarSystemState.getCursorMode() != CursorMode.select) return; 
		
	}
	
	public void onPress(int x, int y, Camera camera) {
		if(SolarSystemState.getCursorMode() == CursorMode.select) {
			Body.selectedBody = this;
			startClickX = x;
			startClickY = y;
			return;
		}
		if(SolarSystemState.getCursorMode() != CursorMode.hand) return;
		if(Body.selectedBody == this) Body.heldBody = this;
		else return;
		
		startingX = startClickX;
		startingY = startClickY;
		endX = startingX;
		endY = startingY;
	}
	
	public void onDrag(int x, int y, Camera camera) {
		if(SolarSystemState.getCursorMode() == CursorMode.select) {
			startClickX = x;
			startClickY = y;
			return;
		}
		if(SolarSystemState.getCursorMode() != CursorMode.hand) return;
		
		endX = x; 
		endY = y; 
	}
	
	public void releaseHeldBody(int x, int y, boolean paused) {
		double xdiff = (startingX - endX);
		double ydiff = (startingY - endY);
		double tdiff = Math.abs(xdiff) + Math.abs(ydiff);
		
		if(tdiff <= 0) return;
		
		double dist = Maths.dist(xdiff, ydiff);
		
		xVel += dist * (xdiff / tdiff);
		yVel += dist * (ydiff / tdiff);
		
		release = true;
	}
	
	public void tick() {
		if(release) {
			release = false;
			heldBody = null;
		}
		if(stationary) return;
		
		// Changes position based on velocity
		double dt = SolarSystemState.getTimeIncrement();
		x = x + xVel * dt + xAcc * Math.pow(dt, 2) / 2;
		y = y + yVel * dt + yAcc * Math.pow(dt, 2) / 2;
		
		xVel += xAcc * dt;
		yVel += yAcc * dt;
		
		velocity = Math.abs(xVel) + Math.abs(yVel);
		if( maxVel < velocity) maxVel = velocity;
		else if( minVel > velocity) minVel = velocity;
		
		double dist = getDistTo(mainBody);
		if( maxDist < dist) maxDist = dist;
		else if( minDist > dist) minDist = dist;
	}
	
	public void draw(Graphics g, Camera camera) {
		Graphics2D g2d = (Graphics2D) g;
		
		AffineTransform at = new AffineTransform();
		
		double xChange = x*camera.getZoom()-radius*camera.getZoom() - camera.getX();
		double yChange = y*camera.getZoom()-radius*camera.getZoom() - camera.getY();
		double rChange = radius*camera.getZoom();
		
		// Will only draw the resources if they are on screen
		if(xChange - rChange*2 > camera.getWidth() || xChange + rChange*2 < 0 || yChange - rChange*2 > camera.getHeight() || yChange + rChange*2 < 0) return;
		
		// Only draw Name and line if not visible
		if(rChange/(imageWidth/2) <= 1E-3) {
			g.setColor(Color.white);
			g.drawLine((int)(xChange + rChange), (int)(yChange + rChange), (int)xChange - 10, (int)yChange - 10);
			g.drawString(name, (int)xChange - 10, (int)yChange - 20);
		} else {
			// Moves the transform to the x and y location and moves the image back half step
			at.translate(xChange, yChange);
			// Images are 64 pixels, dividing by 32 gives proper radius of scaled image
			at.scale(rChange/(imageWidth/2), rChange*ringBuffer/(imageHeight/2));
		
			// Draws the current textured registered
			g2d.drawImage(image, at, null);
		}
		
		// Drag line
		if(Body.heldBody == this || release) {
			int sx = startClickX;
			int sy = startClickY;
			int ex = endX;
			int ey = endY;
			g.setColor(Color.cyan);
			g.drawLine(sx, sy, ex, ey);
			
			g.setColor(Color.yellow);
			g.drawLine(sx, sy, sx + (sx - ex) / 2, sy + (sy - ey) / 2 );
		}
		
		// Shows line of velocity and acceleration
		if(showVectors) {
			double xVel = this.xVel;
			double yVel = this.yVel;
			if(selectedBody != null) {
				double[] velDiff = getRelativeVelocity(selectedBody);
				xVel = velDiff[0];
				yVel = velDiff[1];
			}
			g.setColor(Color.green);
			g.drawLine((int)(xChange + rChange), (int)(yChange + rChange), (int)((xChange + rChange)+xVel/1E+3), (int)((yChange + rChange)+yVel/1E+3));
		
			g.setColor(Color.red);
			g.drawLine((int)(xChange + rChange), (int)(yChange + rChange), (int)((xChange + rChange)+xAcc*1E+3), (int)((yChange + rChange)+yAcc*1E+3));
		}
		
		
	}
	
	// Getters and setters
	
	// Returns valuable information about the planets
	public String getStats() {
		//double velocity = Math.abs(xVel) + Math.abs(yVel);
		// Prints the name along with velocity 
		// TODO Fix the display of stats
		return getStringBuffer(name, 12) + ": " 
					+ getStringBuffer(String.format("%.3f", mass/1E+24) + " 10^24 kg", 21 ) + " "
					+ getStringBuffer(String.format("%.1f", velocity/1E+3) + " km/s", 14) + " "
					+ getStringBuffer(String.format("%.2f", (getDistTo((Body.selectedBody != null) ? Body.selectedBody : Body.mainBody)) / 1E+9) + " 10^6 km", 18) + "   "
					+ "Min/Max Distance: " + getStringBuffer(String.format("%.1f", minDist / 1E+9) + " / " + String.format("%.1f", maxDist/1E+9), 14) + "  "
					+ "Max/Min Velocity: " + getStringBuffer(String.format("%.1f", maxVel/1E+3) + " / " + String.format("%.1f", minVel/1E+3), 14);
	}
	
	// Getters
	public double getX() { return x; }
	public double getY() { return y; }
	public double getMass() { return mass; }
	public double getRadius() { return radius; }
	public double getXVel() { return xVel; }
	public double getYVel() { return yVel; }
	
	public int getStartClickX() { return startClickX; }
	public int getStartClickY() { return startClickY; }
	
	// Sets the velocity
	public void setVel(double xVel, double yVel) {
		this.xVel = xVel;
		this.yVel = yVel;
		startVarXVel = xVel;
		startVarYVel = yVel;
		
		velocity = Math.abs(xVel) + Math.abs(yVel);
		minVel = velocity;
		maxVel = velocity;
	}
	
	// Sets the acceleration
	public void setAcc(double xAcc, double yAcc) {
		this.xAcc = xAcc;
		this.yAcc = yAcc;
	}
	
	// Sets the image
	public void setImage(BufferedImage image) {
		imageWidth = image.getWidth();
		imageHeight = image.getHeight();
		this.image = image;
	}
	
	// Gets distance to a certain body
	public double getDistTo(Body b) {
		return Math.sqrt( Math.pow(x - b.getX(), 2) + Math.pow(y - b.getY(), 2));
	}
	
	public double[] getRelativeVelocity(Body b) {
		double xVelDiff = xVel - b.getXVel();
		double yVelDiff = yVel - b.getYVel();
		
		double[] velDiffs = {xVelDiff, yVelDiff};
		return velDiffs;
	}
	
	// Gets the angle to a certain body
	public double getAngleTo(Body b) {
		double xdiff = b.getX() - x;
		double ydiff = b.getY() - y;
		
		double extraRotation = 0;
		if(xdiff < 0) extraRotation = Math.PI;
		
		return Math.atan(ydiff / xdiff) + extraRotation;
	}

	public static void clearBodies() {
		heldBody = null;
		selectedBody = null;
	}
	
	public void eat(Body b) {
		mass += b.getMass();
		b.remove();
	}
	
	// Removes this planet from the simulation
	public void remove() {
		SolarSystemState.removeBody(this);
		if(selectedBody == this) {
			selectedBody = null;
			SolarSystemState.setCursorMode(CursorMode.select);
		}
		if(heldBody == this) heldBody = null;
	}

	public BufferedImage getResource() {
		return image;
	}

	
	public void applyForce(double xForce, double yForce) {
		this.xAcc = xForce / mass;
		this.yAcc = yForce / mass;
	}

	public void center(int width, int height) {
		startClickX = width/2;
		startClickY = height/2;
	}
	
	public void setRingBuffer(double ringBuffer) {
		this.ringBuffer = ringBuffer;
	}
	
	public void reset() {
		x = startVarX;
		y = startVarY;
		xVel = startVarXVel;
		yVel = startVarYVel;
	}
	
	private String getStringBuffer(String s1, int length) {
		
		if(s1.length() >= length) return s1;
		
		String s2 = "";
		
		for(int i=s1.length(); i<=length; i++) {
			s2 += " ";
		}
		
		return s2+s1;
	}
}
