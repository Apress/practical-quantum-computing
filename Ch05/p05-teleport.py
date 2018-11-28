import sys,time,math
import numpy as np

# Importing QISKit
from qiskit import QuantumCircuit, QuantumProgram

# Q Experience config
sys.path.append('../Config/')
import Qconfig

# Import basic plotting tools
from qiskit.tools.visualization import plot_histogram

def main():
	# Quantum program setup 
	Q_program = QuantumProgram()
	Q_program.set_api(Qconfig.APItoken, Qconfig.config["url"]) 

	# Creating registers
	q = Q_program.create_quantum_register('q', 3)
	c0 = Q_program.create_classical_register('c0', 1)
	c1 = Q_program.create_classical_register('c1', 1)
	c2 = Q_program.create_classical_register('c2', 1)

	# Quantum circuit to make the shared entangled state (Bell Pair)
	teleport = Q_program.create_circuit('teleport', [q], [c0,c1,c2])
	teleport.h(q[1])
	teleport.cx(q[1], q[2])

	# Alice prepares her quantum state to be teleported,
	# psi = a|0> + b|1> where a = cos(theta/2), b = sin (theta/2), theta = pi/4
	teleport.ry(np.pi/4,q[0])

	# Alice now applies CNOT to her two quantum states followed by an H, to entangle them 
	teleport.cx(q[0], q[1])
	teleport.h(q[0])
	teleport.barrier()

	# She now measures her two quantum states:
	teleport.measure(q[0], c0[0])
	teleport.measure(q[1], c1[0])

	circuits = ['teleport']
	print(Q_program.get_qasms(circuits)[0])

	##### BOB Depending on the results of these measurements, Bob applies an X or Z, or both, to his quantum state
	teleport.z(q[2]).c_if(c0, 1)
	teleport.x(q[2]).c_if(c1, 1)

	teleport.measure(q[2], c2[0])

	# dump asm
	circuits = ['teleport']
	print(Q_program.get_qasms(circuits)[0])

	# Execute the quantum circuits on the simulator (the real device does not support it yet), and plot the results:
	#backend = "local_qasm_simulator" 
	backend = "ibmqx_qasm_simulator"
	shots = 1024       # the number of shots in the experiment 

	result = Q_program.execute(circuits, backend=backend, shots=shots, max_credits=3,  timeout=240)

	print("Counts:" + str(result.get_counts("teleport")))
	
	# RESULTS
	# We must manipulate the data to understand these results better, first only plotting the results of Alice's measurement:
	data = result.get_counts('teleport')
	alice = {}
	alice['00'] = data['0 0 0'] + data['1 0 0']
	alice['10'] = data['0 1 0'] + data['1 1 0']
	alice['01'] = data['0 0 1'] + data['1 0 1']
	alice['11'] = data['0 1 1'] + data['1 1 1']
	plot_histogram(alice)	
	
	#BOB
	bob = {}
	bob['0'] = data['0 0 0'] + data['0 1 0'] +  data['0 0 1'] + data['0 1 1']
	bob['1'] = data['1 0 0'] + data['1 1 0'] +  data['1 0 1'] + data['1 1 1']
	plot_histogram(bob)
	
###########################################
# main
if __name__ ==  '__main__':
	start_time = time.time()
	main()
	print("--- %s seconds ---" % (time.time() - start_time))
