package test;

public class Tester {
	
	private static int startingValue = 0;
	private static int finalValue = 1;
	private static int increment = 0;
	
	static double startTime;
	static double endTime;
	
	public static double TimeTest() {
		if(startingValue == increment) startTime = System.nanoTime();
		if(finalValue == increment) {
			increment=0;
			endTime = System.nanoTime();
			return (endTime - startTime) / 1e9; // Converted to seconds
		}
		
		increment++;
		return -1;
	}

}
