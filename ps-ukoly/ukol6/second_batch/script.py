import os
from string import Template
import timeit
import time

def measureTimeToCSV():
    code = Template("""
import os
os.system("{ mosrun ./task6 $len $thr; } 1>/dev/null")
""")
    for i in range(1, 4):
        with open("./{}znaky.csv".format(i), "a") as f:
            f.write("FORKS,TIME\n")
            for j in range(1, 91):
                f.write("{},{}\n".format(j, timeit.timeit(code.substitute(len = str(i), thr=str(j)), number=1)))
                time.sleep(0.5)


if __name__ == "__main__":
    measureTimeToCSV()
