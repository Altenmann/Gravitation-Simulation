package main.buttons;

import main.Resource;
import main.states.SolarSystemState;

public class VectorButton extends Button {
	
	private boolean vectors;

	public VectorButton(int x, int y, int width, int height, boolean vectors) {
		super(x, y, width, height);

		this.vectors = vectors;
		setImage();
	}
	
	public void setImage() {
		if(vectors) image = Resource.vectorOn;
		else image = Resource.vectorOff;
	}

	@Override
	public void onClick() {
		SolarSystemState.vectorSwitch();
		vectors = !vectors;
		setImage();
	}

	@Override
	public void onHover() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void offHover() {
		// TODO Auto-generated method stub
		
	}

}
