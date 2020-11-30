from string import Template
import os
import timeit
import time
import sys

def measureTimeToCSV():
    code = Template("""
import os
os.system("{ $path $thr $matrix $size; } 1>/dev/null")
""")

    for i in range(512, 2049, 512):
        with open(os.path.join(os.path.dirname(sys.argv[0]), "{}_2d.csv".format(i)), "a") as f:
            f.write("FORKS,TIME\n")

            f.write("{},{}\n".format(1, timeit.timeit(code.substitute(path = os.path.join(os.path.dirname(sys.argv[0]), "openmp"),
                                                                      thr = str(1),
                                                                      matrix=str(0), 
                                                                      size = str(i)),
                                                      number=1)))

            for j in range(6, 121, 6):
                f.write("{},{}\n".format(j, timeit.timeit(code.substitute(path = os.path.join(os.path.dirname(sys.argv[0]), "openmp"),
                                                                          thr = str(j),
                                                                          matrix=str(0), 
                                                                          size = str(i)),
                                                          number=1)))

    for i in range(512, 2049, 512):
        with open(os.path.join(os.path.dirname(sys.argv[0]), "{}_1d.csv".format(i)), "a") as f:
            f.write("FORKS,TIME\n")

            f.write("{},{}\n".format(1, timeit.timeit(code.substitute(path = os.path.join(os.path.dirname(sys.argv[0]), "openmp"),
                                                                      thr = str(1),
                                                                      matrix=str(1), 
                                                                      size = str(i)),
                                                      number=1)))

            for j in range(6, 121, 6):
                f.write("{},{}\n".format(j, timeit.timeit(code.substitute(path = os.path.join(os.path.dirname(sys.argv[0]), "openmp"),
                                                                          thr = str(j),
                                                                          matrix=str(1), 
                                                                          size = str(i)),
                                                          number=1)))


if __name__ == "__main__":
    measureTimeToCSV()
