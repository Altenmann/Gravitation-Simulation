package main.buttons;

import main.Resource;
import main.states.SolarSystemState;

public class ResetButton extends Button {

	public ResetButton(int x, int y, int width, int height) {
		super(x, y, width, height);
		
		image = Resource.whiteCircleArrow;
	}

	@Override
	public void onClick() {
		SolarSystemState.reset();
	}
	
	public void onHover() {
		image = Resource.redCircleArrow;
	}

	@Override
	public void offHover() {
		image = Resource.whiteCircleArrow;
	}

}
