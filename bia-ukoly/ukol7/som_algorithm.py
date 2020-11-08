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
    som_algorithm(*options["sphere"])

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

def som_algorithm(foo, dimension, min, max):
    chart = Chart(foo, dimension, min, max)

    population_size = 15
    migrations = 50

    path_length = 5
    step = 1
    PRT = 0.5
    min_div = 1

    population = [tuple(random.uniform(min, max) for _ in range(dimension)) for _ in range(population_size)]

    for _ in range(migrations):
        chart.draw(population)

        new_population = []

        best_pos = population[0]
        for i in population:
            if foo(i) < foo(best_pos):
                best_pos = i

        #stopovaci podminka pokud je delta mensi nez min_div
        for i in population:
            if abs(foo(best_pos) - foo(i)) > min_div:
                break
        else:
            break


        for i in population:
            path = [i]
            for t in range(0, path_length, step):
                prt_vector = [1 if random.uniform(0, 1) < PRT else 0 for __ in range(dimension)]
                
                new_position = []
                for index, value in enumerate(i): 
                    x = value + (best_pos[index] - value) * t * prt_vector[index]
                    x = x if x < max else max
                    x = x if x > min else min
                    new_position.append(x)

                new_position = tuple(new_position)

                path.append(new_position)

            best_individual = path[0]
            for j in path:
                if foo(j) < foo(best_individual):
                    best_individual = j
            new_population.append(best_individual)
        
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
            self.ax.scatter(i[0], i[1], self.foo(i), c='b')
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