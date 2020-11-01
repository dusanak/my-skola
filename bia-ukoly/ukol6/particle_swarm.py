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
    differential_evolution(*options["sphere"])

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

def differential_evolution(foo, dimension, min, max):
    chart = Chart(foo, dimension, min, max)

    population_size = 15
    generations = 50

    v_min, v_max = -1.0, 1.0
    c1, c2 = 2.0, 2.0
    w_s, w_e = 0.9, 0.4

    population = [Particle().generateRandom(dimension, min, max, v_min, v_max) for i in range(population_size)]
    
    g_best = population[0].position
    for particle in population:
        if foo(particle.p_best) < foo(g_best):
            g_best = particle.p_best

    for i in range(generations):
        chart.draw(population)

        for particle in population:
            w = w_s - (((w_s - w_e) * i) / population_size)

            particle.newVelocity(w, c1, c2, g_best, v_min, v_max)
            particle.newPosition(min, max)

            if foo(particle.position) < foo(particle.p_best):
                particle.p_best = particle.position

                if foo(particle.p_best) < foo(g_best):
                    g_best = particle.p_best
        
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

class Particle:
    def __init__ (self):
        self.position = []
        self.velocity = []
        self.p_best = []
    
    def generateRandom(self, dimension, min, max, v_min, v_max):
        self.position = [random.uniform(min, max) for i in range(dimension)]
        self.velocity = [random.uniform(v_min, v_max) for i in range(dimension)]
        self.p_best = self.position

        return self 

    def newVelocity(self, w, c1, c2, g_best, v_min, v_max):
        r1 = random.uniform(0, 1)
        
        for index, value in enumerate(self.velocity):
            self.velocity[index] = value * w \
            + r1 * c1 * (self.p_best[index] - self.position[index]) \
            + r1 * c2 * (g_best[index] - self.position[index])

            if self.velocity[index] < v_min:
                self.velocity[index] = v_min
            
            if self.velocity[index] > v_max:
                self.velocity[index] = v_max

    def newPosition(self, min, max):
        for index, value in enumerate(self.position):
            self.position[index] = value + self.velocity[index]
            
            if self.position[index] < min:
                self.position[index] = min
            
            if self.position[index] > max:
                self.position[index] = max


if __name__ == "__main__":
    main()