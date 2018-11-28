#############################
import sys,time
import qiskit
import logging
from qiskit import QuantumProgram

# Q Experience config
sys.path.append('../Config/')
import Qconfig

# Generate an 2**n bit random number where n = # of qubits
def qrng(n):	

	# create a  program
	qp = QuantumProgram()
	
	# create n qubit(s)
	quantum_r = qp.create_quantum_register("qr", n)
	
	# create n classical registers
	classical_r = qp.create_classical_register("cr", n)
	
	# create a circuit
	circuit = qp.create_circuit("QRNG", [quantum_r], [classical_r])

	# enable logging
	#qp.enable_logs(logging.DEBUG);

	# Hadamard gate to all qubits 
	for i in range(n):
		circuit.h(quantum_r[i])
	
	# measure qubit n and store in classical n
	for i in range(n):
		circuit.measure(quantum_r[i], classical_r[i])

	# backend simulator
	#backend = 'local_qasm_simulator' 
	backend = 'ibmq_qasm_simulator' 
	
	# Group of circuits to execute
	circuits = ['QRNG']  

	# Compile your program: ASM print(qp.get_qasm('Circuit')), JSON: print(str(qobj))
	# set the APIToken and Q Experience API url
	qp.set_api(Qconfig.APItoken, Qconfig.config['url']) 
	shots=1024
	result = qp.execute(circuits, backend, shots=shots, max_credits=3, timeout=240)
	
	# Show result counts
	# counts={'100': 133, '101': 134, '011': 131, '110': 125, '001': 109, '111': 128, '010': 138, '000': 126}
	counts = result.get_counts('QRNG')
	bits = ""
	for v in counts.values():
		if v > shots/(2**n) :
			bits += "1"
		else:
			bits += "0"
	
	#print ("counts=" + str(counts) ) 
	#print ("items=" + str(counts.values()) )
	return int(bits, 2)
	

###########################################
if __name__ ==  '__main__':
	start_time = time.time()
	numbers = []
	
	# generate 100 8 bit rands
	size = 10
	qubits = 3 # bits = 2**qubits
	
	for i in range(size):
		n = qrng(qubits)
		numbers.append(n)
		
	print ("list=" + str(numbers))
	print("--- %s seconds ---" % (time.time() - start_time))
	