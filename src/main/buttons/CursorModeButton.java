package main.buttons;

import main.Resource;
import main.states.SolarSystemState;

public class CursorModeButton extends Button {

	public CursorModeButton(int x, int y, int width, int height) {
		super(x, y, width, height);
		
		setCursorImage();
	}
	
	private void setCursorImage() {
		switch(SolarSystemState.getCursorMode()) {
		case select:
			image = Resource.cursorSelector;
			break;
		case hand:
			image = Resource.cursorHand;
			break;
		default:
			image = Resource.cursorSelector;
		}
	}
	
	public void setImage() {
		
	}

	@Override
	public void onClick() {
		SolarSystemState.switchCursorMode();
		setCursorImage();
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
