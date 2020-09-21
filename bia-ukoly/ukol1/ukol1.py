from matplotlib import cm
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import numpy as np
import math

def main():
    fig = plt.figure()
    ax = fig.gca(projection='3d')

    data = zakharov(2)

    ax.plot_surface(data[0], data[1], data[2], cmap=cm.get_cmap("Spectral"))

    plt.show()

def sphere(dimension):
    data = plot_generator(sphere_foo, dimension, generate_input(dimension, -5.12, 5.12, 0.01))
    return data

def rosenbrock(dimension):
    data = plot_generator(rosenbrock_foo, dimension, generate_input(dimension, -10, 10, 0.01))
    return data

def rastrigin(dimension):
    data = plot_generator(rastrigin_foo, dimension, generate_input(dimension, -5.12, 5.12, 0.01))
    return data

def griewangk(dimension):
    data = plot_generator(griewangk_foo, dimension, generate_input(dimension, -50, 50, 0.1))
    return data 

def levy(dimension):
    data = plot_generator(levy_foo, dimension, generate_input(dimension, -10, 10, 0.1))
    return data 

def michalewicz(dimension):
    data = plot_generator(michalewicz_foo, dimension, generate_input(dimension, 0, 5, 0.01))
    return data 

def zakharov(dimension):
    data = plot_generator(zakharov_foo, dimension, generate_input(dimension, -10, 10, 0.1))
    return data

def ackley(dimension):
    data = plot_generator(ackley_foo, dimension, generate_input(dimension, -32.768, 32.768, 0.1))

    return data 

def sphere_foo(input_vector):
    result = 0
    for i in input_vector:
        result += i**2
    return result

def rosenbrock_foo(input_vector):
    result = 0
    for i in range(len(input_vector) - 1):
        result += (100*((input_vector[i+1] - (input_vector[i]**2))**2)) + ((input_vector[i] - 1)**2)
    return result

def rastrigin_foo(input_vector):
    result = 10 * len(input_vector)
    for i in input_vector:
        result += (i**2) - (10 * math.cos(2 * math.pi * i)) 
    return result

def griewangk_foo(input_vector):
    vector_sum = 0
    for i in input_vector:
        vector_sum += ((i**2) / 4000)

    vector_product = 1
    for i, value in enumerate(input_vector):
        vector_product *= math.cos(value / math.sqrt(i + 1))

    return vector_sum - vector_product + 1

def levy_foo(input_vector):
    def levy_helper(input_number):
        return 1 + ((input_number - 1) / 4)

    result = math.sin(math.pi * levy_helper(input_vector[0]))**2

    for i in input_vector[:len(input_vector) - 1]:
        result += ((levy_helper(i) - 1)**2 * (1 + 10 * (math.sin(math.pi * levy_helper(i) + 1)**2)) +
        (levy_helper(input_vector[len(input_vector) - 1]) - 1)**2 * (1 + (math.sin(2 * math.pi * levy_helper(input_vector[len(input_vector) - 1])**2))))

    return result

def michalewicz_foo(input_vector):
    m = 10
    result = 0
    
    for i, value in enumerate(input_vector):
        result -= math.sin(value) * (math.sin(((i + 1) * (value**2)) / math.pi)**(2 * m))

    return result

def zakharov_foo(input_vector):
    result = 0

    for i in input_vector:
        result += i**2

    temp = 0
    for i, value in enumerate(input_vector):
        temp += 0.5 * (i + 1) * value
    
    result += temp**2
    result += temp**4

    return result

def ackley_foo(input_vector):
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

if __name__ == "__main__":
    main()