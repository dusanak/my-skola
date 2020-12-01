import random

class Client:
    def sum(self, data):
        sum = 0
        for i in data:
            sum += data
        return sum

class Supplier:
    def seed(self, seed):
        random.seed(seed)

    def supplyRandomData(self, number):
        data = [random.randint(1, 10) for i in range(number)]
        return data

    def createLineStrip(self, number):
        data = self.supplyRandomData(number)
        return LineStrip(data)

class LineStrip:
    def __init__(self, data):
        self.lines = []

        for index, value in enumerate(data[:-1]):
            self.lines.append((value, data[index+1]))

