package main;

import main.states.SolarSystemState;
import main.states.State;

public class Main {
	
	private static GameEngine engine;
	
	public static void main(String[] args) {
		engine = new GameEngine();
		State.currentState = new SolarSystemState(engine);
	}
}
