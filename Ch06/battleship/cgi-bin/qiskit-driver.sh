#!/bin/sh

# home dir
root=/home/centos

#program=qiskit-basic-test.py
program=qbattleship.py

# Activate python 3
source $root/qiskit/qiskit/bin/activate

#echo -e "Content-type: text/html\n\n"
#echo "Hello, World from Bash."

# execute python quantum program
python $program

