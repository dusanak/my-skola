from matplotlib import cm
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import numpy as np
import math
import random
import time
from itertools import chain

def main():
    ant_colony_travelling_salesman(2, 0, 100)

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
    best_path = cities + [cities[-1]]

    for _ in range(generations):
        population = [Ant(random.choice(world.cities), alpha, beta) for i in range(population_size)]

        for ant in population:
            ant.findPath(world)

            if world.pathLength(ant.path) < world.pathLength(best_path):
                best_path = ant.path.copy()

            chart.draw(current_path_length = world.pathLength(ant.path),
                       current_path = ant.path,
                       best_path_length = world.pathLength(best_path),
                       best_path = best_path)
        
        world.updateAllPheromones([i.path for i in population])

    plt.pause(10)

class Ant:
    def __init__(self, starting_position, alpha, beta):
        self.path = [starting_position]
        self.alpha = alpha
        self.beta = beta

    def nextPathCity(self, world):
        path_segments = world.pathSegmentsFromCity(self.path[-1])
        random.shuffle(path_segments)
        probabilities = []

        #odfiltrovani hran spojujici dve mesta ktera uz v ceste jsou
        filtered_path_segments = list(filter((lambda x: not ((x[0] in self.path) and (x[1] in self.path))), path_segments))

        magic_sum = 0
        for i in filtered_path_segments:
            magic_sum += world.path_segments[i]**self.alpha + (1 / world.pathSegmentLength(i))**self.beta

        for i in filtered_path_segments:
            probability = (world.path_segments[i]**self.alpha + (1 / world.pathSegmentLength(i))**self.beta) / magic_sum
            probabilities.append(probability)

        distribution_function = [probabilities[0]]
        for index, value in enumerate(probabilities[1:]):
            distribution_function.append(value + distribution_function[index - 1])
        distribution_function[-1] = 1.0

        random_number = random.uniform(0.0, 1.0)
        for index, value in enumerate(distribution_function[::-1]):
            if random_number < value:
                return filtered_path_segments[index][0] if filtered_path_segments[index][0] != self.path[-1] else filtered_path_segments[index][1]

    def findPath(self, world):
        while len(self.path) < len(world.cities):
            self.path.append(self.nextPathCity(world))

        self.path.append(self.path[0])

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
        for path_segment in self.path_segments.keys():
            paths_containing_segment = []
            for path in paths:
                for index, value in enumerate(path[1:]):
                    if (value == path_segment[0]) or (value == path_segment[1]):
                        if (path[index - 1] == path_segment[0]) or (path[index - 1] == path_segment[1]):
                            paths_containing_segment.append(path)
                            break
            self.pheromoneConversion(path_segment, paths_containing_segment)

    def pheromoneConversion(self, path_segment, paths):
        deposited_pheromone = 0
        for i in paths:
            deposited_pheromone += 1.0 / self.pathLength(i)
        self.path_segments[path_segment] = ((self.evaporation_ratio * self.path_segments[path_segment]) + deposited_pheromone)

    def getPathSegment(self, city1, city2):
        if (city1, city2) in self.path_segments:
            return (city1, city2)
        else:
            return (city2, city1)

    def pathSegmentsFromCity(self, city):
        possible_path_segments = []
        for key in self.path_segments:
            if (key[0] == city) or (key[1] == city):
                possible_path_segments.append(self.getPathSegment(key[0], key[1]))
        return possible_path_segments

    def pathSegmentLength(self, path_segment):
        temp = 0
        for index, _ in enumerate(path_segment):
            temp += (path_segment[0][index] - path_segment[1][index])**2
        return math.sqrt(temp)

    def pathLength(self, path):
        path_length = 0

        for i in range(len(path[1:])):
            path_length += self.pathSegmentLength(self.getPathSegment(path[i - 1], path[i]))

        return path_length

class Chart:
    def __init__(self):
        fig = plt.figure()
        self.ax_current = fig.add_subplot(1, 2, 1)
        self.ax_best = fig.add_subplot(1, 2, 2)
        plt.ion()
        plt.show()

    def draw(self, current_path_length, current_path, best_path_length, best_path):
        self.ax_current.clear()
        self.ax_current.title.set_text('{:5.5f}'.format(current_path_length))
        self.ax_current.plot(np.array(current_path)[:, 0], np.array(current_path)[:, 1], c='r')
        self.ax_current.scatter(np.array(current_path)[:, 0], np.array(current_path)[:, 1], c='b')

        self.ax_best.clear()
        self.ax_best.title.set_text('{:5.5f}'.format(best_path_length))
        self.ax_best.plot(np.array(best_path)[:, 0], np.array(best_path)[:, 1], c='r')
        self.ax_best.scatter(np.array(best_path)[:, 0], np.array(best_path)[:, 1], c='b')

        plt.draw()
        plt.pause(0.01)

if __name__ == "__main__":
    main()