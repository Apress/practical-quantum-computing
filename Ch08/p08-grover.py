import sys,time,math
import numpy as np
import logging

# Importing QISKit
from qiskit import QuantumCircuit, QuantumProgram

# Q Experience config
sys.path.append('../Config/')
import Qconfig

# Import basic plotting tools
from qiskit.tools.visualization import plot_histogram

# Set the input bits to search for
def input_phase (circuit, qubits):
	# Uncomment for A = 00
	# Comment for A = 11
	#circuit.s(qubits[0])
	#circuit.s(qubits[1])
	return

# circuit: Grover 2-qubit circuit
# qubits: Array of 2 qubits
def invert_over_the_mean (circuit, qubits):
	for i in range (2):
		circuit.h(qubits[i])
		circuit.x(qubits[i])

	circuit.h(qubits[1])
	circuit.cx(qubits[0], qubits[1])
	circuit.h(qubits[1])

	for i in range (2):
		circuit.x(qubits[i])
		circuit.h(qubits[i])

def invert_phase (circuit, qubits):
	# Oracle
	circuit.h(qubits[1])
	circuit.cx(qubits[0], qubits[1])
	circuit.h(qubits[1])


def main():
	# Quantum program setup 
	qp = QuantumProgram()
	qp.set_api(Qconfig.APItoken, Qconfig.config["url"]) 

	# enable logging
	#qp.enable_logs(logging.DEBUG);

	print("Backends=" + str(qp.available_backends()))
	
	# Create qubits/registers
	size = 2
	q = qp.create_quantum_register('q', size)
	c = qp.create_classical_register('c', size)
	
	# Quantum circuit 
	grover = qp.create_circuit('grover', [q], [c]) 

	#  loops = sqrt(2^n) * PI/4
	#loops = math.floor(math.sqrt(2**size) * (math.pi/4))
	
	# 1. put all qubits in superposition
	for i in range (size):
		grover.h(q[i])

	# Set the input
	input_phase(grover, q)
	
	# 2. Phase inversion
	invert_phase(grover, q)
	
	input_phase(grover, q)

	# 3. Invert over the mean
	invert_over_the_mean (grover, q)
	
	# measure
	for i in range (size):
		grover.measure(q[i], c[i])

	circuits = ['grover']

	# Execute the quantum circuits on the simulator
	backend = "local_qasm_simulator" 
	#backend = "ibmq_qasm_simulator"
	# the number of shots in the experiment 
	shots = 1024       

	result = qp.execute(circuits, backend=backend, shots=shots, max_credits=3,  timeout=240)
	counts = result.get_counts("grover")
	print("Counts:" + str(counts))
	
	# RESULTS
	#plot_histogram(counts)
	
###########################################
# main
if __name__ ==  '__main__':
	start_time = time.time()
	main()
	print("--- %s seconds ---" % (time.time() - start_time))
