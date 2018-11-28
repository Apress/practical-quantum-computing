import sys,time,math
    
# Importing QISKit
from qiskit import QuantumCircuit, QuantumProgram

sys.path.append('../Config/')
import Qconfig

# Import basic plotting tools
from qiskit.tools.visualization import plot_histogram

def main():
	# Quantum program setup 
	Q_program = QuantumProgram()
	Q_program.set_api(Qconfig.APItoken, Qconfig.config["url"]) 

	# Creating registers
	q = Q_program.create_quantum_register("q", 2)
	c = Q_program.create_classical_register("c", 2)

	# Quantum circuit to make the shared entangled state 
	superdense = Q_program.create_circuit("superdense", [q], [c])
	superdense.h(q[0])
	superdense.cx(q[0], q[1])	

	# For 00, do nothing

	# For 10, apply X
	#shared.x(q[0])

	# For 01, apply Z
	#shared.z(q[0])

	# For 11, apply XZ
	superdense.z(q[0]) 
	superdense.x(q[0])
	superdense.barrier()

	superdense.cx(q[0], q[1])
	superdense.h(q[0])
	superdense.measure(q[0], c[0])
	superdense.measure(q[1], c[1])	

	circuits = ["superdense"]
	print(Q_program.get_qasms(circuits)[0])

	backend = "local_qasm_simulator" #'ibmqx2'  # the device to run on
	shots = 1024       # the number of shots in the experiment 

	result = Q_program.execute(circuits, backend=backend, shots=shots, max_credits=3, wait=10, timeout=240)

	print("Counts:" + str(result.get_counts("superdense")))
	
	plot_histogram(result.get_counts("superdense"))
	
###########################################
# main
if __name__ ==  '__main__':
	start_time = time.time()
	main()
	print("--- %s seconds ---" % (time.time() - start_time))
