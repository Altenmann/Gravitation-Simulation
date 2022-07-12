package main.buttons;

import main.Resource;
import main.states.SolarSystemState;

public class VectorButton extends Button {

	public VectorButton(int x, int y, int width, int height) {
		super(x, y, width, height);

		
		setImage();
	}
	
	public void setImage() {
		if(SolarSystemState.isBodyVectors()) image = Resource.vectorOn;
		else image = Resource.vectorOff;
	}

	@Override
	public void onClick() {
		SolarSystemState.vectorSwitch();
		setImage();
	}

	@Override
	public void onHover() {}

	@Override
	public void offHover() {}

}
