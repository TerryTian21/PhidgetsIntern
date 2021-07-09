package LCD;

import java.util.ArrayList;
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
	int time = 1;
    int counter = 0;
    int count;
    Double temp;
    double scale;
    double maxTemp = 0;
    double minTemp = 0;
    LCD lcd;
    boolean recordData;
    Record writeData;
    
    public LCDPlot() {
    	
    }

    public LCDPlot(int dataPoints, boolean recordData) throws PhidgetException{
    	
    	numPoints = dataPoints;
    	lcd = new LCD();
    	lcd.open(1000);
    	this.recordData = recordData;
    	if(recordData)
    		writeData = new Record();
	}
    
	public void start() throws PhidgetException, IOException{

		if(recordData)
			writeData.writeValue();
    	if(data.size()>numPoints) {
    		clear();
    		LCD_Init();
    		displayTemp(counter);
    		yScaling();
    		xScaling();
    		graph();
    		lcd.flush();
    		counter++;
    	}
    	
    }
	
    public ArrayList<Double> getData() {
		return data;
	}

	public void setData(ArrayList<Double> data) {
		this.data = data;
	}

	private void LCD_Init() throws PhidgetException {

            //This initializes the graph axis
            lcd.drawLine(20, 11, 20, 56);
            lcd.drawLine(20, 56, 127, 56);
  
    }
    
    private void displayTemp(int counter) throws PhidgetException{
    	
    	lcd.drawRect(0, 0, 36, 10, false, false);
		lcd.drawRect(41, 0, 81, 10, false, false);
		lcd.drawRect(86, 0, 126, 10, false, false);
    	
		maxTemp = getMax(counter);
		minTemp = getMin(counter);
    	//Writes Max and Min Temp
		lcd.writeText(LCDFont.DIMENSIONS_5X8, 1, 1, "Cu " + String.valueOf(df.format(data.get(data.size()-1))));
		lcd.writeText(LCDFont.DIMENSIONS_5X8, 42, 1, "Hi " + String.valueOf(df.format(maxTemp)));
		lcd.writeText(LCDFont.DIMENSIONS_5X8, 87, 1, "Lo " +String.valueOf(df.format(minTemp)));
	
		//TODO: Get current data;
    }
    
    private void clear() throws PhidgetException {
    	
    	lcd.drawRect(21, 11, 127, 55, true, true);
    }
    
    private void yScaling() throws PhidgetException {
    	
    	temp = getMax(counter);
		scale = ((maxTemp-minTemp)/5);

		for(int i = 11; i<=56; i+= 9) {
			lcd.drawLine(20,i,21,i);
			lcd.writeText(LCDFont.DIMENSIONS_5X8, 0, i, Double.toString(Double.valueOf(df.format(temp))));
			temp -= scale;
		}

    }
    
    private void xScaling() throws PhidgetException {
    	
    	int scale = Math.round(107/(numPoints-1));
    	
		for(int i = 20 + scale; i<127; i+= scale)
			lcd.drawLine(i,56,i,58);
    			
    }
    
    private void graph() throws PhidgetException {
	
    	double range = maxTemp - minTemp;
		int scale = Math.round(107/(numPoints-1));
		
		//Changes the pixel location of the temp based on min and max data points
		for(int i =0; i<numPoints; i++) {
			double pixel = 56-(data.get(counter + i)-minTemp)/range*45;
			pixelData.add((int)Math.round(pixel));
		}
		//Temp 2 gives the pixel for the first time data point
		int temp2 = 20 ;
		
		//Graphs data
		for(int i = 0; i<numPoints; i++) {
			
			lcd.drawPixel(temp2, pixelData.get(i), LCDPixelState.ON);
			if(i >0)
				lcd.drawLine(temp2-scale, pixelData.get(i -1), temp2, pixelData.get(i));
		
			temp2 += scale;
		}
	
		pixelData.clear();
    }
    
    private static double getMax(int counter) {
		
		double max = 0;
		
		for(int i = counter; i < counter +numPoints; i++) {		
			if(data.get(i) > max)
				max = data.get(i);
		}
		
		return max;
		
	}
	 
	private static double getMin(int counter) {
		
		double min = data.get(counter);
		
		for(int i = counter; i < counter + numPoints; i++)
			if(data.get(i) < min)
				min = data.get(i);
		
		return min;
	}	
	
	class Record extends LCDPlot {

		String name;
		
		public Record() {
			
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
			LocalDateTime now = LocalDateTime.now();  
			name = dtf.format(now);
			name = name.replace("/", "_");
			name = name.replace(":", ".");
		}

		public void writeValue() throws IOException {
			
			FileWriter outfile =  new FileWriter(name + ".csv");
		   
			int count = 0;
			
			outfile.write("Data point");
			outfile.write("Temperature \n");
			
		
			while(count < data.size()) {
				String num = Double.toString(data.get(count));
				outfile.write(Integer.toString(count) + "," + num);
				outfile.write("\n");
				count++;
			}
			
			outfile.close();
				
		
		}

	}
	
		
    public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

   public double getMaxTemp() {
		return maxTemp;
	}

	public void setMaxTemp(double maxTemp) {
		this.maxTemp = maxTemp;
	}

	public double getMinTemp() {
		return minTemp;
	}

	public void setMinTemp(double minTemp) {
		this.minTemp = minTemp;
	}
		
	
}



