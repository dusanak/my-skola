import os
from string import Template
import timeit

def measureTimeToCSV():
    code_old = Template("""
import os
os.system("{ ./ukol5/speed_comparison/task3 $len $thr; } 1>/dev/null")
""")

    code_new = Template("""
import os
os.system("{ ./ukol5/task5 $len $thr; } 1>/dev/null")
""")

    for i in range(6, 6):
        with open("./ukol5/speed_comparison/old.csv", "a") as f:
            f.write("Znaky: {}\n".format(i))
            f.write("---------------------------\n")
            f.write("FORKS        TIME\n")
            for j in range(12, 13):
                f.write("{:2d}           {:f}\n".format(j, timeit.timeit(code_old.substitute(len = str(i), thr=str(j)), number=1)))
            f.write("\n")

    for i in range(1, 6):
        with open("./ukol5/speed_comparison/new.csv", "a") as f:
            f.write("Znaky: {}\n".format(i))
            f.write("---------------------------\n")
            f.write("FORKS        TIME\n")
            for j in range(12, 13):
                f.write("{:2d}           {:f}\n".format(j, timeit.timeit(code_new.substitute(len = str(i), thr=str(j)), number=1)))
            f.write("\n")

if __name__ == "__main__":
    measureTimeToCSV()
