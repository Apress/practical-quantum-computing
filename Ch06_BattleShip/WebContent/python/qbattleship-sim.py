##C:/Python27/python.exe

# Simulates damage data for Win32 Test

import sys
#from qiskit import QuantumProgram
#import Qconfig
import getpass, random, math

import cgi
import cgitb

# solve the relative dependencies if you clone QISKit from the Git repo and use like a global.
sys.path.append('../../qiskit-sdk-py/') 

# debug
cgitb.enable(display=0, logdir=".")

form = cgi.FieldStorage()

# Damage  [[0,0,0,0,0],[0,0,0,0,0]]
damage 		= [ [0]*5 for _ in range(2)] # this will hold the prob of a 1 for each qubit for each player
#randPos 	= random.sample(range(5), 3)

for ship in range(5):
	damage[0][ship] = random.random() # randPos[ship]
	damage[1][ship] = random.random()

#print ("Content-type: application/json\n\n")
print ("{\"status\": 200, \"message\":\"" + str(sys.argv) + "\", \"damage\":" + str(damage) + " }")

