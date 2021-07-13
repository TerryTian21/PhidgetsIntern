using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Phidget22;

namespace PhidgetsIntern

{

    class LCDMain
    {

        public static void Main(String [] args)
        {

            TemperatureSensor temperatureSensor = new TemperatureSensor();
            temperatureSensor.Open(1000);

            LCDPlot test = new LCDPlot(12, true);

            Console.WriteLine("Start");

            while (true)
            {
                test.addDataPoint(temperatureSensor.Temperature);
                test.start();
                Thread.Sleep(250);

            }
        }



    }
        
   
}
