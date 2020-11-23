from matplotlib import cm
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import numpy as np
import math
import random
import time

def main():
    options = {
        "sphere": (sphere, 2, -5.12, 5.12),
        "michalewicz": (michalewicz, 2, 0, 5),
        "ackley": (ackley, 2, -32.768, 32.768),
        "zakharov": (zakharov, 2, -10, 10)
    }
    firefly_algo(*options["sphere"])

def sphere(input_vector):
    result = 0
    for i in input_vector:
        result += i**2
    return result

def michalewicz(input_vector):
    m = 10
    result = 0
    
    for i, value in enumerate(input_vector):
        result -= math.sin(value) * (math.sin(((i + 1) * (value**2)) / math.pi)**(2 * m))

    return result

def ackley(input_vector):
    a = 20
    b = 0.2
    c = 2 * math.pi

    result = 0

    temp = 0
    for i in input_vector:
        temp += i**2
    result += -a * math.exp(-b * math.sqrt((1 / len(input_vector)) * temp))

    temp = 0
    for i in input_vector:
        temp += math.cos(c * i)
    result -= math.exp((1 / len(input_vector)) * temp)

    result += a
    result += math.exp(1)

    return result

def zakharov(input_vector):
    result = 0

    for i in input_vector:
        result += i**2

    temp = 0
    for i, value in enumerate(input_vector):
        temp += 0.5 * (i + 1) * value
    
    result += temp**2
    result += temp**4

    return result

def firefly_algo(foo, dimension, min, max):
    chart = Chart(foo, dimension, min, max)

    population_size = 15
    generations = 50

    alpha = 0.3
    beta = 0.1
    gamma = 0.1

    population = [Firefly().generateRandom(dimension, min, max) for i in range(population_size)]

    for _ in range(generations):
        chart.draw(population)

        new_population = population.copy()
        for firefly in new_population:
            for another_firefly in new_population:
                if firefly.getLightIntensity(another_firefly, foo, gamma) > firefly.getLightIntensity(firefly, foo, gamma):
                    firefly.moveToFirefly(another_firefly, alpha, beta, min, max)

        new_population.sort(key = (lambda x: foo(x.position)))

        #random movement of best firefly
        new_population[0].moveFireflyRandom(alpha, min, max, foo)

        population = new_population
        
    plt.pause(10)


class Chart:
    def __init__(self, foo, dimension, min, max):
        fig = plt.figure()
        self.ax = fig.gca(projection='3d')
        plt.ion()
        plt.show()

        self.data = self.plot_generator(foo, dimension, self.generate_input(dimension, min, max, 0.1))
        self.foo = foo

    def draw(self, population):
        self.ax.clear()
        for i in population:
            self.ax.scatter(i.position[0], i.position[1], self.foo(i.position), c='b')
        self.ax.plot_surface(*self.data, alpha = 0.25, cmap=cm.get_cmap("inferno"))

        plt.draw()
        plt.pause(0.01)

    def generate_input(self, dimension, min, max, step):
        data = []

        for _ in range(dimension):
            data.append(np.arange(min, max, step))
        
        return data

    def plot_generator(self, foo, dimension, input_data):
        data_mesh = np.meshgrid(*input_data)

        result = []
        
        for i in np.dstack(data_mesh):
            column = []

            for j in i:
                column.append(foo(j))

            result.append(np.array(column))

        data_mesh.append(np.array(result))

        return data_mesh

class Firefly:
    def __init__ (self):
        self.position = []
    
    def generateRandom(self, dimension, min, max):
        self.position = [random.uniform(min, max) for i in range(dimension)]
        return self

    def moveFireflyRandom(self, alpha, min, max, foo):
        old_pos = self.position.copy()

        self.moveToFirefly(self, alpha, 0, min, max)

        if foo(old_pos) < foo(self.position):
            self.position = old_pos

    def moveToFirefly(self, another_firefly, alpha, beta, min, max):
        for index, _ in enumerate(self.position):
            x = self.position[index] + (self.getAttractiveness(another_firefly, beta) * 
                                    (another_firefly.position[index] - self.position[index]) +
                                    (alpha * random.gauss(0, 1)))

            x = x if x < max else max
            x = x if x > min else min
            self.position[index] = x

    def getAttractiveness(self, another_firefly, beta):
        return beta / (1 + self.getDistance(another_firefly))

    def getLightIntensity(self, another_firefly, foo, gamma):
        # mam zde (1 / foo(another_firefly.position)) aby stoupala svetelna intenzita s klesajici hodnou
        # jelikoz u nasich funkci hledame minima
        return (1 / foo(another_firefly.position))*math.exp(-gamma * self.getDistance(another_firefly))

    def getDistance(self, another_firefly):
        temp = 0
        for index, _ in enumerate(self.position):
            temp += (self.position[index] - another_firefly.position[index])**2
        return math.sqrt(temp)


if __name__ == "__main__":
    main()