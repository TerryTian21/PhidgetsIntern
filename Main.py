from Phidget22.Devices.TemperatureSensor import *
from LCDPlot import LCDPlot
import time


def main():
    temperatureSensor = TemperatureSensor()
    temperatureSensor.openWaitForAttachment(1000)

    test = LCDPlot(10, True)

    print("start")

    while True:
        test.setDataPoint(temperatureSensor.getTemperature())
        test.start()
        time.sleep(.250)


if __name__ == '__main__':
    main()
