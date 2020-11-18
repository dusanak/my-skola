import os
from string import Template
import timeit

def measureTimeToCSV():
    code = Template("""
import os
os.system("{ ./ukol7/task $len; } 1>/dev/null")
""")
    with open("./ukol7/times/znaky.csv", "a") as f:
        f.write("LENGTH,TIME\n")
        for i in range(1, 3):
            f.write("{},{}\n".format(i, timeit.timeit(code.substitute(len = str(i), number=1))))

if __name__ == "__main__":
    measureTimeToCSV()
