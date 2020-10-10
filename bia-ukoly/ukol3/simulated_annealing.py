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

    simulated_annealing(*options["sphere"])

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

def generate_input(dimension, min, max, step):
    data = []

    for _ in range(dimension):
        data.append(np.arange(min, max, step))
    
    return data

def plot_generator(foo, dimension, input_data):
    data_mesh = np.meshgrid(*input_data)

    result = []
    
    for i in np.dstack(data_mesh):
        column = []

        for j in i:
            column.append(foo(j))

        result.append(np.array(column))

    data_mesh.append(np.array(result))

    return data_mesh

def simulated_annealing(foo, dimension, min, max):
    fig = plt.figure()
    data = plot_generator(foo, dimension, generate_input(dimension, min, max, 0.1))
    ax = fig.gca(projection='3d')
    plt.ion()
    plt.show()

    T_0 = 100.0
    T_min = 0.5
    alpha = 0.95

    T = T_0

    best_case = [random.uniform(min, max) for i in range(dimension)]
    best_case.append(foo(best_case))
    while T > T_min:
        print(best_case[dimension])

        ax.clear()
        ax.plot_surface(data[0], data[1], data[2], alpha = 0.25, cmap=cm.get_cmap("inferno"))
        ax.scatter(*best_case, 'bo')
        plt.draw()
        plt.pause(0.5)

        solution = []  
        for i in range(dimension):
            val = random.gauss(best_case[i], 1)

            #abychom zustali v rozsahu souradnic
            if (val < min):
                val = min
            if (val > max):
                val = max

            solution.append(val)
        solution.append(foo(solution))

        if solution[dimension] < best_case[dimension]:
            best_case = solution
        else:
            r = random.uniform(0, 1)

            if r < math.pow(math.e, (-(solution[dimension] - best_case[dimension]))/T):
                best_case = solution
        T *= alpha
        
    plt.pause(10)

    return best_case

if __name__ == "__main__":
    main()