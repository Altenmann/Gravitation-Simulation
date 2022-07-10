package main.buttons;

import main.Resource;
import main.states.SolarSystemState;

public class PlayPauseButton extends Button {
	
	private boolean paused;

	public PlayPauseButton(int x, int y, int width, int height, boolean paused) {
		super(x, y, width, height);
		this.paused = paused;
		
		setImage();
	}
	
	private void setImage() {
		if(paused) image = Resource.pauseButton;
		else image = Resource.playButton;
	}

	@Override
	public void onClick() {
		paused = !paused;
		
		setImage();
		
		SolarSystemState.pauseSwitch();
	}

	@Override
	public void onHover() {
		if(paused) image = Resource.playButton;
		else image = Resource.pauseButton;
	}

	@Override
	public void offHover() {
		setImage();
	}

}
