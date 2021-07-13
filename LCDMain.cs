// Import Phidgets Library
using Phidget22;

// Import Libraries used in Program
using System;
using System.Threading;

namespace TemperatureGraph

{

    class LCDMain
    {

        public static void Main(String [] args)
        {

            // Create temperature sensor
            TemperatureSensor temperatureSensor = new TemperatureSensor();

            // Open
            temperatureSensor.Open(1000);


            // Creating a new object which will plot the data from the temperature sensor
            // First argument is the number of data points displayed at 1 time on the graph
            // Second argument is whether user wants to record from sensor to a file
            LCDPlot graph = new LCDPlot(12, true);

            //Indication that program has started
            Console.WriteLine("Start");


            // Continuous loop which collects the temperature every 250 milliseconds
            // Data is then passed to the test class and graphed/logged in a file
            while (true)
            {
                graph.addDataPoint(temperatureSensor.Temperature);
                graph.start();
                Thread.Sleep(250);

            }
        }


    }


}
