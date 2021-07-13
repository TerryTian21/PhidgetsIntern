using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
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

        public LCDPlot()
        {


        }

        public LCDPlot(int dataPoints, Boolean recordData)
        {

            numPoints = dataPoints;
            lcd = new LCD();
            lcd.Open(1000);

            this.recordData = recordData;

            if (recordData)
                writeData = new Record();
        }

        public void start()
        {

            if (recordData)
                writeData.writeData();

            display();
            xScaling();
            yScaling();
            lcd.Flush();
            graph();

        }

        public void addDataPoint(double num)
        {
            if (data.Count() >= numPoints)
                data.RemoveAt(0);

            data.Add(num);

        }

        private void display()
        {

            lcd.DrawLine(20, 11, 20, 56);
            lcd.DrawLine(20, 56, 127, 56);

            maxTemp = data.Max();
            minTemp = data.Min();

            // Writes Max, Min and Current Temp
            lcd.WriteText(LCDFont.Dimensions_5x8, 1, 1, "Min: " + minTemp.ToString("0.0"));
            lcd.WriteText(LCDFont.Dimensions_5x8, 51, 1, "Max: " + maxTemp.ToString("0.0"));
            lcd.WriteText(LCDFont.Dimensions_6x12, 101, 1, data[data.Count - 1].ToString("0.0"));

        }

        private void xScaling()
        {
            double temp = 107 / (numPoints - 1);
            int scale = (int)Math.Round(temp);

            for(int i = 20 + scale; i<127; i += scale)
            {
                lcd.DrawLine(i, 56, i, 58);
            }


        }

        private void yScaling()
        {
            double temp = data.Max();
            double scale = (maxTemp - minTemp) / 5;

            for(int i =11; i <= 56; i+=9)
            {
                lcd.DrawLine(20, i, 21, i);
                lcd.WriteText(LCDFont.Dimensions_5x8, 0, i, temp.ToString("0.0"));
                temp -= scale;
            }


        }


        private void graph()
        {
            double temp = (107/(numPoints -1));
            double size = maxTemp - minTemp;
            int scale = (int)Math.Round(temp);

            if (size == 0)
                size = 2;

            Console.WriteLine(data.Count);

            for (int i =0; i<data.Count; i++)
            {
                double pixel = 56 - (data[i] - minTemp) / size * 45;
     
                pixelData.Add((int)Math.Round(pixel));
            }

           
            int temp2 = 20;

            for(int i =0; i <data.Count; i++)
            {
                if (i > 0)
                    lcd.DrawLine(temp2 - scale, pixelData[i - 1], temp2, pixelData[i]);

                temp2 += scale;
            }

            pixelData.Clear();
            lcd.Flush();
            lcd.DrawRectangle(21, 11, 127, 55, true, true);
        }

    }


    class Record : LCDPlot{

        String name;
        int count = 0;
        int counter = 0;

        public Record()
        {
            name = DateTime.Now.ToString();
            name = name.Replace("/", "_");
            name = name.Replace(":", ".");

        }


        public void writeData()
        {
            StreamWriter sw = new StreamWriter(@name + ".csv", true);

            if (count == 0)
                sw.WriteLine("data points, temperature");
            
            if (count >= numPoints)
                counter = numPoints - 1;

            sw.WriteLine(count.ToString() + "," + data[counter].ToString());
       
            count++;
            counter++;

            sw.Close();

        }

    }
}
