package main.buttons;

import main.Resource;
import main.states.SolarSystemState;

public class AddBodyButton extends Button {
	
	// Used to add a body to the simulation
	// TODO: Customization menu

	public AddBodyButton(int x, int y, int width, int height) {
		super(x, y, width, height);
		
		image = Resource.addButton;
	}

	@Override
	public void onClick() {
		SolarSystemState.addBlackHole();
	}

	@Override
	public void onHover() {
		
	}

	@Override
	public void offHover() {
		
	}

}
