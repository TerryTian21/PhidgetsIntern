package LCD;

import com.phidget22.TemperatureSensor;

public class LCDMain {

	public static void main(String args[]) throws Exception {

		TemperatureSensor temperatureSensor = new TemperatureSensor();
		temperatureSensor.open(1000);

		// First argument is the number of data points displayed at 1 time on the graph,
		// second is whether user wants to file stream, third is how many data points
		// user wants to collect total
		LCDPlot test = new LCDPlot(12, true);

		System.out.println("Start");

		while (true) {

			test.addDataPoint(temperatureSensor.getTemperature());
			test.start();
			Thread.sleep(250);
		}

	}

}
