import os
from string import Template

command = Template("/bin/bash -c \"{ time ./ukol4/task3 $len $thr; } 1>/dev/null 2>> ./ukol4/times2.txt\"")

for i in range(1, 49):
    os.system("echo " + "\"threads: " + str(i) + "\">>./ukol4/times2.txt")
    for j in range(1, 6):
        d = dict(len=str(j), thr=str(i))
        os.system(command.substitute(d))