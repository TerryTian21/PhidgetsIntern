
//Add Phidgets Library
import com.phidget22.*;

public class LCDMain {

	public static void main(String args[]) throws Exception {
		
		//Create Temperature Sensor
		TemperatureSensor temperatureSensor = new TemperatureSensor();
		
		//Open
		temperatureSensor.open(1000);

		//Creating a new object which will plot the data from the temperature sensor
		// First argument is the number of data points displayed at 1 time on the graph
		// Second argument is whether user wants to record data from sensor to a file
		LCDPlot graph = new LCDPlot(12, true);

		//Indication that program has started
		System.out.println("Start");

		
		//Continuous loop which collects the temperature every 250 milliseconds
		//Data is then passed to the test class and graphed/logged in file
		while (true) {

			graph.addDataPoint(temperatureSensor.getTemperature());
			graph.start();
			Thread.sleep(250);
		}

	}

