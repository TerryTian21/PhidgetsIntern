from Phidget22.Devices.LCD import *
import datetime

data = []
pixel_data = []
num_points = 0
max_temp = 0
min_temp = 0
lcd = LCD()
record = True


class LCDPlot:
    name = ""
    counting = 0
    count = 0

    # Constructor that records the data passed by user
    def __init__(self, dataPoints, recordData):

        lcd.openWaitForAttachment(1000)

        global record, num_points
        num_points = dataPoints
        record = recordData

        if record:
            currentTime = datetime.datetime.now()
            self.name = currentTime.strftime("%m_%d_%Y %H.%M.%S")

    # Main function that runs all the other functions
    def start(self):

        if record:
            self.write_file()

        self.display()
        self.y_scaling()
        self.x_scaling()
        self.graph()

    # Gets temp from temperature sensor
    @staticmethod
    def set_data_point(num):

        if len(data) >= num_points:
            data.pop(0)

        data.append(num)

    # Displays elements of graph
    @staticmethod
    def display():

        lcd.drawLine(20, 11, 20, 56)
        lcd.drawLine(20, 56, 127, 56)

        global max_temp, min_temp
        max_temp = max(data)
        min_temp = min(data)

        lcd.writeText(LCDFont.FONT_5x8, 1, 1, "Min: " + str(round(min_temp, 1)))
        lcd.writeText(LCDFont.FONT_5x8, 51, 1, "Max: " + str(round(max_temp, 1)))
        lcd.writeText(LCDFont.FONT_6x12, 101, 1, str(round(data[len(data) - 1], 1)))

    # Auto scales x-axis
    @staticmethod
    def x_scaling():

        scale = round(107 / (num_points - 1))
        i = 20 + scale
        while i < 127:
            lcd.drawLine(i, 56, i, 58)
            i += scale

    # Auto scales y-axis
    @staticmethod
    def y_scaling():

        temp = max(data)
        scale = ((max_temp - min_temp) / 5)
        i = 11
        while i <= 56:
            lcd.drawLine(20, i, 21, i)
            lcd.writeText(LCDFont.FONT_5x8, 0, i, str(round(temp, 1)))
            temp -= scale
            i += 9

    # Draws the graph
    @staticmethod
    def graph():

        size = max_temp - min_temp
        scale = round(107 / (num_points - 1))

        if size == 0:
            size = 2

        for i in range(len(data)):
            pixel = 56 - (data[i] - min_temp) / size * 45
            pixel_data.append(round(pixel))

        temp2 = 20

        for i in range(len(data)):
            if i > 0:
                lcd.drawLine(temp2 - scale, pixel_data[i - 1], temp2, pixel_data[i])
            temp2 += scale

        pixel_data.clear()
        lcd.flush()
        lcd.drawRect(21, 11, 127, 55, True, True)

    # Records the temp into a file
    def write_file(self):

        file = open(self.name + ".csv", "a")

        if self.count == 0:
            file.write("data points" + "," + "temperature \n")

        if self.counting >= num_points:
            self.counting = num_points - 1

        file.write(str(self.count) + "," + str(data[self.counting]) + "\n")
        self.count += 1

        self.counting += 1

        file.close()
