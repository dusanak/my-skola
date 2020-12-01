class Bank:
    def __init__(self):
        self.accounts = {}

    def withdraw(self, client, amount):
        if self.accounts[client] >= amount:
            self.accounts[client] -= amount
            return True
        return False

    def deposit(self, client, amount):
        if client not in self.accounts:
            self.accounts[client] = amount
            return
        self.accounts[client] += amount