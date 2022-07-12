package main.buttons;

import main.Resource;
import main.states.SolarSystemState;

public class ClampButton extends Button {

	public ClampButton(int x, int y, int width, int height) {
		super(x, y, width, height);
		
		image = Resource.ball;
	}

	@Override
	public void onClick() {
		SolarSystemState.clampSwitch();
	}

	@Override
	public void onHover() {}

	@Override
	public void offHover() {}

}
