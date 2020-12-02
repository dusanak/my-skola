import random
from functions import CallCounter

def tlbo_algorithm(foo, dimension, min, max, population_size, max_ofe):
    counter = CallCounter(foo, max_ofe)

    population = [tuple(random.uniform(min, max) for i in range(dimension)) for i in range(population_size)]
    values = {i: foo(i) for i in population}
    
    best_case = population[0]

    while(counter.isUnderLimit()):
        new_population = population.copy()
        
        mean = []
        for i in range(dimension):
            sum = 0
            for j in population:
                sum += j[i]
            mean.append(sum / len(population))

        teacher = population[0]
        for i in population[1:]:
            if values[i] < values[teacher]:
                teacher = i

        Tf = (random.randint(1, 2))
        difference = [random.uniform(0, 1) * (teacher[i] - Tf * mean[i]) for i in range(dimension)]

        teacher_new = tuple([teacher[i] + difference[i] for i in range(dimension)])
        if counter.call(teacher_new) < values[teacher]:
            new_population.remove(teacher)
            new_population.append(teacher_new)
            population.remove(teacher)
            population.append(teacher_new)
            del values[teacher]
            values[teacher_new] = counter.call(teacher_new)
            teacher = teacher_new

        for index, learner in enumerate(new_population):
            random_learner = random.choice(population)
            while random_learner == learner:
                random_learner = random.choice(population)

            new_learner = []
            if values[learner] < values[random_learner]:
                for i in range(dimension):
                    new_learner.append(learner[i] + random.uniform(0, 1) * (learner[i] - random_learner[i]))
            else:
                for i in range(dimension):
                    new_learner.append(learner[i] + random.uniform(0, 1) * (random_learner[i] - learner[i]))

            new_learner = tuple(new_learner)
            if counter.call(new_learner) < values[learner]:
                new_population[index] = new_learner

        population = new_population
        values = {i: counter.call(i) for i in population}

    best_case = population[0]
    for i in population:
        if foo(i) < foo(best_case):
            best_case = i

    return foo(best_case)
