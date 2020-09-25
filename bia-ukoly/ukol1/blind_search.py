from matplotlib import cm
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import numpy as np
import math
import random
import time

def main():
    blind_search_visualised(2, -5.12, 5.12, 20)

def sphere_visualised(dimension):
    data = plot_generator(sphere, dimension, generate_input(dimension, -5.12, 5.12, 0.01))
    return data

def sphere(input_vector):
    result = 0
    for i in input_vector:
        result += i**2
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

def blind_search_visualised(dimension, min, max, tries):
    best_case = [i for i in range(dimension)]
    best_case.append(float("inf"))
    
    fig = plt.figure()
    data = sphere_visualised(2)
    ax = fig.gca(projection='3d')

    plt.ion()
    plt.show()

    for _ in range(tries):
        current_best_case = blind_search(sphere, dimension, min, max)

        if (current_best_case[dimension] < best_case[dimension]):
            best_case = current_best_case

        ax.clear()
        ax.plot_surface(data[0], data[1], data[2], alpha = 0.5, cmap=cm.get_cmap("Spectral"))
        ax.plot(*best_case, 'bo')
        
        print(best_case[dimension])

        plt.draw()
        plt.pause(1)
        
    plt.pause(10)

    return best_case

def blind_search(foo, dimension, min, max):
    best_case = [i for i in range(dimension)]
    best_case.append(float("inf"))

    for _ in range(10):
            vector = []
            
            for __ in range(dimension):
                vector.append(random.uniform(min, max))
            
            result = foo(vector)

            if (result < best_case[dimension]):
                vector.append(result)
                
                for index, value in enumerate(vector):
                    best_case[index] = value

    return best_case


if __name__ == "__main__":
    main()