using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using Phidget22;

namespace PhidgetsIntern
{
    class LCDPlot
    {
        public static List<Double> data = new List<Double>();
        static List<int> pixelData = new List<int>();
        public static int numPoints;
        double maxTemp;
        double minTemp;
        LCD lcd;
        Boolean recordData;
        Record writeData;
        int LCD_WIDTH = 127;
        int Graph_WIDTH = 107;
        int Graph_HEIGHT = 45;

        public LCDPlot()
        {

        }

        // Constructor that recrods data passed by user
        public LCDPlot(int dataPoints, Boolean recordData)
        {
            numPoints = dataPoints;
            lcd = new LCD();
            lcd.Open(1000);

            this.recordData = recordData;

            if (recordData)
                writeData = new Record();
        }

        // Main function that runs all other functions
        public void start()
        {
            if (recordData)
                writeData.writeData();

            Display();
            XScaling();
            YScaling();
            Graph();

        }

        // Gets temp from temp sensor
        public void addDataPoint(double num)
        {
            // Limits array size to the number of points displayed on the screen
            if (data.Count() >= numPoints)
                data.RemoveAt(0);

            data.Add(num);
            start();

        }

        //Displays elements of graph
        private void Display()
        {
            // This initializes the graph axis
            lcd.DrawLine(20, 11, 20, 56);
            lcd.DrawLine(20, 56, LCD_WIDTH, 56);

            maxTemp = data.Max();
            minTemp = data.Min();

            // Writes Max, Min and Current Temp
            lcd.WriteText(LCDFont.Dimensions_5x8, 1, 1, "Min: " + minTemp.ToString("0.0"));
            lcd.WriteText(LCDFont.Dimensions_5x8, 51, 1, "Max: " + maxTemp.ToString("0.0"));
            lcd.WriteText(LCDFont.Dimensions_6x12, 101, 1, data[data.Count - 1].ToString("0.0"));

        }

        // Scales x-axis
        private void XScaling()
        {
            double temp = Graph_WIDTH / (numPoints - 1);
            int scale = (int)Math.Round(temp);

            // Draws ticks on x-axis |The graph starts at pixel 20, and draws a vertical
            // tick of width 2 across the x-axis for each data point
            for (int i = 20 + scale; i<LCD_WIDTH; i += scale)
            {
                lcd.DrawLine(i, 56, i, 58);
            }


        }

        // Scales y-axis
        private void YScaling()
        {
            double temp = data.Max();
            double scale = (maxTemp - minTemp) / 5;

            // Draws ticks and numbers | Starts on pixel 11 and moves down 9 pixels at a
            // time. At each increment, a small visible line is drawn to indicate the
            // location as well as a numeric value is printed out on the screen.
            for (int i =11; i <= 56; i+=9)
            {
                lcd.DrawLine(20, i, 21, i);
                lcd.WriteText(LCDFont.Dimensions_5x8, 0, i, temp.ToString("0.0"));
                temp -= scale;
            }


        }

        //Draws the graph
        private void Graph()
        {
            double temp = (Graph_WIDTH/(numPoints -1));
            double size = maxTemp - minTemp;
            int scale = (int)Math.Round(temp);

            if (size == 0)
                size = 2;

            // Changes the pixel location of the data based on min and max data points |
            // Math: Let x be the pixel location of the data point. x/Graph_Height in pixels
            // = (data point - min temp)/temp range.
            // Solving for x will result in a ratio where the pixel location simulates the
            // distribution of the temperature
            for (int i =0; i<data.Count; i++)
            {
                double pixel = 56 - (data[i] - minTemp) / size * Graph_HEIGHT;
     
                pixelData.Add((int)Math.Round(pixel));
            }

            // Temp 2 gives the pixel for the first time data point
            int temp2 = 20;

            // Graphs data | The LCD will draw a line between the pixel of the previous data
            // point and the current data point
            for (int i =0; i <data.Count; i++)
            {
                if (i > 0)
                    lcd.DrawLine(temp2 - scale, pixelData[i - 1], temp2, pixelData[i]);

                temp2 += scale;
            }

            pixelData.Clear();
            lcd.Flush();

            // Clears the space of the graph essential refreshing the screen every time a
            // new data point is added
            lcd.DrawRectangle(21, 11, LCD_WIDTH, 55, true, true);
        }

    }

    // Records temp into a file
    class Record : LCDPlot{

        String name;
        int count = 0;
        int counter = 0;

        public Record()
        {
            name = DateTime.Now.ToString();

            // Can't title files with "/" or ":" so they are replaced by "_" and "."
            name = name.Replace("/", "_");
            name = name.Replace(":", ".");

        }


        public void writeData()
        {
            // Creates a file writer to the csv file
            StreamWriter sw = new StreamWriter(@name + ".csv", true);

            // If it is the first data point, then it titles the columns in excel file
            if (count == 0)
                sw.WriteLine("data points, temperature");

            // Count keeps track of what data point we are on | If count exceeds the total
            // numPoints (i.e. the number of points present in the data array) then we
            // control counter (the index on the most recent data point in the array) to be the last element
            // in the array
            if (count >= numPoints)
                counter = numPoints - 1;

            // Writes data point number and data value to excel file
            sw.WriteLine(count.ToString() + "," + data[counter].ToString());
       
            count++;
            counter++;

            sw.Close();

        }

    }
}
