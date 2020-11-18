import os
from string import Template
import timeit

def measureTimeToCSV():
    code = Template("""
import os
os.system("{ ./ukol7/task $len $thr; } 1>/dev/null")
""")
    with open("./ukol7/times/znaky.csv", "a") as f:
        f.write("LENGTH,TIME\n")
        for j in range(1, 7):
            f.write("{},{}\n".format(j, timeit.timeit(code.substitute(len = str(j), thr = str(1024)), number=1)))
        f.write("\n")

if __name__ == "__main__":
    measureTimeToCSV()
