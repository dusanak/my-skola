import numpy as np
import random
from afunctions import CallCounter

def som_algorithm(foo, dimension, min, max, population_size, max_ofe):
    counter = CallCounter(foo, max_ofe)

    path_length = 5
    step = 0.2
    PRT = 0.5
    min_div = 0.1

    population = [tuple(random.uniform(min, max) for _ in range(dimension)) for _ in range(population_size)]
    values = {i: foo(i) for i in population}

    while(counter.isUnderLimit()):
        new_population = []

        best_pos = population[0]
        for i in population:
            if values[i] < values[best_pos]:
                best_pos = i

        #stopovaci podminka pokud je delta mensi nez min_div
        for i in population:
            if abs(values[best_pos] - values[i]) > min_div:
                break
        else:
            break

        for i in population:
            path = [i]
            for t in np.arange(0, path_length + step, step):
                prt_vector = [1 if random.uniform(0, 1) < PRT else 0 for __ in range(dimension)]
                
                new_position = []
                for index, value in enumerate(i): 
                    x = value + (best_pos[index] - value) * t * prt_vector[index]
                    x = x if x < max else max
                    x = x if x > min else min
                    new_position.append(x)

                path.append(tuple(new_position))

            best_individual = path[0]
            for j in path:
                if counter.call(j) < counter.call(best_individual):
                    best_individual = j
            new_population.append(best_individual)
        
        population = new_population
        values = {i: foo(i) for i in population}
        
    best_case = population[0]
    for i in population:
        if foo(i) < foo(best_case):
            best_case = i

    return foo(best_case)