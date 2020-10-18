from matplotlib import cm
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import numpy as np
import math
import random
import time

def main():
    genetic_travelling_salesman(2, 0, 1000)

def genetic_travelling_salesman(dimension, min, max):
    chart = Chart()

    population_size = 20
    generations = 100
    number_of_cities = 20

    cities = [tuple(random.randint(min, max) for i in range(dimension)) for j in range(number_of_cities)]

    population = [Path().generate_random_path(cities) for i in range(population_size)]
    new_population = population.copy()

    best_case = population[0]

    for i in range(generations):
        for index, value in enumerate(population):
            parent_a = value
            parent_b = random.choice([i for i in population if i != parent_a])

            if parent_a.length < best_case.length:
                print("Gen", i, ":", best_case.length)
                best_case = parent_a
            
            chart.draw(current_path = parent_a, best_path = best_case)

            offspring_AB = Path.crossover(parent_a, parent_b)
            offspring_AB.mutate()

            if offspring_AB.length < parent_a.length:
                new_population[index] = offspring_AB

        population = new_population
        
    plt.pause(10)

    best_case = population[0]
    for i in population:
        if i.length < best_case.length:
            best_case = i

    return best_case


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

class Path:
    def __init__(self):
        self.path = []
        self.length = 0

    def generate_random_path(self, cities):
        self.path = cities.copy()
        random.shuffle(self.path)

        self.length = self.calculate_length()
        
        return self

    def calculate_length(self):
        path_length = 0

        for i in range(1, len(self.path)):
            temp = 0
            for index, value in enumerate(self.path[i]):
                temp += (value - (self.path[i - 1])[index])**2
            path_length += math.sqrt(temp)

        return path_length

    def mutate(self):
        if random.randint(0, 1) == 1:
            pos_a = random.randint(0, len(self.path) - 1)
            pos_b = random.randint(0, len(self.path) - 1)

            while pos_a == pos_b:
                pos_b = random.randint(0, len(self.path) - 1)

            temp = self.path[pos_a]
            self.path[pos_a] = self.path[pos_b]
            self.path[pos_b] = temp

            self.length = self.calculate_length()
        
        return self
                
    @classmethod
    def crossover(cls, path1, path2):
        new_path = cls()

        for i in path1.path[:len(path1.path)//2]:
            new_path.path.append(i)
        
        for i in path2.path:
            if not (i in new_path.path):
                new_path.path.append(i)
        
        new_path.length = new_path.calculate_length()

        return new_path


if __name__ == "__main__":
    main()