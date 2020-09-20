import matplotlib.pyplot as plt
from matplotlib import cm
from mpl_toolkits.mplot3d import Axes3D
import numpy as np

def main():
    fig = plt.figure()
    ax = fig.gca(projection='3d')

    data, z = sphere(2)

    ax.plot_surface(data[0], data[1], z, cmap=cm.coolwarm)

    plt.show()

def sphere(dimension):
    temp = []

    for i in range(dimension):
        temp = np.arange(-5.12, 5.12, 0.01)
        temp = temp**2


    tempgrids = np.meshgrid(temp)

    z = tempgrids[0]

    for i in range(1, len(tempgrids)):
        z = z + tempgrids[i]

    return tempgrids, z

def rosenbrock():
    pass

def rastrigin():
    pass

def griewangk():
    pass

def levy():
    pass

def michalewicz():
    pass

def zakharov():
    pass

def ackley():
    pass

if __name__ == "__main__":
    main()