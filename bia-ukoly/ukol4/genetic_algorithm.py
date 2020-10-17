from matplotlib import cm
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import numpy as np
import math
import random
import time

def main():
    genetic_algorithm(2, 0, 1000)


def generate_individual(dimension, min, max):
    pass

def genetic_algorithm(dimension, min, max):
    fig = plt.figure()
    ax = fig.gca()
    plt.ion()
    plt.show()

    NP = 20
    G = 200
    D = 40  # In TSP, it will be a number of cities

    cities = [tuple(random.randint(min, max) for i in range(dimension)) for j in range(D)]

    population = [Path().generate_random_path(cities) for i in range(NP)]
    new_population = population.copy()

    for i in range(G):
        best_case = population[0]
        for i in population:
            if i.length() < best_case.length():
                best_case = i

        ax.clear()
        print(best_case.length())
        ax.scatter(np.array(cities)[:, 0], np.array(cities)[:, 1], c='b')
        ax.plot(np.array(best_case.path)[:, 0], np.array(best_case.path)[:, 1], c='r')
        plt.draw()
        plt.pause(0.1)

        for index, value in enumerate(population):
            parent_A = value
            parent_B = population[random.randint(0, NP - 1)]
            
            while parent_B == parent_A:
                parent_B = population[random.randint(0, NP - 1)]

            offspring_AB = Path.crossover(parent_A, parent_B)
            offspring_AB.mutate()

            if offspring_AB.length() < parent_A.length():
                new_population[index] = offspring_AB

        population = new_population
        
    plt.pause(10)

    best_case = population[0]
    for i in population:
        if i.length() < best_case.length():
            best_case = i

    return best_case


class Path:
    def __init__(self):
        self.path = []

    def generate_random_path(self, cities):
        random_order = list(range(len(cities)))
        
        while len(random_order) > 0:
            random_city = cities[random_order.pop(random.randint(0, len(random_order) - 1))]
            self.path.append(random_city)
        
        return self

    def length(self):
        path_length = 0

        for i in range(1, len(self.path)):
            temp = 0
            for index, value in enumerate(self.path[i]):
                temp += (value - (self.path[i - 1])[index])**2
            path_length += math.sqrt(temp)

        return path_length

    def mutate(self):
        if random.randint(0, 1) == 1:
            posA = random.randint(0, len(self.path) - 1)
            posB = random.randint(0, len(self.path) - 1)

            while posA == posB:
                posB = random.randint(0, len(self.path) - 1)

            temp = posA
            posA = posB
            posB = temp
        
        return self
                

    @classmethod
    def crossover(cls, path1, path2):
        new_path = cls()

        for i in path1.path[:len(path1.path)//2]:
            new_path.path.append(i)
        
        for i in path2.path:
            if not (i in new_path.path):
                new_path.path.append(i)
        
        return new_path


if __name__ == "__main__":
    main()