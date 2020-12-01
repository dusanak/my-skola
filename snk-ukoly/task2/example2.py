class Animal:
    pass

class Species:
    pass

class Dog(Animal, Species):
    pass

class Breed:
    pass

class BorderCollie(Dog, Breed):
    def __init__(self, name):
        self.name = name

shep = BorderCollie("shep")
print(shep.name)
