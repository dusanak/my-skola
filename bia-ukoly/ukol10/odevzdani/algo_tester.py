from functions import sphere, michalewicz, ackley, zakharov, rosenbrock, rastrigin, griewangk, levy, schwefel
from differential_evolution import differential_evolution
from firefly_algo import firefly_algo
from particle_swarm import particle_swarm
from som_algorithm import som_algorithm
from tlbo_algorithm import tlbo_algorithm
import os
import sys

def main():
    dim = 30
    number_of_experiments = 30
    population_size = 30
    max_ofe = 3000

    function_options = {
        "sphere": (sphere, dim, -5.12, 5.12),
        "michalewicz": (michalewicz, dim, 0, 5),
        "ackley": (ackley, dim, -32.768, 32.768),
        "zakharov": (zakharov, dim, -10, 10),
        "rosenbrock": (rosenbrock, dim, -10, 10),
        "rastrigin": (rastrigin, dim, -5.12, 5.12),
        "griewangk": (griewangk, dim, -50, 50),
        "levy": (levy, dim, -10, 10),
        "schwefel": (griewangk, dim, -500, 500)
    }

    optimisation_algorithms = [
        differential_evolution,
        firefly_algo,
        particle_swarm,
        som_algorithm,
        tlbo_algorithm
    ]

    for foo_name, foo_options in function_options.items():
        row = "experiment"
        for algo in optimisation_algorithms:
            row += ","
            row += algo.__name__

        with open(os.path.join(os.path.dirname(sys.argv[0]), "experiments", "{}.csv".format(foo_name)), "a") as afunctions:
            afunctions.write(row + "\n")

        for i in range(number_of_experiments):
            row = str(i)
            for algo in optimisation_algorithms:
                row += ","
                row += str(algo(*foo_options, population_size, max_ofe))

            with open(os.path.join(os.path.dirname(sys.argv[0]), "experiments", "{}.csv".format(foo_name)), "a") as afunctions:
                afunctions.write(row + "\n")
                
if __name__ == "__main__":
    main()