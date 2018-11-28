#!/bin/sh

# Usage qiskit-driver.sh [QPROGRAM-PATH]
#	Example:  qiskit-driver.sh  /home/centos/public_html/cgi-bin/qiskit-basic-test.py

# home of the python env (update this)
root=/home/centos
pythonEnv=$root/qiskit/qiskit

#program=qiskit-basic-test.py
#program=qbattleship.py
program=$1

# Activate python 3
source $pythonEnv/bin/activate

#echo -e "Content-type: text/html\n\n"
#echo "Hello, World from Bash."

# http://localhost:8080/BattleShip/TestSysRunner?p1=/home/centos/public_html/cgi-bin/qiskit-driver.sh
# execute python quantum program @ /home/centos/public_html/cgi-bin/

python $program $@

