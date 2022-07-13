package main.buttons;

import main.states.SolarSystemState;
import objects.Body;

public class BodyButton extends Button {
	
	private Body b;

	public BodyButton(int x, int y, int width, int height, Body b) {
		super(x, y, width, height);
		this.b = b;
		
		image = b.getResource();
	}

	@Override
	public void onClick() {
		Body.selectedBody = b;
		SolarSystemState.center(b);
	}

	@Override
	public void onHover() {
		SolarSystemState.center(b);
	}

	@Override
	public void offHover() {
		//SolarSystemState.center((Body.selectedBody != null) ? Body.selectedBody : Body.mainBody);
	}

}
