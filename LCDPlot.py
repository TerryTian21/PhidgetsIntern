from Phidget22.Devices.LCD import *
import datetime


class LCDPlot:
    name = ""
    counting = 0
    count = 0
    data = []
    pixel_data = []
    num_points = 0
    max_temp = 0
    min_temp = 0
    lcd = LCD()
    record = True
    LCD_WIDTH = 127
    Graph_WIDTH = 107
    Graph_HEIGHT = 45

    # Constructor that self.self.records the self.self.data passed by user
    def __init__(self, dataPoints, record_data):

        self.lcd.openWaitForAttachment(1000)
        self.num_points = dataPoints
        self.record = record_data

        if self.record:
            currentTime = datetime.datetime.now()
            self.name = currentTime.strftime("%m_%d_%Y %H.%M.%S")

    # Main function that runs all the other functions
    def start(self):

        if self.record:
            self.write_file()

        self.display()
        self.y_scaling()
        self.x_scaling()
        self.graph()

    # Gets temp from temperature sensor and records in array
    def set_data_point(self, num):

        # Limits array size to the number of points displayed on the screen
        if len(self.data) >= self.num_points:
            self.data.pop(0)

        self.data.append(num)
        self.start()

    # Displays elements of graph
    def display(self):

        # This initializes the graph axis
        self.lcd.drawLine(20, 11, 20, 56)
        self.lcd.drawLine(20, 56, 127, 56)

        self.max_temp = max(self.data)
        self.min_temp = min(self.data)

        # Writes Max, Min and Current Temp
        self.lcd.writeText(LCDFont.FONT_5x8, 1, 1, "Min: " + str(round(self.min_temp, 1)))
        self.lcd.writeText(LCDFont.FONT_5x8, 51, 1, "Max: " + str(round(self.max_temp, 1)))
        self.lcd.writeText(LCDFont.FONT_6x12, 101, 1, str(round(self.data[len(self.data) - 1], 1)))

    # Auto scales x-axis
    def x_scaling(self):

        # Draws ticks on x-axis |The graph starts at pixel 20, and draws a vertical
        # tick of width 2 across the x-axis for each data point
        scale = round(self.Graph_WIDTH / (self.num_points - 1))
        i = 20 + scale

        while i < self.LCD_WIDTH:
            self.lcd.drawLine(i, 56, i, 58)
            i += scale

    # Auto scales y-axis
    def y_scaling(self):

        temp = max(self.data)
        scale = ((self.max_temp - self.min_temp) / 5)

        # Draws ticks and numbers | Starts on pixel 11 and moves down 9 pixels at a
        # time. At each increment, a small visible line is drawn to indicate the
        # location as well as a numeric value is printed out on the screen.
        i = 11
        while i <= 56:
            self.lcd.drawLine(20, i, 21, i)
            self.lcd.writeText(LCDFont.FONT_5x8, 0, i, str(round(temp, 1)))
            temp -= scale
            i += 9

    # Draws the graph

    def graph(self):

        size = self.max_temp - self.min_temp
        scale = round(self.Graph_WIDTH / (self.num_points - 1))

        # Ensures that when there is only 1 data point the scale is not 0. | In this case, an arbitrary value for
        # the scale is set so the the pixel location formula will not divide by zero. This is auto adjusted
        # with more data points.
        if size == 0:
            size = 2

        # Changes the pixel location of the data based on min and max data points |
        # Math: Let x be the pixel location of the data point. x/Graph_Height in pixels
        # = (data point - min temp)/temp range.
        # Solving for x will result in a ratio where the pixel location simulates the
        # distribution of the temperature
        for i in range(len(self.data)):
            pixel = 56 - (self.data[i] - self.min_temp) / size * self.Graph_HEIGHT
            self.pixel_data.append(round(pixel))

        # Temp 2 gives the pixel for the first time data point
        temp2 = 20

        # Graphs data | The LCD will draw a line between the pixel of the previous data
        # point and the current data point
        for i in range(len(self.data)):
            if i > 0:
                self.lcd.drawLine(temp2 - scale, self.pixel_data[i - 1], temp2, self.pixel_data[i])
            temp2 += scale

        self.pixel_data.clear()
        self.lcd.flush()

        # Clears the space of the graph essential refreshing the screen every time a
        # new data point is added
        self.lcd.drawRect(21, 11, self.LCD_WIDTH, 55, True, True)

    # self.self.records the temp into a file
    def write_file(self):

        # Creates a file writer to the csv file
        file = open(self.name + ".csv", "a")

        # If it is the first data point, then it titles the columns in excel file
        if self.count == 0:
            file.write("self.data points" + "," + "temperature \n")

        # Count keeps track of what data point we are on | If count exceeds the total
        # numPoints (i.e. the number of points present in the data array) then we
        # control counter (the index on the most recent data point in the array) to be the last element
        # in the array
        if self.counting >= self.num_points:
            self.counting = self.num_points - 1

        # Writes data point number and data value to excel file
        file.write(str(self.count) + "," + str(self.data[self.counting]) + "\n")

        self.count += 1
        self.counting += 1

        file.close()
