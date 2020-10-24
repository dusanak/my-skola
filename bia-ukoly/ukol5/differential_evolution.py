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
        "ackley": (ackley, 2, -32.768, 32.768)
    }
    differential_evolution(*options["ackley"])

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

def differential_evolution(foo, dimension, min, max):
    chart = Chart(foo, dimension, min, max)

    population_size = 20
    generations = 100
    F = 0.5
    CR = 0.5

    population = [tuple(random.uniform(min, max) for i in range(dimension)) for i in range(population_size)]
    
    best_case = population[0]

    for _ in range(generations):
        chart.draw(population)

        new_population = population.copy()
        for index, value in enumerate(new_population):
            r1 = random.choice([i for i in population if i != value])
            r2 = random.choice([i for i in population if (i != value) and (i != r1)])
            r3 = random.choice([i for i in population if (i != value) and (i != r1) and (i != r2)])

            v = []
            for i, x in enumerate(r1):
                v.append((x - r2[i]) * F + r3[i])
                if v[i] < min:
                    v[i] = min
                if v[i] > max:
                    v[i] = max

            u = [0 for i in range(dimension)]

            i_rnd = random.uniform(min, max)

            for i in range(dimension):
                if random.uniform(0, 1) < CR or i == i_rnd:
                    u[i] = v[i]
                else:
                    u[i] = value[i]

            if foo(u) <= foo(value):
                new_population[index] = tuple(u)

        population = new_population
        
    plt.pause(10)

    best_case = population[0]
    for i in population:
        if i.length < best_case.length:
            best_case = i

    return best_case


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
        self.ax.scatter(np.array(population)[:, 0], np.array(population)[:, 1], [self.foo(i) for i in population], c='b')
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


if __name__ == "__main__":
    main()