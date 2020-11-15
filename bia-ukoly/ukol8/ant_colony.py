from matplotlib import cm
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import numpy as np
import math
import random
import time
from itertools import chain

#TODO
#Modify chart, finish the algorithm itself.

def main():
    ant_colony_travelling_salesman(2, 0, 1000)

def ant_colony_travelling_salesman(dimension, min, max):
    chart = Chart()

    population_size = 20
    generations = 100
    number_of_cities = 20
    evaporation_ratio = 0.5
    alpha = 1
    beta = 2

    cities = [tuple(random.randint(min, max) for i in range(dimension)) for j in range(number_of_cities)]
    world = World(cities, evaporation_ratio)
    best_path = world.getBestPath()

    for _ in range(generations):
        population = [Ant(random.choice(world.cities), alpha, beta) for i in range(population_size)]

        for ant in population:
            ant.findPath(world)
        
        world.updateAllPheromones([i.path for i in population])
        best_path = world.getBestPath

class Ant:
    def __init__(self, starting_position, alpha, beta):
        self.path = [] #TODO
        self.alpha = alpha
        self.beta = beta

    def nextPathSegment(self, world):
        path_segments = world.pathSegmentsFromCity(self.path[-1][1])
        probabilities = []

        magic_sum = 0
        for i in path_segments:
            magic_sum += world.path_segments[i]**self.alpha + (1 / world.pathSegmentLength(i))**self.beta

        for i in path_segments:
            probability = (world.path_segments[i]**self.alpha + (1 / world.pathSegmentLength(i))**self.beta) / magic_sum
            probabilities.append(probability)

        distribution_function = [probabilities[0]]
        for index, value in enumerate(probabilities[1:]):
            distribution_function.append(value + distribution_function[index - 1])
        distribution_function[-1] = 1.0

        random_number = random.uniform(0.0, 1.0)
        for i in distribution_function[::-1]:
            if random_number < i:
                return i

    def findPath(self, world):
        while len(self.path) + 1 < len(world.cities):
            self.path.append(self.nextPathSegment(world))

        if self.path[-1][0] in self.path[-2]:
            self.path.append((self.path[-1][1], self.path[0][0]))      
        if self.path[-1][1] in self.path[-2]:
            self.path.append((self.path[-1][1], self.path[0][0])) 

class World:
    def __init__(self, cities, evaporation_ratio):
        self.cities = cities
        self.path_segments = {}
        self.evaporation_ratio = evaporation_ratio

        self.generateAllPathSegments()

    def generateAllPathSegments(self):
        for index, value in enumerate(self.cities):
            for j in self.cities[index + 1:]:
                self.path_segments[(value, j)] = 1.0

    def updateAllPheromones(self, paths):
        for key, _ in self.path_segments:
            self.pheromoneConversion(key, [i for i in paths if key in i])

    def pheromoneConversion(self, path_segment, paths):
        deposited_pheromone = 0
        for i in paths:
            deposited_pheromone += 1.0 / self.pathLength(i)
        self.path_segments[path_segment] = ((self.evaporation_ratio * self.path_segments[path_segment]) +
                                            (deposited_pheromone if path_segment in chain.from_iterable(paths) else 0))

    def pathSegmentLength(self, path_segment):
        temp = 0
        for index, _ in enumerate(path_segment):
            temp += (path_segment[0][index] - path_segment[1][index])**2
        return math.sqrt(temp)

    def pathLength(self, path):
        path_length = 0

        for i in path:
            path_length += self.pathSegmentLength(path[i])

        return path_length

    def getBestPath(self):
        best_path = [self.cities[0]]
        cities_not_in_path = self.cities[1:]

        while len(best_path) < len(self.cities):
            #get all edges that are incident with last city in path and do not lead to another city already in path
            filtered_path_segments = list(
                filter((lambda x: (x[0] in cities_not_in_path) or (x[1] in cities_not_in_path)), 
                self.pathSegmentsFromCity(best_path[-1]))
                )

            best_path_segment = filtered_path_segments[0]
            for i in filtered_path_segments[1:]:
                if self.path_segments[i] < self.path_segments[best_path_segment]:
                    best_path_segment = i
            
            best_path.append(best_path_segment)
            if best_path_segment[0] in cities_not_in_path:
                cities_not_in_path.remove(best_path_segment[0])
            if best_path_segment[1] in cities_not_in_path:
                cities_not_in_path.remove(best_path_segment[1])

        return best_path
            
    def pathSegmentsFromCity(self, city):
        possible_path_segments = []
        for key in self.path_segments:
            if (key[0] == city) or (key[1] == city):
                possible_path_segments.append(key)
        return possible_path_segments

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

if __name__ == "__main__":
    main()