package LCD;

import com.phidget22.TemperatureSensor;
import java.util.ArrayList;

public class LCDMain {
	      
	public static void main(String args[]) throws Exception{
		
		TemperatureSensor temperatureSensor = new TemperatureSensor();
		temperatureSensor.open(1000);
		
		//First argument is the number of data points displayed at 1 time on the graph
		LCDPlot test = new LCDPlot(12, true);
		
		System.out.println("Start");
		
		while(true) {
			
			test.getData().add(temperatureSensor.getTemperature());
			test.start();
			Thread.sleep(350);
		
		}

	}
       
}
