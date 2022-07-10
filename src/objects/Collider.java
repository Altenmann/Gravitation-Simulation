package objects;

import java.util.ArrayList;

import math.Maths;

public interface Collider {
	
	public static void checkCollisions(ArrayList<Body> bodies) {
		double xdiff, ydiff, dist;
		Body biggerBody, smallerBody;
		for(Body b1 : bodies) {
			
			for(Body b2 : bodies) {
				if(b1 == b2) continue;
				
				xdiff = b2.getX() - b1.getX();
				ydiff = b2.getY() - b1.getY();
				
				dist = Maths.dist(xdiff, ydiff);
				
				if(dist > b1.getRadius() && dist > b2.getRadius()) continue;
				
				biggerBody = (b1.getMass() > b2.getMass()) ? b1 : b2;
				smallerBody = (biggerBody == b1) ? b2 : b1;
				biggerBody.eat(smallerBody);
			}
		}
	}

}
