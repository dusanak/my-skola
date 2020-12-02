import numpy as np
import math

def sphere(input_vector):
    result = 0
    for i in input_vector:
        result += i**2
    return result

def rosenbrock(input_vector):
    result = 0
    for i in range(len(input_vector) - 1):
        result += (100*((input_vector[i+1] - (input_vector[i]**2))**2)) + ((input_vector[i] - 1)**2)
    return result

def rastrigin(input_vector):
    result = 10 * len(input_vector)
    for i in input_vector:
        result += (i**2) - (10 * math.cos(2 * math.pi * i)) 
    return result

def griewangk(input_vector):
    vector_sum = 0
    for i in input_vector:
        vector_sum += ((i**2) / 4000)

    vector_product = 1
    for i, value in enumerate(input_vector):
        vector_product *= math.cos(value / math.sqrt(i + 1))

    return vector_sum - vector_product + 1

def levy(input_vector):
    def levy_helper(input_number):
        return 1 + ((input_number - 1) / 4)

    result = math.sin(math.pi * levy_helper(input_vector[0]))**2

    for i in input_vector[:len(input_vector) - 1]:
        result += ((levy_helper(i) - 1)**2 * (1 + 10 * (math.sin(math.pi * levy_helper(i) + 1)**2)) +
        (levy_helper(input_vector[len(input_vector) - 1]) - 1)**2 * (1 + (math.sin(2 * math.pi * levy_helper(input_vector[len(input_vector) - 1])**2))))

    return result

def michalewicz(input_vector):
    m = 10
    result = 0
    
    for i, value in enumerate(input_vector):
        result -= math.sin(value) * (math.sin(((i + 1) * (value**2)) / math.pi)**(2 * m))

    return result

def zakharov(input_vector):
    result = 0

    for i in input_vector:
        result += i**2

    temp = 0
    for i, value in enumerate(input_vector):
        temp += 0.5 * (i + 1) * value
    
    result += temp**2
    result += temp**4

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

def schwefel(input_vector):
    sum = 0
    d = len(input_vector)
    for i in range(d):
        sum += input_vector[i] * np.sin(np.sqrt(abs(input_vector[i])))
    return 418.9829 * d - sum

class CallCounter:
    def __init__(self, foo, limit):
        self.counter = 0
        self.foo = foo
        self.limit = limit

    def call(self, input_vector):
        self.counter += 1
        return self.foo(input_vector)

    def isUnderLimit(self):
        return self.counter < self.limit
