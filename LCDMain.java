package LCD;

import com.phidget22.TemperatureSensor;
import java.util.ArrayList;

public class LCDMain {
	      
	public static void main(String args[]) throws Exception{
		
		TemperatureSensor temperatureSensor = new TemperatureSensor();
		temperatureSensor.open(1000);
		
		//First argument is the number of data points displayed at 1 time on the graph, second is whether user wants to file stream, third is how many data points user wants to collect total
		LCDPlot test = new LCDPlot(12, true, 100);
		
		boolean keepGoing = true;
		System.out.println("Start");
		
		while(keepGoing) {
			
			test.getData(temperatureSensor.getTemperature());
			keepGoing = test.start();
			Thread.sleep(250);
		}
		
		System.out.println("The program has ended");

	}
       
}

