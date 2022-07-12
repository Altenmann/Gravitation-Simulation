package main;

import main.states.GameState;
import main.states.SolarSystemState;
import main.states.State;

public class Main {
	
	private static GameEngine engine;
	
	public static void main(String[] args) {
		engine = new GameEngine();
		SolarSystemState sss = new SolarSystemState(engine);
		GameState gs = new GameState(engine);
		
		State.currentState = sss;
	}
}
