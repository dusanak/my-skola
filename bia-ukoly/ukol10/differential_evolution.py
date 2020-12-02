import random
from functions import CallCounter

def differential_evolution(foo, dimension, min, max, population_size, max_ofe):
    counter = CallCounter(foo, max_ofe)
    
    F = 0.5
    CR = 0.5

    population = [tuple(random.uniform(min, max) for i in range(dimension)) for i in range(population_size)]
    values = {i: foo(i) for i in population}
    
    best_case = population[0]

    while(counter.isUnderLimit()):
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
                if (random.uniform(0, 1) < CR) or (i == i_rnd):
                    u[i] = v[i]
                else:
                    u[i] = value[i]

            if counter.call(u) <= values[value]:
                new_population[index] = tuple(u)

        population = new_population
        values = {i: counter.call(i) for i in population}

    best_case = population[0]
    for i in population:
        if foo(i) < foo(best_case):
            best_case = i

    return foo(best_case)
