package LCD;

import java.io.FileWriter;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

import com.phidget22.LCD;
import com.phidget22.LCDFont;

public class LCDPlot {

	static ArrayList<Double> data = new ArrayList<Double>();
	ArrayList<Integer> pixelData = new ArrayList<Integer>();
	DecimalFormat df = new DecimalFormat("0.0");
	static int numPoints;
	double maxTemp = 0;
	double minTemp = 0;
	LCD lcd;
	boolean recordData;
	Record writeData;
	int LCD_WIDTH = 127;
	int Graph_WIDTH = 107;
	int Graph_HEIGHT = 45;

	public LCDPlot() {

	}

	// Constructor that records the data passed by user
	public LCDPlot(int dataPoints, boolean recordData) throws Exception {

		numPoints = dataPoints;
		lcd = new LCD();
		lcd.open(1000);

		this.recordData = recordData;

		if (recordData)
			writeData = new Record();
	}

	// Main function that runs all the other functions
	public void start() throws Exception {

		if (recordData)
			writeData.writeValue();

		display();
		yScaling();
		xScaling();
		graph();

	}

	// Gets temp from temperature sensor and records in array
	public void addDataPoint(double num) throws Exception {

		// Limits array size to the number of points displayed on the screen
		if (data.size() >= numPoints)
			data.remove(0);

		data.add(num);
		start();
	}

	// Displays elements of graph
	private void display() throws Exception {

		// This initializes the graph axis
		lcd.drawLine(20, 11, 20, 56);
		lcd.drawLine(20, 56, 127, 56);

		maxTemp = Collections.max(data);
		minTemp = Collections.min(data);

		// Writes Max, Min and Current Temp
		lcd.writeText(LCDFont.DIMENSIONS_5X8, 1, 1, "Min: " + String.valueOf(df.format(minTemp)));
		lcd.writeText(LCDFont.DIMENSIONS_5X8, 51, 1, "Max: " + String.valueOf(df.format(maxTemp)));
		lcd.writeText(LCDFont.DIMENSIONS_6X12, 101, 1, String.valueOf(df.format(data.get(data.size() - 1))));
	}

	// Auto scales y-axis
	private void yScaling() throws Exception {

		Double temp = Collections.max(data);
		Double scale = ((maxTemp - minTemp) / 5);

		// Draws ticks and numbers | Starts on pixel 11 and moves down 9 pixels at a
		// time. At each increment, a small visible line is drawn to indicate the
		// location as well as a numeric value is printed out on the screen.
		for (int i = 11; i <= 56; i += 9) {
			lcd.drawLine(20, i, 21, i);
			lcd.writeText(LCDFont.DIMENSIONS_5X8, 0, i, Double.toString(Double.valueOf(df.format(temp))));
			temp -= scale;
		}

	}

	// Auto scales x-axis
	private void xScaling() throws Exception {

		// Draws ticks on x-axis |The graph starts at pixel 20, and draws a vertical
		// tick of width 2 across the x-axis for each data point
		int scale = Math.round(Graph_WIDTH / (numPoints - 1));

		for (int i = 20 + scale; i < LCD_WIDTH; i += scale)
			lcd.drawLine(i, 56, i, 58);

	}

	// Draws the graph
	private void graph() throws Exception {

		double range = maxTemp - minTemp;
		int scale = Math.round(Graph_WIDTH / (numPoints - 1));

		// Changes the pixel location of the data based on min and max data points |
		// Math: Let x be the pixel location of the data point. x/Graph_Height in pixels
		// = (data point - min temp)/temp range.
		// Solving for x will result in a ratio where the pixel location simulates the
		// distribution of the temperature
		for (int i = 0; i < data.size(); i++) {
			double pixel = 56 - (data.get(i) - minTemp) / range * Graph_HEIGHT;
			pixelData.add((int) Math.round(pixel));
		}

		// Temp 2 gives the pixel for the first time data point
		int temp2 = 20;

		// Graphs data | The LCD will draw a line between the pixel of the previous data
		// point and the current data point
		for (int i = 0; i < data.size(); i++) {
			if (i > 0)
				lcd.drawLine(temp2 - scale, pixelData.get(i - 1), temp2, pixelData.get(i));

			temp2 += scale;
		}

		pixelData.clear();
		lcd.flush();

		// Clears the space of the graph essential refreshing the screen every time a
		// new data point is added
		lcd.drawRect(21, 11, LCD_WIDTH, 55, true, true);
	}

	// Records the temp into a file
	class Record extends LCDPlot {

		String name;
		FileWriter outfile;
		int count = 0;
		int counter = 0;

		public Record() throws Exception {

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			name = dtf.format(now);

			// Can't title files with "/" or ":" so they are replaced by "_" and "."
			name = name.replace("/", "_");
			name = name.replace(":", ".");

		}

		public void writeValue() throws Exception {

			// Creates a file writer to the csv file
			outfile = new FileWriter(name + ".csv", true);

			// If it is the first data point, then it titles the columns in excel file
			if (count == 0)
				outfile.write("data point, temperature \n");

			// Count keeps track of what data point we are on | If count exceeds the total
			// numPoints (i.e. the number of points present in the data array) then we
			// control counter (the index on the most recent data point in the array) to be the last element
			// in the array
			if (count >= numPoints)
				this.counter = numPoints - 1;

			// Writes data point number and data value to excel file
			String num = Double.toString(data.get(this.counter));
			outfile.write(Integer.toString(count) + "," + num + "\n");

			count++;
			this.counter++;

			outfile.close();

		}

	}

}
