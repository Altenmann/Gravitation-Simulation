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
	
	public static boolean clamp = false;
	private static int clampType = 1;
	public static boolean shadows = false;
	public static boolean showVectors = false;
	
	private String name;
	
	// TODO Make a vectors and/or physics body class
	private double x, y, mass, radius, xVel, yVel, xAcc, yAcc;
	private int startingX, startingY, endX, endY;
	
	private int startClickX, startClickY;
	
	public boolean stationary = false;
	private boolean emitsPhotons = false;
	private boolean release = false;
	
	private BufferedImage image;
	
	private double bounciness = .777;
	
	public Body(String name, double x, double y, double diameter, double mass) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.mass = mass;
		this.radius = diameter/2;
		
		xVel = 0;
		yVel = 0;
		
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
		
		xVel += dist * (xdiff / tdiff) / 200;
		yVel += dist * (ydiff / tdiff) / 200;
		
		release = true;
	}
	
	public void clamp(int minX, int minY, int maxX, int maxY) {
		if (!Body.clamp) return; // Objects will fly away
		
		switch(clampType) {
		
			case 0: // Loops through the screen
				if(x + radius < minX) x = maxX + radius;
				if(x - radius > maxX) x = minX - radius;
				if(y + radius < minY) y = maxY + radius;
				if(y - radius > maxY) y = minY - radius;
				break;
			case 1: // Bounces off walls
				if(x - radius <= minX) { x = minX + radius; xVel *= -bounciness; } 
				else if(x + radius >= maxX) { x = maxX - radius; xVel *= -bounciness; }
				if(y - radius <= minY) { y = minY + radius; yVel *= -bounciness; }
				else if(y + radius >= maxY) { y = maxY - radius; yVel *= -bounciness; }
				break;
			default: // Stops at edge of screen
				if(x - radius < minX) x = minX + radius;
				if(x + radius > maxX) x = maxX - radius;
				if(y - radius < minY) y = minY + radius;
				if(y + radius > maxY) y = maxY - radius;
		}
	}
	
	public void tick() {
		if(release) {
			release = false;
			heldBody = null;
		}
		if(stationary) return;
		
		// Changes position based on velocity
		long dt = SolarSystemState.getTimeIncrement();
		x = x + xVel * dt + xAcc * Math.pow(dt, 2) / 2;
		y = y + yVel * dt + yAcc * Math.pow(dt, 2) / 2;
		
		xVel += xAcc * dt;
		yVel += yAcc * dt;
	}
	
	// Used to create basic shadow effects on bodies
	private void shadows(Graphics2D g2d, AffineTransform at) {
		// Rotates the shadow to be opposite of where the light is
		at.rotate(getAngleTo(Body.mainBody) - Math.PI/2, 32, 32);
		g2d.drawImage(Resource.shadow, at, null);
	}
	
	private void drawSelection(Graphics2D g2d, Camera camera) {
		AffineTransform at = new AffineTransform();
		at.translate(x - radius - 3 - camera.getX(), y - radius - 3 - camera.getY());
		at.scale((radius*camera.getZoom()+3)/32, (radius*camera.getZoom()+3)/32);
		g2d.drawImage(Resource.cyanSelector, at, null);
	}
	
	public void draw(Graphics g, Camera camera) {
		Graphics2D g2d = (Graphics2D) g;
		
		AffineTransform at = new AffineTransform();
		if(Body.selectedBody == this && SolarSystemState.getCursorMode() == CursorMode.select) {
			drawSelection(g2d, camera);
		}
		
		double xChange = x*camera.getZoom()-radius*camera.getZoom() - camera.getX();
		double yChange = y*camera.getZoom()-radius*camera.getZoom() - camera.getY();
		double rChange = radius*camera.getZoom();
		
		// Moves the transform to the x and y location and moves the image back half step
		at.translate(xChange, yChange);
		// Images are 64 pixels, dividing by 32 gives proper radius of scaled image
		at.scale(rChange/32, rChange/32);
		
		// Draws the current textured registered
		g2d.drawImage(image, at, null);
		
		g.setColor(Color.white);
		
		g.drawLine((int)(xChange + rChange), (int)(yChange + rChange), (int)xChange - 10, (int)yChange - 10);
		g.drawString(name, (int)xChange - 10, (int)yChange - 20);
		
		
		// toggle shadows
		if(shadows && !emitsPhotons) shadows( g2d, at );
		
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
		
		if(!showVectors) return;
		
		double xVel = this.xVel;
		double yVel = this.yVel;
		if(selectedBody != null) {
			double[] velDiff = getRelativeVelocity(selectedBody);
			xVel = velDiff[0];
			yVel = velDiff[1];
		}
		g.setColor(Color.green);
		g.drawLine((int)x-camera.getX(), (int)y-camera.getY(), (int)(x-camera.getX()+xVel*50), (int)(y-camera.getY()+yVel*50));
		
		g.setColor(Color.red);
		g.drawLine((int)x-camera.getX(), (int)y-camera.getY(), (int)(x-camera.getX()+xAcc*800), (int)(y-camera.getY()+yAcc*800));
	}
	
	// Getters and setters
	
	// Returns valuable information about the planets
	public String getStats() {
		double velocity = Math.abs(xVel) + Math.abs(yVel);
		// Prints the name along with velocity 
		// TODO Fix the display of stats
		return name + ": " 
					+ String.format("%.3f", mass*1e-24) + "10^24 kg " 
					+ String.format("%.1f", velocity*1e-3) + "km/s " 
					+ String.format("%.2f", (getDistTo((Body.selectedBody != null) ? Body.selectedBody : Body.mainBody)) / 1e9) + "10^6 km";
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
	}
	
	// Sets the acceleration
	public void setAcc(double xAcc, double yAcc) {
		this.xAcc = xAcc;
		this.yAcc = yAcc;
	}
	
	// Sets the image
	public void setImage(BufferedImage image) {
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
	
	// Used with shadows thinking of removing it
	public void toggleEmitter() {
		emitsPhotons = !emitsPhotons;
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
}
