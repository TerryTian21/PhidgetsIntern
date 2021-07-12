package LCD;

import java.util.ArrayList;
import java.util.Collections;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.phidget22.*;

public class LCDPlot {
	
	static ArrayList<Double> data = new ArrayList<Double>();;
	ArrayList<Integer> pixelData = new ArrayList<Integer>();
    DecimalFormat df = new DecimalFormat("0.0");
    static int numPoints;
    int counter = 0;
    Double temp;
    double scale;
    double maxTemp = 0;
    double minTemp = 0;
    LCD lcd;
    boolean recordData;
    Record writeData;
    int total;
    
    public LCDPlot() {
    	
    }

    //Contructor that records the data passed by user
    public LCDPlot(int dataPoints, boolean recordData, int total) throws PhidgetException, IOException{
    	
    	numPoints = dataPoints;
    	lcd = new LCD();
    	lcd.open(1000);
    	
    	this.recordData = recordData;
    
    	this.total = total;
    	if(recordData)
    		writeData = new Record();
	}
    
    //Main function that runs all the other functions
	public boolean start() throws PhidgetException, IOException{
		
		if(counter < total) {
			
			if(recordData)
				writeData.writeValue();
			
    		display(counter);
    		yScaling();
    		xScaling();
    		graph();
    		counter++;
	    	
	    	return true;
		}
		
		else
			return false;
    }
	
	
	//Gets temp from temperature sensor
	public void getData(double num) {
		
		if(data.size()>= numPoints)
			data.remove(0);
		
		data.add(num);
		
	}
	
	//Displays elements of graph
    private void display(int counter) throws PhidgetException{
    	
    	//This initializes the graph axis
        lcd.drawLine(20, 11, 20, 56);
        lcd.drawLine(20, 56, 127, 56);
    	
        //Draws boxes for current, max and min temp
    	lcd.drawRect(0, 0, 36, 10, false, false);
		lcd.drawRect(41, 0, 81, 10, false, false);
		lcd.drawRect(86, 0, 126, 10, false, false);
    	
		maxTemp = Collections.max(data);
		minTemp = Collections.min(data);
    	
		//Writes Max and Min Temp
		lcd.writeText(LCDFont.DIMENSIONS_5X8, 1, 1, "Cu " + String.valueOf(df.format(data.get(data.size()-1))));
		lcd.writeText(LCDFont.DIMENSIONS_5X8, 42, 1, "Hi " + String.valueOf(df.format(maxTemp)));
		lcd.writeText(LCDFont.DIMENSIONS_5X8, 87, 1, "Lo " +String.valueOf(df.format(minTemp)));
    }
    
    //Auto scales y-axis
    private void yScaling() throws PhidgetException {
    	
    	temp = Collections.max(data);
		scale = ((maxTemp-minTemp)/5);

		for(int i = 11; i<=56; i+= 9) {
			lcd.drawLine(20,i,21,i);
			lcd.writeText(LCDFont.DIMENSIONS_5X8, 0, i, Double.toString(Double.valueOf(df.format(temp))));
			temp -= scale;
		}

    }
    
    //Auto scales x-axis
    private void xScaling() throws PhidgetException {
    	
    	int scale = Math.round(107/(numPoints-1));
    	
		for(int i = 20 + scale; i<127; i+= scale)
			lcd.drawLine(i,56,i,58);
    			
    }
    
    //Draws the graph
    private void graph() throws PhidgetException {
	
    	double range = maxTemp - minTemp;
		int scale = Math.round(107/(numPoints-1));
		
		//Changes the pixel location of the temp based on min and max data points
		for(int i =0; i<data.size(); i++) {
			double pixel = 56-(data.get(i)-minTemp)/range*45;
			pixelData.add((int)Math.round(pixel));
		}
	
		//Temp 2 gives the pixel for the first time data point
		int temp2 = 20 ;
		
		//Graphs data
		for(int i = 0; i<data.size(); i++) {
			if(i >0)
				lcd.drawLine(temp2-scale, pixelData.get(i -1), temp2, pixelData.get(i));
		
			temp2 += scale;
		}
	
		pixelData.clear();
		lcd.flush();
    	lcd.drawRect(21, 11, 127, 55, true, true);
    }
    	
	
    //Records the temp into a file
	class Record extends LCDPlot {

		String name;
		FileWriter outfile;
		int count= 0;
		int counter = 0;
		
		public Record() throws IOException {
		
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
			LocalDateTime now = LocalDateTime.now();  
			name = dtf.format(now);
			name = name.replace("/", "_");
			name = name.replace(":", ".");

		}

		public void writeValue() throws IOException {
			
			outfile =  new FileWriter(name + ".csv",true);
			
			if(count == 0)	
				outfile.write("data point, temperature \n");

			String num = Double.toString(data.get(this.counter));
			outfile.write(Integer.toString(count) + "," + num);
			outfile.write("\n");
			count++;
			
			if(count >= numPoints)
				this.counter = numPoints -1;
			else
				this.counter++;	
			
			outfile.close();

		}
	

	}
		
}


