import math
import random
import copy
from afunctions import CallCounter

def firefly_algo(foo, dimension, min, max, population_size, max_ofe):
    counter = CallCounter(foo, max_ofe)

    alpha = 0.3
    beta = 0.1
    gamma = 0.1

    population = [Firefly().generateRandom(dimension, min, max) for i in range(population_size)]
    values = {tuple(i.position): foo(i.position) for i in population}

    while(counter.isUnderLimit()):
        new_population = copy.deepcopy(population)
        for firefly in new_population:
            for another_firefly in population:
                if firefly.getLightIntensity(another_firefly, counter.call, gamma) > firefly.getLightIntensity(firefly, counter.call, gamma):
                    firefly.moveToFirefly(another_firefly, alpha, beta, min, max)

        new_population.sort(key = (lambda x: counter.call(x.position)))

        #random movement of best firefly
        new_population[0].moveFireflyRandom(alpha, min, max, counter.call)

        population = new_population
        values = {tuple(i.position): foo(i.position) for i in population}
        
    best_case = population[0]
    for i in population:
        if i.getLightIntensity(i, foo, gamma) > best_case.getLightIntensity(best_case, foo, gamma):
            best_case = i

    return foo(best_case.position)

class Firefly:
    def __init__ (self):
        self.position = []
    
    def generateRandom(self, dimension, min, max):
        self.position = [random.uniform(min, max) for i in range(dimension)]
        return self

    def moveFireflyRandom(self, alpha, min, max, foo):
        old_pos = self.position.copy()

        self.moveToFirefly(self, alpha, 0, min, max)

        if foo(old_pos) < foo(self.position):
            self.position = old_pos

    def moveToFirefly(self, another_firefly, alpha, beta, min, max):
        for index, _ in enumerate(self.position):
            x = self.position[index] + (self.getAttractiveness(another_firefly, beta) * 
                                    (another_firefly.position[index] - self.position[index]) +
                                    (alpha * random.gauss(0, 1)))

            x = x if x < max else max
            x = x if x > min else min
            self.position[index] = x

    def getAttractiveness(self, another_firefly, beta):
        return beta / (1 + self.getDistance(another_firefly))

    def getLightIntensity(self, another_firefly, foo, gamma):
        # mam zde (1 / foo(another_firefly.position)) aby stoupala svetelna intenzita s klesajici hodnou
        # jelikoz u nasich funkci hledame minima
        return (1 / foo(another_firefly.position))*math.exp(-gamma * self.getDistance(another_firefly))

    def getDistance(self, another_firefly):
        temp = 0
        for index, _ in enumerate(self.position):
            temp += (self.position[index] - another_firefly.position[index])**2
        return math.sqrt(temp)