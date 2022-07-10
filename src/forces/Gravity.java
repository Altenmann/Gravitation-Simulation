package forces;

import java.util.ArrayList;

import math.Maths;
import objects.Body;

// Used to simulate gravity given a set of bodies
public class Gravity {
	
	public static void Gravitate(ArrayList<Body> bodies) {
		
		// Gravitational constant
		double G = .1;
		
		// X and Y ratios given x dist over total dist
		double xRatio, yRatio;
		
		// The variables used to determine the acceleration applied to given bodies
		double xdiff, ydiff, dist, tdiff, xAcc, yAcc, acc;
		
		// TODO: Add a more efficient implementation of gravitational forces
		
		// Loops through all bodies
		for (Body b1: bodies) {
			// Sets the acceleration back to 0 for each body
			xAcc = 0; 
			yAcc = 0;
			
			// Loops through all bodies and applies the given acceleration
			for (Body b2: bodies) {
				if (b1==b2) continue; // Skips itself (b1)
				
				// Finds the x and y distances
				xdiff = b2.getX() - b1.getX();
				ydiff = b2.getY() - b1.getY();
				
				// Finds the distance
				dist = Maths.dist(xdiff, ydiff);
				
				// Takes the total of absolute value of the x and y distance
				tdiff = Math.abs(xdiff) + Math.abs(ydiff);
				
				// Ratio of x and y distance of total distance
				xRatio = xdiff / tdiff;
				yRatio = ydiff / tdiff;
				
				// Acceleration to be applied to b1 from b2
				acc = G * b2.getMass() / Math.pow(dist, 2);
				
				// Adds the acceleration to the total
				xAcc += acc * xRatio;
				yAcc += acc * yRatio;
			}
			
			// Sets the acceleration of the body
			b1.setAcc(xAcc, yAcc);
		}
	}

}
