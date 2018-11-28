#https://github.com/QISKit/qiskit-tutorial/blob/old_tutorial_format/5_games/quantum_counterfeit_coin_problem.ipynb
# Checking the version of PYTHON; we only support > 3.5
import sys

# useful additional packages 
import matplotlib.pyplot as plt
import numpy as np

# useful math functions
from math import pi, cos, acos, sqrt
from collections import Counter

# importing the QISKit
from qiskit import QuantumProgram
sys.path.append('../Config/')
import Qconfig

# import basic plot tools
from qiskit.tools.visualization import plot_histogram

#########################################################
# M = 16                Maximum number of  qubits available
# numberOfCoins = 8     The number of qubits available (up to M-1)
# indexOfFalseCoin = 6  false coin index
#########################################################
def main(M = 16, numberOfCoins = 8 , indexOfFalseCoin = 6
	, backend = "local_qasm_simulator" , shots = 1 ):

	if numberOfCoins < 4 or numberOfCoins >= M:
		raise Exception("Please use numberOfCoins between 4 and ", M-1)
	if indexOfFalseCoin < 0 or indexOfFalseCoin >= numberOfCoins:
		raise Exception("indexOfFalseCoin must be between 0 and ", numberOfCoins-1)	
		
	# ------- Query the quantum beam balance
	Q_program = QuantumProgram()
	Q_program.set_api(Qconfig.APItoken, Qconfig.config["url"]) # set the APIToken and API url

	# Create registers
	# numberOfCoins qubits for the binary query string and 1 qubit for 
	# working and recording the result of quantum balance
	qr = Q_program.create_quantum_register("qr", numberOfCoins+1)
	
	# for recording the measurement on qr
	cr = Q_program.create_classical_register("cr", numberOfCoins+1)

	circuitName = "QueryStateCircuit"
	circuit 	= Q_program.create_circuit(circuitName, [qr], [cr])

	N = numberOfCoins
	
	#Create uniform superposition of all strings of length N
	for i in range(N):
		circuit.h(qr[i])

	#Perform XOR(x) by applying CNOT gates sequentially from qr[0] to qr[N-1] and storing the result to qr[N]
	for i in range(N):
		circuit.cx(qr[i], qr[N])

	#Measure qr[N] and store the result to cr[N]. We continue if cr[N] is zero, or repeat otherwise
	circuit.measure(qr[N], cr[N])

	# we proceed to query the quantum beam balance if the value of cr[0]...cr[N] is all zero
	# by preparing the Hadamard state of |1>, i.e., |0> - |1> at qr[N]
	circuit.x(qr[N]).c_if(cr, 0)
	circuit.h(qr[N]).c_if(cr, 0)

	# we rewind the computation when cr[N] is not zero
	for i in range(N):
		circuit.h(qr[i]).c_if(cr, 2**N)	

		
	#----- Construct the quantum beam balance
	k = indexOfFalseCoin
	
	# Apply the quantum beam balance on the desired superposition state (marked by cr equal to zero)
	circuit.cx(qr[k], qr[N]).c_if(cr, 0)
	
	# --- Identify the false coin
	# Apply Hadamard transform on qr[0] ... qr[N-1]
	for i in range(N):
		circuit.h(qr[i]).c_if(cr, 0)

	# Measure qr[0] ... qr[N-1]
	for i in range(N):
		circuit.measure(qr[i], cr[i])
	
	print(Q_program.get_qasm(circuitName))
	
	results 	= Q_program.execute([circuitName], backend=backend, shots=shots)
	answer 		= results.get_counts(circuitName)
	
	print("Device " + backend + " counts " + str(answer))
	
	#plot_histogram(answer)
	
	for key in answer.keys():
		normalFlag, _ = Counter(key[1:]).most_common(1)[0] #get most common label
		for i in range(2,len(key)):
			if key[i] != normalFlag:
				print("False coin index is: ", len(key) - i - 1)
				
#################################################
# main
#################################################
if __name__ ==  '__main__':
	M = 8                   #Maximum qubits available
	numberOfCoins = 4        #Up to M-1, where M is the number of qubits available
	indexOfFalseCoin = 2     #This should be 0, 1, ..., numberOfCoins - 1, 
	
	backend 	= "ibmq_qasm_simulator"
	#backend = "ibmqx3"
	shots 		= 1 		# We perform a one-shot experiment

	main(M, numberOfCoins, indexOfFalseCoin, backend, shots)
	