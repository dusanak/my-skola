import random
from afunctions import CallCounter

def particle_swarm(foo, dimension, min, max, population_size, max_ofe):
    counter = CallCounter(foo, max_ofe)

    v_min, v_max = min / 10.0, max / 10.0
    c1, c2 = 2.0, 2.0
    w_s, w_e = 0.9, 0.4

    population = [Particle().generateRandom(dimension, min, max, v_min, v_max) for i in range(population_size)]
    values = {i.p_best: foo(i.p_best) for i in population}

    g_best = population[0].position
    for particle in population:
        if counter.call(particle.p_best) < counter.call(g_best):
            g_best = tuple(particle.p_best)

    i = 0
    while(counter.isUnderLimit()):
        for particle in population:
            w = w_s - (((w_s - w_e) * i) / population_size)

            particle.newVelocity(w, c1, c2, g_best, v_min, v_max)
            particle.newPosition(min, max)

            if counter.call(particle.position) < values[particle.p_best]:
                particle.p_best = tuple(particle.position)
                values[particle.p_best] = counter.call(particle.p_best)

                if values[particle.p_best] < counter.call(g_best):
                    g_best = tuple(particle.p_best)

        values = {i.p_best: counter.call(i.p_best) for i in population}
        i += 1

    best_case = population[0].p_best
    for i in population:
        if foo(i.p_best) < foo(best_case):
            best_case = i.p_best

    return foo(best_case)

class Particle:
    def __init__ (self):
        self.position = []
        self.velocity = []
        self.p_best = []
    
    def generateRandom(self, dimension, min, max, v_min, v_max):
        self.position = [random.uniform(min, max) for i in range(dimension)]
        self.velocity = [random.uniform(v_min, v_max) for i in range(dimension)]
        self.p_best = tuple(self.position)

        return self 

    def newVelocity(self, w, c1, c2, g_best, v_min, v_max):
        r1 = random.uniform(0, 1)
        
        for index, value in enumerate(self.velocity):
            self.velocity[index] = value * w \
            + r1 * c1 * (self.p_best[index] - self.position[index]) \
            + r1 * c2 * (g_best[index] - self.position[index])

            if self.velocity[index] < v_min:
                self.velocity[index] = v_min
            
            if self.velocity[index] > v_max:
                self.velocity[index] = v_max

    def newPosition(self, min, max):
        for index, value in enumerate(self.position):
            self.position[index] = value + self.velocity[index]
            
            if self.position[index] < min:
                self.position[index] = min
            
            if self.position[index] > max:
                self.position[index] = max