#############################
import sys,time,math
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
	quantum_r = qp.create_quantum_register("qr", 5)
	
	# create 1 classical register
	classical_r = qp.create_classical_register("cr", 5)
	
	# create a circuit
	circuit = qp.create_circuit("Circuit", [quantum_r], [classical_r])

	# enable logging
	qp.enable_logs(logging.DEBUG);

	# first physical gate: u1(lambda) to qubit 0
	circuit.u2(-4 *math.pi/3, 2 * math.pi, quantum_r[0])
	circuit.u2(-3 *math.pi/2, 2 * math.pi, quantum_r[0])
	circuit.u3(-math.pi, 0, -math.pi, quantum_r[0])
	circuit.u3(-math.pi, 0, -math.pi/2, quantum_r[0])
	circuit.u2(math.pi, -math.pi/2, quantum_r[0])
	circuit.u3(-math.pi, 0, -math.pi/2, quantum_r[0])

	# measure gate from qubit 0 to classical bit 0
	circuit.measure(quantum_r[0], classical_r[0])
	circuit.measure(quantum_r[1], classical_r[1])
	circuit.measure(quantum_r[2], classical_r[2])

	# backend simulator QASM: print(qp.get_qasm('Circuit'))
	backend = 'ibmqx_qasm_simulator' 
	#backend = 'ibmqx4' 
		
	# Group of circuits to execute
	circuits = ['Circuit']  

	# set the APIToken and Q Experience API url
	qp.set_api(Qconfig.APItoken, Qconfig.config['url']) 
	
	result = qp.execute(circuits, backend, shots=512, max_credits=3, timeout=240)
	
	# Show result counts
	print ("Job id=" + str(result.get_job_id()) + " Status:" + result.get_status())

###########################################
# windows
if __name__ ==  '__main__':
	start_time = time.time()
	main()
	print("--- %s seconds ---" % (time.time() - start_time))