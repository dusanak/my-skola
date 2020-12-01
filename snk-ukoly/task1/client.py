class Client:
    def __init__(self, bank, cash):
        self.bank = bank
        self.cash = cash

    def withdraw(self, amount):
        if self.bank.withdraw(self, amount):
            self.cash += amount
            return True
        return False

    def deposit(self, amount):
        if self.cash >= amount:
            self.cash -= amount
            self.bank.deposit(self, amount)
            return True
        return False
