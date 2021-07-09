package LCD;

import com.phidget22.TemperatureSensor;
import java.util.ArrayList;

public class LCDMain {
	      
	public static void main(String args[]) throws Exception{
		
		TemperatureSensor temperatureSensor = new TemperatureSensor();
		temperatureSensor.open(1000);
		
		//First argument is the number of data points displayed at 1 time on the graph
		LCDPlot test = new LCDPlot(12, true);
		Record recordData = new Record();
		
		System.out.println("Start");
		
		while(true) {
			
			recordData.getData().add(temperatureSensor.getTemperature());
			test.getData().add(temperatureSensor.getTemperature());
			recordData.writeValue(); 
			test.start();
			Thread.sleep(350);
		
		}

	}
       
}
