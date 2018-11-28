#############################
import sys,time
import qiskit
import logging
from qiskit import QuantumProgram

# Q Experience config
sys.path.append('../Config/')
import Qconfig

# Main sub
def main():	

	# create a  program
	qp = QuantumProgram()
	
	# create 1 qubit
	quantum_r = qp.create_quantum_register("qr", 1)
	
	# create 1 classical register
	classical_r = qp.create_classical_register("cr", 1)
	
	# create a circuit
	circuit = qp.create_circuit("Circuit", [quantum_r], [classical_r])

	# enable logging
	qp.enable_logs(logging.DEBUG);

	# Pauli X gate to qubit 1 in the Quantum Register "qr" 
	circuit.x(quantum_r[0])
	
	# measure gate from qubit 0 to classical bit 0
	circuit.measure(quantum_r[0], classical_r[0])

	# backend simulator
	backend = 'ibmqx_qasm_simulator' 

	# Group of circuits to execute
	circuits = ['Circuit']  

	# set the APIToken and Q Experience API url
	qp.set_api(Qconfig.APItoken, Qconfig.config['url']) 
	
	result = qp.execute(circuits, backend, shots=512, max_credits=3,  timeout=240)
	
	# Show result counts
	print (str(result.get_counts('Circuit')))

###########################################
# Linux :main() 
# windows
if __name__ ==  '__main__':
	start_time = time.time()
	main()
	print("--- %s seconds ---" % (time.time() - start_time))