import os
from string import Template
import timeit

def measureTimeToCSV():
    code = Template("""
import os
os.system("{ ./speed_comparison/$exec $len $thr; } 1>/dev/null")
""")

    executable = "task5_nomd5"
    length = 6
    threads = 12

    with open("./speed_comparison/times.csv", "a") as f:
        f.write("Name: {}\n".format(executable))
        f.write("Znaky: {}\n".format(length))
        f.write("---------------------------\n")
        f.write("FORKS        TIME\n")
        f.write("{:2d}           {:f}\n".format(threads, timeit.timeit(code.substitute(exec = executable, len = str(length), thr=str(threads)), number=1)))
        f.write("\n")

if __name__ == "__main__":
    measureTimeToCSV()
