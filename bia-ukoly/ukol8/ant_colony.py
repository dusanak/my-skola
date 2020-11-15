from matplotlib import cm
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import numpy as np
import math
import random
import time

def main():
    ant_colony_travelling_salesman(2, 0, 1000)

def ant_colony_travelling_salesman(dimension, min, max):
    chart = Chart()

    population_size = 20
    generations = 100
    number_of_cities = 20

    cities = [tuple(random.randint(min, max) for i in range(dimension)) for j in range(number_of_cities)]

class Ant:
    def __init__(self):
        self.path = []

    def chooseNextPathSegment(self, map):
        pass    
        

class Chart:
    def __init__(self):
        fig = plt.figure()
        self.ax_current = fig.add_subplot(1, 2, 1)
        self.ax_best = fig.add_subplot(1, 2, 2)
        plt.ion()
        plt.show()

    def draw(self, current_path, best_path):
        self.ax_current.clear()
        self.ax_current.title.set_text('{:5.5f}'.format(current_path.length))
        self.ax_current.scatter(np.array(current_path.path)[:, 0], np.array(current_path.path)[:, 1], c='b')
        self.ax_current.plot(np.array(current_path.path)[:, 0], np.array(current_path.path)[:, 1], c='r')

        self.ax_best.clear()
        self.ax_best.title.set_text('{:5.5f}'.format(best_path.length))
        self.ax_best.scatter(np.array(best_path.path)[:, 0], np.array(best_path.path)[:, 1], c='b')
        self.ax_best.plot(np.array(best_path.path)[:, 0], np.array(best_path.path)[:, 1], c='r')

        plt.draw()
        plt.pause(0.01)

class Map:
    def __init__(self):
        self.cities = []
        self.path_segments = {}

    def takePathSegment(self, path):
        if path in self.path_segments:
            self.path_segments[path] += 1
        else:
            self.path_segments[path] = 1

    def pathLength(self, path):
        path_length = 0

        for i in range(1, len(path)):
            temp = 0
            for index, value in enumerate(path[i]):
                temp += (value - (path[i - 1])[index])**2
            path_length += math.sqrt(temp)

        return path_length

    def getShortestPath(self):
        pass


if __name__ == "__main__":
    main()