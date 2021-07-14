# Adding Phidgets Library
from Phidget22.Devices.TemperatureSensor import *
# Importing libraries used in program
from LCDPlot import LCDPlot
import time

# Create temperature sensor
temperatureSensor = TemperatureSensor()

# Open
temperatureSensor.openWaitForAttachment(1000)

# Creating a new object which will plot the data from the temperature sensor
# First argument is the number of data points displayed at 1 time on the graph
# Second argument is whether user wants to record from sensor to a file
graph = LCDPlot(10, True)

# Indication that program has started
print("start")

# Continuous loop which collects the temperature every 250 milliseconds
# Data is then passed to the test class and graphed/logged in file
while True:
    graph.set_data_point(temperatureSensor.getTemperature())
    time.sleep(temperatureSensor.getDataInterval()/1000)
