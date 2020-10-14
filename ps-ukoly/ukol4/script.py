import os
from string import Template
import timeit


def oldScript():
    command = Template("/bin/bash -c \"{ time ./ukol4/task3 $len $thr; } 1>/dev/null 2>> ./ukol4/times2.txt\"")
    for i in range(1, 49):
        os.system("echo " + "\"threads: " + str(i) + "\">>./ukol4/times2.txt")
        for j in range(1, 6):
            d = dict(len=str(j), thr=str(i))
            os.system(command.substitute(d))

def measureTimeToCSV():
    code = Template("""
import os
os.system("{ ./ukol4/task3 $len $thr; } 1>/dev/null")
""")
    for i in range(5, 6):
        with open("./ukol4/{}znaky.csv".format(i), "a") as f:
            #f.write("Znaky: {}\n".format(i))
            #f.write("---------------------------\n")
            #f.write("FORKS        TIME\n")
            f.write("FORKS,TIME\n")
            for j in range(1, 97):
                #f.write("{:2d}           {:f}\n".format(j, timeit.timeit(code.substitute(len = str(i), thr=str(j)), number=1)))
                f.write("{},{}\n".format(j, timeit.timeit(code.substitute(len = str(i), thr=str(j)), number=1)))

if __name__ == "__main__":
    measureTimeToCSV()
