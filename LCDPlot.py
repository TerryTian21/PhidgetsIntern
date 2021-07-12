from Phidget22.Devices.LCD import *
import datetime

data = []
pixelData = []
numPoints = 0
maxTemp = 0
minTemp = 0
lcd = LCD()
record = True


class LCDPlot:
    name = ""
    counting = 0
    count = 0

    # Constructor that records the data passed by user
    def __init__(self, dataPoints, recordData):

        lcd.openWaitForAttachment(1000)

        global record, numPoints
        numPoints = dataPoints
        record = recordData

        if record:
            currentTime = datetime.datetime.now()
            self.name = currentTime.strftime("%m_%d_%Y %H.%M.%S")

    # Main function that runs all the other functions
    def start(self):

        if record:
            self.writeFile()

        self.display()
        self.yScaling()
        self.xScaling()
        self.graph()

    # Gets temp from temperature sensor
    @staticmethod
    def setDataPoint(num):

        if len(data) >= numPoints:
            data.pop(0)

        data.append(num)

    # Displays elements of graph
    @staticmethod
    def display():

        lcd.drawLine(20, 11, 20, 56)
        lcd.drawLine(20, 56, 127, 56)

        global maxTemp, minTemp
        maxTemp = max(data)
        minTemp = min(data)

        lcd.writeText(LCDFont.FONT_5x8, 1, 1, "Min: " + str(round(minTemp, 1)))
        lcd.writeText(LCDFont.FONT_5x8, 51, 1, "Max: " + str(round(maxTemp, 1)))
        lcd.writeText(LCDFont.FONT_6x12, 101, 1, str(round(data[len(data) - 1], 1)))

    # Auto scales x-axis
    @staticmethod
    def xScaling():

        scale = round(107 / (numPoints - 1))
        i = 20 + scale
        while i < 127:
            lcd.drawLine(i, 56, i, 58)
            i += scale

    # Auto scales y-axis
    @staticmethod
    def yScaling():

        temp = max(data)
        scale = ((maxTemp - minTemp) / 5)
        i = 11
        while i < 56:
            lcd.drawLine(20, i, 21, i)
            lcd.writeText(LCDFont.FONT_5x8, 0, i, str(round(temp, 1)))
            temp -= scale
            i += 9

    # Draws the graph
    @staticmethod
    def graph():

        size = maxTemp - minTemp
        scale = round(107 / (numPoints - 1))

        if size == 0:
            size = 2

        for i in range(len(data)):
            pixel = 56 - (data[i] - minTemp) / size * 45
            pixelData.append(round(pixel))

        temp2 = 20

        for i in range(len(data)):
            if i > 0:
                lcd.drawLine(temp2 - scale, pixelData[i - 1], temp2, pixelData[i])
            temp2 += scale

        pixelData.clear()
        lcd.flush()
        lcd.drawRect(21, 11, 127, 55, True, True)

    # Records the temp into a file
    def writeFile(self):

        file = open(self.name + ".csv", "a")

        if self.count == 0:
            file.write("data points" + "," + "temperature \n")

        if self.counting >= numPoints:
            self.counting = numPoints - 1

        file.write(str(self.count) + "," + str(data[self.counting]) + "\n")
        self.count += 1

        self.counting += 1

        file.close()
