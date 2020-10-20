from client import Client
from bank import Bank

bank1 = Bank()
client1 = Client(bank1, 1000)
client2 = Client(bank1, 2000)

print("Bank1: ", bank1.accounts)
print("Client1: ", client1.cash)
print("Client2: ", client2.cash)

client1.deposit(500)
print("Bank1: ", bank1.accounts)
print("Client1: ", client1.cash)
print("Client2: ", client2.cash)

client2.deposit(1500)
print("Bank1: ", bank1.accounts)
print("Client1: ", client1.cash)
print("Client2: ", client2.cash)